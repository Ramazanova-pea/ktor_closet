package ru.fanofstars.debug

import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.fanofstars.cache.InMemoryCache

fun Route.configureDebugRoutes() {

    get("/debug/users") {
        val users = transaction {
            Users.selectAll().map {
                UserDto(
                    id = it[Users.id],
                    name = it[Users.name]
                )
            }
        }
        call.respond(users)
    }

}

object Users : Table() {
    val id = integer("id").autoIncrement()
    val name = varchar("name", 255)
    override val primaryKey = PrimaryKey(id)
}

data class UserDto(val id: Int, val name: String)

