package com.example.ui.screens

import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SchoolConfig
import com.example.data.model.StudentRecord
import com.example.data.model.SubjectMarks
import com.example.ui.components.FullScreenBroadsheetDialog
import com.example.ui.theme.AcademicTeal
import com.example.ui.theme.AccentGold
import com.example.ui.theme.DangerRed
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SuccessGreen
import com.example.util.PdfExporter

@Composable
fun MasterSheetScreen(
    students: List<StudentRecord>,
    schoolConfig: SchoolConfig,
    onSelectStudent: (index: Int) -> Unit,
    onOpenMarksEntry: (index: Int) -> Unit,
    onOpenFullScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val totalStudents = students.size
    val passedCount = students.count { it.overallPercentage >= 33.0 }
    val averagePerc = if (totalStudents > 0) students.map { it.overallPercentage }.average() else 0.0

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 8.dp)
    ) {
        Spacer(modifier = Modifier.height(8.dp))

        // Actions Header Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Examination Result Broadsheet",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = SchoolNavy
                        )
                        Text(
                            text = "Session ${schoolConfig.session} (${schoolConfig.schoolName})",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF64748B)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Action Buttons - Clean 2-Tier Row Layout (Zero Overflow, Consistent Sizing)
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    // Row 1: Primary Broad Actions
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onOpenFullScreen,
                            colors = ButtonDefaults.buttonColors(containerColor = AcademicTeal),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("btn_fullscreen_master_pdf")
                        ) {
                            Icon(Icons.Default.Fullscreen, contentDescription = null, modifier = Modifier.size(17.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Full Screen View", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        Button(
                            onClick = {
                                try {
                                    val file = PdfExporter.generateMasterBroadsheetPdf(
                                        context = context,
                                        students = students,
                                        schoolConfig = schoolConfig
                                    )
                                    val saved = PdfExporter.savePdfToPublicDownloads(
                                        context = context,
                                        file = file,
                                        customName = "Master_Broadsheet_Session_${schoolConfig.session.replace("/", "-")}.pdf"
                                    )
                                    if (!saved) {
                                        Toast.makeText(context, "Saved to cache: ${file.name}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "PDF Save Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(38.dp)
                                .testTag("btn_save_master_pdf")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(5.dp))
                            Text("Save Broadsheet PDF", fontSize = 12.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }
                    }

                    // Row 2: Secondary Utility Actions (Print, Share, CSV)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Button(
                            onClick = {
                                try {
                                    val file = PdfExporter.generateMasterBroadsheetPdf(
                                        context = context,
                                        students = students,
                                        schoolConfig = schoolConfig
                                    )
                                    PdfExporter.printPdf(
                                        context = context,
                                        file = file,
                                        jobTitle = "Master Broadsheet - Session ${schoolConfig.session}"
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Print Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_print_master_pdf")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, modifier = Modifier.size(15.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val file = PdfExporter.generateMasterBroadsheetPdf(
                                        context = context,
                                        students = students,
                                        schoolConfig = schoolConfig
                                    )
                                    PdfExporter.openOrSharePdf(
                                        context = context,
                                        file = file,
                                        title = "Master Broadsheet - Session ${schoolConfig.session}"
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "PDF Share Error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_share_master_pdf")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(15.dp), tint = SchoolNavy)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Share", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SchoolNavy, maxLines = 1)
                        }

                        OutlinedButton(
                            onClick = { exportCsv(context, students, schoolConfig) },
                            contentPadding = PaddingValues(horizontal = 6.dp, vertical = 0.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(36.dp)
                                .testTag("btn_export_csv")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = null, modifier = Modifier.size(15.dp), tint = SchoolNavy)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("CSV", fontSize = 11.5.sp, fontWeight = FontWeight.Bold, color = SchoolNavy, maxLines = 1)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Stats Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MiniStatChip(title = "Total Students", value = "$totalStudents", color = SchoolNavy, modifier = Modifier.weight(1f))
                    MiniStatChip(title = "Passed (>=33%)", value = "$passedCount", color = SuccessGreen, modifier = Modifier.weight(1f))
                    MiniStatChip(title = "Class Average", value = String.format("%.1f%%", averagePerc), color = AccentGold, modifier = Modifier.weight(1f))
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Broadsheet Table Matching Image 2, 3, 4
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            shape = RoundedCornerShape(6.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(8.dp)
                ) {
                    // Header Row
                    item {
                        BroadsheetHeader()
                    }

                    // Student 4-Row Records
                    itemsIndexed(students) { index, student ->
                        BroadsheetStudentRow(
                            student = student,
                            isOdd = index % 2 == 1,
                            onClick = {
                                onSelectStudent(index)
                                onOpenMarksEntry(index)
                            }
                        )
                    }
                }
            }
        }
        Spacer(modifier = Modifier.height(56.dp))
    }
}

@Composable
private fun BroadsheetHeader() {
    val thBg = Color(0xFFCBD5E1)
    val gridBorder = Color.Black

    Column(
        modifier = Modifier
            .background(thBg)
            .border(0.8.dp, gridBorder)
    ) {
        // Tier 1
        Row(
            modifier = Modifier.height(22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BHeaderCell(text = "Roll No.", width = 38.dp)
            BHeaderCell(text = "NAME OF THE STUDENT", width = 120.dp)
            BHeaderCell(text = "EXAM TERM", width = 64.dp)
            BHeaderCell(text = "STUDENT SR. NO.", width = 80.dp)

            // 10 Subjects
            StudentRecord.ALL_SUBJECTS.forEach { subName ->
                BHeaderCell(text = subName, width = 120.dp)
            }

            BHeaderCell(text = "GRAND TOTAL", width = 76.dp)
            BHeaderCell(text = "RESULT", width = 64.dp)
        }

        // Tier 2 (Subject Sub-columns: I, II, III, TOTAL)
        Row(
            modifier = Modifier
                .background(Color(0xFFE2E8F0))
                .height(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            BHeaderCell(text = "", width = 38.dp)
            BHeaderCell(text = "", width = 120.dp)
            BHeaderCell(text = "", width = 64.dp)
            BHeaderCell(text = "", width = 80.dp)

            for (i in 0 until 10) {
                BHeaderCell(text = "I", width = 28.dp)
                BHeaderCell(text = "II", width = 28.dp)
                BHeaderCell(text = "III", width = 28.dp)
                BHeaderCell(text = "TOTAL", width = 36.dp)
            }

            BHeaderCell(text = "", width = 76.dp)
            BHeaderCell(text = "", width = 64.dp)
        }
    }
}

@Composable
private fun BroadsheetStudentRow(
    student: StudentRecord,
    isOdd: Boolean,
    onClick: () -> Unit
) {
    val rowBg = if (isOdd) Color(0xFFF8FAFC) else Color.White
    val totalRowBg = Color(0xFFF1F5F9)

    Column(
        modifier = Modifier
            .background(rowBg)
            .border(0.6.dp, Color.Black)
            .clickable(onClick = onClick)
    ) {
        // We have 4 sub-rows: R1, R2, R3, and Total
        for (subRow in 0..3) {
            val isTotal = subRow == 3
            val currentBg = if (isTotal) totalRowBg else rowBg

            Row(
                modifier = Modifier
                    .background(currentBg)
                    .height(20.dp)
                    .border(0.3.dp, Color(0xFFCBD5E1)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Fixed Metadata columns (Show on Row 1 or 2)
                if (subRow == 0) {
                    BDataCell(text = "${student.rollNo}", width = 38.dp, bold = true)
                    BDataCell(text = student.name.ifEmpty { "Unnamed" }, width = 120.dp, alignLeft = true, bold = true)
                    BDataCell(text = student.exam.ifEmpty { "-" }, width = 64.dp)
                    BDataCell(text = student.sr.ifEmpty { "-" }, width = 80.dp)
                } else {
                    BDataCell(text = "", width = 38.dp)
                    BDataCell(text = "", width = 120.dp)
                    BDataCell(text = "", width = 64.dp)
                    BDataCell(text = "", width = 80.dp)
                }

                // 10 Subject Data Columns
                var rowTotalSum = 0.0
                StudentRecord.ALL_SUBJECTS.forEach { subName ->
                    val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
                    if (!isTotal) {
                        val eval = when (subRow) {
                            0 -> subMarks.r1UnitTest
                            1 -> subMarks.r2HalfYearly
                            else -> subMarks.r3Annual
                        }
                        BDataCell(text = eval.t1.ifEmpty { "-" }, width = 28.dp)
                        BDataCell(text = eval.t2.ifEmpty { "-" }, width = 28.dp)
                        BDataCell(text = eval.t3.ifEmpty { "-" }, width = 28.dp)
                        BDataCell(text = "${eval.total.toInt()}", width = 36.dp, bold = true)
                        rowTotalSum += eval.total
                    } else {
                        // Grand Total of Subject aligned under TOTAL column (columns 1, 2, 3 empty)
                        BDataCell(text = "", width = 28.dp)
                        BDataCell(text = "", width = 28.dp)
                        BDataCell(text = "", width = 28.dp)
                        BDataCell(text = "${subMarks.grandTotal.toInt()}", width = 36.dp, bold = true, textColor = SchoolNavy)
                    }
                }

                // Grand Total & Result
                if (!isTotal) {
                    BDataCell(text = "${rowTotalSum.toInt()}", width = 76.dp)
                    BDataCell(text = "", width = 64.dp)
                } else {
                    BDataCell(text = "${student.grandTotal.toInt()}", width = 76.dp, bold = true, textColor = SchoolNavy)
                    BDataCell(text = String.format("%.2f%%", student.overallPercentage), width = 64.dp, bold = true, textColor = AcademicTeal)
                }
            }
        }
    }
}

@Composable
private fun BHeaderCell(
    text: String,
    width: androidx.compose.ui.unit.Dp
) {
    Box(
        modifier = Modifier
            .width(width)
            .border(0.4.dp, Color.Black)
            .padding(horizontal = 2.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 8.5.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Black,
            textAlign = TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun BDataCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    alignLeft: Boolean = false,
    bold: Boolean = false,
    textColor: Color = Color.Black
) {
    Box(
        modifier = Modifier
            .width(width)
            .border(0.3.dp, Color.Black)
            .padding(horizontal = 2.dp),
        contentAlignment = if (alignLeft) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 8.5.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = textColor,
            textAlign = if (alignLeft) TextAlign.Start else TextAlign.Center,
            maxLines = 1
        )
    }
}

@Composable
private fun MiniStatChip(title: String, value: String, color: Color, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = color.copy(alpha = 0.08f),
        border = androidx.compose.foundation.BorderStroke(0.8.dp, color.copy(alpha = 0.2f))
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, fontSize = 9.sp, color = Color(0xFF64748B))
            Text(text = value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color)
        }
    }
}

private fun exportCsv(context: Context, students: List<StudentRecord>, schoolConfig: SchoolConfig) {
    val sb = StringBuilder()
    sb.append("School,${schoolConfig.schoolName}\n")
    sb.append("Session,${schoolConfig.session}\n")
    sb.append("Class,${schoolConfig.classSec}\n\n")

    sb.append("Roll No,NAME OF THE STUDENT,EXAM TERM,STUDENT SR. NO.,")
    StudentRecord.ALL_SUBJECTS.forEach {
        sb.append("$it I,$it II,$it III,$it TOTAL,")
    }
    sb.append("GRAND TOTAL,RESULT\n")

    for (s in students) {
        sb.append("${s.rollNo},\"${s.name}\",\"${s.exam}\",\"${s.sr}\",")
        StudentRecord.ALL_SUBJECTS.forEach { subName ->
            val sub = s.marks[subName] ?: SubjectMarks(subjectName = subName)
            sb.append("${sub.r1UnitTest.total.toInt()},${sub.r2HalfYearly.total.toInt()},${sub.r3Annual.total.toInt()},${sub.grandTotal.toInt()},")
        }
        sb.append("${s.grandTotal.toInt()},${String.format("%.2f", s.overallPercentage)}%\n")
    }

    val sendIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, sb.toString())
        type = "text/csv"
    }
    val shareIntent = Intent.createChooser(sendIntent, "Export Examination Broadsheet CSV")
    context.startActivity(shareIntent)
}
