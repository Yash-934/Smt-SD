package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.local.SettingsEntity
import com.example.data.local.StudentEntity
import com.example.data.model.SampleData
import com.example.data.model.SchoolConfig
import com.example.data.model.StudentRecord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class StudentRepository(private val database: AppDatabase) {

    private val studentDao = database.studentDao()
    private val settingsDao = database.settingsDao()

    val allStudents: Flow<List<StudentRecord>> = studentDao.getAllStudents().map { entities ->
        entities.map { it.toModel() }
    }

    val schoolConfig: Flow<SchoolConfig> = settingsDao.getSettings().map { entity ->
        entity?.toModel() ?: SchoolConfig()
    }

    suspend fun insertStudent(student: StudentRecord): Long = withContext(Dispatchers.IO) {
        val entity = StudentEntity.fromModel(student)
        studentDao.insert(entity)
    }

    suspend fun updateStudent(student: StudentRecord) = withContext(Dispatchers.IO) {
        val entity = StudentEntity.fromModel(student)
        studentDao.update(entity)
    }

    suspend fun deleteStudent(id: Long) = withContext(Dispatchers.IO) {
        studentDao.deleteById(id)
    }

    suspend fun deleteAllStudents() = withContext(Dispatchers.IO) {
        studentDao.deleteAll()
    }

    suspend fun saveSchoolConfig(config: SchoolConfig) = withContext(Dispatchers.IO) {
        settingsDao.saveSettings(SettingsEntity.fromModel(config))
    }

    suspend fun seedSampleDataIfEmpty() = withContext(Dispatchers.IO) {
        val count = studentDao.getStudentCount()
        if (count == 0) {
            loadFullSampleData()
        }
    }

    suspend fun loadFullSampleData() = withContext(Dispatchers.IO) {
        studentDao.deleteAll()
        val sampleList = SampleData.getFull28Students()
        val entities = sampleList.map { StudentEntity.fromModel(it) }
        studentDao.insertAll(entities)
        saveSchoolConfig(SchoolConfig(session = "2026-27", classSec = "7th"))
    }

    suspend fun restoreStudents(students: List<StudentRecord>, config: SchoolConfig?) = withContext(Dispatchers.IO) {
        studentDao.deleteAll()
        val entities = students.mapIndexed { idx, s ->
            StudentEntity.fromModel(s.copy(id = 0, rollNo = idx + 1))
        }
        studentDao.insertAll(entities)
        if (config != null) {
            saveSchoolConfig(config)
        }
    }
}
