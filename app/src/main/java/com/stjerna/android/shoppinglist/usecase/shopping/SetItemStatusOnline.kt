package com.stjerna.android.shoppinglist.usecase.shopping

import com.stjerna.android.shoppinglist.Failure
import com.stjerna.android.shoppinglist.Success
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class SetItemStatusOnline(
    private val presenter: SetItemStatusPresenter,
    private val remote: ShoppingListGateway
) {

    fun execute(shoppingListId: UUID, itemId: UUID, isChecked: Boolean) {
        remote.get(shoppingListId) { result ->
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
        remote.put(value) { result ->
            when (result) {
                is Success -> presenter.setItemStatusResult(Success(Unit))
                is Failure -> presenter.setItemStatusResult(Failure(Exception("")))
            }
        }
    }

}