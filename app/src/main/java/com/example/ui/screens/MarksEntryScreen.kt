package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentRecord
import com.example.data.model.SubjectMarks
import com.example.ui.theme.AcademicTeal
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolNavy

@Composable
fun MarksEntryScreen(
    student: StudentRecord?,
    totalStudents: Int,
    currentIndex: Int,
    onPrevStudent: () -> Unit,
    onNextStudent: () -> Unit,
    onDeleteStudent: () -> Unit,
    onUpdateMeta: (name: String, exam: String, sr: String) -> Unit,
    onUpdateTier: (subjectName: String, tierIndex: Int, t1: String?, t2: String?, t3: String?) -> Unit,
    onOpenReportCard: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (student == null) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("No student record found. Please add a student.")
        }
        return
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 12.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Top Navigation Controls
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        OutlinedButton(
                            onClick = onPrevStudent,
                            enabled = currentIndex > 0,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_prev_student")
                        ) {
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Previous",
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", fontSize = 12.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        Surface(
                            shape = RoundedCornerShape(20.dp),
                            color = SchoolNavy
                        ) {
                            Text(
                                text = "Roll: ${currentIndex + 1} / $totalStudents",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                            )
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = onNextStudent,
                            enabled = currentIndex < totalStudents - 1,
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("btn_next_student")
                        ) {
                            Text("Next", fontSize = 12.sp)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = "Next",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Row {
                        IconButton(
                            onClick = onDeleteStudent,
                            modifier = Modifier.testTag("btn_delete_current_student")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = DangerRed)
                        }
                    }
                }
            }
        }

        // Student Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Student Profile Information",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = SchoolNavy
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = student.name,
                            onValueChange = { onUpdateMeta(it, student.exam, student.sr) },
                            label = { Text("Student Name", fontSize = 11.sp) },
                            placeholder = { Text("Full Name") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1.4f)
                                .testTag("entry_student_name"),
                            textStyle = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold)
                        )

                        OutlinedTextField(
                            value = student.sr,
                            onValueChange = { onUpdateMeta(student.name, student.exam, it) },
                            label = { Text("SR. NO.", fontSize = 11.sp) },
                            placeholder = { Text("e.g. SR-101") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("entry_student_sr"),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )

                        OutlinedTextField(
                            value = student.exam,
                            onValueChange = { onUpdateMeta(student.name, it, student.sr) },
                            label = { Text("Exam Term", fontSize = 11.sp) },
                            placeholder = { Text("Term") },
                            singleLine = true,
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("entry_student_exam"),
                            textStyle = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        // Section Title
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Evaluation Marks Matrix",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Text(
                        text = "R1: Unit Test 1 & 2 • R2: Half Yearly • R3: Annual",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        fontSize = 11.sp
                    )
                }

                Button(
                    onClick = onOpenReportCard,
                    colors = ButtonDefaults.buttonColors(containerColor = AcademicTeal),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("btn_quick_view_marksheet")
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Result Marksheet", fontSize = 12.sp)
                }
            }
        }

        // 10 Subject Cards
        StudentRecord.ALL_SUBJECTS.forEach { subName ->
            val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
            item(key = subName) {
                SubjectMarksCard(
                    subMarks = subMarks,
                    onUpdateTier = { tierIdx, t1, t2, t3 ->
                        onUpdateTier(subName, tierIdx, t1, t2, t3)
                    }
                )
            }
        }

        // Grand Total Summary Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SchoolNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "Marks Summary & Aggregates",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SummaryColumnItem(
                            label = "Unit Test (Ob.M)",
                            value = "${student.totalUtObtained.toInt()} / 1000"
                        )
                        SummaryColumnItem(
                            label = "Half Yearly (Ob.M)",
                            value = "${student.totalHalfYearlyObtained.toInt()} / 1000"
                        )
                        SummaryColumnItem(
                            label = "Annual (Ob.M)",
                            value = "${student.totalAnnualObtained.toInt()} / 1000"
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Grand Total (HY + Annual)",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = "${student.grandTotal.toInt()} / 2000",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        }

                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "Percentage",
                                color = Color.White.copy(alpha = 0.8f),
                                fontSize = 12.sp
                            )
                            Text(
                                text = String.format("%.2f%%", student.overallPercentage),
                                color = Color(0xFFFDE047),
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 18.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SummaryColumnItem(label: String, value: String) {
    Column {
        Text(
            text = label,
            fontSize = 11.sp,
            color = Color.White.copy(alpha = 0.75f)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            fontSize = 13.sp
        )
    }
}

@Composable
fun SubjectMarksCard(
    subMarks: SubjectMarks,
    onUpdateTier: (tierIndex: Int, t1: String?, t2: String?, t3: String?) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            // Subject header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = subMarks.subjectName,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = SchoolNavy
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "Total: ${subMarks.grandTotal.toInt()}/200",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        color = if (subMarks.grandTotal > 0 && subMarks.grandTotal < 80) DangerRed else AcademicTeal
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Text(
                            text = String.format("%.1f%%", subMarks.percentage),
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = SchoolNavy,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // R1 Unit Test Row
            TierInputRow(
                label = "R1 (UT)",
                t1 = subMarks.r1UnitTest.t1,
                t2 = subMarks.r1UnitTest.t2,
                t3 = subMarks.r1UnitTest.t3,
                calculatedSum = "${subMarks.utObtained.toInt()}",
                onT1Change = { onUpdateTier(0, it, null, null) },
                onT2Change = { onUpdateTier(0, null, it, null) },
                onT3Change = { onUpdateTier(0, null, null, it) },
                testTagPrefix = "${subMarks.subjectName}_r1"
            )

            Spacer(modifier = Modifier.height(6.dp))

            // R2 Half Yearly Row
            TierInputRow(
                label = "R2 (HY)",
                t1 = subMarks.r2HalfYearly.t1,
                t2 = subMarks.r2HalfYearly.t2,
                t3 = subMarks.r2HalfYearly.t3,
                calculatedSum = "${subMarks.halfYearlyObtained.toInt()}",
                onT1Change = { onUpdateTier(1, it, null, null) },
                onT2Change = { onUpdateTier(1, null, it, null) },
                onT3Change = { onUpdateTier(1, null, null, it) },
                testTagPrefix = "${subMarks.subjectName}_r2"
            )

            Spacer(modifier = Modifier.height(6.dp))

            // R3 Annual Row
            TierInputRow(
                label = "R3 (Ann)",
                t1 = subMarks.r3Annual.t1,
                t2 = subMarks.r3Annual.t2,
                t3 = subMarks.r3Annual.t3,
                calculatedSum = "${subMarks.annualObtained.toInt()}",
                onT1Change = { onUpdateTier(2, it, null, null) },
                onT2Change = { onUpdateTier(2, null, it, null) },
                onT3Change = { onUpdateTier(2, null, null, it) },
                testTagPrefix = "${subMarks.subjectName}_r3"
            )
        }
    }
}

@Composable
private fun TierInputRow(
    label: String,
    t1: String,
    t2: String,
    t3: String,
    calculatedSum: String,
    onT1Change: (String) -> Unit,
    onT2Change: (String) -> Unit,
    onT3Change: (String) -> Unit,
    testTagPrefix: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(60.dp)
        )

        MiniScoreField(
            value = t1,
            onValueChange = onT1Change,
            placeholder = "T1",
            modifier = Modifier
                .weight(1f)
                .testTag("${testTagPrefix}_t1")
        )

        MiniScoreField(
            value = t2,
            onValueChange = onT2Change,
            placeholder = "T2",
            modifier = Modifier
                .weight(1f)
                .testTag("${testTagPrefix}_t2")
        )

        MiniScoreField(
            value = t3,
            onValueChange = onT3Change,
            placeholder = "T3",
            modifier = Modifier
                .weight(1f)
                .testTag("${testTagPrefix}_t3")
        )

        Surface(
            shape = RoundedCornerShape(4.dp),
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.width(42.dp)
        ) {
            Text(
                text = calculatedSum,
                textAlign = TextAlign.Center,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(vertical = 5.dp)
            )
        }
    }
}

@Composable
fun MiniScoreField(
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = { input ->
            // Allow numbers up to 3 digits or decimal
            if (input.isEmpty() || input.matches(Regex("""^\d{0,3}(\.\d{0,1})?$"""))) {
                onValueChange(input)
            }
        },
        placeholder = { Text(placeholder, fontSize = 11.sp, textAlign = TextAlign.Center) },
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = modifier.height(44.dp),
        textStyle = MaterialTheme.typography.bodySmall.copy(
            textAlign = TextAlign.Center,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        ),
        shape = RoundedCornerShape(6.dp),
        colors = OutlinedTextFieldDefaults.colors(
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f)
        )
    )
}
