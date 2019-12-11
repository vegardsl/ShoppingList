package com.stjerna.android.shoppinglist.usecase.editlist

import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.R
import com.stjerna.android.shoppinglist.entity.ShoppingList
import com.stjerna.android.shoppinglist.usecase.shopping.ShoppingActivity
import kotlinx.android.synthetic.main.activity_manage_list.*
import java.util.*

class ManageListActivity : AppCompatActivity(), CreateShoppingListPresenter {
    override fun onResult(interactionResult: InteractionResult) {
        Toast.makeText(this, interactionResult.toString(), Toast.LENGTH_LONG).show()
    }

    lateinit var selectedListId: UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_list)

        val adapter = ManageListRecyclerViewAdapter()
        shopping_items_recyclerView.layoutManager = LinearLayoutManager(this)
        shopping_items_recyclerView.adapter = adapter
        adapter.onListItemSelected { id ->
            Toast.makeText(this, "Item selected: $id", Toast.LENGTH_LONG).show()
        }
        if (intent.hasExtra("SELECTED_LIST_ID")) {
            selectedListId = UUID.fromString(intent.getStringExtra("SELECTED_LIST_ID"))
            val model = ManageListViewModelFactory(selectedListId)
                .create(ManageListViewModel::class.java)

            model.shoppingList.observe(this, Observer { shoppingList ->
                if (shoppingList == null) {
                    finish()
                    return@Observer
                }
                adapter.setListItems(shoppingList.items.values)
            })
        } else {
            finish()
        }

        add_item_floatingActionButton.setOnClickListener {
            createDialog()
        }

        go_shopping_floatingActionButton.setOnClickListener {
            val intent = Intent(this, ShoppingActivity::class.java)
            intent.putExtra("SELECTED_LIST_ID", selectedListId.toString())
            startActivity(intent)
        }

    }

    private fun addNewListItem(listItemName: String) {
        AddListItemOnline(
            this,
            CloudShoppingListGateway()
        ).execute(selectedListId, listItemName)
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add an item to the list")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        builder.setView(input)

        builder.setPositiveButton("Add") { _, _ -> addNewListItem(input.text.toString()) }
        builder.setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

}

class ManageListViewModel(private val listId: UUID, repository: Repository) : ViewModel() {
    val shoppingList: LiveData<ShoppingList?> = Transformations.map(repository.shoppingLists, ::mapToShoppingList)

    private fun mapToShoppingList(shoppingLists: Map<UUID, ShoppingList>): ShoppingList? {
        return shoppingLists[listId]
    }
}

class ManageListViewModelFactory(private val listId: UUID) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ManageListViewModel(
            listId,
            Repository.getInstance(
                RealmShoppingListGateway.getInstance()
            )
        ) as T
    }

}