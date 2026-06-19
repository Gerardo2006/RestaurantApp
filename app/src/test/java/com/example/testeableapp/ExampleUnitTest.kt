package com.example.testeableapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Test
import org.junit.Assert.*
import org.junit.Before

/**
 * Pruebas unitarias para RestaurantViewModel.
 * Cubre los requisitos de gestión de pedido y lógica de negocio.
 */
@OptIn(ExperimentalCoroutinesApi::class)
class ExampleUnitTest {

    private lateinit var viewModel: RestaurantViewModel
    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = RestaurantViewModel()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    /**
     * 1. Test: Agregar item al pedido
     */
    @Test
    fun `agregar item al pedido actualiza la lista y el estado`() {
        val itemId = 1
        viewModel.addItem(itemId)

        val items = viewModel.orderedItems.value
        val quantities = viewModel.quantities.value

        assertTrue("El item debería estar en el pedido", items.any { it.id == itemId })
        assertEquals("La cantidad debería ser 1", 1, quantities[itemId])
        assertFalse("El pedido ya no debería estar vacío", viewModel.isEmpty.value)
    }

    /**
     * 2. Test: Incrementar y decrementar cantidad
     */
    @Test
    fun `incrementar y decrementar cantidad actualiza el conteo correctamente`() {
        val itemId = 2

        // Incrementar
        viewModel.addItem(itemId)
        viewModel.incrementItem(itemId)
        viewModel.incrementItem(itemId)
        assertEquals("La cantidad debería ser 3 tras incrementos", 3, viewModel.quantities.value[itemId])

        // Decrementar
        viewModel.decrementItem(itemId)
        assertEquals("La cantidad debería ser 2 tras un decremento", 2, viewModel.quantities.value[itemId])
    }

    /**
     * 3. Test: Eliminar item al decrementar desde 1
     */
    @Test
    fun `eliminar item al decrementar cuando la cantidad es 1`() {
        val itemId = 3
        viewModel.addItem(itemId)
        
        // Decrementar desde 1 debería eliminar el item
        viewModel.decrementItem(itemId)

        val items = viewModel.orderedItems.value
        assertFalse("El item debería eliminarse del pedido", items.any { it.id == itemId })
        assertTrue("El pedido debería volver a estar vacío", viewModel.isEmpty.value)
    }

    /**
     * 4. Test: Cálculo del total a pagar
     */
    @Test
    fun `calculo del total a pagar con multiples productos y cantidades`() {
        //5.50 * 2 = 11.00
        viewModel.addItem(1)
        viewModel.incrementItem(1)
        
        //6.00 * 1 = 6.00
        viewModel.addItem(2)
        
        //1.50 * 2 = 3.00
        viewModel.addItem(5)
        viewModel.incrementItem(5)

        //11.00 + 6.00 + 3.00 = 20.00
        assertEquals(20.00, viewModel.total.value, 0.001)
    }

    /**
     * 5. Test: Generación de confirmación de pedido
     */
    @Test
    fun `realizar pedido genera la confirmacion con datos correctos`() {
        viewModel.addItem(1) // 5.50
        viewModel.addItem(2) // 6.00
        viewModel.incrementItem(2) // +6.00 (Total: 17.50, Cantidad: 3)
        
        viewModel.placeOrder()
        val confirmation = viewModel.confirmation.value

        assertNotNull("La confirmación no debería ser nula", confirmation)
        assertEquals("El conteo de items debería ser 3", 3, confirmation?.itemCount)
        assertEquals("El total de la confirmación debería ser 17.50", 17.50, confirmation?.total ?: 0.0, 0.001)
    }

    /**
     * 6. Test: Reset completo al cerrar confirmación
     */
    @Test
    fun `cerrar confirmacion limpia el pedido y resetea el estado`() {
        // Llenar el pedido
        viewModel.addItem(1)
        viewModel.placeOrder()
        
        // Cerrar
        viewModel.dismissConfirmation()

        assertNull("La confirmación debería ser nula tras cerrar", viewModel.confirmation.value)
        assertTrue("El pedido debería estar vacío", viewModel.isEmpty.value)
        assertEquals("No debería haber items en la lista", 0, viewModel.orderedItems.value.size)
        assertEquals("El total debería volver a ser 0", 0.0, viewModel.total.value, 0.0)
    }
}
