package com.example.englishwordtrainer.model

import com.example.englishwordtrainer.utils.UserState

data class Chat(

    val chatId: Long,

    var userState: UserState,

    var trainingSession: List<TrainingSession>? = null,

    val personalDictionary: String = "",

    )