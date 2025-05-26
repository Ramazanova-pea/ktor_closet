package ru.fanofstars.debug

import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDebugRoutes() {

    routing {
        get("/debug/users") {
            val users = transaction {
                Users.selectAll().map {
                    UserDto(
                        id = it[Users.id_user],
                        name = it[Users.name]
                    )
                }
            }
            call.respond(users)
        }
    }


}

object Users : Table() {
    val id_user = integer("id_user").autoIncrement()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id_user)
}

data class UserDto(val id: Int, val name: String)

