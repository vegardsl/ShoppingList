package com.stjerna.android.shoppinglist.entity

import com.stjerna.android.shoppinglist.Try

interface UserGateway {
    fun put(user: User, onCompletion: (Try<Unit>) -> Unit)

    fun get(id: String, onCompletion: (Try<User>) -> Unit)

    fun currentUserId(): String?
}