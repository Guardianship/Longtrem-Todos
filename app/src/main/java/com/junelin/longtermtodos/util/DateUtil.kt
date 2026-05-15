package com.junelin.longtermtodos.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

object DateUtil {

    fun formatDate(date: LocalDate): String {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
    }

    fun daysUntil(date: LocalDate): Long {
        return ChronoUnit.DAYS.between(LocalDate.now(), date)
    }

    fun isOverdue(date: LocalDate): Boolean {
        return daysUntil(date) < 0
    }
}
