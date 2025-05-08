package ru.fanofstars.register

import kotlinx.serialization.Serializable

@Serializable
data class RegisterReceiveRemote(
    val login: String,
    val email: String,
    val password: String,
    val username: String,
)

@Serializable
data class RegisterResponseRemote(
    val token: String,
)