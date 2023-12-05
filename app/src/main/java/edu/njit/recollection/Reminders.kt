package edu.njit.recollection

data class Reminders(
    val title: String?,
    val description: String?,
    val date: String?,
    val time: String?,  // New property for time
    var key: String? = "",
    var addToCal: Boolean? = false,
    var addToNotes: Boolean? = false
) : java.io.Serializable
