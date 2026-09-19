package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.CloudDownload
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PlaylistAddCheck
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.AddStudentDialog
import com.example.ui.components.BackupRestoreDialog
import com.example.ui.components.ConfirmClearDialog
import com.example.ui.components.FullScreenBroadsheetScreen
import com.example.ui.components.FullScreenMarksheetScreen
import com.example.ui.components.SettingsDialog
import com.example.ui.screens.MarksEntryScreen
import com.example.ui.screens.MassSubjectEntryScreen
import com.example.ui.screens.MasterSheetScreen
import com.example.ui.screens.ReportCardScreen
import com.example.ui.screens.StudentListScreen
import com.example.ui.theme.AcademicTeal
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SchoolNavy
import com.example.ui.viewmodel.AppScreen
import com.example.ui.viewmodel.StudentViewModel

class MainActivity : ComponentActivity() {

    private val viewModel: StudentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainAppPortal(viewModel = viewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppPortal(viewModel: StudentViewModel) {
    val students by viewModel.students.collectAsStateWithLifecycle()
    val schoolConfig by viewModel.schoolConfig.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentStudentIndex.collectAsStateWithLifecycle()
    val currentStudent by viewModel.currentStudent.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val selectedMassSubject by viewModel.selectedMassSubject.collectAsStateWithLifecycle()
    val customRemarks by viewModel.customRemarks.collectAsStateWithLifecycle()

    val showSettingsDialog by viewModel.showSettingsDialog.collectAsStateWithLifecycle()
    val showAddStudentDialog by viewModel.showAddStudentDialog.collectAsStateWithLifecycle()
    val showClearConfirmDialog by viewModel.showClearConfirmDialog.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    var showBackupRestoreDialog by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.clearSnackbar()
        }
    }

    val isFullScreen = currentScreen == AppScreen.FULLSCREEN_MARKSHEET || currentScreen == AppScreen.FULLSCREEN_BROADSHEET

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            if (!isFullScreen) {
                TopAppBar(
                    title = {
                    Column {
                        Text(
                            text = schoolConfig.schoolName,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${schoolConfig.classSec} • ${schoolConfig.session}",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = Color.White,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Offline Marks Portal",
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.8f)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SchoolNavy,
                    titleContentColor = Color.White,
                    actionIconContentColor = Color.White
                ),
                actions = {
                    IconButton(
                        onClick = { viewModel.loadAllSampleData() },
                        modifier = Modifier.testTag("btn_menu_sample_data")
                    ) {
                        Icon(
                            Icons.Default.CloudDownload,
                            contentDescription = "Load Sample Data",
                            tint = Color.White
                        )
                    }

                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.testTag("btn_top_menu")
                    ) {
                        Icon(Icons.Default.MoreVert, contentDescription = "Menu")
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("School & Session Settings") },
                            leadingIcon = { Icon(Icons.Default.Settings, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.showSettingsDialog(true)
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Load Sample 28 Students") },
                            leadingIcon = { Icon(Icons.Default.CloudDownload, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.loadAllSampleData()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Backup & Restore JSON") },
                            leadingIcon = { Icon(Icons.Default.Storage, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                showBackupRestoreDialog = true
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("Clear All Data") },
                            leadingIcon = { Icon(Icons.Default.DeleteSweep, contentDescription = null) },
                            onClick = {
                                showMenu = false
                                viewModel.showClearConfirmDialog(true)
                            }
                        )
                    }
                }
            )
        }
    },
    bottomBar = {
        if (!isFullScreen) {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 6.dp
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.ROSTER,
                    onClick = { viewModel.setScreen(AppScreen.ROSTER) },
                    icon = { Icon(Icons.Default.People, contentDescription = "Students") },
                    label = { Text("Students", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SchoolNavy,
                        indicatorColor = SchoolNavy
                    ),
                    modifier = Modifier.testTag("nav_students")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.MARKS_ENTRY,
                    onClick = { viewModel.setScreen(AppScreen.MARKS_ENTRY) },
                    icon = { Icon(Icons.Default.Edit, contentDescription = "Marks Entry") },
                    label = { Text("Marks", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SchoolNavy,
                        indicatorColor = SchoolNavy
                    ),
                    modifier = Modifier.testTag("nav_marks_entry")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.MASS_ENTRY,
                    onClick = { viewModel.setScreen(AppScreen.MASS_ENTRY) },
                    icon = { Icon(Icons.Default.PlaylistAddCheck, contentDescription = "Mass Entry") },
                    label = { Text("Mass View", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SchoolNavy,
                        indicatorColor = SchoolNavy
                    ),
                    modifier = Modifier.testTag("nav_mass_entry")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.REPORT_CARD,
                    onClick = { viewModel.setScreen(AppScreen.REPORT_CARD) },
                    icon = { Icon(Icons.Default.Assessment, contentDescription = "Report Card") },
                    label = { Text("Marksheet", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SchoolNavy,
                        indicatorColor = SchoolNavy
                    ),
                    modifier = Modifier.testTag("nav_report_card")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.MASTER_SHEET,
                    onClick = { viewModel.setScreen(AppScreen.MASTER_SHEET) },
                    icon = { Icon(Icons.Default.GridOn, contentDescription = "Master Sheet") },
                    label = { Text("Broadsheet", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color.White,
                        selectedTextColor = SchoolNavy,
                        indicatorColor = SchoolNavy
                    ),
                    modifier = Modifier.testTag("nav_master_sheet")
                )
            }
        }
    }
) { innerPadding ->
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(if (isFullScreen) androidx.compose.foundation.layout.PaddingValues(0.dp) else innerPadding)
    ) {
            when (currentScreen) {
                AppScreen.ROSTER -> {
                    StudentListScreen(
                        students = students,
                        schoolConfig = schoolConfig,
                        searchQuery = searchQuery,
                        onSearchQueryChange = { viewModel.setSearchQuery(it) },
                        onSelectStudent = { viewModel.selectStudentByIndex(it) },
                        onOpenMarksEntry = {
                            viewModel.selectStudentByIndex(it)
                            viewModel.setScreen(AppScreen.MARKS_ENTRY)
                        },
                        onOpenReportCard = {
                            viewModel.selectStudentByIndex(it)
                            viewModel.setScreen(AppScreen.REPORT_CARD)
                        },
                        onAddNewStudentClick = { viewModel.showAddStudentDialog(true) }
                    )
                }
                AppScreen.MARKS_ENTRY -> {
                    MarksEntryScreen(
                        student = currentStudent,
                        totalStudents = students.size,
                        currentIndex = currentIndex,
                        onPrevStudent = { viewModel.prevStudent() },
                        onNextStudent = { viewModel.nextStudent() },
                        onDeleteStudent = { viewModel.deleteCurrentStudent() },
                        onUpdateMeta = { name, exam, sr -> viewModel.updateStudentMeta(name, exam, sr) },
                        onUpdateTier = { subject, tier, t1, t2, t3 ->
                            viewModel.updateSubjectTier(subject, tier, t1, t2, t3)
                        },
                        onOpenReportCard = { viewModel.setScreen(AppScreen.REPORT_CARD) }
                    )
                }
                AppScreen.MASS_ENTRY -> {
                    MassSubjectEntryScreen(
                        students = students,
                        selectedSubject = selectedMassSubject,
                        onSelectSubject = { viewModel.setMassSubject(it) },
                        onUpdateMarks = { studentId, tierIdx, t1, t2, t3 ->
                            viewModel.updateMassSubjectMarks(selectedMassSubject, studentId, tierIdx, t1, t2, t3)
                        }
                    )
                }
                AppScreen.REPORT_CARD -> {
                    ReportCardScreen(
                        student = currentStudent,
                        schoolConfig = schoolConfig,
                        totalStudents = students.size,
                        currentIndex = currentIndex,
                        customRemarks = customRemarks,
                        onPrevStudent = { viewModel.prevStudent() },
                        onNextStudent = { viewModel.nextStudent() },
                        onCustomRemarksChange = { viewModel.setCustomRemarks(it) },
                        onOpenFullScreen = { viewModel.setScreen(AppScreen.FULLSCREEN_MARKSHEET) }
                    )
                }
                AppScreen.MASTER_SHEET -> {
                    MasterSheetScreen(
                        students = students,
                        schoolConfig = schoolConfig,
                        onSelectStudent = { viewModel.selectStudentByIndex(it) },
                        onOpenMarksEntry = {
                            viewModel.selectStudentByIndex(it)
                            viewModel.setScreen(AppScreen.MARKS_ENTRY)
                        },
                        onOpenFullScreen = { viewModel.setScreen(AppScreen.FULLSCREEN_BROADSHEET) }
                    )
                }
                AppScreen.FULLSCREEN_MARKSHEET -> {
                    FullScreenMarksheetScreen(
                        student = currentStudent,
                        schoolConfig = schoolConfig,
                        displayRemarks = customRemarks ?: currentStudent?.computeSmartRemarks().orEmpty(),
                        onBack = { viewModel.setScreen(AppScreen.REPORT_CARD) }
                    )
                }
                AppScreen.FULLSCREEN_BROADSHEET -> {
                    FullScreenBroadsheetScreen(
                        students = students,
                        schoolConfig = schoolConfig,
                        onBack = { viewModel.setScreen(AppScreen.MASTER_SHEET) }
                    )
                }
            }
        }
    }

    // Dialogs
    if (showSettingsDialog) {
        SettingsDialog(
            currentConfig = schoolConfig,
            onDismiss = { viewModel.showSettingsDialog(false) },
            onSave = { name, session, cls -> viewModel.saveSchoolConfig(name, session, cls) }
        )
    }

    if (showAddStudentDialog) {
        AddStudentDialog(
            onDismiss = { viewModel.showAddStudentDialog(false) },
            onAdd = { name, sr, exam -> viewModel.addNewStudent(name, sr, exam) }
        )
    }

    if (showClearConfirmDialog) {
        ConfirmClearDialog(
            onDismiss = { viewModel.showClearConfirmDialog(false) },
            onConfirm = { viewModel.clearAllData() }
        )
    }

    if (showBackupRestoreDialog) {
        BackupRestoreDialog(
            onDismiss = { showBackupRestoreDialog = false },
            onGetBackupJson = { viewModel.exportDatabaseAsJson() },
            onRestoreJson = { viewModel.restoreFromJson(it) }
        )
    }
}

// Preserved for screenshot test compatibility
@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(text = "Hello $name!", modifier = modifier)
}
