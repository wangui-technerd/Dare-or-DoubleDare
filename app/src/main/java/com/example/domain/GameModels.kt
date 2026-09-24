package com.example.domain

data class Player(
    val id: String,
    val name: String,
    val position: Int = 0,
    val points: Int = 0,
    val colorHex: String,
    val userId: Long? = null
)

enum class SafetyLevel(val icon: String, val label: String, val colorHex: String, val description: String) {
    GREEN("🟢", "Green", "#6FA98A", "Comfortable • Keep pushing boundaries"),
    YELLOW("🟡", "Yellow", "#D9B65C", "Caution • Ease up on intensity"),
    RED("🔴", "Red", "#C93B4E", "Safe Word / Stop • Game pauses immediately")
}

data class CategoryMeta(
    val key: String,
    val emoji: String,
    val label: String,
    val description: String
)

object GameConstants {
    const val BOARD_LENGTH = 40
    const val BONUS_POINTS = 2

    val LEVEL_POINTS = mapOf(
        1 to 1,
        2 to 2,
        3 to 3,
        4 to 5
    )

    val SCOPE_MULTIPLIERS = mapOf(
        "solo" to 1.0f,
        "paired" to 1.5f,
        "group" to 2.5f
    )

    val SPECIAL_SPACES = mapOf(
        8 to "DOUBLE_DARE",
        16 to "SWAP",
        24 to "CONFESS",
        32 to "DOUBLE_DARE",
        40 to "JACKPOT"
    )

    val CATEGORIES = listOf(
        CategoryMeta("AFTER DARK", "🖤", "After Dark", "Late-night spicy truths, ceiling stares, and unhinged tension"),
        CategoryMeta("ICEBREAKER", "🥂", "Icebreakers", "Couples & group tension-building icebreakers"),
        CategoryMeta("TROUBLE", "🫦", "Type of Trouble", "Attraction triggers, weaknesses, and magnetic chemistry"),
        CategoryMeta("TENSION", "🔥", "Tension Games", "Proximity, eye contact, and slow teasing games"),
        CategoryMeta("POWER", "👑", "Power & Control", "Dominance, surrender, commands, and boundaries"),
        CategoryMeta("CONFESSIONS", "💌", "Confessions", "Unspoken fantasies and secret desires"),
        CategoryMeta("ADMITTING", "😈", "Admitting Things", "No more filters, undeniable confessions"),
        CategoryMeta("SENSATION", "🧊", "Sensation", "Touch, temperature, textures, and sensory tease"),
        CategoryMeta("ROLEPLAY", "🎭", "Roleplay", "Seductive scenarios, strangers, and flirtation"),
        CategoryMeta("PRAISE", "💬", "Praise & Denial", "Compliments, teasing, and delayed gratification"),
        CategoryMeta("WILDCARD", "🃏", "Wildcard", "Bold questions, wild dares, and surprises")
    )

    fun getCategoryMeta(key: String): CategoryMeta {
        val normalized = key.uppercase().replace("_", " ")
        return CATEGORIES.find { it.key.equals(normalized, ignoreCase = true) }
            ?: CategoryMeta(key, "✨", key, "Exciting questions and dares")
    }

    fun categoryDieMapping(dieValue: Int): String {
        return when (dieValue) {
            1 -> "POWER"
            2 -> "SENSATION"
            3 -> "ROLEPLAY"
            4 -> "PRAISE"
            5 -> "AFTER DARK"
            6 -> "ICEBREAKER"
            7 -> "TROUBLE"
            8 -> "TENSION"
            9 -> "CONFESSIONS"
            10 -> "ADMITTING"
            11 -> "WILDCARD"
            else -> "WILDPICK" // Die value 12 lets the player pick!
        }
    }

    fun zoneOf(position: Int): Int {
        if (position <= 0) return 1
        return minOf(4, ((position - 1) / 10) + 1)
    }

    fun zoneCapFor(position: Int): Int = zoneOf(position)

    fun computePoints(level: Int, scope: String): Int {
        val base = LEVEL_POINTS[level] ?: 1
        val mult = SCOPE_MULTIPLIERS[scope.lowercase()] ?: 1.0f
        return Math.round(base * mult)
    }
}

data class LevelDetails(
    val level: Int,
    val name: String,
    val emoji: String,
    val colorHex: String,
    val tagline: String
)

val LEVEL_INFO = mapOf(
    1 to LevelDetails(1, "TEASE", "🌸", "#E598A8", "Let's ease in."),
    2 to LevelDetails(2, "TEMPT", "🍷", "#8B2247", "Things are getting interesting."),
    3 to LevelDetails(3, "HEAT", "🔥", "#C92A45", "You asked for this."),
    4 to LevelDetails(4, "AFTER DARK", "🖤", "#1C1320", "No more warm-up.")
)

data class RollOutcome(
    val catDie: Int,
    val intDie: Int,
    val category: String,
    val rolledLevel: Int,
    val cappedLevel: Int,
    val movement: Int,
    val isWildPick: Boolean,
    val wasCapped: Boolean
)

data class CategoryAffinity(
    val categoryKey: String,
    val categoryLabel: String,
    val emoji: String,
    val totalPresented: Int,
    val completedCount: Int,
    val affinityPercent: Int,
    val status: String
)

data class LearnedPatterns(
    val totalDaresPlayed: Int = 0,
    val totalCompleted: Int = 0,
    val totalUncomfortable: Int = 0,
    val totalSkipped: Int = 0,
    val overallCompletionRate: Int = 0,
    val categoryAffinities: List<CategoryAffinity> = emptyList(),
    val topCategories: List<CategoryAffinity> = emptyList(),
    val intensityComfortPercents: Map<Int, Int> = emptyMap(),
    val intensitySweetSpot: Int = 2,
    val playstyleArchetype: String = "Sensual Explorer",
    val archetypeDescription: String = "Balanced curiosity with respect for chemistry and boundaries",
    val blockedCardIds: Set<Long> = emptySet(),
    val blockedCards: List<com.example.data.model.DareCardEntity> = emptyList(),
    val adaptiveEngineEnabled: Boolean = true,
    val boostCustomDares: Boolean = true
)
