package com.pickledgames.stardewvalleyguide.enums

enum class Reaction(
        val type: String
) {
    Love("Love"),
    Like("Like"),
    Neutral("Neutral"),
    Dislike("Dislike"),
    Hate("Hate");

    override fun toString(): String {
        val value = when (this) {
            Love -> "+80"
            Like -> "+45"
            Neutral -> "+20"
            Dislike -> "-20"
            Hate -> "-40"
        }
        return "$type $value"
    }
}
