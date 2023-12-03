package edu.njit.recollection
data class CalendarEntry(
    val date: String?,
    val title: String?,
    val description: String?,
    var timeStart: String?,
    var timeEnd: String?,
    var key: String? = "",
    var addToReminders: Boolean? = false,
    var addToNotes: Boolean? = false,
    var addToFinances: Boolean? = false
) : java.io.Serializable