package com.example.englishwordtrainer.model

import com.example.englishwordtrainer.utils.SessionState

data class TrainingSessionDto (

    val sessionId: String,
    var currentWordIndex: Int,
    var correctAnswers: Int,
    val totalQuestions: Int,
    var isCorrectAnswer: Boolean = false,
    val words: List<WordContainer>,
    var stateSession: SessionState
)