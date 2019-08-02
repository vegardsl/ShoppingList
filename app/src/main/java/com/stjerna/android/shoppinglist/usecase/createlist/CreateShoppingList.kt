package com.stjerna.android.shoppinglist.usecase.createlist

import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class CreateShoppingList(
    private val presenter: CreateShoppingListPresenter,
    private val remote: ShoppingListGateway,
    private val local: ShoppingListGateway
) {
    fun execute(shoppingListName: String) {
        val shoppingList = ShoppingList(UUID.randomUUID(), shoppingListName)
        remote.put(shoppingList) { result -> handleResult(result, shoppingList) }
    }

    private fun handleResult(
        result: Try<Unit>,
        shoppingList: ShoppingList
    ) {
        when (result) {
            is Success -> storeLocally(shoppingList)
            is Failure -> presenter.onResult(InteractionResult.ERROR)
        }
    }

    private fun storeLocally(shoppingList: ShoppingList) {
        local.put(shoppingList) { result ->
            when (result) {
                is Success -> presenter.onResult(InteractionResult.SUCCESS)
                is Failure -> presenter.onResult(InteractionResult.ERROR)
            }
        }
    }
}