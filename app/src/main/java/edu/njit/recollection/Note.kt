package edu.njit.recollection

data class Note (
    var title: String?,
    var body: String?,
    var imageLocation: String?,
    var key: String? = "",
    var addToReminders: Boolean? = false,
    var addToCal: Boolean? = false
) : java.io.Serializable
