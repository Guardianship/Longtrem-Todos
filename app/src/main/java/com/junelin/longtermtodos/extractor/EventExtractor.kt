package com.junelin.longtermtodos.extractor

import com.junelin.longtermtodos.data.local.entity.EventSource
import com.junelin.longtermtodos.data.model.ExtractedEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.regex.Pattern

object EventExtractor {

    private val datePatterns = listOf(
        // 2026年5月20日 / 2026-05-20 / 2026/05/20
        Pattern.compile("(\\d{4})[年/-](\\d{1,2})[月/-](\\d{1,2})[日]?"),
        // 5月20日 / 05-20
        Pattern.compile("(\\d{1,2})[月/-](\\d{1,2})[日]?"),
        // 5.20
        Pattern.compile("(\\d{1,2})\\.(\\d{1,2})")
    )

    private val keywords = listOf(
        "年检", "保险", "生日", "续费", "到期", "预约", "缴费",
        "过户", "换证", "体检", "还款", "缴纳", "截止", "期限"
    )

    private val titlePatterns = listOf(
        Pattern.compile("(.{2,20}?(?:年检|保险|生日|续费|到期|预约|缴费|过户|换证|体检|还款|缴纳))"),
        Pattern.compile("(?:您的|你的|尊敬的用户)(.{2,15}?)(?:即将|已经|将于|将在)"),
        Pattern.compile("(.{2,15}?)(?:将于|将在|已经|即将)(\\d{1,2}月\\d{1,2}日)")
    )

    suspend fun extractFromSms(text: String): List<ExtractedEvent> = withContext(Dispatchers.Default) {
        extractEvents(text, EventSource.SMS)
    }

    suspend fun extractFromNotification(title: String, text: String): List<ExtractedEvent> = withContext(Dispatchers.Default) {
        val combined = "$title $text"
        extractEvents(combined, EventSource.WECHAT)
    }

    private fun extractEvents(text: String, source: EventSource): List<ExtractedEvent> {
        if (!containsKeyword(text)) return emptyList()

        val dates = extractDates(text)
        if (dates.isEmpty()) return emptyList()

        val title = extractTitle(text) ?: text.take(30)
        val inferredCategory = CategoryInference.infer(title, text)

        return dates.map { date ->
            ExtractedEvent(
                rawText = text,
                extractedTitle = title,
                extractedDate = date,
                inferredCategoryId = inferredCategory,
                source = source
            )
        }
    }

    private fun containsKeyword(text: String): Boolean {
        return keywords.any { text.contains(it) }
    }

    private fun extractDates(text: String): List<LocalDate> {
        val results = mutableListOf<LocalDate>()
        val currentYear = LocalDate.now().year

        for (pattern in datePatterns) {
            val matcher = pattern.matcher(text)
            while (matcher.find()) {
                try {
                    val date = when (matcher.groupCount()) {
                        3 -> {
                            val year = matcher.group(1)!!.toInt()
                            val month = matcher.group(2)!!.toInt()
                            val day = matcher.group(3)!!.toInt()
                            LocalDate.of(year, month, day)
                        }
                        2 -> {
                            val month = matcher.group(1)!!.toInt()
                            val day = matcher.group(2)!!.toInt()
                            LocalDate.of(currentYear, month, day)
                        }
                        else -> null
                    }
                    date?.let { results.add(it) }
                } catch (_: Exception) {
                    // Invalid date
                }
            }
        }

        return results.distinct()
    }

    private fun extractTitle(text: String): String? {
        for (pattern in titlePatterns) {
            val matcher = pattern.matcher(text)
            if (matcher.find()) {
                return matcher.group(1)?.trim()?.take(50)
            }
        }
        return text.take(30)
    }
}
