package com.example.casonaapp.data

import java.util.Date

data class UserProfile(
    val uid: String = "",
    val email: String = "",
    val userName: String = "",
    val displayName: String = "",
    val createdAt: Date = Date(),
    val lastLogin: Date? = null,
    val active: Boolean = true,
    val phoneNumber: String? = null,
    val bio: String = "",
    val userType: UserType? = UserType.CLIENT
)

enum class UserType {
    ADMIN, CLIENT
}