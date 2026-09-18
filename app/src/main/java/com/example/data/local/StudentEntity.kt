package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.StudentRecord

@Entity(tableName = "students")
data class StudentEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val rollNo: Int,
    val name: String,
    val exam: String = "",
    val sr: String = "",
    val marksJson: String = ""
) {
    fun toModel(): StudentRecord {
        return StudentRecord(
            id = id,
            rollNo = rollNo,
            name = name,
            exam = exam,
            sr = sr,
            marks = MarksJsonConverter.fromJson(marksJson)
        )
    }

    companion object {
        fun fromModel(model: StudentRecord): StudentEntity {
            return StudentEntity(
                id = model.id,
                rollNo = model.rollNo,
                name = model.name,
                exam = model.exam,
                sr = model.sr,
                marksJson = MarksJsonConverter.toJson(model.marks)
            )
        }
    }
}
