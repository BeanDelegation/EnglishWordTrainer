package com.example.englishwordtrainer.bot

import com.example.englishwordtrainer.model.Chat
import com.example.englishwordtrainer.model.TrainingSession
import com.example.englishwordtrainer.service.ChatService
import com.example.englishwordtrainer.utils.SessionState
import com.example.englishwordtrainer.utils.UserState
import org.telegram.abilitybots.api.sender.SilentSender
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove

class ResponseHandler(
    private val sender: SilentSender?, private val chatService: ChatService
) {

    private val chatStates: HashMap<Long, Chat> = HashMap()

    fun replyToStart(chatId: Long) {
        val message = SendMessage()
        message.setChatId(chatId)

        message.text =
            "Добро пожаловать. Этот бот предназначен для изучения иностранных языков." + "\nДля того, что бы начать практиковаться, давай создадим учетную запись." + "\nХватит всего ли твоего имени. Как вас зовут?"
        sender!!.execute(message)
        chatStates.put(chatId, Chat(chatId = chatId, userState = UserState.AWAITING_NAME))
        chatStates[chatId]!!.userState = UserState.AWAITING_NAME;
    }

    fun replyToButtons(chatId: Long, message: Message) {
        if (message.text.equals("/stop", ignoreCase = true)) {
            stopChat(chatId)
        }
        when (chatStates[chatId]!!.userState) {
            UserState.AWAITING_NAME -> replyToName(chatId, message)
            UserState.MAIN_MENU -> replyToMainMenu(chatId, message)
            UserState.LANGUAGE_SELECTION -> replyToLanguageSelectionSelection(chatId, message)
            UserState.AWAITING_START_TRAINING -> replyToTraining(chatId, message)
            UserState.LEARN_ENGLISH -> replyToLearnEnglish(chatId, message)
            UserState.DICTIONARY_MODE -> replyToAddWordToDictionary(chatId, message)
            UserState.AWAITING_ENGLISH_WORD -> replyToEnglishWord(chatId, message)
            UserState.AWAITING_RUSSIAN_WORD -> replyToRussianWord(chatId, message)
            else -> unexpectedMessage(chatId);
        }
    }

    private fun replyToName(chatId: Long, message: Message) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)
        sendMessage.text =
            "Отлично, " + message.text + ". " + "\nЭто главное меню, здесь ты можешь начать изучать слова или добавить слова в личный словарь слово."
        sendMessage.replyMarkup = KeyboardFactory().getMainMenuKeyBoard()
        sender!!.execute(sendMessage)

        chatStates[chatId]!!.userState = UserState.MAIN_MENU;
    }

    private fun replyToLanguageSelectionSelection(chatId: Long, message: Message) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)
        if ("germany".equals(message.text, ignoreCase = true)) {
            sendMessage.text = "Изучение немецкого языка временно недоступно"
            sendMessage.replyMarkup = KeyboardFactory().getLanguagesKeyboard()
            sender!!.execute(sendMessage)
        } else if ("english".equals(message.text, ignoreCase = true)) {
            sendMessage.text =
                "Отлично. Английский язык мы любим. Приступим к изучению. Вам будет приходить слово на русском языке." + " Ваша задача выбрать правильный вариант. Начинаем?"
            sendMessage.replyMarkup = KeyboardFactory().getYesOrNo()
            sender!!.execute(sendMessage)

            chatStates[chatId]!!.userState = UserState.AWAITING_START_TRAINING
        } else {
            sendMessage.text = ("Мы не поняли какой язык вы выбрали")
            sendMessage.replyMarkup = KeyboardFactory().getLanguagesKeyboard()
            sender!!.execute(sendMessage)
        }
    }

    private fun replyToTraining(chatId: Long, message: Message) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)
        if ("да".equals(message.text, ignoreCase = true)) {

            val currentSession = chatStates[chatId].let {
                if (it != null) {
                    chatService.createChatSession(it)
                } else {
                    throw RuntimeException("Не найдена сессия для $chatId")
                }
            }

            val wordContainers = chatService.getWords(currentSession.trainingSession!!.words)
            val wordContainer = wordContainers[currentSession.trainingSession!!.currentWordIndex]
            sendMessage.replyMarkup = KeyboardFactory().getWordsKeyboard(wordContainer.translationOptions)
            sendMessage.text =
                "Отлично, тогда начинаем. Твоя задача выбирать правильный перевод слова." + "Выбери перевод слова ${wordContainer.word}"
            sender!!.execute(sendMessage)
            chatStates[chatId]!!.userState = UserState.LEARN_ENGLISH
        } else if ("нет".equals(message.text, ignoreCase = true)) {
            sendMessage.text = "Жаль, возвращаем тебя в главное меню"
            sendMessage.replyMarkup = KeyboardFactory().getMainMenuKeyBoard()
            sender!!.execute(sendMessage)
            chatStates[chatId]!!.userState = UserState.MAIN_MENU
        } else {
            sendMessage.text = "Выбери да или нет"
            sendMessage.replyMarkup = KeyboardFactory().getYesOrNo()
            sender!!.execute(sendMessage)
        }
    }

    private fun replyToLearnEnglish(chatId: Long, message: Message) {

        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)

        chatStates[chatId].let {
            if (it != null) {
                val currentSession = chatService.checkAnswer(message.text, it)

                if (it.trainingSession!!.isCorrectAnswer) {

                    if (SessionState.COMPLETED == currentSession.trainingSession!!.stateSession) {
                        sendMessage.text = "Тренировка закончена. Попробуем еще раз?"

                        sendMessage.replyMarkup = KeyboardFactory().getYesOrNo()
                        sender!!.execute(sendMessage)

                        chatStates[chatId]!!.userState = UserState.MAIN_MENU
                        return
                    }

                    val wordContainers = chatService.getWords(currentSession.trainingSession!!.words)
                    val wordContainer = wordContainers[currentSession.trainingSession!!.currentWordIndex]

                    sendMessage.text = "Ответ правильный! Следующее слово ${wordContainer.word}"

                    sendMessage.replyMarkup = KeyboardFactory().getWordsKeyboard(wordContainer.translationOptions)
                    sender!!.execute(sendMessage)
                } else {

                    val wordContainers = chatService.getWords(currentSession.trainingSession!!.words)
                    val wordContainer = wordContainers[currentSession.trainingSession!!.currentWordIndex]

                    sendMessage.text = "Ответ не правильный! Попробуй еще раз. Слово ${wordContainer.word}"

                    sendMessage.replyMarkup = KeyboardFactory().getWordsKeyboard(wordContainer.translationOptions)
                    sender!!.execute(sendMessage)
                }
            }
        }
    }


    private fun replyToRussianWord(chatId: Long, message: Message) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)

        val word = message.text

        if (isRussianWord(word)) {
            sendMessage.text = "Отлично, слово добавлено в словарь."
            sendMessage.replyMarkup = KeyboardFactory().getMainMenuKeyBoard()
            sender!!.execute(sendMessage)

            chatStates[chatId]!!.userState = UserState.MAIN_MENU
        } else {
            sendMessage.text = "Это не русское слово. Введите русское слово"
            sender!!.execute(sendMessage)
        }
    }

    private fun replyToEnglishWord(chatId: Long, message: Message) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)

        val word = message.text

        if (isEnglishWord(word)) {
            sendMessage.text = "Отлично, теперь введи русский перевод слова $word"
            sender!!.execute(sendMessage)
            chatStates[chatId]!!.userState = UserState.AWAITING_RUSSIAN_WORD
        }
    }

    fun isEnglishWord(word: String): Boolean {
        val englishPattern = Regex("^[a-zA-Z]+$")
        return word.matches(englishPattern)
    }

    fun isRussianWord(word: String): Boolean {
        val russianPattern = Regex("^[а-яёА-ЯЁ]+$")
        return word.matches(russianPattern)
    }

    private fun replyToAddWordToDictionary(chatId: Long, message: Message) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)
        if ("Англо-Русский".equals(message.text, ignoreCase = true)) {
            sendMessage.text = "Введи слово по английски"
            sender!!.execute(sendMessage)
            chatStates[chatId]!!.userState = UserState.AWAITING_ENGLISH_WORD
        } else if ("Немецко-Русский".equals(message.text, ignoreCase = true)) {


        } else {
            sendMessage.text = "Мы не поняли вас."
            sender!!.execute(sendMessage)
        }
    }

    private fun unexpectedMessage(chatId: Long) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)
        sendMessage.text = "Мы не поняли вас."
        sender!!.execute(sendMessage)
    }

    private fun stopChat(chatId: Long) {
        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)
        chatStates.remove(chatId)
        sendMessage.replyMarkup = ReplyKeyboardRemove(true)
        sender!!.execute(sendMessage)
    }


    private fun replyToMainMenu(chatId: Long, message: Message) {

        val sendMessage = SendMessage()
        sendMessage.setChatId(chatId)
        if ("словарь".equals(message.text, ignoreCase = true)) {
            sendMessage.text = "Для того, что бы добавить слово в словарь, выбери тип словаря."
            sendMessage.replyMarkup = KeyboardFactory().getDictionariesKeyboard()
            sender!!.execute(sendMessage)
            chatStates[chatId]!!.userState = UserState.DICTIONARY_MODE
        } else if ("тренировка".equals(message.text, ignoreCase = true)) {
            sendMessage.text = "Выбери язык для изучения."
            sendMessage.replyMarkup = KeyboardFactory().getLanguagesKeyboard()
            sender!!.execute(sendMessage)
            chatStates[chatId]!!.userState = UserState.LANGUAGE_SELECTION

        } else {
            sendMessage.text = "Мы не поняли вас."
            sendMessage.replyMarkup = KeyboardFactory().getMainMenuKeyBoard()
            sender!!.execute(sendMessage)
        }
    }

    fun userIsActive(chatId: Long): Boolean {
        return chatStates.containsKey(chatId)
    }

}