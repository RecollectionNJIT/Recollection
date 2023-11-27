package edu.njit.recollection
data class FinanceEntry(
    val date: String?,
    val monthDate: String?,
    val type: String?,
    val category: String?,
    val amount: Double?,
    val key: String? = ""
) : java.io.Serializable