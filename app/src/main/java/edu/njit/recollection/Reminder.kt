package edu.njit.recollection

data class Reminder(
    val title: String?,
    val description: String?,
    val date: String?,
    val id: String?
) : java.io.Serializable
