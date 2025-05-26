package ru.fanofstars.login

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import ru.fanofstars.database.users.Users
import java.util.UUID

class LoginController(private val call: ApplicationCall) {
    suspend fun performLogin() {
        val receive = call.receive<LoginReceiveRemote>()
        val user = Users.fetchUser(receive.login)

        if (user == null) {
            call.respond(HttpStatusCode.BadRequest, "User not found")
            return
        }

        if (user.password != receive.password) {
            call.respond(HttpStatusCode.BadRequest, "Wrong password")
            return
        }

        val newToken = UUID.randomUUID().toString()
        Users.updateToken(receive.login, newToken)

        call.respond(LoginResponseRemote(token = newToken))
    }
}