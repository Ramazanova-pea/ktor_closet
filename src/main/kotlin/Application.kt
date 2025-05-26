package ru.fanofstars

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import ru.fanofstars.debug.configureDebugRoutes
import ru.fanofstars.login.configureLoginRouting
import ru.fanofstars.register.configureRegisterRouting
import tags.configureTagsRouting
import users.configureUsersRouting

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {

    configureSerialization()
    configureTemplating()
    configureHTTP()
    configureSecurity()
    configureRouting()


    configureRegisterRouting()
    configureDebugRoutes()
    configureLoginRouting()
    configureTagsRouting()
    configureUsersRouting()



    Database.connect(
        url = System.getenv("DB_URL"),
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD")
    )
    transaction {
        addLogger(StdOutSqlLogger)
    }

}
