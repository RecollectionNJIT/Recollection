package edu.njit.recollection
data class CalendarEntry(
    val date: String?,
    val title: String?,
    val description: String?,
    val timeStart: String?,
    val timeEnd: String?,
    val key: String? = ""
) : java.io.Serializable