package com.example.englishwordtrainer.service

import com.example.englishwordtrainer.dto.WordContainer
import com.example.englishwordtrainer.model.Chat
import com.example.englishwordtrainer.model.TrainingSession
import com.example.englishwordtrainer.utils.SessionState
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChatService {

    private val objectMapper: ObjectMapper = ObjectMapper()

    fun createChatSession(chat: Chat): Chat {

        val session = TrainingSession(
            currentWordIndex = 0,
            correctAnswers = 0,
            totalQuestions = 5,
            words = objectMapper.writeValueAsString(createWords()),
            stateSession = SessionState.IN_PROGRESS
        )

        chat.trainingSession = session

        return chat
    }

    fun getWords(jsonWords: String): List<WordContainer> {
        return objectMapper.readValue(jsonWords)
    }


    fun checkAnswer(answer: String, session: Chat): Chat {

        val wordContainers: List<WordContainer> = objectMapper.readValue(session.trainingSession!!.words)
        val wordContainer = wordContainers[session.trainingSession!!.currentWordIndex]

        if (answer == wordContainer.correctTranslation) {
            session.trainingSession!!.correctAnswers++
            session.trainingSession!!.currentWordIndex++
            session.trainingSession!!.isCorrectAnswer = true
            if (session.trainingSession!!.currentWordIndex >= session.trainingSession!!.totalQuestions) {
                session.trainingSession!!.stateSession = SessionState.COMPLETED
            }
        } else {
            session.trainingSession!!.isCorrectAnswer = false
        }

        return session
    }


    private fun createWords(): List<WordContainer> {
        val wordContainer1 = WordContainer(
            "кошка",
            listOf("cat", "dog", "elephant", "rabbit"),
            "cat"
        )

        val wordContainer2 = WordContainer(
            "дом",
            listOf("glass", "table", "castle", "home"), "home"
        )

        val wordContainer3 = WordContainer(
            "свинья",
            listOf("tiger", "pork", "pig", "mouse"), "pig"
        )

        val wordContainer4 = WordContainer(
            "вилка",
            listOf("spoon", "fork", "frog", "sunglasses"), "fork"
        )

        val wordContainer5 = WordContainer(
            "солнцезащитные очки",
            listOf("sunglasses", "cap", "jacket", "boots"), "sunglasses"
        )

        return listOf(wordContainer1, wordContainer2, wordContainer3, wordContainer4, wordContainer5)
    }


}