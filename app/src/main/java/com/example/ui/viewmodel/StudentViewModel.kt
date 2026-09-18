package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.AppDatabase
import com.example.data.local.MarksJsonConverter
import com.example.data.model.SchoolConfig
import com.example.data.model.StudentRecord
import com.example.data.model.SubjectEvaluation
import com.example.data.model.SubjectMarks
import com.example.data.repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

enum class AppScreen {
    ROSTER,
    MARKS_ENTRY,
    MASS_ENTRY,
    REPORT_CARD,
    MASTER_SHEET
}

class StudentViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: StudentRepository

    init {
        val db = AppDatabase.getDatabase(application)
        repository = StudentRepository(db)
        viewModelScope.launch {
            repository.seedSampleDataIfEmpty()
        }
    }

    val students: StateFlow<List<StudentRecord>> = repository.allStudents
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val schoolConfig: StateFlow<SchoolConfig> = repository.schoolConfig
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = SchoolConfig()
        )

    private val _currentScreen = MutableStateFlow(AppScreen.ROSTER)
    val currentScreen: StateFlow<AppScreen> = _currentScreen.asStateFlow()

    private val _currentStudentIndex = MutableStateFlow(0)
    val currentStudentIndex: StateFlow<Int> = _currentStudentIndex.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedMassSubject = MutableStateFlow("HINDI")
    val selectedMassSubject: StateFlow<String> = _selectedMassSubject.asStateFlow()

    private val _customRemarks = MutableStateFlow<String?>(null)
    val customRemarks: StateFlow<String?> = _customRemarks.asStateFlow()

    // Dialog flags
    private val _showSettingsDialog = MutableStateFlow(false)
    val showSettingsDialog: StateFlow<Boolean> = _showSettingsDialog.asStateFlow()

    private val _showAddStudentDialog = MutableStateFlow(false)
    val showAddStudentDialog: StateFlow<Boolean> = _showAddStudentDialog.asStateFlow()

    private val _showClearConfirmDialog = MutableStateFlow(false)
    val showClearConfirmDialog: StateFlow<Boolean> = _showClearConfirmDialog.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun setScreen(screen: AppScreen) {
        _currentScreen.value = screen
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setMassSubject(subject: String) {
        _selectedMassSubject.value = subject
    }

    fun setCustomRemarks(remarks: String?) {
        _customRemarks.value = remarks
    }

    fun selectStudentByIndex(index: Int) {
        val list = students.value
        if (list.isNotEmpty()) {
            _currentStudentIndex.value = index.coerceIn(0, list.size - 1)
            _customRemarks.value = null
        }
    }

    fun nextStudent() {
        val list = students.value
        if (list.isNotEmpty()) {
            val next = (_currentStudentIndex.value + 1).coerceAtMost(list.size - 1)
            _currentStudentIndex.value = next
            _customRemarks.value = null
        }
    }

    fun prevStudent() {
        val list = students.value
        if (list.isNotEmpty()) {
            val prev = (_currentStudentIndex.value - 1).coerceAtLeast(0)
            _currentStudentIndex.value = prev
            _customRemarks.value = null
        }
    }

    val currentStudent: StateFlow<StudentRecord?> = combine(students, _currentStudentIndex) { list, idx ->
        if (list.isNotEmpty()) list.getOrNull(idx) ?: list.firstOrNull() else null
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    fun updateStudentMeta(name: String, exam: String, sr: String) {
        val current = currentStudent.value ?: return
        val updated = current.copy(name = name, exam = exam, sr = sr)
        viewModelScope.launch {
            repository.updateStudent(updated)
        }
    }

    fun updateSubjectTier(
        subjectName: String,
        tierIndex: Int, // 0 = R1 (Unit Test), 1 = R2 (Half Yearly), 2 = R3 (Annual)
        t1: String? = null,
        t2: String? = null,
        t3: String? = null
    ) {
        val current = currentStudent.value ?: return
        val currentMarks = current.marks.toMutableMap()
        val subject = currentMarks[subjectName] ?: SubjectMarks(subjectName = subjectName)

        val updatedSubject = when (tierIndex) {
            0 -> {
                val currentEval = subject.r1UnitTest
                subject.copy(
                    r1UnitTest = currentEval.copy(
                        t1 = t1 ?: currentEval.t1,
                        t2 = t2 ?: currentEval.t2,
                        t3 = t3 ?: currentEval.t3
                    )
                )
            }
            1 -> {
                val currentEval = subject.r2HalfYearly
                subject.copy(
                    r2HalfYearly = currentEval.copy(
                        t1 = t1 ?: currentEval.t1,
                        t2 = t2 ?: currentEval.t2,
                        t3 = t3 ?: currentEval.t3
                    )
                )
            }
            2 -> {
                val currentEval = subject.r3Annual
                subject.copy(
                    r3Annual = currentEval.copy(
                        t1 = t1 ?: currentEval.t1,
                        t2 = t2 ?: currentEval.t2,
                        t3 = t3 ?: currentEval.t3
                    )
                )
            }
            else -> subject
        }

        currentMarks[subjectName] = updatedSubject
        val updatedStudent = current.copy(marks = currentMarks)

        viewModelScope.launch {
            repository.updateStudent(updatedStudent)
        }
    }

    fun updateMassSubjectMarks(
        subjectName: String,
        studentId: Long,
        tierIndex: Int,
        t1: String,
        t2: String,
        t3: String
    ) {
        val target = students.value.find { it.id == studentId } ?: return
        val marks = target.marks.toMutableMap()
        val sub = marks[subjectName] ?: SubjectMarks(subjectName = subjectName)

        val updatedSub = when (tierIndex) {
            0 -> sub.copy(r1UnitTest = SubjectEvaluation(t1, t2, t3))
            1 -> sub.copy(r2HalfYearly = SubjectEvaluation(t1, t2, t3))
            2 -> sub.copy(r3Annual = SubjectEvaluation(t1, t2, t3))
            else -> sub
        }
        marks[subjectName] = updatedSub
        viewModelScope.launch {
            repository.updateStudent(target.copy(marks = marks))
        }
    }

    fun addNewStudent(name: String, sr: String, exam: String) {
        viewModelScope.launch {
            val list = students.value
            val nextRoll = (list.maxOfOrNull { it.rollNo } ?: 0) + 1
            val newStudent = StudentRecord(
                id = 0,
                rollNo = nextRoll,
                name = name.trim().ifEmpty { "Student $nextRoll" },
                exam = exam.trim(),
                sr = sr.trim().ifEmpty { "SR-$nextRoll" }
            )
            repository.insertStudent(newStudent)
            _currentStudentIndex.value = list.size
            _snackbarMessage.value = "Added Roll No. $nextRoll: ${newStudent.name}"
        }
    }

    fun deleteCurrentStudent() {
        val current = currentStudent.value ?: return
        val list = students.value
        if (list.size <= 1) {
            _snackbarMessage.value = "Cannot delete the only student!"
            return
        }
        viewModelScope.launch {
            repository.deleteStudent(current.id)
            val newIndex = _currentStudentIndex.value.coerceAtMost(list.size - 2)
            _currentStudentIndex.value = newIndex
            _snackbarMessage.value = "Deleted student ${current.name}"
        }
    }

    fun loadAllSampleData() {
        viewModelScope.launch {
            repository.loadFullSampleData()
            _currentStudentIndex.value = 0
            _snackbarMessage.value = "Loaded 28 sample students successfully!"
        }
    }

    fun clearAllData() {
        viewModelScope.launch {
            repository.deleteAllStudents()
            // insert 1 initial blank student
            val blank = StudentRecord(
                id = 0,
                rollNo = 1,
                name = "",
                sr = "SR-101"
            )
            repository.insertStudent(blank)
            _currentStudentIndex.value = 0
            _snackbarMessage.value = "Data cleared successfully."
        }
    }

    fun saveSchoolConfig(schoolName: String, session: String, classSec: String) {
        viewModelScope.launch {
            repository.saveSchoolConfig(
                SchoolConfig(
                    schoolName = schoolName.trim(),
                    session = session.trim(),
                    classSec = classSec.trim()
                )
            )
            _snackbarMessage.value = "School settings updated."
        }
    }

    fun showSettingsDialog(show: Boolean) {
        _showSettingsDialog.value = show
    }

    fun showAddStudentDialog(show: Boolean) {
        _showAddStudentDialog.value = show
    }

    fun showClearConfirmDialog(show: Boolean) {
        _showClearConfirmDialog.value = show
    }

    fun exportDatabaseAsJson(): String {
        val root = JSONObject()
        root.put("app", "SMT_SDEI_MARKS_PORTAL")
        root.put("version", "1.0")
        root.put("exportedAt", System.currentTimeMillis())

        val cfg = schoolConfig.value
        val cfgObj = JSONObject().apply {
            put("schoolName", cfg.schoolName)
            put("session", cfg.session)
            put("classSec", cfg.classSec)
        }
        root.put("globalSettings", cfgObj)

        val studentsArray = JSONArray()
        for (s in students.value) {
            val sObj = JSONObject().apply {
                put("rollNo", s.rollNo)
                put("name", s.name)
                put("exam", s.exam)
                put("sr", s.sr)
                put("marks", JSONObject(MarksJsonConverter.toJson(s.marks)))
            }
            studentsArray.put(sObj)
        }
        root.put("students", studentsArray)

        return root.toString(2)
    }

    fun restoreFromJson(jsonString: String): Boolean {
        return try {
            val root = JSONObject(jsonString)
            val studentsArr = root.optJSONArray("students") ?: return false

            val restoredList = mutableListOf<StudentRecord>()
            for (i in 0 until studentsArr.length()) {
                val sObj = studentsArr.getJSONObject(i)
                val marksObj = sObj.optJSONObject("marks")
                val marksJson = marksObj?.toString() ?: ""

                restoredList.add(
                    StudentRecord(
                        id = 0,
                        rollNo = sObj.optInt("rollNo", i + 1),
                        name = sObj.optString("name", ""),
                        exam = sObj.optString("exam", ""),
                        sr = sObj.optString("sr", ""),
                        marks = MarksJsonConverter.fromJson(marksJson)
                    )
                )
            }

            var cfg: SchoolConfig? = null
            val cfgObj = root.optJSONObject("globalSettings")
            if (cfgObj != null) {
                cfg = SchoolConfig(
                    schoolName = cfgObj.optString("schoolName", "SMT SDEI SECONDARY SCHOOL"),
                    session = cfgObj.optString("session", "2026-27"),
                    classSec = cfgObj.optString("classSec", "7th")
                )
            }

            viewModelScope.launch {
                repository.restoreStudents(restoredList, cfg)
                _currentStudentIndex.value = 0
                _snackbarMessage.value = "Restored ${restoredList.size} students successfully!"
            }
            true
        } catch (_: Exception) {
            _snackbarMessage.value = "Failed to parse backup JSON."
            false
        }
    }
}
