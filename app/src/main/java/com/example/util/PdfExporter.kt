package com.example.util

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.CancellationSignal
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.print.PageRange
import android.print.PrintAttributes
import android.print.PrintDocumentAdapter
import android.print.PrintDocumentInfo
import android.print.PrintManager
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.content.FileProvider
import com.example.data.model.SchoolConfig
import com.example.data.model.StudentRecord
import com.example.data.model.SubjectMarks
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

object PdfExporter {

    private const val PAGE_WIDTH = 842 // A4 Landscape width in points
    private const val PAGE_HEIGHT = 595 // A4 Landscape height in points

    fun generateSingleMarksheetPdf(
        context: Context,
        student: StudentRecord,
        schoolConfig: SchoolConfig,
        remarks: String
    ): File {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, 1).create()
        val page = pdfDocument.startPage(pageInfo)
        val canvas = page.canvas

        drawSingleMarksheetOnCanvas(canvas, student, schoolConfig, remarks)

        pdfDocument.finishPage(page)

        val outputDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val fileName = "Marksheet_Roll_${student.rollNo}_${student.name.replace(" ", "_")}.pdf"
        val outputFile = File(outputDir, fileName)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    private fun drawSingleMarksheetOnCanvas(
        canvas: Canvas,
        student: StudentRecord,
        schoolConfig: SchoolConfig,
        remarks: String
    ) {
        val margin = 18f
        val right = PAGE_WIDTH - margin
        val bottom = PAGE_HEIGHT - margin

        // Background
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // Outer Double Border Frame (Identical to WebApp)
        val borderPaint = Paint().apply {
            color = Color.parseColor("#1E3A8A")
            style = Paint.Style.STROKE
            strokeWidth = 2f
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(margin, margin, right, bottom), 6f, 6f, borderPaint)

        val innerBorderPaint = Paint().apply {
            color = Color.parseColor("#1E3A8A")
            style = Paint.Style.STROKE
            strokeWidth = 0.6f
            isAntiAlias = true
        }
        canvas.drawRoundRect(RectF(margin + 2.5f, margin + 2.5f, right - 2.5f, bottom - 2.5f), 5f, 5f, innerBorderPaint)

        // Header Section - Spaced proportionally to cover full height
        var currentY = margin + 24f

        val titlePaint = Paint().apply {
            color = Color.parseColor("#1E3A8A")
            textSize = 21f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(schoolConfig.schoolName.uppercase(), PAGE_WIDTH / 2f, currentY, titlePaint)

        currentY += 15f
        val subTitlePaint = Paint().apply {
            color = Color.parseColor("#475569")
            textSize = 10f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "Affiliated to Board • Academic Session: ${schoolConfig.session}",
            PAGE_WIDTH / 2f,
            currentY,
            subTitlePaint
        )

        currentY += 16f
        val pillText = "ANNUAL PROGRESS REPORT"
        val pillPaint = Paint().apply {
            color = Color.parseColor("#1E293B")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            letterSpacing = 0.06f
            isAntiAlias = true
        }
        canvas.drawText(pillText, PAGE_WIDTH / 2f, currentY, pillPaint)

        currentY += 12f

        // Student Meta Section Box (Expanded height for clear typography)
        val tableLeft = margin + 8f
        val tableRight = right - 8f
        val tableWidth = tableRight - tableLeft

        val metaBoxTop = currentY
        val metaBoxHeight = 44f
        val metaBoxBottom = metaBoxTop + metaBoxHeight

        val metaBoxPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        canvas.drawRect(tableLeft, metaBoxTop, tableRight, metaBoxBottom, metaBoxPaint)

        val metaLabelPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val metaValPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        val leftColX = tableLeft + 10f
        val midColX = PAGE_WIDTH / 2f + 40f

        canvas.drawText("Student Name:", leftColX, metaBoxTop + 18f, metaLabelPaint)
        canvas.drawText(student.name.ifEmpty { "N/A" }, leftColX + 85f, metaBoxTop + 18f, metaValPaint)

        canvas.drawText("Roll No:", midColX, metaBoxTop + 18f, metaLabelPaint)
        canvas.drawText("${student.rollNo}", midColX + 54f, metaBoxTop + 18f, metaValPaint)

        canvas.drawText("Class & Section:", leftColX, metaBoxTop + 36f, metaLabelPaint)
        canvas.drawText(schoolConfig.classSec, leftColX + 85f, metaBoxTop + 36f, metaValPaint)

        canvas.drawText("Scholar / SR No:", midColX, metaBoxTop + 36f, metaLabelPaint)
        canvas.drawText(student.sr.ifEmpty { "N/A" }, midColX + 92f, metaBoxTop + 36f, metaValPaint)

        currentY = metaBoxBottom + 9f

        // Evaluation Table - Column widths spanning exactly tableWidth (790pt)
        // SN(30), Subject(154), UT1(44), UT2(44), UT-MM(50), UT-Ob(54), HY-MM(52), HY-Ob(56), Ann-MM(52), Ann-Ob(56), Grand-MM(62), Grand-Ob(68), Perc(68) = 790pt
        val colWidths = floatArrayOf(30f, 154f, 44f, 44f, 50f, 54f, 52f, 56f, 52f, 56f, 62f, 68f, 68f)
        val colStarts = FloatArray(colWidths.size + 1)
        colStarts[0] = tableLeft
        for (i in colWidths.indices) {
            colStarts[i + 1] = colStarts[i] + colWidths[i]
        }

        // Full-page row height expansion so marksheet covers entire printed page without bottom gap
        val tableHeaderH = 30f
        val rowH = 24.5f
        val totalRowH = 26.5f

        // Table Header Background
        val thBgPaint = Paint().apply {
            color = Color.parseColor("#F1F5F9")
            style = Paint.Style.FILL
        }
        canvas.drawRect(tableLeft, currentY, tableRight, currentY + tableHeaderH, thBgPaint)

        val gridLinePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        // Outer table rectangle
        val totalTableRows = 10 // 10 subjects
        val tableBottom = currentY + tableHeaderH + (totalTableRows * rowH) + totalRowH
        canvas.drawRect(tableLeft, currentY, tableRight, tableBottom, gridLinePaint)

        // Draw header horizontal split lines
        val thMidY = currentY + (tableHeaderH / 2f)
        canvas.drawLine(colStarts[2], thMidY, colStarts[12], thMidY, gridLinePaint)
        canvas.drawLine(tableLeft, currentY + tableHeaderH, tableRight, currentY + tableHeaderH, gridLinePaint)

        val headerTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val subHeaderTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        // Rowspan headers (SN, Subject, Percentage)
        canvas.drawText("SN", (colStarts[0] + colStarts[1]) / 2f, currentY + 19f, headerTextPaint)
        canvas.drawText("Subject", (colStarts[1] + colStarts[2]) / 2f, currentY + 19f, headerTextPaint)
        canvas.drawText("Percentage", (colStarts[12] + colStarts[13]) / 2f, currentY + 19f, headerTextPaint)

        // Colspan headers in top tier
        canvas.drawText("Unit Test", (colStarts[2] + colStarts[4]) / 2f, currentY + 11f, headerTextPaint)
        canvas.drawText("Total", (colStarts[4] + colStarts[6]) / 2f, currentY + 11f, headerTextPaint)
        canvas.drawText("Half Yearly", (colStarts[6] + colStarts[8]) / 2f, currentY + 11f, headerTextPaint)
        canvas.drawText("Annual", (colStarts[8] + colStarts[10]) / 2f, currentY + 11f, headerTextPaint)
        canvas.drawText("Grand Total", (colStarts[10] + colStarts[12]) / 2f, currentY + 11f, headerTextPaint)

        // Subheaders in bottom tier
        canvas.drawText("1", (colStarts[2] + colStarts[3]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("2", (colStarts[3] + colStarts[4]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("M.M.", (colStarts[4] + colStarts[5]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("Ob.M.", (colStarts[5] + colStarts[6]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("M.M.", (colStarts[6] + colStarts[7]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("Ob.M.", (colStarts[7] + colStarts[8]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("M.M.", (colStarts[8] + colStarts[9]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("Ob.M.", (colStarts[9] + colStarts[10]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("M.M", (colStarts[10] + colStarts[11]) / 2f, thMidY + 11f, subHeaderTextPaint)
        canvas.drawText("Ob.M.", (colStarts[11] + colStarts[12]) / 2f, thMidY + 11f, subHeaderTextPaint)

        // Vertical lines in header
        canvas.drawLine(colStarts[1], currentY, colStarts[1], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[2], currentY, colStarts[2], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[4], currentY, colStarts[4], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[6], currentY, colStarts[6], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[8], currentY, colStarts[8], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[10], currentY, colStarts[10], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[12], currentY, colStarts[12], currentY + tableHeaderH, gridLinePaint)

        // Subheader internal vertical lines
        canvas.drawLine(colStarts[3], thMidY, colStarts[3], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[5], thMidY, colStarts[5], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[7], thMidY, colStarts[7], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[9], thMidY, colStarts[9], currentY + tableHeaderH, gridLinePaint)
        canvas.drawLine(colStarts[11], thMidY, colStarts[11], currentY + tableHeaderH, gridLinePaint)

        // Rows
        var rowY = currentY + tableHeaderH

        val cellTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val subTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }

        StudentRecord.ALL_SUBJECTS.forEachIndexed { index, subName ->
            val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
            val baseline = rowY + 16.5f

            // SN
            canvas.drawText("${index + 1}", (colStarts[0] + colStarts[1]) / 2f, baseline, cellTextPaint)
            // Subject
            canvas.drawText(subName, colStarts[1] + 8f, baseline, subTextPaint)
            // UT1
            canvas.drawText(subMarks.r1UnitTest.t1.ifEmpty { "-" }, (colStarts[2] + colStarts[3]) / 2f, baseline, cellTextPaint)
            // UT2
            canvas.drawText(subMarks.r1UnitTest.t2.ifEmpty { "-" }, (colStarts[3] + colStarts[4]) / 2f, baseline, cellTextPaint)
            // UT MM (100)
            canvas.drawText("100", (colStarts[4] + colStarts[5]) / 2f, baseline, cellTextPaint)
            // UT Ob.M.
            canvas.drawText("${subMarks.utObtained.toInt()}", (colStarts[5] + colStarts[6]) / 2f, baseline, cellTextPaint)
            // HY MM (100)
            canvas.drawText("100", (colStarts[6] + colStarts[7]) / 2f, baseline, cellTextPaint)
            // HY Ob.M.
            canvas.drawText("${subMarks.halfYearlyObtained.toInt()}", (colStarts[7] + colStarts[8]) / 2f, baseline, cellTextPaint)
            // Ann MM (100)
            canvas.drawText("100", (colStarts[8] + colStarts[9]) / 2f, baseline, cellTextPaint)
            // Ann Ob.M.
            canvas.drawText("${subMarks.annualObtained.toInt()}", (colStarts[9] + colStarts[10]) / 2f, baseline, cellTextPaint)
            // Grand MM (200)
            canvas.drawText("200", (colStarts[10] + colStarts[11]) / 2f, baseline, cellTextPaint)
            // Grand Ob.M.
            canvas.drawText("${subMarks.grandTotal.toInt()}", (colStarts[11] + colStarts[12]) / 2f, baseline, cellTextPaint)
            // Percentage
            canvas.drawText(String.format("%.1f%%", subMarks.percentage), (colStarts[12] + colStarts[13]) / 2f, baseline, cellTextPaint)

            // Horizontal row line
            canvas.drawLine(tableLeft, rowY + rowH, tableRight, rowY + rowH, gridLinePaint)
            rowY += rowH
        }

        // Total Row (Elevated styling with background)
        val totalBgPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }
        canvas.drawRect(tableLeft, rowY, tableRight, tableBottom, totalBgPaint)

        val totalBaseline = rowY + 18f
        val totalLabelPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.RIGHT
            isAntiAlias = true
        }
        val boldCellPaint = Paint().apply {
            color = Color.BLACK
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        canvas.drawText("Total", colStarts[2] - 10f, totalBaseline, totalLabelPaint)
        canvas.drawText("${student.totalUt1.toInt()}", (colStarts[2] + colStarts[3]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("${student.totalUt2.toInt()}", (colStarts[3] + colStarts[4]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("1000", (colStarts[4] + colStarts[5]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("${student.totalUtObtained.toInt()}", (colStarts[5] + colStarts[6]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("1000", (colStarts[6] + colStarts[7]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("${student.totalHalfYearlyObtained.toInt()}", (colStarts[7] + colStarts[8]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("1000", (colStarts[8] + colStarts[9]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("${student.totalAnnualObtained.toInt()}", (colStarts[9] + colStarts[10]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("2000", (colStarts[10] + colStarts[11]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText("${student.grandTotal.toInt()}", (colStarts[11] + colStarts[12]) / 2f, totalBaseline, boldCellPaint)
        canvas.drawText(String.format("%.2f%%", student.overallPercentage), (colStarts[12] + colStarts[13]) / 2f, totalBaseline, boldCellPaint)

        // Draw all vertical lines across the table rows down to tableBottom
        for (i in 1 until colStarts.size - 1) {
            canvas.drawLine(colStarts[i], currentY + tableHeaderH, colStarts[i], tableBottom, gridLinePaint)
        }

        currentY = tableBottom + 11f

        // Clean Remarks Box
        val remarksBoxH = 30f
        val remarksBg = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }
        val remarksBorder = Paint().apply {
            color = Color.parseColor("#CBD5E1")
            style = Paint.Style.STROKE
            strokeWidth = 0.8f
        }
        canvas.drawRoundRect(RectF(tableLeft, currentY, tableRight, currentY + remarksBoxH), 4f, 4f, remarksBg)
        canvas.drawRoundRect(RectF(tableLeft, currentY, tableRight, currentY + remarksBoxH), 4f, 4f, remarksBorder)

        val remTextPaint = Paint().apply {
            color = Color.parseColor("#1E3A8A")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            isAntiAlias = true
        }
        val remValPaint = Paint().apply {
            color = Color.parseColor("#0F172A")
            textSize = 9.5f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
            isAntiAlias = true
        }

        canvas.drawText("📌 REMARKS:", tableLeft + 10f, currentY + 19f, remTextPaint)
        canvas.drawText(remarks, tableLeft + 84f, currentY + 19f, remValPaint)

        currentY += remarksBoxH + 46f

        // Signatures Line (Positioned near bottom border to cover full height elegantly)
        val sigLinePaint = Paint().apply {
            color = Color.BLACK
            strokeWidth = 1.2f
        }
        val sigLabelPaint = Paint().apply {
            color = Color.BLACK
            textSize = 10f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        val sig1X = tableLeft + 85f
        val sig2X = PAGE_WIDTH / 2f
        val sig3X = tableRight - 85f

        canvas.drawLine(sig1X - 60f, currentY, sig1X + 60f, currentY, sigLinePaint)
        canvas.drawText("Class Teacher", sig1X, currentY + 15f, sigLabelPaint)

        canvas.drawLine(sig2X - 60f, currentY, sig2X + 60f, currentY, sigLinePaint)
        canvas.drawText("Exam In-Charge", sig2X, currentY + 15f, sigLabelPaint)

        canvas.drawLine(sig3X - 60f, currentY, sig3X + 60f, currentY, sigLinePaint)
        canvas.drawText("Principal", sig3X, currentY + 15f, sigLabelPaint)
    }

    fun generateMasterBroadsheetPdf(
        context: Context,
        students: List<StudentRecord>,
        schoolConfig: SchoolConfig
    ): File {
        val pdfDocument = PdfDocument()
        val pageSize = 10
        val totalPages = (students.size + pageSize - 1) / pageSize

        for (pageIdx in 0 until totalPages) {
            val pageInfo = PdfDocument.PageInfo.Builder(PAGE_WIDTH, PAGE_HEIGHT, pageIdx + 1).create()
            val page = pdfDocument.startPage(pageInfo)
            val canvas = page.canvas

            val start = pageIdx * pageSize
            val end = (start + pageSize).coerceAtMost(students.size)
            val pageStudents = students.subList(start, end)

            drawMasterBroadsheetPage(canvas, pageStudents, schoolConfig, pageIdx + 1, totalPages)

            pdfDocument.finishPage(page)
        }

        val outputDir = File(context.cacheDir, "pdfs").apply { mkdirs() }
        val fileName = "Master_Broadsheet_Session_${schoolConfig.session.replace("-", "_")}.pdf"
        val outputFile = File(outputDir, fileName)

        FileOutputStream(outputFile).use { out ->
            pdfDocument.writeTo(out)
        }
        pdfDocument.close()

        return outputFile
    }

    private fun drawMasterBroadsheetPage(
        canvas: Canvas,
        students: List<StudentRecord>,
        schoolConfig: SchoolConfig,
        pageNumber: Int,
        totalPages: Int
    ) {
        val margin = 16f
        val right = PAGE_WIDTH - margin
        val bottom = PAGE_HEIGHT - margin

        // Background
        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, PAGE_WIDTH.toFloat(), PAGE_HEIGHT.toFloat(), bgPaint)

        // Outer Border
        val borderPaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 1.5f
        }
        canvas.drawRect(margin, margin, right, bottom, borderPaint)

        // Title Header
        var currentY = margin + 17f
        val titlePaint = Paint().apply {
            color = Color.BLACK
            textSize = 12f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        canvas.drawText(
            "Examination Result For The Session ${schoolConfig.session} (${schoolConfig.schoolName}) - Page $pageNumber of $totalPages",
            PAGE_WIDTH / 2f,
            currentY,
            titlePaint
        )

        currentY += 8f

        // Table definition - Spanning all 810pt width from tableLeft(16f) to tableRight(826f)
        val tableLeft = margin
        val tableRight = right
        val gridLinePaint = Paint().apply {
            color = Color.BLACK
            style = Paint.Style.STROKE
            strokeWidth = 0.6f
        }

        // Exact Column widths summing to 810pt:
        // Roll No (26), Name (92), Exam (42), SR No (42),
        // 10 Subjects * 51 = 510, Grand Total (50), Result (48)
        val colRoll = 26f
        val colName = 92f
        val colExam = 42f
        val colSr = 42f
        val colSub = 51f // 10 * 51 = 510f (each sub-column = 12.75f)
        val colGrand = 50f
        val colResult = 48f

        val colStarts = FloatArray(17)
        colStarts[0] = tableLeft
        colStarts[1] = colStarts[0] + colRoll
        colStarts[2] = colStarts[1] + colName
        colStarts[3] = colStarts[2] + colExam
        colStarts[4] = colStarts[3] + colSr
        for (s in 0 until 10) {
            colStarts[5 + s] = colStarts[4 + s] + colSub
        }
        colStarts[15] = colStarts[14] + colGrand
        colStarts[16] = colStarts[15] + colResult

        val thH = 22f
        val thMidY = currentY + (thH / 2f)

        // Table Header Rect
        val thBgPaint = Paint().apply {
            color = Color.parseColor("#E2E8F0")
            style = Paint.Style.FILL
        }
        canvas.drawRect(tableLeft, currentY, tableRight, currentY + thH, thBgPaint)
        canvas.drawRect(tableLeft, currentY, tableRight, currentY + thH, gridLinePaint)

        val headerTextPaint = Paint().apply {
            color = Color.BLACK
            textSize = 7.2f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }

        canvas.drawText("Roll No.", (colStarts[0] + colStarts[1]) / 2f, currentY + 14f, headerTextPaint)
        canvas.drawText("NAME OF STUDENT", (colStarts[1] + colStarts[2]) / 2f, currentY + 14f, headerTextPaint)
        canvas.drawText("EXAM TERM", (colStarts[2] + colStarts[3]) / 2f, currentY + 14f, headerTextPaint)
        canvas.drawText("STUDENT SR.", (colStarts[3] + colStarts[4]) / 2f, currentY + 14f, headerTextPaint)

        // 10 Subject Headers
        val subNamesShort = arrayOf("HINDI", "ENGLISH", "MATH", "SCIENCE", "SANSKRIT", "SOC SCIENCE", "G.K.", "ART", "P.T.", "COMPUTER")
        for (i in 0 until 10) {
            val subLeft = colStarts[4 + i]
            val subRight = colStarts[5 + i]
            canvas.drawText(subNamesShort[i], (subLeft + subRight) / 2f, currentY + 8f, headerTextPaint)
            canvas.drawLine(subLeft, thMidY, subRight, thMidY, gridLinePaint)

            // Sub columns: I, II, III, TOTAL
            val subColW = colSub / 4f
            canvas.drawText("I", subLeft + subColW * 0.5f, thMidY + 8f, headerTextPaint)
            canvas.drawText("II", subLeft + subColW * 1.5f, thMidY + 8f, headerTextPaint)
            canvas.drawText("III", subLeft + subColW * 2.5f, thMidY + 8f, headerTextPaint)
            canvas.drawText("TOT", subLeft + subColW * 3.5f, thMidY + 8f, headerTextPaint)

            canvas.drawLine(subLeft + subColW, thMidY, subLeft + subColW, currentY + thH, gridLinePaint)
            canvas.drawLine(subLeft + subColW * 2, thMidY, subLeft + subColW * 2, currentY + thH, gridLinePaint)
            canvas.drawLine(subLeft + subColW * 3, thMidY, subLeft + subColW * 3, currentY + thH, gridLinePaint)
        }

        canvas.drawText("GRAND TOT", (colStarts[14] + colStarts[15]) / 2f, currentY + 14f, headerTextPaint)
        canvas.drawText("RESULT", (colStarts[15] + colStarts[16]) / 2f, currentY + 14f, headerTextPaint)

        for (i in 1..15) {
            canvas.drawLine(colStarts[i], currentY, colStarts[i], currentY + thH, gridLinePaint)
        }

        // Student Data Rows
        var rowTop = currentY + thH
        val subRowH = 11.5f
        val studentH = subRowH * 4f // 4 sub-rows per student

        val cellPaint = Paint().apply {
            color = Color.BLACK
            textSize = 6.8f
            textAlign = Paint.Align.CENTER
            isAntiAlias = true
        }
        val namePaint = Paint().apply {
            color = Color.BLACK
            textSize = 6.8f
            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
            textAlign = Paint.Align.LEFT
            isAntiAlias = true
        }
        val shadedPaint = Paint().apply {
            color = Color.parseColor("#F8FAFC")
            style = Paint.Style.FILL
        }

        students.forEachIndexed { sIdx, student ->
            val sTop = rowTop
            val sBottom = sTop + studentH

            // Alternating student subtle shading
            if (sIdx % 2 == 1) {
                canvas.drawRect(tableLeft, sTop, tableRight, sBottom, shadedPaint)
            }

            // Student meta info (spans all 4 sub-rows)
            canvas.drawText("${student.rollNo}", (colStarts[0] + colStarts[1]) / 2f, sTop + studentH / 2f + 2.5f, cellPaint)
            canvas.drawText(student.name.take(15), colStarts[1] + 3f, sTop + studentH / 2f + 2.5f, namePaint)
            canvas.drawText(student.exam.ifEmpty { "Term" }, (colStarts[2] + colStarts[3]) / 2f, sTop + studentH / 2f + 2.5f, cellPaint)
            canvas.drawText(student.sr.ifEmpty { "-" }, (colStarts[3] + colStarts[4]) / 2f, sTop + studentH / 2f + 2.5f, cellPaint)

            // 4 sub-rows: R1, R2, R3, TOTAL
            for (subRow in 0..3) {
                val lineY = sTop + (subRow * subRowH)
                val baseline = lineY + 8f

                if (subRow < 3) {
                    // UT / HY / Annual
                    var rowSum = 0.0
                    for (subIdx in 0 until 10) {
                        val subName = StudentRecord.ALL_SUBJECTS[subIdx]
                        val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
                        val eval = when (subRow) {
                            0 -> subMarks.r1UnitTest
                            1 -> subMarks.r2HalfYearly
                            else -> subMarks.r3Annual
                        }
                        val subLeft = colStarts[4 + subIdx]
                        val subColW = colSub / 4f

                        canvas.drawText(eval.t1.ifEmpty { "-" }, subLeft + subColW * 0.5f, baseline, cellPaint)
                        canvas.drawText(eval.t2.ifEmpty { "-" }, subLeft + subColW * 1.5f, baseline, cellPaint)
                        canvas.drawText(eval.t3.ifEmpty { "-" }, subLeft + subColW * 2.5f, baseline, cellPaint)
                        canvas.drawText("${eval.total.toInt()}", subLeft + subColW * 3.5f, baseline, cellPaint)
                        rowSum += eval.total
                    }
                    canvas.drawText("${rowSum.toInt()}", (colStarts[14] + colStarts[15]) / 2f, baseline, cellPaint)
                } else {
                    // Total Sub-row
                    for (subIdx in 0 until 10) {
                        val subName = StudentRecord.ALL_SUBJECTS[subIdx]
                        val subMarks = student.marks[subName] ?: SubjectMarks(subjectName = subName)
                        val subLeft = colStarts[4 + subIdx]
                        val subColW = colSub / 4f

                        val boldSubPaint = Paint().apply {
                            color = Color.BLACK
                            textSize = 7f
                            typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
                            textAlign = Paint.Align.CENTER
                            isAntiAlias = true
                        }
                        // Draw subject grand total under TOT (4th column)
                        canvas.drawText("${subMarks.grandTotal.toInt()}", subLeft + subColW * 3.5f, baseline, boldSubPaint)
                    }
                    canvas.drawText("${student.grandTotal.toInt()}", (colStarts[14] + colStarts[15]) / 2f, baseline, headerTextPaint)
                    canvas.drawText(String.format("%.2f%%", student.overallPercentage), (colStarts[15] + colStarts[16]) / 2f, baseline, headerTextPaint)
                }

                if (subRow < 3) {
                    canvas.drawLine(colStarts[4], lineY + subRowH, colStarts[15], lineY + subRowH, gridLinePaint)
                }
            }

            // Draw student box borders
            canvas.drawRect(tableLeft, sTop, tableRight, sBottom, gridLinePaint)
            for (i in 1..15) {
                canvas.drawLine(colStarts[i], sTop, colStarts[i], sBottom, gridLinePaint)
            }

            // Draw inner sub-column lines for subjects for all 4 sub-rows
            for (subIdx in 0 until 10) {
                val subLeft = colStarts[4 + subIdx]
                val subColW = colSub / 4f
                for (c in 1..3) {
                    canvas.drawLine(subLeft + subColW * c, sTop, subLeft + subColW * c, sBottom, gridLinePaint)
                }
            }

            rowTop += studentH
        }
    }

    fun openOrSharePdf(context: Context, file: File, title: String) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider",
            file
        )

        val sendIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, title)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(sendIntent, title).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        }
        context.startActivity(chooser)
    }

    fun savePdfToPublicDownloads(context: Context, file: File, customName: String? = null): Boolean {
        val targetName = customName ?: file.name
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val values = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, targetName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/SchoolMarksheets")
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    context.contentResolver.openOutputStream(uri)?.use { outputStream ->
                        FileInputStream(file).use { inputStream ->
                            inputStream.copyTo(outputStream)
                        }
                    }
                    Toast.makeText(
                        context,
                        "✓ Saved to Downloads/SchoolMarksheets/$targetName",
                        Toast.LENGTH_LONG
                    ).show()
                    return true
                }
            }

            // Fallback for older or direct filesystem access
            val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
            val appFolder = File(downloadsDir, "SchoolMarksheets").apply { mkdirs() }
            val targetFile = File(appFolder, targetName)
            
            FileInputStream(file).use { input ->
                FileOutputStream(targetFile).use { output ->
                    input.copyTo(output)
                }
            }
            MediaScannerConnection.scanFile(
                context,
                arrayOf(targetFile.absolutePath),
                arrayOf("application/pdf"),
                null
            )
            Toast.makeText(
                context,
                "✓ Saved to Downloads/SchoolMarksheets/$targetName",
                Toast.LENGTH_LONG
            ).show()
            return true
        } catch (e: Exception) {
            Toast.makeText(
                context,
                "Saved to app cache (${file.name}): ${e.localizedMessage}",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }
    }

    fun printPdf(context: Context, file: File, jobTitle: String) {
        val printManager = context.getSystemService(Context.PRINT_SERVICE) as? PrintManager
        if (printManager == null) {
            Toast.makeText(context, "Printing not supported on this device", Toast.LENGTH_SHORT).show()
            return
        }

        val printAdapter = object : PrintDocumentAdapter() {
            override fun onLayout(
                oldAttributes: PrintAttributes?,
                newAttributes: PrintAttributes?,
                cancellationSignal: CancellationSignal?,
                callback: LayoutResultCallback?,
                extras: Bundle?
            ) {
                if (cancellationSignal?.isCanceled == true) {
                    callback?.onLayoutCancelled()
                    return
                }
                val info = PrintDocumentInfo.Builder(file.name)
                    .setContentType(PrintDocumentInfo.CONTENT_TYPE_DOCUMENT)
                    .setPageCount(PrintDocumentInfo.PAGE_COUNT_UNKNOWN)
                    .build()
                callback?.onLayoutFinished(info, true)
            }

            override fun onWrite(
                pages: Array<out PageRange>?,
                destination: ParcelFileDescriptor?,
                cancellationSignal: CancellationSignal?,
                callback: WriteResultCallback?
            ) {
                if (destination == null) {
                    callback?.onWriteFailed("Invalid destination")
                    return
                }
                try {
                    FileInputStream(file).use { input ->
                        FileOutputStream(destination.fileDescriptor).use { output ->
                            val buffer = ByteArray(8192)
                            var bytesRead: Int
                            while (input.read(buffer).also { bytesRead = it } >= 0) {
                                if (cancellationSignal?.isCanceled == true) {
                                    callback?.onWriteCancelled()
                                    return
                                }
                                output.write(buffer, 0, bytesRead)
                            }
                        }
                    }
                    callback?.onWriteFinished(arrayOf(PageRange.ALL_PAGES))
                } catch (e: Exception) {
                    callback?.onWriteFailed(e.message)
                }
            }
        }

        val printAttributes = PrintAttributes.Builder()
            .setMediaSize(PrintAttributes.MediaSize.ISO_A4.asLandscape())
            .setColorMode(PrintAttributes.COLOR_MODE_COLOR)
            .build()

        printManager.print(jobTitle, printAdapter, printAttributes)
    }
}
