package look

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.insert
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import item.ItemsTable
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

fun Application.configureLookRoutes() {
    routing {
        post("/createLook") {
            val request = call.receive<CreateLookRequest>()


            // Генерируем id_look
            val lookId = UUID.randomUUID().toString()

            transaction {
                // Вставляем новый образ
                LookTable.insert {
                    it[idLook] = lookId
                    it[name] = request.name
                    it[notes] = request.notes
                }

                // Вставляем записи в itemInLook
                request.items.forEach { itemId ->
                    ItemInLookTable.insert {
                        it[idIil] = UUID.randomUUID().toString()
                        it[idLook] = lookId
                        it[idItem] = itemId
                    }
                }
            }

            call.respond(HttpStatusCode.Created, mapOf("id_look" to lookId))
        }

    }
}

object LookTable : Table("look") {
    val idLook = varchar("id_look", 50)
    val name = varchar("name", 25)
    val notes = text("notes").nullable()
}

object ItemInLookTable : Table("itemInLook") {
    val idIil = varchar("id_iil", 50)
    val idLook = varchar("id_look", 50).references(LookTable.idLook)
    val idItem = varchar("id_item", 50).references(ItemsTable.idItem)
}