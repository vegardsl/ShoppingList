package com.stjerna.android.shoppinglist.entity

import com.stjerna.android.shoppinglist.Try
import java.util.*

interface ShoppingListGateway {
    fun put(shoppingList: ShoppingList, onCompletion: (Try<Unit>) -> Unit)

    fun get(id: UUID, onCompletion: (Try<ShoppingList>) -> Unit)

    fun getAll(lists: List<UUID>, onCompletion: (Try<List<ShoppingList>>) -> Unit)
    fun observe(listsToObserve: List<UUID>, onChanged: () -> Unit)
    fun delete(id: UUID, onCompletion: (Try<Unit>) -> Unit)
}