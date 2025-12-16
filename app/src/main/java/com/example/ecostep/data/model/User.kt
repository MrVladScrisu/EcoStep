package com.example.ecostep.data.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val qrCode: String = "", // QR code pentru login
    val createdAt: Long = System.currentTimeMillis()
)

