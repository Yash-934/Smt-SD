package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.SchoolConfig

@Entity(tableName = "settings")
data class SettingsEntity(
    @PrimaryKey val id: Int = 1,
    val schoolName: String = "SMT SDEI SECONDARY SCHOOL",
    val session: String = "2026-27",
    val classSec: String = "7th"
) {
    fun toModel(): SchoolConfig {
        return SchoolConfig(
            schoolName = schoolName,
            session = session,
            classSec = classSec
        )
    }

    companion object {
        fun fromModel(model: SchoolConfig): SettingsEntity {
            return SettingsEntity(
                id = 1,
                schoolName = model.schoolName,
                session = model.session,
                classSec = model.classSec
            )
        }
    }
}
