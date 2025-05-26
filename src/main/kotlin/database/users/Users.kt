package ru.fanofstars.database.users



import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Users: Table("users") {
    val id_user = varchar("id_user", 50)
    val login = varchar("login", 25)
    val password = varchar("password", 25)
    val username =  varchar("username", 30)
    val email = varchar("email", 30)

    override val primaryKey = PrimaryKey(id_user)

    fun insert(userDTO: UserDTO){
        transaction {
            insert{
                it[id_user] = userDTO.id_user
                it[login] = userDTO.login
                it[password] = userDTO.password
                it[username] = userDTO.username
                it[email] = userDTO.email ?: ""
            }
        }
    }

    fun fetchUser(login: String): UserDTO? {
        return try{
            transaction {
                Users.selectAll().where { Users.login eq login }.singleOrNull()?.let{ row ->
                    UserDTO(
                        id_user = row[Users.id_user],
                        login = row[Users.login],
                        password = row[password],
                        username = row[username],
                        email = row[email]
                    )
                }
            }
        } catch (e: Exception) {
            null
        }
    }
}