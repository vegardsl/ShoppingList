package com.stjerna.android.shoppinglist.usecase.shopping

import com.stjerna.android.shoppinglist.Failure
import com.stjerna.android.shoppinglist.Success
import com.stjerna.android.shoppinglist.Try
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class SetItemStatus(
    private val presenter: SetItemStatusPresenter,
    private val local: ShoppingListGateway,
    val remote: ShoppingListGateway
) {

    fun execute(shoppingListId: UUID, itemId: UUID, isChecked: Boolean) {
        local.get(shoppingListId) { result ->
            when (result) {
                is Success -> updateState(result.value, itemId, isChecked)
                is Failure -> presenter.setItemStatusResult(Failure(Exception("")))
            }
        }
    }

    private fun updateState(
        value: ShoppingList,
        itemId: UUID,
        checked: Boolean
    ) {
        value.items[itemId]?.isChecked = checked
        local.put(value) { result ->
            when (result) {
                is Success -> presenter.setItemStatusResult(Success(Unit))
                is Failure -> presenter.setItemStatusResult(Failure(Exception("")))
            }
        }
    }

}

interface SetItemStatusPresenter {
    fun setItemStatusResult(result: Try<Unit>)
}