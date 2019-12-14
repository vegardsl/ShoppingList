package com.stjerna.android.shoppinglist

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.stjerna.android.shoppinglist.entity.User
import com.stjerna.android.shoppinglist.entity.UserGateway
import java.util.*

class CloudUserGateway : UserGateway {
    private var subscription: ListenerRegistration? = null

    override fun subscribeToCurrentUser(onChanged: () -> Unit) {
        currentUserId()?.let {
            val docRef = db.collection("users").document(it)
            subscription = docRef.addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w(CloudShoppingListGateway.TAG, "Listen failed.", e)
                    return@addSnapshotListener
                }

                val source = if (snapshot != null && snapshot.metadata.hasPendingWrites())
                    "Local"
                else
                    "Server"

                if (snapshot != null && snapshot.exists()) {
                    Log.d(CloudShoppingListGateway.TAG, "$source data: ${snapshot.data}")
                } else {
                    Log.d(CloudShoppingListGateway.TAG, "$source data: null")
                }

                onChanged.invoke()
            }
        }
    }

    override fun unsubscribeToCurrentUser() {
        subscription?.remove()
        subscription = null
    }

    private val db = FirebaseFirestore.getInstance()

    override fun put(user: User, onCompletion: (Try<Unit>) -> Unit) {
        val lists = mutableListOf<String>()
        user.lists.forEach { listId -> lists.add(listId.toString()) }

        val dbUser = hashMapOf(
            "email" to user.email,
            "nick" to user.nick,
            "lists" to lists
        )

        db.collection("users")
            .document(user.id)
            .set(dbUser)
            .addOnSuccessListener {
                onCompletion.invoke(Success(Unit))
            }
            .addOnFailureListener {
                onCompletion.invoke(Failure(it))
            }
    }

    override fun get(id: String, onCompletion: (Try<User>) -> Unit) {
        val docRef = db.collection("users").document(id)
        docRef.get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    onCompletion.invoke(
                        Success(
                            User(
                                document.id,
                                document["nick"] as String,
                                document["email"] as String,
                                mapList(document)
                            )
                        )
                    )
                } else {
                    onCompletion.invoke(Failure(Exception("User not found"))) // TODO: Make own use case exceptions.
                }
            }
            .addOnFailureListener { exception ->
                onCompletion.invoke(Failure(exception))
            }
    }

    private fun mapList(document: DocumentSnapshot): MutableList<UUID> {
        val possibleLists = document["lists"]
        if (possibleLists is List<*>) {
            return tryMap(possibleLists)
        } else {
            throw IllegalArgumentException("Wrong data type. Expected List<*>.")
        }
    }

    private fun tryMap(possibleLists: List<*>): MutableList<UUID> {
        val list = mutableListOf<UUID>()
        try {
            possibleLists.forEach { list.add(UUID.fromString(it as String)) }
        } catch (e: Exception) { // TODO: Specify exception.
            e.printStackTrace()
        }

        return list
    }

    override fun currentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}