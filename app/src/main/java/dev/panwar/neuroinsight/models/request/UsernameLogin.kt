package dev.panwar.neuroinsight.models.request

data class UsernameLogin(
    val password: String?,
    val email:String?,
    val userName: String?
)