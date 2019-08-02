package com.stjerna.android.shoppinglist

interface CreateShoppingListPresenter {
    fun onResult(interactionResult: InteractionResult)
}

enum class InteractionResult {
    ERROR, SUCCESS
}

