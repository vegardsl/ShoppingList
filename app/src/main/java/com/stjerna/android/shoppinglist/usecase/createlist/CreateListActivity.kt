package com.stjerna.android.shoppinglist.usecase.createlist

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.stjerna.android.shoppinglist.*
import com.stjerna.android.shoppinglist.usecase.editlist.ManageListActivity
import kotlinx.android.synthetic.main.activity_dashboard.*
import android.text.InputType
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.stjerna.android.shoppinglist.usecase.SignedInActivity

class DashboardActivity : SignedInActivity(), CreateShoppingListPresenter {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        val shoppingListsRecyclerViewAdapter =
            ShoppingListsRecyclerViewAdapter()
        shoppingLists_recyclerView.layoutManager = LinearLayoutManager(this)
        shoppingLists_recyclerView.adapter = shoppingListsRecyclerViewAdapter
        shoppingListsRecyclerViewAdapter.onListItemSelected { id ->
            Toast.makeText(this, "Item selected: $id", Toast.LENGTH_LONG).show()
            val intent = Intent(this, ManageListActivity::class.java)
            intent.putExtra("SELECTED_LIST_ID", id.toString())
            startActivity(intent)
        }

        val model = ShoppingListViewModelFactory()
            .create(ShoppingListsViewModel::class.java)

        model.shoppingLists.observe(this, Observer { shoppingLists ->
            shoppingListsRecyclerViewAdapter.setListItems(shoppingLists.values)
        })

    }

    fun onClick(view: View) {
        when (view.id) {
            R.id.add_floatingActionButton -> createDialog()
        }
    }

    override fun onResult(interactionResult: InteractionResult) {
        Toast.makeText(this, interactionResult.toString(), Toast.LENGTH_LONG).show()
    }

    private fun addNewShoppingList(shoppingListName: String) {
        CreateShoppingListOnline(
            presenter = this,
            remote = CloudShoppingListGateway.getInstance(),
            userGW = CloudUserGateway()
        ).execute(shoppingListName)
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Create a new list")

        val input = EditText(this)
        input.inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_NORMAL
        builder.setView(input)

        builder.setPositiveButton("Create") { dialog, which -> addNewShoppingList(input.text.toString()) }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.cancel() }

        builder.show()
    }
}



class ShoppingListsViewModel(repository: Repository) : ViewModel() {
    val shoppingLists = repository.shoppingLists
}

class ShoppingListViewModelFactory : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        @Suppress("UNCHECKED_CAST")
        return ShoppingListsViewModel(
            Repository.getInstance(
                CloudShoppingListGateway.getInstance(),
                CloudUserGateway()

            )
        ) as T
    }
}
