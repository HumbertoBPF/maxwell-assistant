package com.example.maxwell.models

import androidx.room.Embedded
import androidx.room.Relation

data class StudySubjectWithStudies (
    @Embedded val studySubject: StudySubject,
    @Relation(
        parentColumn = "id",
        entityColumn = "subjectId"
    )
    val studies: List<Study>
)