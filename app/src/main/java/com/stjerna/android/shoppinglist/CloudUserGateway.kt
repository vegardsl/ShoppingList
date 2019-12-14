package com.stjerna.android.shoppinglist

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.stjerna.android.shoppinglist.entity.User
import com.stjerna.android.shoppinglist.entity.UserGateway
import java.util.*

class CloudUserGateway : UserGateway {
    private val db = FirebaseFirestore.getInstance()

    override fun put(user: User, onCompletion: (Try<Unit>) -> Unit) {
        db.collection("users")
            .document(user.id)
            .set(user)
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
                                document["id"] as String,
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
            possibleLists.forEach { list.add(it as UUID) }
        } catch (e: Exception) { // TODO: Specify exception.
            e.printStackTrace()
        }

        return list
    }

    override fun currentUserId(): String? {
        return FirebaseAuth.getInstance().currentUser?.uid
    }
}