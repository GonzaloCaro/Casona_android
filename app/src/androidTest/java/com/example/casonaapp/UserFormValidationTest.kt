package com.example.casonaapp.userManagment

import com.example.casonaapp.data.UserType
import org.junit.Test
import org.junit.Assert.*
import org.junit.runner.RunWith
import org.junit.runners.JUnit4

@RunWith(JUnit4::class)
class UserFormValidationTest {

    @Test
    fun validateFormValidInputs() {
        // Given
        val email = "test@example.com"
        val displayName = "Test User"
        val phoneNumber = "+56912345678"
        val userType = UserType.CLIENT

        // When
        val result = validateForm(email, displayName, phoneNumber, userType)

        // Then
        assertTrue(result)
    }

    @Test
    fun validateFormEmptyEmail() {
        // Given
        val email = ""
        val displayName = "Test User"
        val phoneNumber = "+56912345678"
        val userType = UserType.CLIENT

        // When
        val result = validateForm(email, displayName, phoneNumber, userType)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateFormDisplayNameEmpty() {
        // Given
        val email = "test@example.com"
        val displayName = ""
        val phoneNumber = "+56912345678"
        val userType = UserType.CLIENT

        // When
        val result = validateForm(email, displayName, phoneNumber, userType)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateFormPhoneNumberEmpty() {
        // Given
        val email = "test@example.com"
        val displayName = "Test User"
        val phoneNumber = ""
        val userType = UserType.CLIENT

        // When
        val result = validateForm(email, displayName, phoneNumber, userType)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateFormUserTypeNull() {
        // Given
        val email = "test@example.com"
        val displayName = "Test User"
        val phoneNumber = "+56912345678"
        val userType: UserType? = null

        // When
        val result = validateForm(email, displayName, phoneNumber, userType)

        // Then
        assertFalse(result)
    }

    @Test
    fun validateFormAdminUserType() {
        // Given
        val email = "admin@example.com"
        val displayName = "Admin User"
        val phoneNumber = "+56912345678"
        val userType = UserType.ADMIN

        // When
        val result = validateForm(email, displayName, phoneNumber, userType)

        // Then
        assertTrue(result)
    }

    @Test
    fun validateFormWhiteSpace() {
        // Given
        val email = "  test@example.com  "
        val displayName = "  Test User  "
        val phoneNumber = "  +56912345678  "
        val userType = UserType.CLIENT

        // When
        val result = validateForm(email.trim(), displayName.trim(), phoneNumber.trim(), userType)

        // Then
        assertTrue(result)
    }
}