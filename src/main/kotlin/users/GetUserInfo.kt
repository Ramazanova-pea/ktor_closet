package users

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.fanofstars.database.users.Users

fun Application.configureUsersRouting() {
    routing {
        post("/getUserByToken") {
            val request = call.receive<Map<String, String>>()

            val token = request["token"]

            if (token.isNullOrBlank()) {
                call.respond(HttpStatusCode.BadRequest, "Token is missing")
                return@post
            }

            val selectStatement = Users.selectAll().where { Users.token eq token }

            val user = transaction {
                val selectStatement = Users.selectAll().where { Users.token eq token }

                selectStatement.map {
                    mapOf(
                        "id_user" to it[Users.id_user],
                        "login" to it[Users.login],
                        "username" to it[Users.username],
                        "email" to it[Users.email]
                    )
                }.singleOrNull()
            }

            if (user == null) {
                call.respond(HttpStatusCode.Unauthorized, "Invalid token")
            } else {
                call.respond(HttpStatusCode.OK, user)
            }
        }

    }
}