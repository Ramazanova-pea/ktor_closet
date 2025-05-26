package ru.fanofstars.register

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.fanofstars.cache.InMemoryCache
import ru.fanofstars.cache.TokenCache
import ru.fanofstars.utils.isValidEmail
import java.util.UUID

fun Application.configureRegisterRouting() {

    routing {
        post("/register") {
            val registerController = RegisterController(call)
            registerController.registerNewUser()
        }
    }


}

