package ru.fanofstars.database.users

import java.util.UUID

class UserDTO(
    val id_user: String = UUID.randomUUID().toString(),
    val login: String,
    val password: String,
    val email: String,
    val username: String,
    val token: String? = null,
)