package dev.panwar.neuroinsight.models.response

data class LoginResponse(
    val User: User,
    val jwt: String
)