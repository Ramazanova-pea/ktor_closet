package ru.fanofstars.debug

import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import ru.fanofstars.cache.InMemoryCache

fun Application.configureDebugRoutes() {
    routing {
        get("/debug/users") {
            call.respond(InMemoryCache.userList)
        }
    }
}
