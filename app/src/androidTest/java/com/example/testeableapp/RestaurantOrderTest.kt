package com.example.testeableapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import org.junit.Rule
import org.junit.Test

class RestaurantOrderTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    /**
     * Test: Mensaje de pedido visible al inicio
     */
    @Test
    fun empty_order_message_is_displayed_on_start() {
        composeTestRule.onNodeWithTag("emptyOrderMessage")
            .assertIsDisplayed()
    }

    /**
     * Test: Los items del menú son visibles (usando scroll para los que están abajo)
     */
    @Test
    fun all_items_displayed_on_start() {
        // Items al principio
        composeTestRule.onNodeWithTag("menuItem_1").assertIsDisplayed()
        
        // Item en medio/final
        composeTestRule.onNodeWithTag("menuItem_5").performScrollTo().assertIsDisplayed()
        composeTestRule.onNodeWithTag("menuItem_10").performScrollTo().assertIsDisplayed()
    }

    /**
     * Test: El total general se actualiza correctamente al añadir un item
     */
    @Test
    fun total_price_updates_after_adding_item() {
        // Añadir producto
        composeTestRule.onNodeWithTag("addButton_1").performClick()

        // El totalValue está al final
        composeTestRule.onNodeWithTag("totalValue")
            .performScrollTo()
            .assertIsDisplayed()
            //el texto contenga el precio
            .assertTextContains("5.50", substring = true)
    }

    /**
     * Test: El botón de Realizar Pedido aparece solo cuando hay items y desaparece al vaciar
     */
    @Test
    fun place_order_button_visibility_flow() {
        //No debe existir al inicio
        composeTestRule.onNodeWithTag("placeOrderButton").assertDoesNotExist()

        composeTestRule.onNodeWithTag("addButton_1").performClick()

        //debe estar visible
        composeTestRule.onNodeWithTag("placeOrderButton")
            .performScrollTo()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag("decrementOrderItem_1").performScrollTo().performClick()
        
        //desaparecer y mostrar el mensaje de vacío
        composeTestRule.onNodeWithTag("placeOrderButton").assertDoesNotExist()
        composeTestRule.onNodeWithTag("emptyOrderMessage").assertIsDisplayed()
    }

    /**
     * Test: Control de cantidades dentro del pedido
     */
    @Test
    fun order_quantities_increment_decrement() {
        // Añadir item
        composeTestRule.onNodeWithTag("addButton_2").performClick()
        
        // Verificar cantidad inicial en el pedido es 1
        composeTestRule.onNodeWithTag("orderItemQuantity_2")
            .performScrollTo()
            .assertTextContains("1")
            
        // Incrementar
        composeTestRule.onNodeWithTag("incrementOrderItem_2").performClick()
        composeTestRule.onNodeWithTag("orderItemQuantity_2").assertTextContains("2")
        
        //el subtotal se actualiza
        composeTestRule.onNodeWithTag("orderItemSubtotal_2").assertTextContains("12.00", substring = true)
    }
}
