package ru.fanofstars

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database
import ru.fanofstars.debug.configureDebugRoutes
import ru.fanofstars.login.configureLoginRouting
import ru.fanofstars.register.configureRegisterRouting

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)
}

fun Application.module() {
    configureSerialization()
    configureDatabases()
    configureTemplating()
    configureHTTP()
    configureSecurity()
    configureRouting()

    configureRegisterRouting()
    configureDebugRoutes()
    configureLoginRouting()

    Database.connect(
        url = "jdbc:postgresql://localhost:5432/closetdb",
        driver = "org.postgresql.Driver",
        user = "myuser",
        password = "mypassword"
    )
}
