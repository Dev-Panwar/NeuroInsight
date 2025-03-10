package dev.panwar.neuroinsight.models.request

data class PersonalDetailsRequest(
    val age: String,
    val covidInfected: String,
    val depressionHistory: String,
    val duringLockdownStayingAt: String,
    val education: String,
    val gender: String,
    val job: String,
    val jobNature: String,
    val jobStatusDuringLockdown: String,
    val knownCovidInfected: String,
    val longIllness: String,
    val maritalStatus: String,
    val monthlyIncome: Int,
    val practiceYoga: String,
    val state: String
)