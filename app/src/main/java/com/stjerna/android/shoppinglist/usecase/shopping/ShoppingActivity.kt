package com.stjerna.android.shoppinglist.usecase.shopping

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.usecase.SignedInActivity
import kotlinx.android.synthetic.main.activity_shopping.*
import java.util.*

class ShoppingActivity : SignedInActivity(), SetItemStatusPresenter, CompleteShoppingPresenter {
    override fun onFinishShoppingResult(wasDeleted: Boolean) {
        if (wasDeleted) tryFinish()
    }

    override fun failedToCompleteShopping(failure: Failure<Throwable>) {
        Toast.makeText(
            this,
            "Failed to complete shopping: ${failure.e.message}.",
            Toast.LENGTH_LONG
        ).show()
    }

    override fun aboutToCloseIncompleteList(continuation: (CompleteShoppingPresenter.Options) -> Unit) {
        getDeleteConfirmation(continuation)
    }

    override fun setItemStatusResult(result: Try<Unit>) {
        Toast.makeText(this, "Item state was updated.", Toast.LENGTH_LONG).show()
    }

    lateinit var selectedListId: UUID
    private val setItemStatus = SetItemStatusOnline(this, CloudShoppingListGateway.getInstance())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping)

        val adapter = ShoppingRecyclerViewAdapter()
        shopping_items_recyclerView.layoutManager = LinearLayoutManager(this)
        shopping_items_recyclerView.adapter = adapter
        adapter.onListItemSelected { id, isChecked ->
            setItemStatus.execute(selectedListId, id, isChecked)
        }
        if (intent.hasExtra("SELECTED_LIST_ID")) {
            selectedListId = UUID.fromString(intent.getStringExtra("SELECTED_LIST_ID"))
            val model = ShoppingViewModelFactory(selectedListId)
                .create(ShoppingViewModel::class.java)

            model.shoppingList.observe(this, Observer { shoppingList ->
                if (shoppingList == null) {
                    tryFinish()
                    return@Observer
                }
                adapter.setListItems(shoppingList.items.values)
                title = shoppingList.name
            })
        } else {
            finish()
        }

        complete_shopping_button.setOnClickListener {
            CompleteShopping(this, CloudShoppingListGateway.getInstance()).execute(
                selectedListId
            )
        }

    }

    private fun getDeleteConfirmation(continuation: (CompleteShoppingPresenter.Options) -> Unit) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Forgetting something?")
        builder.setMessage("Some items in the list have not been checked off.")
        builder.setPositiveButton("Delete") { _, _ -> continuation.invoke(CompleteShoppingPresenter.Options.DELETE) }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            continuation.invoke(CompleteShoppingPresenter.Options.CANCEL)
            dialog.cancel()
        }

        builder.show()
    }

    private fun tryFinish() {
        try {
            finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


}

class ShoppingViewModel(private val listId: UUID, repository: Repository) : ViewModel() {
    val shoppingList: LiveData<ShoppingList?> =
        Transformations.map(repository.shoppingLists, ::mapToShoppingList)

    private fun mapToShoppingList(shoppingLists: Map<UUID, ShoppingList>): ShoppingList? {
        return shoppingLists[listId]
    }
}

class ShoppingViewModelFactory(private val listId: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ShoppingViewModel(
            listId,
            Repository.getInstance(
                CloudShoppingListGateway.getInstance(),
                CloudUserGateway()
            )
        ) as T
    }

}