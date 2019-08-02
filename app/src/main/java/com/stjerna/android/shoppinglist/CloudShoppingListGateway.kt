package com.stjerna.android.shoppinglist

import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class CloudShoppingListGateway : ShoppingListGateway {
    override fun delete(id: UUID, onCompletion: (Try<Unit>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observe(onChanged: () -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun put(shoppingList: ShoppingList, onCompletion: (Try<Unit>) -> Unit) {
        onCompletion.invoke(Success(Unit))
    }

    override fun get(id: UUID, onCompletion: (Try<ShoppingList>) -> Unit) {
        onCompletion.invoke(Failure(NotImplementedError("get not implemented.")))
    }

    override fun getAll(onCompletion: (Try<List<ShoppingList>>) -> Unit) {
        onCompletion.invoke(Failure(NotImplementedError("Get all not implemented.")))
    }

}