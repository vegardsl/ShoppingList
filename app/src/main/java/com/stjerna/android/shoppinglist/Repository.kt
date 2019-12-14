package com.stjerna.android.shoppinglist

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import com.stjerna.android.shoppinglist.entity.UserGateway
import java.lang.Error
import java.util.*

class Repository private constructor(
    private val shoppingListGateway: ShoppingListGateway,
    private val userGateway: UserGateway
) {
    private val _shoppingLists: MutableLiveData<Map<UUID, ShoppingList>> = MutableLiveData()
    val shoppingLists: LiveData<Map<UUID, ShoppingList>> = _shoppingLists

    companion object {
        private var instance: Repository? = null

        fun getInstance(shoppingListGateway: ShoppingListGateway, userGateway: UserGateway): Repository {
            if (instance == null) instance = Repository(shoppingListGateway, userGateway)
            return instance!!
        }
    }

    init {
        userGateway.currentUserId()?.let { id ->
            userGateway.get(id) {result ->
                when(result) {
                    is Success -> {
                        getAllAndUpdateLiveData(result.value.lists)
                        shoppingListGateway.observe(result.value.lists) {
                            getAllAndUpdateLiveData(result.value.lists)
                        }
                    }
                    is Error -> throw IllegalStateException("No user") // TODO: Handle error state.
                }

            }
        }
    }

    private fun getAllAndUpdateLiveData(lists: MutableList<UUID>) {
        shoppingListGateway.getAll(lists) { result ->
            when (result) {
                is Success -> _shoppingLists.value = result.value.map { it.id to it }.toMap().toSortedMap()
                is Failure -> Log.e(Repository::class.java.simpleName, result.e.message)
            }
        }
    }

}