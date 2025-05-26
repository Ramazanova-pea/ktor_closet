package ru.fanofstars

import io.ktor.server.application.*
import io.ktor.server.routing.routing
import kotlinx.io.files.SystemFileSystem
import org.jetbrains.exposed.sql.Database
import ru.fanofstars.cache.InMemoryCache
import ru.fanofstars.debug.configureDebugRoutes
import ru.fanofstars.login.configureLoginRouting
import ru.fanofstars.register.configureRegisterRouting
import tags.configureTagsRouting

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



    Database.connect(
        url = System.getenv("DB_URL"),
        user = System.getenv("DB_USER"),
        password = System.getenv("DB_PASSWORD")
    )
}
