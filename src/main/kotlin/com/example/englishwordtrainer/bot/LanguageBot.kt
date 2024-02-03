package com.example.englishwordtrainer.bot

import com.example.englishwordtrainer.service.ChatService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component
import org.telegram.abilitybots.api.bot.AbilityBot
import org.telegram.abilitybots.api.bot.BaseAbilityBot
import org.telegram.abilitybots.api.objects.*
import org.telegram.abilitybots.api.util.AbilityUtils.getChatId
import org.telegram.telegrambots.meta.api.objects.Update
import java.util.function.BiConsumer
import java.util.function.Predicate

@Component
class PizzaBot(
    environment: Environment,
    @Autowired chatService: ChatService,
) : AbilityBot(environment.getProperty("BOT_TOKEN"), "learnEnglishBot") {

    private var responseHandler: ResponseHandler? = null

    init {
        responseHandler = ResponseHandler(
            silent,
            chatService = chatService
        )
    }

    fun startBot(): Ability {
        return Ability
            .builder()
            .name("hello")
            .info("Hello!")
            .locality(Locality.USER)
            .privacy(Privacy.PUBLIC)
            .action { ctx: MessageContext -> responseHandler!!.replyToStart(ctx.chatId()) }
            .build()
    }

    fun replyToButtons(): Reply {
        val action: BiConsumer<BaseAbilityBot, Update> = BiConsumer<BaseAbilityBot, Update> { abilityBot, upd ->
            responseHandler!!.replyToButtons(
                getChatId(upd),
                upd.message
            )
        }
        return Reply.of(action, Flag.TEXT,
            Predicate<Update> { upd: Update? ->
                responseHandler!!.userIsActive(
                    getChatId(upd)
                )
            })
    }

    override fun creatorId(): Long {
        return 1L
    }

}