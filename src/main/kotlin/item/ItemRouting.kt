package item

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import ru.fanofstars.database.users.Users
import tags.TagsTable
import java.util.UUID

fun Application.configureItemRouting() {
    routing {
        post("/createItem") {
            val request = call.receive<CreateItemRequest>()
            val token = request.token

            if (token.isNullOrBlank()) {
                return@post call.respond(HttpStatusCode.BadRequest, "Token is missing")
            }

            val userId = transaction {
                Users.selectAll().where { Users.token eq token }
                    .map { it[Users.id_user] }
                    .singleOrNull()
            }

            if (userId == null) {
                return@post call.respond(HttpStatusCode.Unauthorized, "Invalid token")
            }

            val itemId = UUID.randomUUID().toString()

            transaction {
                // Создаем запись в item
                ItemsTable.insert {
                    it[idItem] = itemId
                    it[picture_path] = request.imagePath // или путь, если это поле текстовое
                    it[name] = request.name
                    it[notes] = request.notes
                    it[idUser] = userId
                }

                // Обновляем тэги (находим их id)
                val allTags = TagsTable.selectAll()
                    .map { it[TagsTable.name] to it[TagsTable.idTags] }
                    .toMap()

                val tagIds = request.tags.mapNotNull { tagName ->
                    allTags[tagName]
                }

                // Записываем в item_tags
                tagIds.forEach { tagId ->
                    ItemTagsTable.insert {
                        it[idItem] = itemId
                        it[idTags] = tagId
                    }
                }
            }

            call.respond(HttpStatusCode.Created, mapOf("id_item" to itemId))
        }

    }
}

object ItemsTable : Table("item") {
    val idItem = varchar("id_item", 50)
    val picture_path = varchar("picture_path", 255) // если путь
    val name = varchar("name", 25)
    val notes = text("notes").nullable()
    val idUser = varchar("id_user", 50).references(Users.id_user)
    override val primaryKey = PrimaryKey(idItem)
}

object ItemTagsTable : Table("item_tags") {
    val idItem = varchar("id_item", 50).references(ItemsTable.idItem)
    val idTags = varchar("id_tags", 50).references(TagsTable.idTags)
    override val primaryKey = PrimaryKey(idItem, idTags)
}