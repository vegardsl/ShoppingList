package com.stjerna.android.shoppinglist

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.util.*

class CloudShoppingListGateway : ShoppingListGateway {
    private val db = FirebaseFirestore.getInstance()

    override fun delete(id: UUID, onCompletion: (Try<Unit>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observe(onChanged: () -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun put(shoppingList: ShoppingList, onCompletion: (Try<Unit>) -> Unit) {
        val dbShoppingList = hashMapOf(
            "id" to shoppingList.id,
            "items" to shoppingList.items
        )

        db.collection("lists")
            .add(dbShoppingList)
            .addOnSuccessListener {
                onCompletion.invoke(Success(Unit))
            }
            .addOnFailureListener {
                onCompletion.invoke(Failure(it))
            }
    }

    override fun get(id: UUID, onCompletion: (Try<ShoppingList>) -> Unit) {
        onCompletion.invoke(Failure(NotImplementedError("get not implemented.")))
    }

    override fun getAll(onCompletion: (Try<List<ShoppingList>>) -> Unit) {
        db.collection("lists")
            .get()
            .addOnSuccessListener { result ->
                val mutableList = mutableListOf<ShoppingList>()
                for (document in result) {
                    Log.d(TAG, "${document.id} => ${document.data}")
                }
                onCompletion(Success(mutableList))
            }
            .addOnFailureListener { exception ->
                onCompletion.invoke(Failure(exception))
            }
    }

    companion object {
        val TAG = CloudShoppingListGateway::class.java.simpleName
    }

}