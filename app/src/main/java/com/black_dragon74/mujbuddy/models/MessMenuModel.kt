package com.black_dragon74.mujbuddy.models

data class MessMenuModel (
    val last_updated_at: String?,
    val last_updated_meal: String?,
    val breakfast: Array<String>?,
    val lunch: Array<String>?,
    val high_tea: Array<String>?,
    val dinner: Array<String>?
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MessMenuModel

        if (last_updated_at != other.last_updated_at) return false
        if (last_updated_meal != other.last_updated_meal) return false
        if (breakfast != null) {
            if (other.breakfast == null) return false
            if (!breakfast.contentEquals(other.breakfast)) return false
        } else if (other.breakfast != null) return false
        if (lunch != null) {
            if (other.lunch == null) return false
            if (!lunch.contentEquals(other.lunch)) return false
        } else if (other.lunch != null) return false
        if (high_tea != null) {
            if (other.high_tea == null) return false
            if (!high_tea.contentEquals(other.high_tea)) return false
        } else if (other.high_tea != null) return false
        if (dinner != null) {
            if (other.dinner == null) return false
            if (!dinner.contentEquals(other.dinner)) return false
        } else if (other.dinner != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = last_updated_at?.hashCode() ?: 0
        result = 31 * result + (last_updated_meal?.hashCode() ?: 0)
        result = 31 * result + (breakfast?.contentHashCode() ?: 0)
        result = 31 * result + (lunch?.contentHashCode() ?: 0)
        result = 31 * result + (high_tea?.contentHashCode() ?: 0)
        result = 31 * result + (dinner?.contentHashCode() ?: 0)
        return result
    }
}