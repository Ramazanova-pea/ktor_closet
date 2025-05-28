package look

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.html.insert
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import item.ItemsTable
import org.jetbrains.exposed.sql.SqlExpressionBuilder.inList
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID

fun Application.configureLookRoutes() {
    routing {
        post("/createLook") {
            val request = call.receive<CreateLookRequest>()

            val itemIds: List<String> = try {
                transaction {
                    val ids = ItemsTable
                        .selectAll()
                        .where { ItemsTable.name inList request.items }
                        .map { it[ItemsTable.idItem] }

                    if (ids.size != request.items.size) {
                        throw IllegalArgumentException("Some items not found by name")
                    }

                    ids
                }
            } catch (e: IllegalArgumentException) {
                return@post call.respond(HttpStatusCode.BadRequest, e.message ?: "Error")
            }

            val lookId = UUID.randomUUID().toString()

            transaction {
                // Создаем запись в таблице look
                LookTable.insert {
                    it[idLook] = lookId
                    it[name] = request.name
                    it[notes] = request.notes
                }

                // Вставляем элементы в itemInLook
                itemIds.forEach { itemId ->
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