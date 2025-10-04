package com.example.casonaapp

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CreateEditUserViewIntegrationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun showCreateUser() {
        // When
        composeTestRule.setContent {
            // Necesitarías mockear el navController y ViewModel
            // CreateEditUserView(navController = mockNavController, userId = null)
        }

        // Then
        composeTestRule.onNodeWithText("Crear Nuevo Usuario").assertExists()
        composeTestRule.onNodeWithText("Crear Usuario").assertExists()
    }

    @Test
    fun showEditUser() {
        // When
        composeTestRule.setContent {
            // CreateEditUserView(navController = mockNavController, userId = "123")
        }

        // Then
        composeTestRule.onNodeWithText("Editar Usuario").assertExists()
        composeTestRule.onNodeWithText("Actualizar Usuario").assertExists()
    }

    @Test
    fun enableSaveButton() {
        // Given
        composeTestRule.setContent {
            // CreateEditUserView(navController = mockNavController, userId = null)
        }

        // When
        composeTestRule.onNodeWithText("Email del usuario").performTextInput("test@example.com")
        composeTestRule.onNodeWithText("Nombre del usuario").performTextInput("Test User")
        composeTestRule.onNodeWithText("Número de contacto").performTextInput("+56912345678")

        // Then
        composeTestRule.onNodeWithText("Crear Usuario").assertExists().assertIsEnabled()
    }

    @Test
    fun showErrorEmailEmpty() {
        // Given
        composeTestRule.setContent {
            // CreateEditUserView(navController = mockNavController, userId = null)
        }

        // When
        composeTestRule.onNodeWithText("Nombre del usuario").performTextInput("Test User")
        composeTestRule.onNodeWithText("Número de contacto").performTextInput("+56912345678")

        // Then
        composeTestRule.onNodeWithText("Crear Usuario").assertExists().assertIsNotEnabled()
    }
}