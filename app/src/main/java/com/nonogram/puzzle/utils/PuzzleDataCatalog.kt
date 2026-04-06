package com.nonogram.puzzle.utils

import com.nonogram.puzzle.data.model.Difficulty
import com.nonogram.puzzle.data.model.Puzzle
import com.nonogram.puzzle.data.model.PuzzleCategory

/**
 * Manually authored puzzle grids (compact row strings). IDs not listed are filled by [PuzzleData].
 */
internal object PuzzleDataCatalog {

    private fun line(line: List<Int>): List<Int> {
        val c = mutableListOf<Int>()
        var n = 0
        line.forEach {
            if (it == 1) n++
            else if (n > 0) {
                c.add(n)
                n = 0
            }
        }
        if (n > 0) c.add(n)
        return if (c.isEmpty()) listOf(0) else c
    }

    private fun build(
        id: Int,
        level: Int,
        title: String,
        category: PuzzleCategory,
        difficulty: Difficulty,
        solution: List<List<Int>>
    ): Puzzle {
        val size = solution.size
        return Puzzle(
            id = id,
            levelNumber = level,
            title = title,
            category = category,
            difficulty = difficulty,
            gridSize = size,
            solution = solution,
            rowClues = solution.map { line(it) },
            colClues = List(size) { col ->
                line(List(size) { r -> solution[r][col] })
            }
        )
    }

    private fun g5(vararg rows: String): List<List<Int>> =
        rows.map { r -> r.map { ch -> if (ch == '1') 1 else 0 } }

    private fun g7(vararg rows: String): List<List<Int>> =
        rows.map { r -> r.map { ch -> if (ch == '1') 1 else 0 } }

    private fun g10(vararg rows: String): List<List<Int>> =
        rows.map { r -> r.map { ch -> if (ch == '1') 1 else 0 } }

    val animals: List<Puzzle> = listOf(
        build(1001, 1, "Fish", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("00100", "01110", "11111", "01110", "10001")),
        build(1002, 2, "Bird", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("01100", "11110", "01100", "00100", "00110")),
        build(1003, 3, "Cat Face", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("10001", "11111", "10101", "11111", "01010")),
        build(1004, 4, "Duck", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("01100", "11100", "01111", "01110", "01110")),
        build(1005, 5, "Snail", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("00110", "01010", "00110", "11111", "00010")),
        build(1006, 6, "Ant", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("10101", "01110", "10101", "01110", "10001")),
        build(1007, 7, "Frog", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("10101", "11111", "10101", "01110", "10001")),
        build(1008, 8, "Crab", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("10101", "11111", "01110", "11111", "10001")),
        build(1009, 9, "Bee", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("01110", "10101", "01110", "10101", "00100")),
        build(1010, 10, "Turtle", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("01110", "11111", "11011", "11111", "01010")),
        build(1011, 11, "Rabbit", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("10001", "10001", "11111", "10101", "11111")),
        build(1012, 12, "Dog", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("11010", "11110", "01111", "01110", "01010")),
        build(1013, 13, "Horse", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("01100", "01110", "11000", "01000", "01010")),
        build(1014, 14, "Swan", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("00100", "00110", "01100", "11100", "01111")),
        build(1015, 15, "Whale", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("01110", "11111", "11111", "01110", "00101")),
        build(1016, 16, "Pig", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("10101", "11111", "10001", "11111", "01010")),
        build(1017, 17, "Lion", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("11111", "10101", "11111", "01110", "01010")),
        build(1018, 18, "Owl", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("10101", "11111", "01010", "11111", "01010")),
        build(1019, 19, "Elephant", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("11011", "11111", "11111", "01010", "01010")),
        build(1020, 20, "Penguin", PuzzleCategory.ANIMALS, Difficulty.EASY, g5("01110", "10101", "11111", "01110", "01010")),
        build(1021, 21, "Butterfly", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("1100011", "1110111", "0111110", "0011100", "0111110", "1110111", "1100011")),
        build(1022, 22, "Dolphin", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("0001000", "0011100", "0111110", "1111111", "0111101", "0011000", "0001000")),
        build(1023, 23, "Octopus", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("0111110", "1111111", "1011101", "1111111", "1010101", "1000001", "1000001")),
        build(1024, 24, "Panda", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("1100011", "1111111", "0101010", "0111110", "0011100", "0100010", "0100010")),
        build(1025, 25, "Fox", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("1100011", "1110111", "0111110", "0011100", "0011100", "0100010", "1000001")),
        build(1026, 26, "Shark", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("0001000", "0011100", "0111110", "1111111", "0111110", "0010100", "0000000")),
        build(1027, 27, "Spider", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("1000001", "0101010", "1111111", "0111110", "1100011", "1000001", "1000001")),
        build(1028, 28, "Monkey", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("1011101", "1111111", "1011101", "0111110", "0011100", "0100010", "1100011")),
        build(1029, 29, "Crocodile", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("1010101", "1111111", "0111110", "0111110", "0111110", "0100010", "0100010")),
        build(1030, 30, "Parrot", PuzzleCategory.ANIMALS, Difficulty.MEDIUM, g7("0011100", "0110110", "1111111", "0111110", "0011100", "0001000", "0011100")),
        build(1041, 41, "Cat", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("1100000011", "1110001111", "0111111110", "0101110110", "0111111110", "0011111100", "0010000100", "0110000110", "1100000011", "1000000001")),
        build(1042, 42, "Dog", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("1100000100", "1110001100", "0111111110", "0101110110", "0111111110", "0011111100", "0010110110", "0011001100", "0111000110", "1100000011")),
        build(1043, 43, "Horse", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("0011000000", "0111100000", "1111110000", "0110000000", "0111111100", "0111111110", "0011111111", "0011001100", "0011001100", "0010001000")),
        build(1044, 44, "Eagle", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("0000110000", "0001111000", "1001111001", "1101111011", "1111111111", "0111111110", "0011111100", "0001111000", "0001010000", "0001010000")),
        build(1045, 45, "Penguin", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("0011111100", "0110000110", "0101000101", "0111111110", "0100110010", "0111111110", "0011001100", "0111111110", "1101111011", "1100000011")),
        build(1046, 46, "Deer", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("1010001010", "1110001110", "0100000100", "0111111100", "0101110110", "0111111100", "0011111000", "0010000100", "0011001100", "0010001000")),
        build(1047, 47, "Crab", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("1001010011", "1101110111", "0111111110", "0101110110", "0111111110", "1100110011", "1000110001", "0000110000", "0000000000", "0000000000")),
        build(1048, 48, "Octopus", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("0011111100", "0111111110", "1101111011", "1111111111", "1011111101", "1010000101", "1010000101", "1000000001", "1000000001", "0000000000")),
        build(1049, 49, "Whale", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("0011111000", "0111111100", "1111111110", "1111111111", "1111111111", "0111111110", "0011111100", "0001111000", "0000110001", "0000000001")),
        build(1050, 50, "Dragon", PuzzleCategory.ANIMALS, Difficulty.HARD, g10("1000000001", "1100110011", "0110110110", "0011111100", "0111111110", "1101111011", "1001111001", "0011001100", "0111000110", "1100000011"))
    )

    val food: List<Puzzle> = listOf(
        build(2001, 1, "Apple", PuzzleCategory.FOOD, Difficulty.EASY, g5("00100", "01110", "11111", "11111", "01110")),
        build(2002, 2, "Pizza", PuzzleCategory.FOOD, Difficulty.EASY, g5("00100", "01110", "11011", "10101", "11111")),
        build(2003, 3, "Ice Cream", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "11111", "11111", "01110", "00100")),
        build(2004, 4, "Cake", PuzzleCategory.FOOD, Difficulty.EASY, g5("00100", "01110", "11111", "10101", "11111")),
        build(2005, 5, "Carrot", PuzzleCategory.FOOD, Difficulty.EASY, g5("00110", "01100", "01110", "01100", "00010")),
        build(2006, 6, "Cherry", PuzzleCategory.FOOD, Difficulty.EASY, g5("00010", "01110", "10000", "11011", "01010")),
        build(2007, 7, "Banana", PuzzleCategory.FOOD, Difficulty.EASY, g5("00110", "01100", "11000", "01100", "00110")),
        build(2008, 8, "Mushroom", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "11111", "11111", "01110", "01110")),
        build(2009, 9, "Lollipop", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "11111", "01110", "00100", "00100")),
        build(2010, 10, "Egg", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "11111", "11111", "11111", "01110")),
        build(2011, 11, "Cookie", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "10101", "11111", "10101", "01110")),
        build(2012, 12, "Donut", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "11011", "10001", "11011", "01110")),
        build(2013, 13, "Watermelon", PuzzleCategory.FOOD, Difficulty.EASY, g5("00000", "01110", "11111", "10101", "01110")),
        build(2014, 14, "Grapes", PuzzleCategory.FOOD, Difficulty.EASY, g5("00100", "11011", "11111", "01110", "00100")),
        build(2015, 15, "Strawberry", PuzzleCategory.FOOD, Difficulty.EASY, g5("10101", "01110", "11111", "01110", "00100")),
        build(2016, 16, "Pear", PuzzleCategory.FOOD, Difficulty.EASY, g5("00100", "01110", "11111", "11111", "01110")),
        build(2017, 17, "Cupcake", PuzzleCategory.FOOD, Difficulty.EASY, g5("01010", "11111", "01110", "11111", "01110")),
        build(2018, 18, "Popcorn", PuzzleCategory.FOOD, Difficulty.EASY, g5("10101", "11111", "11111", "01110", "01110")),
        build(2019, 19, "Taco", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "11111", "10101", "11011", "01110")),
        build(2020, 20, "Sushi", PuzzleCategory.FOOD, Difficulty.EASY, g5("01110", "11111", "10101", "11111", "01110")),
        build(2021, 21, "Burger", PuzzleCategory.FOOD, Difficulty.MEDIUM, g7("0111110", "1111111", "0101010", "1111111", "1010101", "1111111", "0111110")),
        build(2022, 22, "Hotdog", PuzzleCategory.FOOD, Difficulty.MEDIUM, g7("0111110", "1100011", "1011101", "1011101", "1011101", "1100011", "0111110")),
        build(2023, 23, "Pineapple", PuzzleCategory.FOOD, Difficulty.MEDIUM, g7("0101010", "0011100", "0111110", "1101011", "0111110", "0111110", "0011100")),
        build(2024, 24, "Ramen", PuzzleCategory.FOOD, Difficulty.MEDIUM, g7("0111110", "1111111", "1011101", "1101011", "1010101", "1111111", "0111110")),
        build(2025, 25, "Croissant", PuzzleCategory.FOOD, Difficulty.MEDIUM, g7("1100011", "1110111", "0111110", "0011100", "0111110", "1110111", "1100011"))
    )

    val symbols: List<Puzzle> = listOf(
        build(3001, 1, "Heart", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("01010", "11111", "11111", "01110", "00100")),
        build(3002, 2, "Star", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("00100", "11111", "01110", "10101", "10001")),
        build(3003, 3, "Arrow", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("00100", "01110", "11111", "00100", "00100")),
        build(3004, 4, "Check", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("00001", "00011", "00110", "10100", "11000")),
        build(3005, 5, "Cross", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("00100", "00100", "11111", "00100", "00100")),
        build(3006, 6, "Diamond", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("00100", "01110", "11111", "01110", "00100")),
        build(3007, 7, "Shield", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("11111", "11111", "01110", "00100", "00000")),
        build(3008, 8, "Crown", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("10101", "11111", "11111", "11111", "00000")),
        build(3009, 9, "Lightning", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("01110", "01100", "01110", "00110", "00100")),
        build(3010, 10, "Moon", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("01110", "11000", "11000", "11000", "01110")),
        build(3011, 11, "Infinity", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("00000", "11011", "10101", "11011", "00000")),
        build(3012, 12, "Peace", PuzzleCategory.SYMBOLS, Difficulty.EASY, g5("01110", "10101", "01110", "10101", "01010"))
    )

    val objects: List<Puzzle> = listOf(
        build(4001, 1, "House", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("00100", "01110", "11111", "11011", "11011")),
        build(4002, 2, "Car", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("01110", "11111", "11111", "01010", "00000")),
        build(4003, 3, "Boat", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("00100", "01100", "11111", "01110", "00100")),
        build(4004, 4, "Plane", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("00100", "11111", "00100", "01110", "00100")),
        build(4005, 5, "Rocket", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("00100", "01110", "01110", "11111", "10101")),
        build(4006, 6, "Clock", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("01110", "11011", "10111", "11001", "01110")),
        build(4007, 7, "Key", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("01100", "10010", "01100", "00100", "00111")),
        build(4008, 8, "Lock", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("01110", "10001", "11111", "11011", "11111")),
        build(4009, 9, "Bell", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("00100", "01110", "11111", "11111", "01010")),
        build(4010, 10, "Cup", PuzzleCategory.OBJECTS, Difficulty.EASY, g5("10001", "10001", "11111", "01110", "01110"))
    )

    val nature: List<Puzzle> = listOf(
        build(5001, 1, "Tree", PuzzleCategory.NATURE, Difficulty.EASY, g5("00100", "01110", "11111", "00100", "00100")),
        build(5002, 2, "Flower", PuzzleCategory.NATURE, Difficulty.EASY, g5("01010", "11111", "01110", "11111", "01010")),
        build(5003, 3, "Sun", PuzzleCategory.NATURE, Difficulty.EASY, g5("10101", "01110", "11111", "01110", "10101")),
        build(5004, 4, "Cloud", PuzzleCategory.NATURE, Difficulty.EASY, g5("01100", "11110", "11111", "01110", "00000")),
        build(5005, 5, "Mountain", PuzzleCategory.NATURE, Difficulty.EASY, g5("00100", "01110", "01110", "11111", "11111")),
        build(5006, 6, "Snowflake", PuzzleCategory.NATURE, Difficulty.EASY, g5("00100", "10101", "11111", "10101", "00100")),
        build(5007, 7, "Leaf", PuzzleCategory.NATURE, Difficulty.EASY, g5("00010", "00110", "01110", "11100", "10000")),
        build(5008, 8, "Rain", PuzzleCategory.NATURE, Difficulty.EASY, g5("01110", "11111", "00000", "10101", "01010")),
        build(5009, 9, "Wave", PuzzleCategory.NATURE, Difficulty.EASY, g5("01000", "11100", "01110", "00011", "00001")),
        build(5010, 10, "Rainbow", PuzzleCategory.NATURE, Difficulty.EASY, g5("01110", "10001", "10001", "00000", "00000"))
    )
}
