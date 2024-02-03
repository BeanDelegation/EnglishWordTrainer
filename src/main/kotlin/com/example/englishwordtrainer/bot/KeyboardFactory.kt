package com.example.englishwordtrainer.bot

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

class KeyboardFactory {

    private val languages = listOf("English", "Germany")
    private val mainMenu = listOf("Словарь", "Тренировка")
    private val dictionaries = listOf("Англо-Русский", "Немецко-Русский")

    fun getLanguagesKeyboard(): ReplyKeyboard {
        val row = KeyboardRow()

        languages.forEach {
            row.add(it)
        }

        return ReplyKeyboardMarkup(listOf(row))
    }

    fun getMainMenuKeyBoard(): ReplyKeyboard {
        val row = KeyboardRow()

        mainMenu.forEach {
            row.add(it)
        }

        return ReplyKeyboardMarkup(listOf(row))
    }

    fun getWordsKeyboard(words: List<String>): ReplyKeyboard {
        val row = KeyboardRow()

        words.forEach {
            row.add(it)
        }

        return ReplyKeyboardMarkup(listOf(row))
    }

    fun getDictionariesKeyboard(): ReplyKeyboard {
        val row = KeyboardRow()

        dictionaries.forEach {
            row.add(it)
        }

        return ReplyKeyboardMarkup(listOf(row))
    }

    fun getYesOrNo(): ReplyKeyboard {
        val row = KeyboardRow()
        row.add("Да")
        row.add("Нет")
        return ReplyKeyboardMarkup(listOf(row))
    }

}