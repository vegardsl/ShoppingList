package com.stjerna.android.shoppinglist.usecase.editlist

import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.entity.Item
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class AddListItemOnline(
    private val presenter: CreateShoppingListPresenter,
    private val remote: ShoppingListGateway
) {
    fun execute(id: UUID, itemName: String) {
        remote.get(id) { result ->
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
        remote.put(shoppingList) { result -> handleResult(result) }
    }

    private fun handleResult(
        result: Try<Unit>
    ) {
        when (result) {
            is Success -> presenter.onResult(InteractionResult.SUCCESS)
            is Failure -> presenter.onResult(InteractionResult.ERROR)
        }
    }
}