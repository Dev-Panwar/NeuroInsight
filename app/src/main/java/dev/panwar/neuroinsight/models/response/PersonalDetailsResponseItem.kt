package dev.panwar.neuroinsight.models.response

data class PersonalDetailsResponseItem(
    val age: String,
    val covidInfected: String,
    val depressionHistory: String,
    val duringLockdownStayingAt: String,
    val education: String,
    val gender: String,
    val id: Int,
    val job: String,
    val jobNature: String,
    val jobStatusDuringLockdown: String,
    val knownCovidInfected: String,
    val longIllness: String,
    val maritalStatus: String,
    val monthlyIncome: Int,
    val practiceYoga: String,
    val state: String,
    val userId: Int
)