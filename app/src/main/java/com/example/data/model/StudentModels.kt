package com.example.data.model

data class SubjectEvaluation(
    val t1: String = "",
    val t2: String = "",
    val t3: String = ""
) {
    val total: Double get() = getSum()

    fun getSum(): Double {
        val v1 = t1.toDoubleOrNull() ?: 0.0
        val v2 = t2.toDoubleOrNull() ?: 0.0
        val v3 = t3.toDoubleOrNull() ?: 0.0
        return v1 + v2 + v3
    }

    fun hasAnyScore(): Boolean {
        return t1.isNotBlank() || t2.isNotBlank() || t3.isNotBlank()
    }
}

data class SubjectMarks(
    val subjectName: String,
    val r1UnitTest: SubjectEvaluation = SubjectEvaluation(),
    val r2HalfYearly: SubjectEvaluation = SubjectEvaluation(),
    val r3Annual: SubjectEvaluation = SubjectEvaluation()
) {
    val ut1Score: Double? get() = r1UnitTest.t1.toDoubleOrNull()
    val ut2Score: Double? get() = r1UnitTest.t2.toDoubleOrNull()
    
    // In the webapp: Unit Test Ob.M is t1 + t2
    val utObtained: Double get() {
        val u1 = ut1Score ?: 0.0
        val u2 = ut2Score ?: 0.0
        return if (r1UnitTest.t1.isNotBlank() || r1UnitTest.t2.isNotBlank()) u1 + u2 else 0.0
    }

    val halfYearlyObtained: Double get() = r2HalfYearly.getSum()
    val annualObtained: Double get() = r3Annual.getSum()

    // Grand total for subject = Half Yearly + Annual (Max 200)
    val grandTotal: Double get() = halfYearlyObtained + annualObtained

    val percentage: Double get() = if (grandTotal > 0) (grandTotal / 200.0) * 100.0 else 0.0
}

data class StudentRecord(
    val id: Long = 0,
    val rollNo: Int,
    val name: String,
    val exam: String = "",
    val sr: String = "",
    val marks: Map<String, SubjectMarks> = defaultSubjectMarks()
) {
    val totalUt1: Double get() = marks.values.sumOf { it.ut1Score ?: 0.0 }
    val totalUt2: Double get() = marks.values.sumOf { it.ut2Score ?: 0.0 }
    val totalUtObtained: Double get() = marks.values.sumOf { it.utObtained }
    val totalHalfYearlyObtained: Double get() = marks.values.sumOf { it.halfYearlyObtained }
    val totalAnnualObtained: Double get() = marks.values.sumOf { it.annualObtained }
    val grandTotal: Double get() = totalHalfYearlyObtained + totalAnnualObtained
    val overallPercentage: Double get() = (grandTotal / 2000.0) * 100.0

    val weakSubjects: List<String> get() {
        return marks.values
            .filter { it.grandTotal > 0 && it.grandTotal < 80.0 }
            .map { it.subjectName }
    }

    fun computeSmartRemarks(): String {
        if (grandTotal == 0.0) {
            return "Record initialized. Regular attendance and focus required."
        }
        val baseRemark = when {
            overallPercentage >= 85.0 -> "Outstanding academic performance! Consistent dedication shown across all evaluations."
            overallPercentage >= 70.0 -> "Very good performance! Demonstrates strong understanding and active participation."
            overallPercentage >= 50.0 -> "Satisfactory progress. Capable of achieving higher scores with more regular revision."
            overallPercentage >= 33.0 -> "Needs improvement. Must focus on daily practice and concept revision."
            else -> "Needs immediate academic attention and remedial guidance."
        }
        val weak = weakSubjects
        return if (weak.isNotEmpty()) {
            "$baseRemark Special attention required in: ${weak.joinToString(", ")}."
        } else {
            baseRemark
        }
    }

    companion object {
        val ALL_SUBJECTS = listOf(
            "HINDI",
            "ENGLISH",
            "MATH",
            "SCIENCE",
            "SANSKRIT",
            "SOCIAL SCIENCE",
            "G.K.",
            "ART",
            "P.T.",
            "COMPUTER"
        )

        fun defaultSubjectMarks(): Map<String, SubjectMarks> {
            return ALL_SUBJECTS.associateWith { name ->
                SubjectMarks(subjectName = name)
            }
        }
    }
}

data class SchoolConfig(
    val schoolName: String = "SMT SDEI SECONDARY SCHOOL",
    val session: String = "2026-27",
    val classSec: String = "7th"
)
