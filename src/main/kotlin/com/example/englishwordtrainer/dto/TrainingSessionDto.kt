package com.example.englishwordtrainer.dto

import com.example.englishwordtrainer.utils.SessionState

data class TrainingSessionDto (
    val sessionId: String,
    val userId: Long,
    var currentWordIndex: Int,
    var correctAnswers: Int,
    val totalQuestions: Int,
    var isCorrectAnswer: Boolean = false,
    val words: List<WordContainer>,
    var stateSession: SessionState
)