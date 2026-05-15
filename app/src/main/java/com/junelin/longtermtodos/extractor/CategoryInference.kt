package com.junelin.longtermtodos.extractor

object CategoryInference {

    private val categoryKeywords = mapOf(
        1L to listOf("生日", "寿辰", "诞辰", "出生", "满月", "周岁"),
        2L to listOf("年检", "保险", "车险", "保养", "维修", "加油卡", "驾照", "行驶证", "违章"),
        3L to listOf("证件", "合同", "护照", "签证", "身份证", "居住证", "营业执照", "缴费", "还款", "账单", "罚款"),
        4L to listOf("租房", "房租", "水电", "燃气", "物业", "旅行", "旅游", "机票", "酒店", "采购", "购物", "装修")
    )

    fun infer(title: String, content: String): Long? {
        val combined = "$title $content"
        val scores = mutableMapOf<Long, Int>()

        categoryKeywords.forEach { (categoryId, keywords) ->
            val score = keywords.count { combined.contains(it) }
            if (score > 0) {
                scores[categoryId] = score
            }
        }

        return scores.maxByOrNull { it.value }?.key
    }
}
