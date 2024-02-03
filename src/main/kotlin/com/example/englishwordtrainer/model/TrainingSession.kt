package com.example.englishwordtrainer.model

import com.example.englishwordtrainer.utils.SessionState

data class TrainingSession(
    var currentWordIndex: Int,
    var correctAnswers: Int,
    val totalQuestions: Int,
    var isCorrectAnswer: Boolean = false,
    val words: String,
    var stateSession: SessionState
)
