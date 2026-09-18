package com.example.ui.components

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FullscreenExit
import androidx.compose.material.icons.filled.Print
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.model.SchoolConfig
import com.example.data.model.StudentRecord
import com.example.data.model.SubjectMarks
import com.example.ui.theme.SchoolNavy
import com.example.ui.theme.SuccessGreen
import com.example.util.PdfExporter

/**
 * Fullscreen Landscape Dialog for Single Student Marksheet.
 * Provides interactive zoom, pan, full-screen canvas, and quick export actions.
 */
@Composable
fun FullScreenMarksheetDialog(
    student: StudentRecord,
    schoolConfig: SchoolConfig,
    displayRemarks: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.7f, 3.0f)
        offset += offsetChange
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Toolbar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SchoolNavy)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Fullscreen",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Full Screen Marksheet",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${student.name} • Roll ${student.rollNo} • Class ${schoolConfig.classSec}",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Zoom Out
                        IconButton(
                            onClick = { scale = (scale - 0.2f).coerceAtLeast(0.7f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        // Zoom In
                        IconButton(
                            onClick = { scale = (scale + 0.2f).coerceAtMost(3.0f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        // Save PDF
                        Button(
                            onClick = {
                                try {
                                    val pdfFile = PdfExporter.generateSingleMarksheetPdf(
                                        context = context,
                                        student = student,
                                        schoolConfig = schoolConfig,
                                        remarks = displayRemarks
                                    )
                                    PdfExporter.savePdfToPublicDownloads(
                                        context = context,
                                        file = pdfFile,
                                        customName = "Marksheet_Roll_${student.rollNo}_${student.name.replace(" ", "_")}.pdf"
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        // Print
                        OutlinedButton(
                            onClick = {
                                try {
                                    val pdfFile = PdfExporter.generateSingleMarksheetPdf(
                                        context = context,
                                        student = student,
                                        schoolConfig = schoolConfig,
                                        remarks = displayRemarks
                                    )
                                    PdfExporter.printPdf(context, pdfFile, "Marksheet - ${student.name}")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Print Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                // Interactive Content Area with 2D Scroll + Zoom/Pan
                val vScrollState = rememberScrollState()
                val hScrollState = rememberScrollState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E293B))
                        .verticalScroll(vScrollState)
                        .horizontalScroll(hScrollState)
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .transformable(transformState)
                    ) {
                        MarksheetLandscapeCard(
                            student = student,
                            schoolConfig = schoolConfig,
                            displayRemarks = displayRemarks
                        )
                    }
                }
            }
        }
    }
}

/**
 * Fullscreen Landscape Dialog for Master Broadsheet (All Students).
 */
@Composable
fun FullScreenBroadsheetDialog(
    students: List<StudentRecord>,
    schoolConfig: SchoolConfig,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    var scale by remember { mutableFloatStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }

    val transformState = rememberTransformableState { zoomChange, offsetChange, _ ->
        scale = (scale * zoomChange).coerceIn(0.6f, 3.0f)
        offset += offsetChange
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false,
            decorFitsSystemWindows = false
        )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0F172A)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Action Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(SchoolNavy)
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = "Close Fullscreen",
                                tint = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Column {
                            Text(
                                text = "Full Screen Master Broadsheet",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${students.size} Students • Session ${schoolConfig.session} (${schoolConfig.schoolName})",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp
                            )
                        }
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(
                            onClick = { scale = (scale - 0.2f).coerceAtLeast(0.6f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ZoomOut, contentDescription = "Zoom Out", tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                        IconButton(
                            onClick = { scale = (scale + 0.2f).coerceAtMost(3.0f) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(Icons.Default.ZoomIn, contentDescription = "Zoom In", tint = Color.White, modifier = Modifier.size(18.dp))
                        }

                        Button(
                            onClick = {
                                try {
                                    val pdfFile = PdfExporter.generateMasterBroadsheetPdf(
                                        context = context,
                                        students = students,
                                        schoolConfig = schoolConfig
                                    )
                                    PdfExporter.savePdfToPublicDownloads(
                                        context = context,
                                        file = pdfFile,
                                        customName = "Master_Broadsheet_Session_${schoolConfig.session.replace("/", "-")}.pdf"
                                    )
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("PDF", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        OutlinedButton(
                            onClick = {
                                try {
                                    val pdfFile = PdfExporter.generateMasterBroadsheetPdf(
                                        context = context,
                                        students = students,
                                        schoolConfig = schoolConfig
                                    )
                                    PdfExporter.printPdf(context, pdfFile, "Master Broadsheet")
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Print Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            },
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Icon(Icons.Default.Print, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Print", fontSize = 11.sp, color = Color.White)
                        }
                    }
                }

                // Interactive 2D Scroll View
                val vScrollState = rememberScrollState()
                val hScrollState = rememberScrollState()

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF1E293B))
                        .verticalScroll(vScrollState)
                        .horizontalScroll(hScrollState)
                        .padding(16.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Box(
                        modifier = Modifier
                            .graphicsLayer(
                                scaleX = scale,
                                scaleY = scale,
                                translationX = offset.x,
                                translationY = offset.y
                            )
                            .transformable(transformState)
                    ) {
                        MasterBroadsheetLandscapeCard(
                            students = students,
                            schoolConfig = schoolConfig
                        )
                    }
                }
            }
        }
    }
}

/**
 * Pixel-Perfect Landscape Marksheet Card matching WebApp PDF layout
 */
@Composable
fun MarksheetLandscapeCard(
    student: StudentRecord,
    schoolConfig: SchoolConfig,
    displayRemarks: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(820.dp),
        shape = RoundedCornerShape(6.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(2.dp, Color(0xFF1E3A8A), RoundedCornerShape(6.dp))
                .padding(16.dp)
        ) {
            // Header Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = schoolConfig.schoolName.uppercase(),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    color = Color(0xFF1E3A8A),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Affiliated to Board • Academic Session: ${schoolConfig.session}",
                    fontSize = 10.5.sp,
                    color = Color(0xFF475569),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(3.dp))
                Text(
                    text = "ANNUAL PROGRESS REPORT",
                    color = Color(0xFF1E293B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    letterSpacing = 0.5.sp
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
                    Row(modifier = Modifier.weight(1.3f)) {
                        Text("Student Name: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                        Text(student.name.ifEmpty { "N/A" }, fontSize = 11.sp, color = Color.Black)
                    }
                    Row(modifier = Modifier.weight(0.7f)) {
                        Text("Roll No: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                        Text("${student.rollNo}", fontSize = 11.sp, color = Color.Black)
                    }
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(modifier = Modifier.weight(1.3f)) {
                        Text("Class & Section: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                        Text(schoolConfig.classSec, fontSize = 11.sp, color = Color.Black)
                    }
                    Row(modifier = Modifier.weight(0.7f)) {
                        Text("Scholar / SR No: ", fontWeight = FontWeight.Bold, fontSize = 11.sp, color = Color.Black)
                        Text(student.sr.ifEmpty { "N/A" }, fontSize = 11.sp, color = Color.Black)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Full Evaluation Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, Color.Black)
            ) {
                // Header Tier 1 & 2
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF1F5F9))
                        .border(0.8.dp, Color.Black),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(30.dp).height(44.dp).border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("SN", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(134.dp).height(44.dp).border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("Subject", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                    HeaderGroup(title = "Unit Test", subHeaders = listOf("1" to 42.dp, "2" to 42.dp))
                    HeaderGroup(title = "Total", subHeaders = listOf("M.M." to 48.dp, "Ob.M." to 52.dp))
                    HeaderGroup(title = "Half Yearly", subHeaders = listOf("M.M." to 50.dp, "Ob.M." to 54.dp))
                    HeaderGroup(title = "Annual", subHeaders = listOf("M.M." to 50.dp, "Ob.M." to 54.dp))
                    HeaderGroup(title = "Grand Total", subHeaders = listOf("M.M" to 58.dp, "Ob.M." to 64.dp))
                    Box(modifier = Modifier.width(66.dp).height(44.dp).border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("Percentage", fontSize = 9.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // 10 Subject Rows
                StudentRecord.ALL_SUBJECTS.forEachIndexed { idx, subName ->
                    val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.White)
                            .border(0.4.dp, Color.Black)
                            .height(24.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        GridCell(text = "${idx + 1}", width = 30.dp)
                        GridCell(text = subName, width = 134.dp, alignLeft = true, bold = true)
                        GridCell(text = subMarks.r1UnitTest.t1.ifEmpty { "-" }, width = 42.dp)
                        GridCell(text = subMarks.r1UnitTest.t2.ifEmpty { "-" }, width = 42.dp)
                        GridCell(text = "100", width = 48.dp)
                        GridCell(text = "${subMarks.utObtained.toInt()}", width = 52.dp, bold = true)
                        GridCell(text = "100", width = 50.dp)
                        GridCell(text = "${subMarks.halfYearlyObtained.toInt()}", width = 54.dp)
                        GridCell(text = "100", width = 50.dp)
                        GridCell(text = "${subMarks.annualObtained.toInt()}", width = 54.dp)
                        GridCell(text = "200", width = 58.dp)
                        GridCell(text = "${subMarks.grandTotal.toInt()}", width = 64.dp, bold = true)
                        GridCell(text = String.format("%.1f%%", subMarks.percentage), width = 66.dp, bold = true)
                    }
                }

                // Total Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF8FAFC))
                        .border(0.8.dp, Color.Black)
                        .height(26.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .width(164.dp)
                            .height(26.dp)
                            .border(0.4.dp, Color.Black)
                            .padding(end = 8.dp),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text("Total", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    GridCell(text = "${student.totalUt1.toInt()}", width = 42.dp, bold = true)
                    GridCell(text = "${student.totalUt2.toInt()}", width = 42.dp, bold = true)
                    GridCell(text = "1000", width = 48.dp, bold = true)
                    GridCell(text = "${student.totalUtObtained.toInt()}", width = 52.dp, bold = true)
                    GridCell(text = "1000", width = 50.dp, bold = true)
                    GridCell(text = "${student.totalHalfYearlyObtained.toInt()}", width = 54.dp, bold = true)
                    GridCell(text = "1000", width = 50.dp, bold = true)
                    GridCell(text = "${student.totalAnnualObtained.toInt()}", width = 54.dp, bold = true)
                    GridCell(text = "2000", width = 58.dp, bold = true)
                    GridCell(text = "${student.grandTotal.toInt()}", width = 64.dp, bold = true)
                    GridCell(text = String.format("%.2f%%", student.overallPercentage), width = 66.dp, bold = true)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Remarks Box (Single, Clean)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFFF8FAFC), RoundedCornerShape(3.dp))
                    .border(0.8.dp, Color(0xFFCBD5E1), RoundedCornerShape(3.dp))
                    .padding(horizontal = 10.dp, vertical = 7.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📌 REMARKS: ",
                    fontWeight = FontWeight.Bold,
                    fontSize = 10.sp,
                    color = Color(0xFF1E3A8A)
                )
                Text(
                    text = displayRemarks,
                    fontSize = 10.sp,
                    color = Color(0xFF0F172A)
                )
            }

            Spacer(modifier = Modifier.height(26.dp))

            // Signatures
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 30.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(130.dp).height(1.dp).background(Color.Black))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Class Teacher", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(130.dp).height(1.dp).background(Color.Black))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Exam In-Charge", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(modifier = Modifier.width(130.dp).height(1.dp).background(Color.Black))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Principal", fontWeight = FontWeight.Bold, fontSize = 10.sp, color = Color.Black)
                }
            }
        }
    }
}

/**
 * Pixel-Perfect Master Broadsheet Card matching WebApp PDF layout
 */
@Composable
fun MasterBroadsheetLandscapeCard(
    students: List<StudentRecord>,
    schoolConfig: SchoolConfig,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.width(840.dp),
        shape = RoundedCornerShape(4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.5.dp, Color.Black)
                .padding(10.dp)
        ) {
            // Header
            Text(
                text = "Examination Result For The Session ${schoolConfig.session} (${schoolConfig.schoolName})",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 6.dp)
            )

            // Master Table
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .border(0.8.dp, Color.Black)
            ) {
                // Table Header
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFE2E8F0))
                        .border(0.8.dp, Color.Black)
                        .height(36.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(modifier = Modifier.width(26.dp).fillMaxHeight().border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("Roll", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(92.dp).fillMaxHeight().border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("NAME OF STUDENT", fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(42.dp).fillMaxHeight().border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("TERM", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }
                    Box(modifier = Modifier.width(42.dp).fillMaxHeight().border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("SR NO.", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                    }

                    // 10 Subjects
                    val subNamesShort = arrayOf("HINDI", "ENGLISH", "MATH", "SCIENCE", "SANSKRIT", "SOC SCI", "G.K.", "ART", "P.T.", "COMP")
                    subNamesShort.forEach { sName ->
                        Column(
                            modifier = Modifier
                                .width(51.dp)
                                .fillMaxHeight()
                                .border(0.4.dp, Color.Black)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text(sName, fontSize = 7.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth().weight(1f).border(0.3.dp, Color.Black)) {
                                listOf("I", "II", "III", "TOT").forEach { subCol ->
                                    Box(modifier = Modifier.weight(1f).fillMaxHeight().border(0.2.dp, Color.Black), contentAlignment = Alignment.Center) {
                                        Text(subCol, fontSize = 6.5.sp, fontWeight = FontWeight.Bold)
                                    }
                                }
                            }
                        }
                    }

                    Box(modifier = Modifier.width(50.dp).fillMaxHeight().border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("GRAND\nTOT", fontSize = 7.5.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    }
                    Box(modifier = Modifier.width(48.dp).fillMaxHeight().border(0.4.dp, Color.Black), contentAlignment = Alignment.Center) {
                        Text("RESULT", fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                    }
                }

                // Student Rows
                students.forEachIndexed { sIdx, student ->
                    val bg = if (sIdx % 2 == 1) Color(0xFFF8FAFC) else Color.White
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(bg)
                            .border(0.5.dp, Color.Black)
                            .height(52.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Meta Info
                        Box(modifier = Modifier.width(26.dp).fillMaxHeight().border(0.3.dp, Color.Black), contentAlignment = Alignment.Center) {
                            Text("${student.rollNo}", fontSize = 8.5.sp, fontWeight = FontWeight.Bold)
                        }
                        Box(modifier = Modifier.width(92.dp).fillMaxHeight().border(0.3.dp, Color.Black).padding(horizontal = 3.dp), contentAlignment = Alignment.CenterStart) {
                            Text(student.name, fontSize = 8.sp, fontWeight = FontWeight.Bold, maxLines = 2)
                        }
                        Box(modifier = Modifier.width(42.dp).fillMaxHeight().border(0.3.dp, Color.Black), contentAlignment = Alignment.Center) {
                            Text(student.exam.ifEmpty { "Term" }, fontSize = 7.5.sp)
                        }
                        Box(modifier = Modifier.width(42.dp).fillMaxHeight().border(0.3.dp, Color.Black), contentAlignment = Alignment.Center) {
                            Text(student.sr.ifEmpty { "-" }, fontSize = 7.5.sp)
                        }

                        // 10 Subject Data (4 sub-rows each)
                        StudentRecord.ALL_SUBJECTS.forEach { subName ->
                            val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
                            Column(
                                modifier = Modifier
                                    .width(51.dp)
                                    .fillMaxHeight()
                                    .border(0.3.dp, Color.Black)
                            ) {
                                // Row 1 (UT)
                                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                    BroadsheetCell(subMarks.r1UnitTest.t1.ifEmpty { "-" })
                                    BroadsheetCell(subMarks.r1UnitTest.t2.ifEmpty { "-" })
                                    BroadsheetCell(subMarks.r1UnitTest.t3.ifEmpty { "-" })
                                    BroadsheetCell("${subMarks.r1UnitTest.total.toInt()}", bold = true)
                                }
                                // Row 2 (HY)
                                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                    BroadsheetCell(subMarks.r2HalfYearly.t1.ifEmpty { "-" })
                                    BroadsheetCell(subMarks.r2HalfYearly.t2.ifEmpty { "-" })
                                    BroadsheetCell(subMarks.r2HalfYearly.t3.ifEmpty { "-" })
                                    BroadsheetCell("${subMarks.r2HalfYearly.total.toInt()}", bold = true)
                                }
                                // Row 3 (Annual)
                                Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
                                    BroadsheetCell(subMarks.r3Annual.t1.ifEmpty { "-" })
                                    BroadsheetCell(subMarks.r3Annual.t2.ifEmpty { "-" })
                                    BroadsheetCell(subMarks.r3Annual.t3.ifEmpty { "-" })
                                    BroadsheetCell("${subMarks.r3Annual.total.toInt()}", bold = true)
                                }
                                // Row 4 (Grand Total)
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .weight(1f)
                                        .background(Color(0xFFEEF2F6)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text("${subMarks.grandTotal.toInt()}", fontSize = 7.5.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        // Grand Totals Column (4 sub-rows)
                        Column(
                            modifier = Modifier
                                .width(50.dp)
                                .fillMaxHeight()
                                .border(0.3.dp, Color.Black)
                        ) {
                            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text("${student.totalUtObtained.toInt()}", fontSize = 7.sp)
                            }
                            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text("${student.totalHalfYearlyObtained.toInt()}", fontSize = 7.sp)
                            }
                            Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                                Text("${student.totalAnnualObtained.toInt()}", fontSize = 7.sp)
                            }
                            Box(modifier = Modifier.fillMaxWidth().weight(1f).background(Color(0xFFEEF2F6)), contentAlignment = Alignment.Center) {
                                Text("${student.grandTotal.toInt()}", fontSize = 8.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        // Result Column (Percentage)
                        Box(
                            modifier = Modifier
                                .width(48.dp)
                                .fillMaxHeight()
                                .border(0.3.dp, Color.Black),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                String.format("%.2f%%", student.overallPercentage),
                                fontSize = 8.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (student.overallPercentage >= 33.0) Color(0xFF15803D) else Color(0xFFDC2626)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.BroadsheetCell(text: String, bold: Boolean = false) {
    Box(
        modifier = Modifier
            .weight(1f)
            .fillMaxHeight()
            .border(0.15.dp, Color(0xFFCBD5E1)),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 6.5.sp,
            fontWeight = if (bold) FontWeight.Bold else FontWeight.Normal,
            color = Color.Black
        )
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
            .height(24.dp)
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
