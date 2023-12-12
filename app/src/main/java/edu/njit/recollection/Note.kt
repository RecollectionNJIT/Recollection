package edu.njit.recollection

data class Note (
    val title: String?,
    val body: String?,
    val imageLocation: String?,
    var key: String? = "",
    var addToReminders: Boolean? = false,
    var addToCal: Boolean? = false
) : java.io.Serializable
