package com.example.casonaapp

import com.example.casonaapp.data.UserProfile
import com.example.casonaapp.data.UserType
import org.junit.Test

import org.junit.Assert.*

import java.util.*

class UserProfileTest {

    @Test
    fun `UserProfile default values should be correct`() {
        // When
        val userProfile = UserProfile()

        // Then
        assertEquals("", userProfile.uid)
        assertEquals("", userProfile.email)
        assertEquals("", userProfile.displayName)
        assertEquals(UserType.CLIENT, userProfile.userType)
        assertTrue(userProfile.active)
    }

    @Test
    fun `UserProfile copy should create new instance with updated values`() {
        // Given
        val originalUser = UserProfile(
            uid = "123",
            email = "original@example.com",
            displayName = "Original User",
            userType = UserType.CLIENT,
            active = true
        )

        // When
        val copiedUser = originalUser.copy(
            email = "updated@example.com",
            displayName = "Updated User",
            active = false
        )

        // Then
        assertEquals("123", copiedUser.uid)
        assertEquals("updated@example.com", copiedUser.email)
        assertEquals("Updated User", copiedUser.displayName)
        assertEquals(UserType.CLIENT, copiedUser.userType)
        assertFalse(copiedUser.active)
    }

    @Test
    fun `UserType enum should have correct values`() {
        // When & Then
        assertEquals("ADMIN", UserType.ADMIN.name)
        assertEquals("CLIENT", UserType.CLIENT.name)
        assertEquals(2, UserType.values().size)
    }
}