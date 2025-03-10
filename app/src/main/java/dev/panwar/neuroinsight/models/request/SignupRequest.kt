package dev.panwar.neuroinsight.models.request

data class SignupRequest(
    val email: String,
    val fullName: String,
    val password: String,
    val userName: String
)
