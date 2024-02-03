package com.example.englishwordtrainer.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

data class WordContainer @JsonCreator constructor(
    @JsonProperty("word")
    val word: String,
    @JsonProperty("translationOptions")
    val translationOptions: List<String>,
    @JsonProperty("correctTranslation")
    val correctTranslation: String
)