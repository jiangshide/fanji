package com.fanji.android.net.netprobe.level

/**
 * @author 蒋世德 @ fanji inc
 * @email jiangshide@fanji.com
 * @since 21-6-1 下午12:56
 */
enum class NetHealthLevel(internal val lowValue: Float, internal val highValue: Float) {
    GOOD(0.8F, 1.0F),
    MEDIUM(0.6F, 0.8F),
    BAD(0.0F, 0.6F),
    DEAD(0.0F, 0.0F),
    UNKNOWN(-1F, -1F);

    companion object {
        @JvmStatic
        fun from(healthValue: Float): NetHealthLevel? {
            if (healthValue < -1.0F || healthValue > 1.0F) return null
            return judgeHealthFromValue(healthValue)
        }

        /**
         * 根据网络评分确定等级
         *
         * @param value 网络评分
         * @return 网络等级
         */
        internal fun judgeHealthFromValue(value: Float): NetHealthLevel {
            return when {
                value isIn GOOD -> GOOD
                value isIn MEDIUM -> MEDIUM
                value isIn BAD -> BAD
                value isIn DEAD -> DEAD
                else -> UNKNOWN
            }
        }

        private infix fun Float.isIn(level: NetHealthLevel): Boolean {
            return when {
                this == DEAD.lowValue -> level == DEAD
                this == UNKNOWN.lowValue -> level == UNKNOWN
                else -> this > level.lowValue && this <= level.highValue
            }
        }
    }
}