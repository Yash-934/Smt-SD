package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolConfig
import com.example.data.model.StudentRecord
import com.example.ui.theme.AcademicTeal
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SuccessGreen

@Composable
fun StudentListScreen(
    students: List<StudentRecord>,
    schoolConfig: SchoolConfig,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onSelectStudent: (index: Int) -> Unit,
    onOpenMarksEntry: (index: Int) -> Unit,
    onOpenReportCard: (index: Int) -> Unit,
    onAddNewStudentClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val filteredStudents by remember(students, searchQuery) {
        derivedStateOf {
            if (searchQuery.isBlank()) {
                students
            } else {
                val q = searchQuery.trim().lowercase()
                students.filter {
                    it.name.lowercase().contains(q) ||
                    it.rollNo.toString().contains(q) ||
                    it.sr.lowercase().contains(q)
                }
            }
        }
    }

    val classStats by remember(students) {
        derivedStateOf {
            val total = students.size
            val avg = if (total > 0) students.map { it.overallPercentage }.average() else 0.0
            val top = students.maxByOrNull { it.grandTotal }
            Triple(total, avg, top)
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),
            contentPadding = PaddingValues(top = 10.dp, bottom = 80.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Stats Overview Card (School Navy)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = SchoolNavy),
                    shape = RoundedCornerShape(12.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = schoolConfig.schoolName,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = "Class: ${schoolConfig.classSec} • Session: ${schoolConfig.session}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color.White.copy(alpha = 0.85f)
                                )
                            }
                            Surface(
                                shape = RoundedCornerShape(16.dp),
                                color = Color.White.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = "${classStats.first} Students",
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatMiniBox(
                                label = "Class Avg",
                                value = String.format("%.1f%%", classStats.second),
                                color = Color(0xFFFDE047)
                            )
                            StatMiniBox(
                                label = "Top Score",
                                value = classStats.third?.let { "${it.grandTotal.toInt()}/2000" } ?: "0/2000",
                                color = Color(0xFF86EFAC)
                            )
                            StatMiniBox(
                                label = "Top Ranker",
                                value = classStats.third?.name?.take(12) ?: "N/A",
                                color = Color.White
                            )
                        }
                    }
                }
            }

            // Search Bar
            item {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("search_student_input"),
                    placeholder = { Text("Search by name, roll no, or SR...") },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = SchoolNavy)
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { onSearchQueryChange("") }) {
                                Icon(Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor = SchoolNavy,
                        unfocusedBorderColor = Color(0xFFCBD5E1)
                    )
                )
            }

            // Student Roster Items
            if (filteredStudents.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                modifier = Modifier.size(48.dp),
                                tint = Color(0xFF94A3B8)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No students found",
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Try clearing your search or add a new student.",
                                style = MaterialTheme.typography.bodySmall,
                                color = Color(0xFF64748B)
                            )
                        }
                    }
                }
            } else {
                itemsIndexed(filteredStudents) { _, student ->
                    val originalIndex = students.indexOfFirst { it.id == student.id }.let {
                        if (it == -1) student.rollNo - 1 else it
                    }
                    StudentCardItem(
                        student = student,
                        onCardClick = {
                            onSelectStudent(originalIndex)
                            onOpenMarksEntry(originalIndex)
                        },
                        onEditMarksClick = {
                            onSelectStudent(originalIndex)
                            onOpenMarksEntry(originalIndex)
                        },
                        onReportCardClick = {
                            onSelectStudent(originalIndex)
                            onOpenReportCard(originalIndex)
                        }
                    )
                }
            }
        }

        // Floating Action Button
        FloatingActionButton(
            onClick = onAddNewStudentClick,
            containerColor = SchoolNavy,
            contentColor = Color.White,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
                .testTag("fab_add_student")
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add Student")
        }
    }
}

@Composable
private fun StatMiniBox(label: String, value: String, color: Color) {
    Column(
        modifier = Modifier
            .background(Color.White.copy(alpha = 0.12f), RoundedCornerShape(8.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = Color.White.copy(alpha = 0.75f)
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.Bold,
            color = color,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
fun StudentCardItem(
    student: StudentRecord,
    onCardClick: () -> Unit,
    onEditMarksClick: () -> Unit,
    onReportCardClick: () -> Unit
) {
    val perc = student.overallPercentage
    val badgeColor = when {
        perc >= 75.0 -> SuccessGreen
        perc >= 50.0 -> AcademicTeal
        perc >= 33.0 -> AccentGold
        else -> if (student.grandTotal > 0) DangerRed else Color.Gray
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("student_card_${student.rollNo}"),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.5.dp)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(SchoolNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${student.rollNo}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = student.name.ifEmpty { "Unnamed Student" },
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF0F172A),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = "SR: ${student.sr.ifEmpty { "N/A" }}${if (student.exam.isNotBlank()) " • ${student.exam}" else ""}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Surface(
                        shape = RoundedCornerShape(6.dp),
                        color = badgeColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = String.format("%.1f%%", perc),
                            color = badgeColor,
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "Total: ${student.grandTotal.toInt()} / 2000",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFF64748B)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(
                    onClick = onEditMarksClick,
                    modifier = Modifier.testTag("btn_enter_marks_${student.rollNo}")
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = SchoolNavy
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Marks Matrix", color = SchoolNavy, style = MaterialTheme.typography.labelMedium)
                }

                Spacer(modifier = Modifier.width(6.dp))

                TextButton(
                    onClick = onReportCardClick,
                    modifier = Modifier.testTag("btn_report_card_${student.rollNo}")
                ) {
                    Icon(
                        Icons.Default.Assessment,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AcademicTeal
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Marksheet", color = AcademicTeal, style = MaterialTheme.typography.labelMedium)
                }
            }
        }
    }
}
