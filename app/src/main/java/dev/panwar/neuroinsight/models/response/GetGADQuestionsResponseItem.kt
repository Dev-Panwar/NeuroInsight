package dev.panwar.neuroinsight.models.response

data class GetGADQuestionsResponseItem(
    val id: Int,
    val question: String,
    val options: String,
    val optionsList: List<String>
)