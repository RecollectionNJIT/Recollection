package edu.njit.recollection
data class FinanceEntry(
    val date: String?,
    val monthDate: String?,
    val type: String?,
    val category: String?,
    val amount: Double?,
    var key: String? = "",
    var addToCalendar: Boolean? = false
) : java.io.Serializable