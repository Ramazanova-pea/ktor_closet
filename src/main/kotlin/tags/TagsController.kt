package tags

import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction
import java.util.UUID


class TagsController(val call: ApplicationCall) {
    suspend fun createNewTag() {
        val tagRequest = call.receive<Tags>()

        val condition = Op.build { TagsTable.name eq tagRequest.name }
        val tagExists = transaction {
            TagsTable.select(condition).count() > 0
        }

        if (tagExists) {
            call.respond(HttpStatusCode.Conflict, "Tag with this name already exists.")
            return
        }

        val newId = UUID.randomUUID().toString()

        transaction {
            TagsTable.insert {
                it[idTags] = newId
                it[name] = tagRequest.name
            }
        }

        call.respond(HttpStatusCode.Created, Tags(id_tags = newId, name = tagRequest.name))
    }

    suspend fun getAllTags() {
        val tags = transaction {
            TagsTable.selectAll().map {
                Tags(
                    id_tags = it[TagsTable.idTags],
                    name = it[TagsTable.name]
                )
            }
        }
        call.respond(HttpStatusCode.OK, tags)
    }
}

object TagsTable : Table("tags") {
    val idTags = varchar("id_tags", 50)
    val name = varchar("name", 25).uniqueIndex()
    override val primaryKey = PrimaryKey(idTags)
}