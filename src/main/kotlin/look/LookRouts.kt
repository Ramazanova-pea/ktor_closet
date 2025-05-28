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
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.stringLiteral
import org.jetbrains.exposed.sql.transactions.transaction
import ru.fanofstars.database.users.Users
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

        post("/getLooks") {
            val request = call.receive<Map<String, String>>()
            val token = request["token"]

            if (token.isNullOrBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, "Token is missing")
            }

            val looks = transaction {
                val userId = Users
                    .selectAll()
                    .where { Users.token eq token }
                    .map { it[Users.id_user] }
                    .singleOrNull()

                if (userId == null) {
                    return@transaction null
                }

                // Получаем все луки
                LookTable.selectAll()
                    .map { lookRow ->
                        val lookId = lookRow[LookTable.idLook]

                        // Находим id_item для этого id_look
                        val itemIds = ItemInLookTable
                            .selectAll()
                            .where { ItemInLookTable.idLook eq lookId }
                            .map { it[ItemInLookTable.idItem] }

                        // Получаем подробную информацию об этих items
                        val items = ItemsTable
                            .selectAll()
                            .where { (ItemsTable.idItem inList itemIds) and (ItemsTable.idUser eq userId) }
                            .map { itemRow ->
                                mapOf(
                                    "id_item" to itemRow[ItemsTable.idItem],
                                    "name" to itemRow[ItemsTable.name],
                                    "notes" to itemRow[ItemsTable.notes],
                                    "imagePath" to itemRow[ItemsTable.picture_path]
                                )
                            }

                        mapOf(
                            "id_look" to lookId,
                            "name" to lookRow[LookTable.name],
                            "notes" to lookRow[LookTable.notes],
                            "items" to items
                        )
                    }
            }

            if (looks == null) {
                return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")
            }

            call.respond(HttpStatusCode.OK, looks)
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