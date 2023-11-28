package edu.njit.recollection

data class Note (
    val title: String?,
    val body: String?,
    val imageLocation: String?,
    val key: String? = ""
) : java.io.Serializable
