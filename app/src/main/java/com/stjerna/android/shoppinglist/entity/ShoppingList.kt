package com.stjerna.android.shoppinglist.entity

import java.util.*
import kotlin.collections.HashMap

class ShoppingList(val id: UUID, val name: String) {

    var items = HashMap<UUID, Item>()

    fun addItem(name: String) {
        val id = UUID.randomUUID()
        items[id] = Item(id, name)
    }

    fun toggleCheckItem(id: UUID) {
        items[id]?.let { it.isChecked = !it.isChecked }
    }
}

class Item(val id: UUID, val name: String) {
    var isChecked = false
}
