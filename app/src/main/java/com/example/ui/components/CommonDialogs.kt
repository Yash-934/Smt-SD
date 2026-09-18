package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolConfig
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolNavy

@Composable
fun SettingsDialog(
    currentConfig: SchoolConfig,
    onDismiss: () -> Unit,
    onSave: (schoolName: String, session: String, classSec: String) -> Unit
) {
    var schoolName by remember { mutableStateOf(currentConfig.schoolName) }
    var session by remember { mutableStateOf(currentConfig.session) }
    var classSec by remember { mutableStateOf(currentConfig.classSec) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Academic Session & School Settings",
                style = MaterialTheme.typography.titleMedium,
                color = SchoolNavy
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = schoolName,
                    onValueChange = { schoolName = it },
                    label = { Text("School Name") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_school_name")
                )

                OutlinedTextField(
                    value = session,
                    onValueChange = { session = it },
                    label = { Text("Academic Session (e.g. 2026-27)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_session")
                )

                OutlinedTextField(
                    value = classSec,
                    onValueChange = { classSec = it },
                    label = { Text("Class & Section (e.g. 7th or 8th-A)") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_class_sec")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onSave(schoolName, session, classSec)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                modifier = Modifier.testTag("btn_save_settings")
            ) {
                Text("Save Settings")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun AddStudentDialog(
    onDismiss: () -> Unit,
    onAdd: (name: String, sr: String, exam: String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var sr by remember { mutableStateOf("") }
    var exam by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add New Student",
                style = MaterialTheme.typography.titleMedium,
                color = SchoolNavy
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Student Full Name") },
                    placeholder = { Text("e.g. Rajesh Kumar") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_student_name")
                )

                OutlinedTextField(
                    value = sr,
                    onValueChange = { sr = it },
                    label = { Text("Scholar / SR. No.") },
                    placeholder = { Text("e.g. SR-129") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_student_sr")
                )

                OutlinedTextField(
                    value = exam,
                    onValueChange = { exam = it },
                    label = { Text("Examination Term") },
                    placeholder = { Text("e.g. Annual 2026") },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_student_exam")
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onAdd(name, sr, exam)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                modifier = Modifier.testTag("btn_confirm_add_student")
            ) {
                Text("Add Student")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun ConfirmClearDialog(
    onDismiss: () -> Unit,
    onConfirm: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Clear All Student Data?",
                style = MaterialTheme.typography.titleMedium,
                color = DangerRed
            )
        },
        text = {
            Text(
                text = "This will delete all student records and mark entries. You can restore them later using the 'Load Sample Data' button or JSON backup.",
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = {
                    onConfirm()
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = DangerRed),
                modifier = Modifier.testTag("btn_confirm_clear_data")
            ) {
                Text("Clear All Data")
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun BackupRestoreDialog(
    onDismiss: () -> Unit,
    onGetBackupJson: () -> String,
    onRestoreJson: (String) -> Boolean
) {
    var restoreInput by remember { mutableStateOf("") }
    var tabIndex by remember { mutableStateOf(0) }
    var backupJson by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (tabIndex == 0) "Offline Backup" else "Restore Database",
                style = MaterialTheme.typography.titleMedium,
                color = SchoolNavy
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(onClick = {
                        tabIndex = 0
                        backupJson = onGetBackupJson()
                    }) {
                        Text(
                            "Export / Backup",
                            color = if (tabIndex == 0) SchoolNavy else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    TextButton(onClick = { tabIndex = 1 }) {
                        Text(
                            "Restore JSON",
                            color = if (tabIndex == 1) SchoolNavy else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                if (tabIndex == 0) {
                    if (backupJson.isEmpty()) {
                        backupJson = onGetBackupJson()
                    }
                    Text(
                        "JSON backup of all student records and settings:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = backupJson,
                        onValueChange = {},
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                } else {
                    Text(
                        "Paste JSON backup data here to restore:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = restoreInput,
                        onValueChange = { restoreInput = it },
                        placeholder = { Text("Paste JSON backup here...") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp)
                    )
                }
            }
        },
        confirmButton = {
            if (tabIndex == 1) {
                Button(
                    onClick = {
                        if (restoreInput.isNotBlank()) {
                            val success = onRestoreJson(restoreInput)
                            if (success) onDismiss()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                ) {
                    Text("Restore Now")
                }
            } else {
                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                ) {
                    Text("Close")
                }
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
