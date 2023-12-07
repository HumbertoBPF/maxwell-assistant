package com.example.maxwell.utils.activities.base

open class StudyTests: UITests() {
    protected val studyDao = db.studyDao()
    protected val studySubjectDao = db.studySubjectDao()
}