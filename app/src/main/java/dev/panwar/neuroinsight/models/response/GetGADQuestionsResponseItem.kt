package dev.panwar.neuroinsight.models.response

data class GetGADQuestionsResponseItem(
    val id: Int,
    val options: List<String>,
    val question: String
)