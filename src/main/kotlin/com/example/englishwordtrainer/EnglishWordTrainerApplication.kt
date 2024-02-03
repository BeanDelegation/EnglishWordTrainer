package com.example.englishwordtrainer

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@SpringBootApplication
@EntityScan("com.example.englishwordtrainer.model")
class EnglishWordTrainerApplication

fun main(args: Array<String>) {
    val context = SpringApplication.run(EnglishWordTrainerApplication::class.java, *args)

    try {
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        botsApi.registerBot(context.getBean("languageBot", AbilityBot::class.java))
    } catch (e: TelegramApiException) {
        throw RuntimeException(e)
    }
}