package com.stjerna.android.shoppinglist.usecase.createlist

import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class CreateShoppingListOnline(
    private val presenter: CreateShoppingListPresenter,
    private val remote: ShoppingListGateway
) {
    fun execute(shoppingListName: String) {
        val shoppingList = ShoppingList(UUID.randomUUID(), shoppingListName)
        remote.put(shoppingList) { result -> handleResult(result) }
    }

    private fun handleResult(result: Try<Unit>) {
        when (result) {
            is Success -> presenter.onResult(InteractionResult.SUCCESS)
            is Failure -> presenter.onResult(InteractionResult.ERROR)
        }
    }
}