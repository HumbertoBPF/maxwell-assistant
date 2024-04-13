package com.example.maxwell.models

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(indices = [Index(value = ["name"], unique = true)])
data class StudySubject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0L,
    val name: String
): BaseEntity()