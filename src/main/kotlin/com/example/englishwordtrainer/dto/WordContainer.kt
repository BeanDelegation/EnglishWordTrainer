package com.example.englishwordtrainer.model

data class WordContainer(
    val word: String,
    val translationOptions: List<String>,
    val correctTranslation: String
)