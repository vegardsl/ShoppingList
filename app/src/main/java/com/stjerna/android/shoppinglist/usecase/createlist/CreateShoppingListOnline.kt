package com.stjerna.android.shoppinglist.usecase.createlist

import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import com.stjerna.android.shoppinglist.entity.User
import java.util.*

class CreateShoppingListOnline(
    private val presenter: CreateShoppingListPresenter,
    private val remote: ShoppingListGateway,
    private val userGW: CloudUserGateway
) {
    fun execute(shoppingListName: String) {
        val shoppingList = ShoppingList(UUID.randomUUID(), shoppingListName)
        remote.put(shoppingList) { result -> handleResult(result) }
        userGW.currentUserId()?.let { userId ->
            userGW.get(userId) {
                when (it) {
                    is Success -> associateListWithUser(it.value, shoppingList)
                    is Failure -> presenter.onResult(InteractionResult.ERROR)
                }
            }
        }
    }

    private fun associateListWithUser(user: User, shoppingList: ShoppingList) {
        user.lists.add(shoppingList.id)
        userGW.put(user) {
            handleResult(it)
        }
    }

    private fun handleResult(result: Try<Unit>) {
        when (result) {
            is Success -> presenter.onResult(InteractionResult.SUCCESS)
            is Failure -> presenter.onResult(InteractionResult.ERROR)
        }
    }
}