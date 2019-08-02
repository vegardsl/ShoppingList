package com.stjerna.android.shoppinglist.usecase.editlist

import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.entity.Item
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class AddListItem(
    private val presenter: CreateShoppingListPresenter,
    private val remote: ShoppingListGateway,
    private val local: ShoppingListGateway
) {
    fun execute(id: UUID, itemName: String) {
        local.get(id) {result ->
            when (result) {
                is Success -> addListItem(result.value, itemName)
                is Failure -> presenter.onResult(InteractionResult.ERROR)
            }
        }

    }

    private fun addListItem(
        shoppingList: ShoppingList,
        itemName: String
    ) {
        val randomUUID = UUID.randomUUID()
        shoppingList.items[randomUUID] = Item(randomUUID, itemName)
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