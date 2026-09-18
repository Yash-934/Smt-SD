package com.example.data.local

import com.example.data.model.StudentRecord
import com.example.data.model.SubjectEvaluation
import com.example.data.model.SubjectMarks
import org.json.JSONArray
import org.json.JSONObject

object MarksJsonConverter {

    fun toJson(marks: Map<String, SubjectMarks>): String {
        val root = JSONObject()
        for ((subName, subMarks) in marks) {
            val subObj = JSONObject()

            val r1Arr = JSONArray().apply {
                put(subMarks.r1UnitTest.t1)
                put(subMarks.r1UnitTest.t2)
                put(subMarks.r1UnitTest.t3)
            }
            val r2Arr = JSONArray().apply {
                put(subMarks.r2HalfYearly.t1)
                put(subMarks.r2HalfYearly.t2)
                put(subMarks.r2HalfYearly.t3)
            }
            val r3Arr = JSONArray().apply {
                put(subMarks.r3Annual.t1)
                put(subMarks.r3Annual.t2)
                put(subMarks.r3Annual.t3)
            }

            subObj.put("r1", r1Arr)
            subObj.put("r2", r2Arr)
            subObj.put("r3", r3Arr)
            root.put(subName, subObj)
        }
        return root.toString()
    }

    fun fromJson(jsonStr: String): Map<String, SubjectMarks> {
        val result = mutableMapOf<String, SubjectMarks>()
        // Initialize default for all known subjects first
        for (subName in StudentRecord.ALL_SUBJECTS) {
            result[subName] = SubjectMarks(subjectName = subName)
        }

        if (jsonStr.isBlank()) return result

        try {
            val root = JSONObject(jsonStr)
            for (subName in StudentRecord.ALL_SUBJECTS) {
                if (root.has(subName)) {
                    val subObj = root.optJSONObject(subName)
                    if (subObj != null) {
                        val r1 = subObj.optJSONArray("r1")
                        val r2 = subObj.optJSONArray("r2")
                        val r3 = subObj.optJSONArray("r3")

                        result[subName] = SubjectMarks(
                            subjectName = subName,
                            r1UnitTest = SubjectEvaluation(
                                t1 = r1?.optString(0, "") ?: "",
                                t2 = r1?.optString(1, "") ?: "",
                                t3 = r1?.optString(2, "") ?: ""
                            ),
                            r2HalfYearly = SubjectEvaluation(
                                t1 = r2?.optString(0, "") ?: "",
                                t2 = r2?.optString(1, "") ?: "",
                                t3 = r2?.optString(2, "") ?: ""
                            ),
                            r3Annual = SubjectEvaluation(
                                t1 = r3?.optString(0, "") ?: "",
                                t2 = r3?.optString(1, "") ?: "",
                                t3 = r3?.optString(2, "") ?: ""
                            )
                        )
                    }
                }
            }
        } catch (_: Exception) {
            // Return defaults on parse error
        }
        return result
    }
}
