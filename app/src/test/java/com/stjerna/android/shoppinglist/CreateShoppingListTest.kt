package com.stjerna.android.shoppinglist

import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import com.stjerna.android.shoppinglist.usecase.createlist.CreateShoppingList
import org.junit.Assert.assertEquals
import org.junit.Test

class CreateShoppingListTest : CreateShoppingListPresenter {

    var latestResult: InteractionResult? = null
    private val fakeLocalGateway = FakeShoppingListGateway()
    private val fakeRemoteGateway = FakeShoppingListGateway()

    override fun onResult(interactionResult: InteractionResult) {
        latestResult = interactionResult
    }

    @Test
    fun create() {
        CreateShoppingList(this, fakeRemoteGateway, fakeLocalGateway)
    }

    @Test
    fun createShoppingList_success() {
        fakeRemoteGateway.shallSucceed = true
        fakeLocalGateway.shallSucceed = true
        CreateShoppingList(this, fakeRemoteGateway, fakeLocalGateway).execute(shoppingListName)
        assertEquals(InteractionResult.SUCCESS, latestResult)
    }

    @Test
    fun createShoppingList_remoteFails_returnsFailure() {
        fakeRemoteGateway.shallSucceed = false
        fakeLocalGateway.shallSucceed = true
        CreateShoppingList(this, fakeRemoteGateway, fakeLocalGateway).execute(shoppingListName)
        assertEquals(InteractionResult.ERROR, latestResult)
    }

    @Test
    fun createShoppingList_localFails_returnsFailure() {
        fakeRemoteGateway.shallSucceed = true
        fakeLocalGateway.shallSucceed = false
        CreateShoppingList(this, fakeRemoteGateway, fakeLocalGateway).execute(shoppingListName)
        assertEquals(InteractionResult.ERROR, latestResult)
    }

}

class FakeShoppingListGateway : ShoppingListGateway {
    var shallSucceed = true

    override fun put(shoppingList: ShoppingList, onCompletion: (Try<Unit>) -> Unit) {
        if (shallSucceed) onCompletion.invoke(Success(Unit))
        else onCompletion.invoke(Failure(Exception("Set to fail.")))
    }

    override fun get(id: Long, onCompletion: (Try<ShoppingList>) -> Unit) {
    }

    override fun getAll(onCompletion: (Try<List<ShoppingList>>) -> Unit) {
    }

}

