package ru.fanofstars.database.users



import org.jetbrains.annotations.Debug
import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Op
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update

object Users: Table("users") {
    val id_user = varchar("id_user", 50)
    val login = varchar("login", 25)
    val password = varchar("password", 25)
    val username =  varchar("username", 30)
    val email = varchar("email", 30)
    val token = varchar("token", 50).nullable() // новое поле

    override val primaryKey = PrimaryKey(id_user)

    fun insert(userDTO: UserDTO){
        transaction {
            insert{
                it[id_user] = userDTO.id_user
                it[login] = userDTO.login
                it[password] = userDTO.password
                it[username] = userDTO.username
                it[email] = userDTO.email
                it[token] = userDTO.token
            }
        }
    }

    fun updateToken(loginValue: String, newToken: String) {
        transaction {
            Users.update({ login eq loginValue }) {
                it[token] = newToken
            }
        }
    }

    fun fetchUser(login: String): UserDTO? {
        return try {
            transaction {
                addLogger(StdOutSqlLogger) // Добавляем логгер ВНУТРИ транзакции

                // Используем selectAll() и затем фильтруем с помощью where
                val selectStatement = Users.selectAll().where { Users.login eq login }
                println("SQL Query: ${selectStatement.prepareSQL(this)}") // Логируем SQL запрос

                val result = selectStatement.singleOrNull()
                println("Result: $result") // Логируем результат

                result?.let { row ->
                    UserDTO(
                        id_user = row[Users.id_user],
                        login = row[Users.login],
                        password = row[Users.password],
                        username = row[Users.username],
                        email = row[Users.email],
                        token = row[Users.token] // Убедитесь, что Users.token существует в вашем объекте Table
                    )
                }
            }
        } catch (e: Exception) {
            println("Error fetching user: ${e.message}") // Логируем ошибку
            null
        }
    }
}