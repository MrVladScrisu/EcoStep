package com.example.ecostep.data.model

/**
 * Categorii de mâncare detectate de AI
 */
enum class FoodCategory(val label: String, val emissionFactor: Double) {
    MEAT("Carne", 5.0),           // kg CO2 per porție
    VEGETABLES("Legume", 0.5),    // kg CO2 per porție
    FRUITS("Fructe", 0.3),        // kg CO2 per porție
    DAIRY("Lactate", 1.5),        // kg CO2 per porție
    MIXED("Amestec", 2.0),        // kg CO2 per porție (mâncare mixtă)
    PROCESSED("Procesate", 3.0),  // kg CO2 per porție (fast-food, procesate)
    UNKNOWN("Necunoscut", 1.0)    // default
}

/**
 * Rezultatul detectării AI pentru mâncare
 */
data class FoodDetectionResult(
    val categories: Map<FoodCategory, Double>, // Categorie -> Număr estimat de porții
    val confidence: Float, // Nivel de încredere (0-1)
    val detectedItems: List<String> = emptyList() // Lista de obiecte detectate
)

