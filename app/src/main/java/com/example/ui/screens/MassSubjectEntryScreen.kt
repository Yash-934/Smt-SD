package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.StudentRecord
import com.example.data.model.SubjectMarks
import com.example.ui.theme.AcademicTeal
import com.example.ui.theme.SchoolNavy

@Composable
fun MassSubjectEntryScreen(
    students: List<StudentRecord>,
    selectedSubject: String,
    onSelectSubject: (String) -> Unit,
    onUpdateMarks: (studentId: Long, tierIndex: Int, t1: String, t2: String, t3: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 12.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Subject Selector Chips
        Text(
            text = "Select Target Subject",
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = SchoolNavy
        )

        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            StudentRecord.ALL_SUBJECTS.forEach { subName ->
                val isSelected = subName == selectedSubject
                FilterChip(
                    selected = isSelected,
                    onClick = { onSelectSubject(subName) },
                    label = { Text(subName, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = SchoolNavy,
                        selectedLabelColor = Color.White
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("chip_subject_$subName")
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        Text(
            text = "Entering marks for: $selectedSubject (${students.size} students)",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(6.dp))

        if (students.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text("No student records available.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(bottom = 80.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(students, key = { it.id }) { student ->
                    val subMarks = student.marks[selectedSubject] ?: SubjectMarks(subjectName = selectedSubject)
                    MassSubjectStudentCard(
                        rollNo = student.rollNo,
                        studentName = student.name,
                        subMarks = subMarks,
                        onUpdateTier = { tierIdx, t1, t2, t3 ->
                            onUpdateMarks(student.id, tierIdx, t1, t2, t3)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun MassSubjectStudentCard(
    rollNo: Int,
    studentName: String,
    subMarks: SubjectMarks,
    onUpdateTier: (tierIndex: Int, t1: String, t2: String, t3: String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = SchoolNavy
                    ) {
                        Text(
                            text = "Roll $rollNo",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = studentName.ifEmpty { "Unnamed Student" },
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Text(
                    text = "Total: ${subMarks.grandTotal.toInt()}/200",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp,
                    color = AcademicTeal
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // R1
            TierRowSimple(
                label = "R1 (UT)",
                t1 = subMarks.r1UnitTest.t1,
                t2 = subMarks.r1UnitTest.t2,
                t3 = subMarks.r1UnitTest.t3,
                onChanged = { t1, t2, t3 -> onUpdateTier(0, t1, t2, t3) },
                testTagPrefix = "mass_${rollNo}_r1"
            )

            Spacer(modifier = Modifier.height(4.dp))

            // R2
            TierRowSimple(
                label = "R2 (HY)",
                t1 = subMarks.r2HalfYearly.t1,
                t2 = subMarks.r2HalfYearly.t2,
                t3 = subMarks.r2HalfYearly.t3,
                onChanged = { t1, t2, t3 -> onUpdateTier(1, t1, t2, t3) },
                testTagPrefix = "mass_${rollNo}_r2"
            )

            Spacer(modifier = Modifier.height(4.dp))

            // R3
            TierRowSimple(
                label = "R3 (Ann)",
                t1 = subMarks.r3Annual.t1,
                t2 = subMarks.r3Annual.t2,
                t3 = subMarks.r3Annual.t3,
                onChanged = { t1, t2, t3 -> onUpdateTier(2, t1, t2, t3) },
                testTagPrefix = "mass_${rollNo}_r3"
            )
        }
    }
}

@Composable
private fun TierRowSimple(
    label: String,
    t1: String,
    t2: String,
    t3: String,
    onChanged: (String, String, String) -> Unit,
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
            onValueChange = { onChanged(it, t2, t3) },
            placeholder = "T1",
            modifier = Modifier
                .weight(1f)
                .testTag("${testTagPrefix}_t1")
        )

        MiniScoreField(
            value = t2,
            onValueChange = { onChanged(t1, it, t3) },
            placeholder = "T2",
            modifier = Modifier
                .weight(1f)
                .testTag("${testTagPrefix}_t2")
        )

        MiniScoreField(
            value = t3,
            onValueChange = { onChanged(t1, t2, it) },
            placeholder = "T3",
            modifier = Modifier
                .weight(1f)
                .testTag("${testTagPrefix}_t3")
        )
    }
}
