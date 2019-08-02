package com.stjerna.android.shoppinglist

import com.stjerna.android.shoppinglist.entity.Item
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import io.realm.Realm
import io.realm.RealmList
import io.realm.RealmObject
import io.realm.RealmResults
import io.realm.annotations.PrimaryKey
import java.lang.Error
import java.util.*

class RealmShoppingListGateway private constructor(): ShoppingListGateway {
    override fun delete(id: UUID, onCompletion: (Try<Unit>) -> Unit) {
        val result = realm.where(RealmShoppingList::class.java).equalTo("id", id.toString()).findAll()
        realm.executeTransaction {
            if (result.deleteAllFromRealm()) onCompletion.invoke(Success(Unit))
            else onCompletion.invoke(Failure(Exception("Failed to delete object.")))
        }
    }

    companion object {

        private var instance: ShoppingListGateway = RealmShoppingListGateway()

        fun getInstance(): ShoppingListGateway {
            return instance
        }
    }

    private val realm: Realm = Realm.getDefaultInstance()

    override fun put(shoppingList: ShoppingList, onCompletion: (Try<Unit>) -> Unit) {
        realm.executeTransactionAsync({changingRealm ->
            val realmShoppingList = RealmShoppingList()
            realmShoppingList.id = shoppingList.id.toString()
            realmShoppingList.name = shoppingList.name
            shoppingList.items.values.forEach { item ->
                val realmListItem = RealmListItem()
                realmListItem.uuid = item.id.toString()
                realmListItem.name = item.name
                realmListItem.isChecked = item.isChecked
                realmShoppingList.items.add(realmListItem)
            }
            changingRealm.insertOrUpdate(realmShoppingList)
        }, { // On success.
            onCompletion.invoke(Success(Unit))
        }, { throwable -> // On failure.
            onCompletion.invoke(Failure(throwable))
        })
    }

    override fun get(id: UUID, onCompletion: (Try<ShoppingList>) -> Unit) {
        val results: RealmResults<RealmShoppingList> = realm.where(RealmShoppingList::class.java).equalTo("id", id.toString()).findAll()
        results.first()?.let {
            val name: String = it.name!!
            val shoppingList = ShoppingList(UUID.fromString(it.id), name)
            it.items.forEach {realmListItem ->
                val item = Item(
                    UUID.fromString(realmListItem.uuid),
                    realmListItem.name!!
                )
                item.isChecked = realmListItem.isChecked
                shoppingList.items[item.id] = item
            }
            onCompletion.invoke(Success(shoppingList))
        } ?: onCompletion.invoke(Failure(Error()))
    }

    override fun getAll(onCompletion: (Try<List<ShoppingList>>) -> Unit) {
        val shoppingLists = arrayListOf<ShoppingList>()
        val results: RealmResults<RealmShoppingList> = realm.where(RealmShoppingList::class.java).findAll()
        results.forEach { realmShoppingList ->
            val shoppingList = realmShoppingList.name?.let {
                ShoppingList(
                    UUID.fromString(
                        realmShoppingList.id
                    ), it
                )
            }
            realmShoppingList.items.forEach { realmListItem ->
                val listItem = realmListItem.name?.let {
                    Item(
                        UUID.fromString(
                            realmListItem.uuid
                        ), it
                    )
                }
                listItem?.isChecked = realmListItem.isChecked
                if (listItem != null) shoppingList?.items?.set(listItem.id, listItem)
            }
            shoppingList?.let { shoppingLists.add(it) }
        }
        onCompletion.invoke(Success(shoppingLists))
    }

    override fun observe(onChanged: () -> Unit) {
        realm.addChangeListener {
            onChanged.invoke()
        }
    }

}

open class RealmShoppingList : RealmObject() {
    @PrimaryKey
    var id: String? = null

    var name: String? = null
    var items: RealmList<RealmListItem> = RealmList()
}

open class RealmListItem : RealmObject() {
    @PrimaryKey
    var uuid: String? = null

    var name: String? = null
    var isChecked: Boolean = false
}