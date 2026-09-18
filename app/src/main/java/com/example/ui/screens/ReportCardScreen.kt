package com.example.ui.screens

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
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
import com.example.ui.components.FullScreenMarksheetDialog
import com.example.ui.theme.AcademicTeal
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SuccessGreen
import com.example.util.PdfExporter

@Composable
fun ReportCardScreen(
    student: StudentRecord?,
    schoolConfig: SchoolConfig,
    totalStudents: Int,
    currentIndex: Int,
    customRemarks: String?,
    onPrevStudent: () -> Unit,
    onNextStudent: () -> Unit,
    onCustomRemarksChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showFullScreen by remember { mutableStateOf(false) }
    var showEditRemarksDialog by remember { mutableStateOf(false) }
    var tempRemarks by remember { mutableStateOf("") }

    if (student == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("No student record found.")
        }
        return
    }

    val displayRemarks = customRemarks ?: student.computeSmartRemarks()

    if (showFullScreen) {
        FullScreenMarksheetDialog(
            student = student,
            schoolConfig = schoolConfig,
            displayRemarks = displayRemarks,
            onDismiss = { showFullScreen = false }
        )
    }

    if (showEditRemarksDialog) {
        AlertDialog(
            onDismissRequest = { showEditRemarksDialog = false },
            title = {
                Text(
                    text = "Edit Student Remarks",
                    style = MaterialTheme.typography.titleMedium,
                    color = SchoolNavy
                )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "Customize the teacher's remarks appearing on this student's marksheet:",
                        style = MaterialTheme.typography.bodySmall
                    )
                    OutlinedTextField(
                        value = tempRemarks,
                        onValueChange = { tempRemarks = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("e.g. Excellent academic consistency and leadership.") },
                        minLines = 2
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onCustomRemarksChange(tempRemarks)
                        showEditRemarksDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy)
                ) {
                    Text("Save Remarks")
                }
            },
            dismissButton = {
                OutlinedButton(onClick = { showEditRemarksDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF1F5F9))
            .padding(horizontal = 8.dp),
        contentPadding = PaddingValues(top = 10.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Top Toolbar with Navigation & PDF Export / Print / FullScreen
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Pager Controls Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = onPrevStudent,
                            enabled = currentIndex > 0,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Previous", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Prev", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = SchoolNavy
                        ) {
                            Text(
                                text = "Roll ${currentIndex + 1} of $totalStudents",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                            )
                        }

                        OutlinedButton(
                            onClick = onNextStudent,
                            enabled = currentIndex < totalStudents - 1,
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Next", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = "Next", modifier = Modifier.size(16.dp))
                        }
                    }

                    // Action Buttons Row (Full Screen, Save PDF, Print, Share)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // 1. Full Screen Landscape View Button
                        Button(
                            onClick = { showFullScreen = true },
                            colors = ButtonDefaults.buttonColors(containerColor = AcademicTeal),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1.1f)
                                .testTag("btn_fullscreen_marksheet")
                        ) {
                            Icon(Icons.Default.Fullscreen, contentDescription = "Full Screen", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Full Screen", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 2. Direct Save to Gallery / Downloads
                        Button(
                            onClick = {
                                try {
                                    val pdfFile = PdfExporter.generateSingleMarksheetPdf(
                                        context = context,
                                        student = student,
                                        schoolConfig = schoolConfig,
                                        remarks = displayRemarks
                                    )
                                    val saved = PdfExporter.savePdfToPublicDownloads(
                                        context = context,
                                        file = pdfFile,
                                        customName = "Marksheet_Roll_${student.rollNo}_${student.name.replace(" ", "_")}.pdf"
                                    )
                                    if (!saved) {
                                        Toast.makeText(context, "Export saved to: ${pdfFile.name}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Save error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(1f)
                                .testTag("btn_save_pdf_marksheet")
                        ) {
                            Icon(Icons.Default.Download, contentDescription = "Save PDF", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Save PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 3. Print Button
                        Button(
                            onClick = {
                                try {
                                    val pdfFile = PdfExporter.generateSingleMarksheetPdf(
                                        context = context,
                                        student = student,
                                        schoolConfig = schoolConfig,
                                        remarks = displayRemarks
                                    )
                                    PdfExporter.printPdf(
                                        context = context,
                                        file = pdfFile,
                                        jobTitle = "Marksheet - Roll ${student.rollNo} ${student.name}"
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Print error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SchoolNavy),
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("btn_print_marksheet")
                        ) {
                            Icon(Icons.Default.Print, contentDescription = "Print", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("Print", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // 4. Share Button
                        OutlinedButton(
                            onClick = {
                                try {
                                    val pdfFile = PdfExporter.generateSingleMarksheetPdf(
                                        context = context,
                                        student = student,
                                        schoolConfig = schoolConfig,
                                        remarks = displayRemarks
                                    )
                                    PdfExporter.openOrSharePdf(
                                        context = context,
                                        file = pdfFile,
                                        title = "Marksheet - ${student.name} (Roll ${student.rollNo})"
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Share error: ${e.message}", Toast.LENGTH_LONG).show()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .weight(0.9f)
                                .testTag("btn_share_marksheet")
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share", modifier = Modifier.size(16.dp), tint = SchoolNavy)
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("Share", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = SchoolNavy)
                        }
                    }
                }
            }
        }

        // Exact Match Marksheet Container (Identical to WebApp layout)
        item {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("report_card_container"),
                shape = RoundedCornerShape(4.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(2.dp, Color(0xFF1E3A8A), RoundedCornerShape(4.dp))
                        .padding(14.dp)
                ) {
                    // School Header
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = schoolConfig.schoolName.uppercase(),
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp,
                            color = Color(0xFF1E3A8A),
                            textAlign = TextAlign.Center,
                            letterSpacing = 0.5.sp
                        )
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Affiliated to Board • Academic Session: ${schoolConfig.session}",
                            fontSize = 10.5.sp,
                            color = Color(0xFF475569),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ANNUAL PROGRESS REPORT",
                            color = Color(0xFF1E293B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            letterSpacing = 1.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Student Meta Box
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(0.8.dp, Color.Black, RoundedCornerShape(2.dp))
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetaField(label = "Student Name:", value = student.name.ifEmpty { "N/A" }, modifier = Modifier.weight(1.3f))
                            MetaField(label = "Roll No:", value = "${student.rollNo}", modifier = Modifier.weight(0.7f))
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            MetaField(label = "Class & Section:", value = schoolConfig.classSec, modifier = Modifier.weight(1.3f))
                            MetaField(label = "Scholar / SR No:", value = student.sr.ifEmpty { "N/A" }, modifier = Modifier.weight(0.7f))
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Marks Table with Exact 2-Tier Header (Horizontal Scrollable for Clean Phone Display)
                    Box(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                        Column(
                            modifier = Modifier.border(0.8.dp, Color.Black)
                        ) {
                            // Header Tier 1
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFFF1F5F9))
                                    .border(0.8.dp, Color.Black),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(28.dp)
                                        .height(44.dp)
                                        .border(0.4.dp, Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("SN", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                Box(
                                    modifier = Modifier
                                        .width(116.dp)
                                        .height(44.dp)
                                        .border(0.4.dp, Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Subject", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }

                                // Unit Test (1, 2)
                                HeaderGroup(
                                    title = "Unit Test",
                                    subHeaders = listOf("1" to 38.dp, "2" to 38.dp)
                                )

                                // Total (M.M., Ob.M.)
                                HeaderGroup(
                                    title = "Total",
                                    subHeaders = listOf("M.M." to 42.dp, "Ob.M." to 44.dp)
                                )

                                // Half Yearly (M.M., Ob.M.)
                                HeaderGroup(
                                    title = "Half Yearly",
                                    subHeaders = listOf("M.M." to 44.dp, "Ob.M." to 46.dp)
                                )

                                // Annual (M.M., Ob.M.)
                                HeaderGroup(
                                    title = "Annual",
                                    subHeaders = listOf("M.M." to 44.dp, "Ob.M." to 46.dp)
                                )

                                // Grand Total (M.M, Ob.M.)
                                HeaderGroup(
                                    title = "Grand Total",
                                    subHeaders = listOf("M.M" to 46.dp, "Ob.M." to 48.dp)
                                )

                                Box(
                                    modifier = Modifier
                                        .width(68.dp)
                                        .height(44.dp)
                                        .border(0.4.dp, Color.Black),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("Percentage", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }

                            // 10 Subject Rows
                            StudentRecord.ALL_SUBJECTS.forEachIndexed { idx, subName ->
                                val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
                                Row(
                                    modifier = Modifier
                                        .background(Color.White)
                                        .border(0.4.dp, Color.Black)
                                        .height(26.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    GridCell(text = "${idx + 1}", width = 28.dp)
                                    GridCell(text = subName, width = 116.dp, alignLeft = true, bold = true)
                                    GridCell(text = subMarks.r1UnitTest.t1.ifEmpty { "-" }, width = 38.dp)
                                    GridCell(text = subMarks.r1UnitTest.t2.ifEmpty { "-" }, width = 38.dp)
                                    GridCell(text = "100", width = 42.dp)
                                    GridCell(text = "${subMarks.utObtained.toInt()}", width = 44.dp, bold = true)
                                    GridCell(text = "100", width = 44.dp)
                                    GridCell(text = "${subMarks.halfYearlyObtained.toInt()}", width = 46.dp)
                                    GridCell(text = "100", width = 44.dp)
                                    GridCell(text = "${subMarks.annualObtained.toInt()}", width = 46.dp)
                                    GridCell(text = "200", width = 46.dp)
                                    GridCell(text = "${subMarks.grandTotal.toInt()}", width = 48.dp, bold = true)
                                    GridCell(text = String.format("%.1f%%", subMarks.percentage), width = 68.dp, bold = true)
                                }
                            }

                            // Total Row
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFFF8FAFC))
                                    .border(0.8.dp, Color.Black)
                                    .height(28.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .width(144.dp)
                                        .height(28.dp)
                                        .border(0.4.dp, Color.Black)
                                        .padding(end = 8.dp),
                                    contentAlignment = Alignment.CenterEnd
                                ) {
                                    Text("Total", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                                }

                                GridCell(text = "${student.totalUt1.toInt()}", width = 38.dp, bold = true)
                                GridCell(text = "${student.totalUt2.toInt()}", width = 38.dp, bold = true)
                                GridCell(text = "1000", width = 42.dp, bold = true)
                                GridCell(text = "${student.totalUtObtained.toInt()}", width = 44.dp, bold = true)
                                GridCell(text = "1000", width = 44.dp, bold = true)
                                GridCell(text = "${student.totalHalfYearlyObtained.toInt()}", width = 46.dp, bold = true)
                                GridCell(text = "1000", width = 44.dp, bold = true)
                                GridCell(text = "${student.totalAnnualObtained.toInt()}", width = 46.dp, bold = true)
                                GridCell(text = "2000", width = 46.dp, bold = true)
                                GridCell(text = "${student.grandTotal.toInt()}", width = 48.dp, bold = true)
                                GridCell(text = String.format("%.2f%%", student.overallPercentage), width = 68.dp, bold = true)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Single Clean Remarks Box (Exact Match to WebApp - No Duplicate Remarks)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color(0xFFF8FAFC), RoundedCornerShape(3.dp))
                            .border(0.8.dp, Color(0xFFCBD5E1), RoundedCornerShape(3.dp))
                            .padding(horizontal = 8.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "📌 REMARKS: ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 9.5.sp,
                            color = Color(0xFF1E3A8A)
                        )
                        Text(
                            text = displayRemarks,
                            fontSize = 9.5.sp,
                            color = Color(0xFF0F172A),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = {
                                tempRemarks = displayRemarks
                                showEditRemarksDialog = true
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = "Edit Remarks",
                                modifier = Modifier.size(14.dp),
                                tint = SchoolNavy
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Official Signatures
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        SignatureField(title = "Class Teacher")
                        SignatureField(title = "Exam In-Charge")
                        SignatureField(title = "Principal")
                    }
                }
            }
        }
    }
}

@Composable
private fun HeaderGroup(
    title: String,
    subHeaders: List<Pair<String, androidx.compose.ui.unit.Dp>>
) {
    val totalW = subHeaders.fold(0.dp) { acc, p -> acc + p.second }
    Column(
        modifier = Modifier
            .width(totalW)
            .border(0.4.dp, Color.Black)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(22.dp)
                .border(0.4.dp, Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Text(title, fontSize = 9.sp, fontWeight = FontWeight.Bold)
        }
        Row(modifier = Modifier.fillMaxWidth()) {
            subHeaders.forEach { (subText, w) ->
                Box(
                    modifier = Modifier
                        .width(w)
                        .height(22.dp)
                        .border(0.4.dp, Color.Black),
                    contentAlignment = Alignment.Center
                ) {
                    Text(subText, fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun GridCell(
    text: String,
    width: androidx.compose.ui.unit.Dp,
    alignLeft: Boolean = false,
    bold: Boolean = false
) {
    Box(
        modifier = Modifier
            .width(width)
            .height(26.dp)
            .border(0.4.dp, Color.Black)
            .padding(horizontal = 4.dp),
        contentAlignment = if (alignLeft) Alignment.CenterStart else Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 9.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black,
            maxLines = 1
        )
    }
}

@Composable
private fun MetaField(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(text = label, fontWeight = FontWeight.Bold, fontSize = 10.5.sp, color = Color.Black)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = value, fontSize = 10.5.sp, color = Color.Black)
    }
}

@Composable
private fun SignatureField(title: String) {
    Column(
        modifier = Modifier.width(90.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color.Black)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = title, fontSize = 9.5.sp, fontWeight = FontWeight.Bold, color = Color.Black)
    }
}
