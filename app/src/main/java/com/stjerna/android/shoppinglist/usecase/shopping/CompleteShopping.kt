package com.stjerna.android.shoppinglist.usecase.shopping

import com.stjerna.android.shoppinglist.Failure
import com.stjerna.android.shoppinglist.Success
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class CompleteShopping(
    private val presenter: CompleteShoppingPresenter,
    private val listGateway: ShoppingListGateway
) {
    fun execute(listId: UUID) {
        listGateway.get(listId) {
            when (it) {
                is Failure -> presenter.failedToCompleteShopping(Failure(it.e))
                is Success -> checkListAndDeleteIfComplete(it.value)
            }
        }
    }

    private fun checkListAndDeleteIfComplete(shoppingList: ShoppingList) {
        if (everyItemIsChecked(shoppingList)) deleteList(shoppingList)
        else getConfirmationBeforeDeleting(shoppingList)
    }

    private fun everyItemIsChecked(shoppingList: ShoppingList): Boolean {
        shoppingList.items.values.forEach { item ->
            if (!item.isChecked) return false
        }
        return true
    }

    private fun getConfirmationBeforeDeleting(value: ShoppingList) {
        presenter.aboutToCloseIncompleteList {
            when (it) {
                CompleteShoppingPresenter.Options.DELETE -> deleteList(value)
                CompleteShoppingPresenter.Options.CANCEL -> presenter.onFinishShoppingResult(
                    wasDeleted = false
                )
            }
        }
    }

    private fun deleteList(value: ShoppingList) {
        listGateway.delete(value.id) {
            when (it) {
                is Failure -> presenter.failedToCompleteShopping(Failure(it.e))
                is Success -> presenter.onFinishShoppingResult(wasDeleted = true)
            }
        }
    }
}

interface CompleteShoppingPresenter {
    fun onFinishShoppingResult(wasDeleted: Boolean)

    fun failedToCompleteShopping(failure: Failure<Throwable>)

    fun aboutToCloseIncompleteList(continuation: (Options) -> Unit)

    enum class Options {
        DELETE, CANCEL
    }
}
