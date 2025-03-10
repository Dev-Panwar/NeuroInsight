package dev.panwar.neuroinsight.models.response

data class User(
    val createdAt: String,
    val email: String,
    val fullName: String,
    val id: Int,
    val password: String,
    val roles: List<String>,
    val updatedAt: String,
    val userName: String
)