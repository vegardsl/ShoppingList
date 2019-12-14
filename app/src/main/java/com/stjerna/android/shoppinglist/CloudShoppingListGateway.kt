package com.stjerna.android.shoppinglist

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.stjerna.android.shoppinglist.entity.Item
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class CloudShoppingListGateway : ShoppingListGateway {
    private val db = FirebaseFirestore.getInstance()

    override fun delete(id: UUID, onCompletion: (Try<Unit>) -> Unit) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun observe(listsToObserve: List<UUID>, onChanged: () -> Unit) {
        listsToObserve.forEach { observeList(it, onChanged) }
    }

    private fun observeList(id: UUID, onChanged: () -> Unit) {
        val docRef = db.collection("lists").document(id.toString())
        docRef.addSnapshotListener { snapshot, e ->
            if (e != null) {
                Log.w(TAG, "Listen failed.", e)
                return@addSnapshotListener
            }

            val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                "Local"
            else
                "Server"

            if (snapshot != null && snapshot.exists()) {
                Log.d(TAG, "$source data: ${snapshot.data}")
            } else {
                Log.d(TAG, "$source data: null")
            }

            onChanged.invoke()
        }


    }

    override fun put(shoppingList: ShoppingList, onCompletion: (Try<Unit>) -> Unit) {

        //val batch = db.batch()

        val itemList = mutableListOf<Any?>()

        shoppingList.items.forEach { (uuid, item) ->
            val dbItem = hashMapOf(
                "id" to item.id.toString(),
                "name" to item.name,
                "isChecked" to item.isChecked
            )

            itemList.add(dbItem)
            // val docRef = db.collection("lists")
            //     .document(shoppingList.id.toString())
            //     .collection("items").document(uuid.toString())
//
            // batch.set(docRef, dbItem)
        }

        val dbShoppingList = hashMapOf(
            "name" to shoppingList.name,
            "items" to itemList
        )

        val docRef = db.collection("lists").document(shoppingList.id.toString())
        // batch.set(docRef, dbShoppingList)

        docRef.set(dbShoppingList)
            .addOnSuccessListener {
                onCompletion.invoke(Success(Unit))
            }.addOnFailureListener {
                onCompletion.invoke(Failure(it))
            }
    }

    override fun get(id: UUID, onCompletion: (Try<ShoppingList>) -> Unit) {
        db.collection("lists").document(id.toString())
            .get()
            .addOnSuccessListener { document ->
                val listId = try {
                    UUID.fromString(document.id)
                } catch (e: IllegalArgumentException) {
                    e.printStackTrace()
                    onCompletion.invoke(Failure(IOException("Failed to resolve list id: ${document.id}")))
                    return@addOnSuccessListener
                }
                val sl = ShoppingList(
                    listId,
                    document["name"] as String
                )

                val items = document["items"] as ArrayList<HashMap<String, Any?>>
                items.forEach {
                    val item = Item(
                        id = UUID.fromString(it["id"] as String),
                        name = it["name"] as String
                    )
                    item.isChecked = it["isChecked"] as Boolean
                    sl.items[item.id] = item
                }
                onCompletion(Success(sl))
            }.addOnFailureListener { exception ->
                onCompletion.invoke(Failure(exception))
            }
    }

    override fun getAll(lists: List<UUID>, onCompletion: (Try<List<ShoppingList>>) -> Unit) {
        // TODO: Change query to get lists from the list of lists to get.
        db.collection("lists")
            .get()
            .addOnSuccessListener { result ->
                val mutableList = mutableListOf<ShoppingList>()
                for (document in result) {
                    val id = try {
                        UUID.fromString(document.id)
                    } catch (e: IllegalArgumentException) {
                        e.printStackTrace()
                        continue
                    }
                    val sl = ShoppingList(
                        id,
                        document["name"] as String
                    )

                    val items = document["items"] as ArrayList<HashMap<String, Any?>>
                    items.forEach {
                        val item = Item(
                            id = UUID.fromString(it["id"] as String),
                            name = it["name"] as String
                        )
                        item.isChecked = it["isChecked"] as Boolean
                        sl.items[item.id] = item
                    }
                    mutableList.add(sl)
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