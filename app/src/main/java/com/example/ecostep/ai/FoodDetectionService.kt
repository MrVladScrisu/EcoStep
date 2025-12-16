package com.example.ecostep.ai

import android.graphics.Bitmap
import com.example.ecostep.data.model.FoodCategory
import com.example.ecostep.data.model.FoodDetectionResult
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Serviciu pentru detectarea mâncării folosind ML Kit
 */
class FoodDetectionService {
    
    private val labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS)

    /**
     * Detectează tipurile de mâncare dintr-o imagine și returnează o generalizare/medie
     */
    suspend fun detectFood(bitmap: Bitmap): FoodDetectionResult {
        val image = InputImage.fromBitmap(bitmap, 0)
        
        return try {
            val labels = suspendCancellableCoroutine { continuation ->
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        continuation.resume(labels)
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            }
            
            val categoryMap = mutableMapOf<FoodCategory, Double>()
            val detectedItems = mutableListOf<String>()
            var totalConfidence = 0f
            
            labels.forEach { label ->
                val labelText = label.text.lowercase()
                val confidence = label.confidence
                totalConfidence += confidence
                detectedItems.add("${label.text} (${(confidence * 100).toInt()}%)")
                
                // Clasificare în categorii bazată pe text - ÎMBUNĂTĂȚITĂ
                when {
                    // Fructe - PRIORITATE MAXIMĂ (verificăm PRIMUL)
                    labelText.contains("fruit") || 
                    labelText.contains("apple") || 
                    labelText.contains("banana") || 
                    labelText.contains("orange") || 
                    labelText.contains("berry") ||
                    labelText.contains("strawberry") ||
                    labelText.contains("grape") ||
                    labelText.contains("pear") ||
                    labelText.contains("peach") ||
                    labelText.contains("plum") ||
                    labelText.contains("cherry") ||
                    labelText.contains("watermelon") ||
                    labelText.contains("melon") ||
                    labelText.contains("kiwi") ||
                    labelText.contains("mango") ||
                    labelText.contains("pineapple") ||
                    labelText.contains("fruct") ||
                    labelText.contains("măr") ||
                    labelText.contains("mar") -> {
                        categoryMap[FoodCategory.FRUITS] = 
                            (categoryMap[FoodCategory.FRUITS] ?: 0.0) + confidence.toDouble()
                    }
                    
                    // Legume - PRIORITATE MARE
                    labelText.contains("vegetable") || 
                    labelText.contains("broccoli") || 
                    labelText.contains("carrot") || 
                    labelText.contains("lettuce") || 
                    labelText.contains("tomato") || 
                    labelText.contains("cucumber") ||
                    labelText.contains("pepper") ||
                    labelText.contains("onion") ||
                    labelText.contains("potato") ||
                    labelText.contains("cabbage") ||
                    labelText.contains("spinach") ||
                    labelText.contains("salad") ||
                    labelText.contains("legume") -> {
                        categoryMap[FoodCategory.VEGETABLES] = 
                            (categoryMap[FoodCategory.VEGETABLES] ?: 0.0) + confidence.toDouble()
                    }
                    
                    // Carne
                    labelText.contains("meat") || 
                    labelText.contains("beef") || 
                    labelText.contains("pork") || 
                    labelText.contains("chicken") || 
                    labelText.contains("turkey") || 
                    labelText.contains("lamb") ||
                    labelText.contains("steak") ||
                    labelText.contains("sausage") ||
                    labelText.contains("bacon") ||
                    labelText.contains("ham") ||
                    labelText.contains("carne") -> {
                        categoryMap[FoodCategory.MEAT] = 
                            (categoryMap[FoodCategory.MEAT] ?: 0.0) + confidence.toDouble()
                    }
                    
                    // Lactate
                    labelText.contains("cheese") || 
                    labelText.contains("milk") || 
                    labelText.contains("yogurt") || 
                    labelText.contains("dairy") ||
                    labelText.contains("cream") ||
                    labelText.contains("butter") ||
                    labelText.contains("lactat") -> {
                        categoryMap[FoodCategory.DAIRY] = 
                            (categoryMap[FoodCategory.DAIRY] ?: 0.0) + confidence.toDouble()
                    }
                    
                    // Procesate/Fast-food - DOAR dacă e clar fast food
                    labelText.contains("burger") || 
                    labelText.contains("pizza") || 
                    labelText.contains("fries") || 
                    labelText.contains("fast food") ||
                    labelText.contains("junk food") ||
                    labelText.contains("chips") ||
                    labelText.contains("soda") ||
                    labelText.contains("candy") -> {
                        categoryMap[FoodCategory.PROCESSED] = 
                            (categoryMap[FoodCategory.PROCESSED] ?: 0.0) + confidence.toDouble()
                    }
                    
                    // Food generic - distribuim echitabil
                    labelText.contains("food") ||
                    labelText.contains("dish") ||
                    labelText.contains("meal") -> {
                        // Nu adăugăm nimic specific, lăsăm alte etichete să decidă
                    }
                    
                    // Altele - ignorăm (nu adăugăm la UNKNOWN)
                    else -> {
                        // Nu facem nimic - evităm false positives
                    }
                }
            }
            
            // Calculăm media/generalizarea
            val averageConfidence = if (labels.isNotEmpty()) totalConfidence / labels.size else 0f
            
            // Normalizăm valorile pentru a obține numărul estimat de porții
            val normalizedCategories = categoryMap.mapValues { (_, value) ->
                // Convertim confidența în număr estimat de porții (0-5)
                (value * 5.0).coerceIn(0.0, 5.0)
            }
            
            // Dacă avem mai multe categorii detectate, considerăm amestec
            val finalCategories = if (normalizedCategories.size > 2) {
                // Calculăm media pentru amestec
                val mixedValue = normalizedCategories.values.average()
                mapOf(FoodCategory.MIXED to mixedValue.coerceIn(0.0, 5.0))
            } else {
                normalizedCategories
            }
            
            FoodDetectionResult(
                categories = finalCategories,
                confidence = averageConfidence,
                detectedItems = detectedItems
            )
            
        } catch (e: Exception) {
            // În caz de eroare, returnăm rezultat gol
            FoodDetectionResult(
                categories = emptyMap(),
                confidence = 0f,
                detectedItems = listOf("Eroare la detectare: ${e.message}")
            )
        }
    }
    
    /**
     * Convertește rezultatul detectării în porții pentru formular
     */
    fun convertToPortions(result: FoodDetectionResult): Map<String, Int> {
        val meatValue = result.categories[FoodCategory.MEAT]?.toInt() ?: 0
        val veggiesValue = (result.categories[FoodCategory.VEGETABLES]?.toInt() ?: 0) + 
                          (result.categories[FoodCategory.FRUITS]?.toInt() ?: 0)
        val dairyValue = result.categories[FoodCategory.DAIRY]?.toInt() ?: 0
        val junkValue = (result.categories[FoodCategory.PROCESSED]?.toInt() ?: 0) +
                       (result.categories[FoodCategory.MIXED]?.toInt() ?: 0)
        
        return mapOf(
            "meatPortions" to meatValue,
            "veggiesPortions" to veggiesValue,
            "dairyPortions" to dairyValue,
            "junkFoodPortions" to junkValue
        )
    }
    
    /**
     * Detectează tipul de vehicul dintr-o imagine
     */
    suspend fun detectVehicle(bitmap: Bitmap): VehicleDetectionResult {
        val image = InputImage.fromBitmap(bitmap, 0)
        
        return try {
            val labels = suspendCancellableCoroutine { continuation ->
                labeler.process(image)
                    .addOnSuccessListener { labels ->
                        continuation.resume(labels)
                    }
                    .addOnFailureListener { e ->
                        continuation.resumeWithException(e)
                    }
            }
            
            var detectedType = "walk" // default
            var maxConfidence = 0f
            val detectedItems = mutableListOf<String>()
            
            labels.forEach { label ->
                val labelText = label.text.lowercase()
                val confidence = label.confidence
                detectedItems.add("${label.text} (${(confidence * 100).toInt()}%)")
                
                // Detectăm tipul de vehicul
                when {
                    // Mașină
                    labelText.contains("car") || 
                    labelText.contains("vehicle") || 
                    labelText.contains("automobile") ||
                    labelText.contains("sedan") ||
                    labelText.contains("suv") -> {
                        if (confidence > maxConfidence) {
                            detectedType = "car"
                            maxConfidence = confidence
                        }
                    }
                    
                    // Bicicletă
                    labelText.contains("bike") || 
                    labelText.contains("bicycle") || 
                    labelText.contains("cycling") -> {
                        if (confidence > maxConfidence) {
                            detectedType = "bike"
                            maxConfidence = confidence
                        }
                    }
                    
                    // Autobuz
                    labelText.contains("bus") || 
                    labelText.contains("coach") -> {
                        if (confidence > maxConfidence) {
                            detectedType = "bus"
                            maxConfidence = confidence
                        }
                    }
                    
                    // Mașină electrică (greu de detectat, dar încercăm)
                    labelText.contains("electric") || 
                    labelText.contains("ev") || 
                    labelText.contains("tesla") -> {
                        if (confidence > maxConfidence) {
                            detectedType = "ev"
                            maxConfidence = confidence
                        }
                    }
                    
                    // Mers pe jos
                    labelText.contains("walk") || 
                    labelText.contains("pedestrian") || 
                    labelText.contains("person") -> {
                        if (confidence > maxConfidence && maxConfidence < 0.5f) {
                            detectedType = "walk"
                            maxConfidence = confidence
                        }
                    }
                }
            }
            
            VehicleDetectionResult(
                vehicleType = detectedType,
                confidence = maxConfidence,
                detectedItems = detectedItems
            )
            
        } catch (e: Exception) {
            VehicleDetectionResult(
                vehicleType = "walk",
                confidence = 0f,
                detectedItems = listOf("Eroare la detectare: ${e.message}")
            )
        }
    }
}

/**
 * Rezultatul detectării vehiculului
 */
data class VehicleDetectionResult(
    val vehicleType: String, // car, bike, bus, ev, walk
    val confidence: Float,
    val detectedItems: List<String> = emptyList()
)

