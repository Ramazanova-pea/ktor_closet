package ru.fanofstars.register

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.exceptions.ExposedSQLException
import ru.fanofstars.database.users.UserDTO
import ru.fanofstars.database.users.Users
import ru.fanofstars.utils.isValidEmail
import java.util.UUID

class RegisterController(val call: ApplicationCall) {
    suspend fun registerNewUser() {
        val registerReceiveRemote = call.receive<RegisterReceiveRemote>()
        if (!registerReceiveRemote.email.isValidEmail()) {
            call.respond(HttpStatusCode.BadRequest, "Email is not valid")
            return
        }
        if (registerReceiveRemote.login.isBlank() ||
            registerReceiveRemote.password.isBlank() ||
            registerReceiveRemote.username.isBlank()) {
            call.respond(HttpStatusCode.BadRequest, "All fields must be filled")
            return
        }

        val userDTO = Users.fetchUser(registerReceiveRemote.login)
        if (userDTO != null) {
            call.respond(HttpStatusCode.Conflict, "User already exists")
            return
        } else {
            val token = UUID.randomUUID().toString()
            try {
                Users.insert(
                    UserDTO(
                        id_user = UUID.randomUUID().toString(),
                        login = registerReceiveRemote.login,
                        password = registerReceiveRemote.password,
                        email = registerReceiveRemote.email,
                        username = registerReceiveRemote.username,
                        token = token
                    )
                )
                call.respond(RegisterResponseRemote(token = token))
            } catch (e: ExposedSQLException) {
                e.printStackTrace()
                call.respond(HttpStatusCode.Conflict, "Ошибка вставки: ${e.message}")
            }



        }
    }
}