package com.example.maxwell.utils.activities.base

import com.example.maxwell.utils.UITests

open class StudyTests: UITests() {
    protected val studyDao = db.studyDao()
    protected val studySubjectDao = db.studySubjectDao()
}