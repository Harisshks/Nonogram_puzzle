package com.nonogram.puzzle.data.model

enum class PuzzleCategory(
    val displayName: String,
    val icon: String,
    val colorLight: Long,
    val colorDark: Long
) {
    ANIMALS("Animals", "Animals", 0xFFE8F5E9L, 0xFF2E7D32L),
    FOOD("Food", "Food", 0xFFFFF3E0L, 0xFFE65100L),
    SYMBOLS("Symbols", "Symbols", 0xFFE8EAF6L, 0xFF283593L),
    OBJECTS("Objects", "Objects", 0xFFFCE4ECL, 0xFFC62828L),
    NATURE("Nature", "Nature", 0xFFE0F2F1L, 0xFF00695CL)
}
