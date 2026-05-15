package com.junelin.longtermtodos.util

import java.time.LocalDate

/**
 * 农历转换工具（1900年 ~ 2100年）
 */
object LunarCalendar {

    // 农历数据表：每年用 20 位整数编码
    // 位 0-11:  每月大小（1=大月30天，0=小月29天），从正月到腊月
    // 位 12-15: 闰月月份（0 表示无闰月）
    // 位 16-19: 闰月大小（1=大月，0=小月）
    private val LUNAR_INFO = intArrayOf(
        0x04bd8,0x04ae0,0x0a570,0x054d5,0x0d260,0x0d950,0x16554,0x056a0,0x09ad0,0x055d2,
        0x04ae0,0x0a5b6,0x0a4d0,0x0d250,0x1d255,0x0b540,0x0d6a0,0x0ada2,0x095b0,0x14977,
        0x04970,0x0a4b0,0x0b4b5,0x06a50,0x06d40,0x1ab54,0x02b60,0x09570,0x052f2,0x04970,
        0x06566,0x0d4a0,0x0ea50,0x06e95,0x05ad0,0x02b60,0x186e3,0x092e0,0x1c8d7,0x0c950,
        0x0d4a0,0x1d8a6,0x0b550,0x056a0,0x1a5b4,0x025d0,0x092d0,0x0d2b2,0x0a950,0x0b557,
        0x06ca0,0x0b550,0x15355,0x04da0,0x0a5d0,0x14573,0x052d0,0x0a9a8,0x0e950,0x06aa0,
        0x0aea6,0x0ab50,0x04b60,0x0aae4,0x0a570,0x05260,0x0f263,0x0d950,0x05b57,0x056a0,
        0x096d0,0x04dd5,0x04ad0,0x0a4d0,0x0d4d4,0x0d250,0x0d558,0x0b540,0x0b5a0,0x195a6,
        0x095b0,0x049b0,0x0a974,0x0a4b0,0x0b27a,0x06a50,0x06d40,0x0af46,0x0ab60,0x09570,
        0x04af5,0x04970,0x064b0,0x074a3,0x0ea50,0x06b58,0x055c0,0x0ab60,0x096d5,0x092e0,
        0x0c960,0x0d954,0x0d4a0,0x0da50,0x07552,0x056a0,0x0abb7,0x025d0,0x092d0,0x0cab5,
        0x0a950,0x0b4a0,0x0baa4,0x0ad50,0x055d9,0x04ba0,0x0a5b0,0x15176,0x052b0,0x0a930,
        0x07954,0x06aa0,0x0ad50,0x05b52,0x04b60,0x0a6e6,0x0a4e0,0x0d260,0x0ea65,0x0d530,
        0x05aa0,0x076a3,0x096d0,0x04bd7,0x04ad0,0x0a4d0,0x1d0b6,0x0d250,0x0d520,0x0dd45,
        0x0b5a0,0x056d0,0x055b2,0x049b0,0x0a577,0x0a4b0,0x0aa50,0x1b255,0x06d20,0x0ada0,
        0x14b63 // 2100
    )

    private val TIAN_GAN = arrayOf("甲","乙","丙","丁","戊","己","庚","辛","壬","癸")
    private val DI_ZHI = arrayOf("子","丑","寅","卯","辰","巳","午","未","申","酉","戌","亥")
    private val ZODIAC = arrayOf("鼠","牛","虎","兔","龙","蛇","马","羊","猴","鸡","狗","猪")
    private val LUNAR_MONTHS = arrayOf("正","二","三","四","五","六","七","八","九","十","冬","腊")
    private val LUNAR_DAYS = arrayOf(
        "初一","初二","初三","初四","初五","初六","初七","初八","初九","初十",
        "十一","十二","十三","十四","十五","十六","十七","十八","十九","二十",
        "廿一","廿二","廿三","廿四","廿五","廿六","廿七","廿八","廿九","三十"
    )

    private const val BASE_YEAR = 1900
    private val baseDate = LocalDate.of(1900, 1, 31) // 1900年正月初一

    data class LunarDate(
        val year: Int,
        val month: Int,      // 1-12
        val day: Int,        // 1-30
        val isLeap: Boolean, // 是否闰月
        val zodiac: String = ZODIAC[(year - 4) % 12]
    ) {
        val yearGanZhi: String
            get() = TIAN_GAN[(year - 4) % 10] + DI_ZHI[(year - 4) % 12]

        fun format(): String {
            val leapStr = if (isLeap) "闰" else ""
            return "农历${yearGanZhi}${zodiac}年 ${leapStr}${LUNAR_MONTHS[month - 1]}月${LUNAR_DAYS[day - 1]}"
        }
    }

    /** 公历 → 农历 */
    fun solarToLunar(date: LocalDate): LunarDate {
        var offset = java.time.temporal.ChronoUnit.DAYS.between(baseDate, date).toInt()

        var year = BASE_YEAR
        var daysOfYear: Int

        while (true) {
            daysOfYear = lunarYearDays(year)
            if (offset < daysOfYear) break
            offset -= daysOfYear
            year++
        }

        var month = 1
        var leapMonth = getLeapMonth(year)
        var isLeap = false
        var daysOfMonth: Int

        while (month <= 12) {
            if (leapMonth > 0 && month == leapMonth + 1 && !isLeap) {
                month--
                isLeap = true
                daysOfMonth = leapDays(year)
            } else {
                daysOfMonth = monthDays(year, month)
            }

            if (offset < daysOfMonth) break
            offset -= daysOfMonth

            if (isLeap && leapMonth == month) {
                isLeap = false
            }
            month++
        }

        return LunarDate(year, month, offset + 1, isLeap)
    }

    /** 农历 → 公历 */
    fun lunarToSolar(lunarYear: Int, lunarMonth: Int, lunarDay: Int, isLeap: Boolean = false): LocalDate {
        var offset = 0L

        for (y in BASE_YEAR until lunarYear) {
            offset += lunarYearDays(y)
        }

        val leapMonth = getLeapMonth(lunarYear)
        for (m in 1 until lunarMonth) {
            if (leapMonth == m) offset += leapDays(lunarYear)
            offset += monthDays(lunarYear, m)
        }
        if (isLeap && leapMonth == lunarMonth) {
            offset += monthDays(lunarYear, lunarMonth)
        }

        offset += (lunarDay - 1)
        return baseDate.plusDays(offset)
    }

    private fun lunarYearDays(year: Int): Int {
        var sum = 348
        for (i in 0x8000 downTo 0x8) {
            if ((LUNAR_INFO[year - BASE_YEAR] and i) != 0) sum++
        }
        return sum + leapDays(year)
    }

    private fun leapDays(year: Int): Int {
        if (getLeapMonth(year) == 0) return 0
        return if ((LUNAR_INFO[year - BASE_YEAR] and 0x10000) != 0) 30 else 29
    }

    private fun monthDays(year: Int, month: Int): Int {
        return if ((LUNAR_INFO[year - BASE_YEAR] and (0x10000 shr month)) != 0) 30 else 29
    }

    private fun getLeapMonth(year: Int): Int {
        return LUNAR_INFO[year - BASE_YEAR] and 0xf
    }

    /** 判断某年是否有闰月 */
    fun hasLeapMonth(year: Int): Boolean = getLeapMonth(year) != 0
}
