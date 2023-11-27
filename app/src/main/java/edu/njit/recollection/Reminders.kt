package edu.njit.recollection

data class Reminders(
    val title: String?,
    val description: String?,
    val date: String?,
    val time: String?,  // New property for time
    val id: String?
) : java.io.Serializable
