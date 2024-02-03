package com.example.englishwordtrainer.service

class TrainingSessionService (
) {
/*
    private val objectMapper: ObjectMapper = ObjectMapper()

    fun checkAnswer(chat: Chat, chatId: Long, answer: String): TrainingSession {

        val session =
            Optional.ofNullable(trainingSessionRepository.findBySessionIdAndStateSession(chatId, SessionState.IN_PROGRESS))
                .orElseThrow { RuntimeException("Не удалось найти сессию по chatId $chat.id в статусе SessionState.IN_PROGRESS") }

        var word: WordContainer? = null
        try {
            val wordContainers: List<WordContainer> = objectMapper.readValue(session.words)
            word = wordContainers[session.currentWordIndex]
        } catch (e: Exception) {
            e.printStackTrace()
        }

        if (answer == word?.correctTranslation) {
            println("Ответ $answer правильный")

            session.correctAnswers++
            session.currentWordIndex++
            session.isCorrectAnswer = true
            if (session.currentWordIndex >= session.totalQuestions) {
                session.stateSession = SessionState.COMPLETED

                chatService.updateChatUserState(chatId, UserState.AWAITING_START_TRAINING)
            }
        } else {
            session.isCorrectAnswer = false
            println("Ответ $answer не правильный")
        }

        return trainingSessionRepository.save(session)

    }


    fun getListWordContainer(session: TrainingSession): List<WordContainer> {
        var wordContainers: List<WordContainer> = listOf()
        try {
            wordContainers = objectMapper.readValue(session.words)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return wordContainers
    }*/

}