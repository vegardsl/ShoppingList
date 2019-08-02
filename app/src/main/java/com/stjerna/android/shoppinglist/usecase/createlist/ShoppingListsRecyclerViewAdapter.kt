package com.stjerna.android.shoppinglist.usecase.createlist

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stjerna.android.shoppinglist.R
import com.stjerna.android.shoppinglist.entity.ShoppingList
import java.util.*
import kotlin.collections.ArrayList

class ShoppingListsRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ListItem> = listOf()
    private var onItemSelectedListener: ((id: UUID) -> Unit)? = null

    fun onListItemSelected(onSelected : (id: UUID) -> Unit) {
        onItemSelectedListener = onSelected
    }

    fun setListItems(shoppingLists: Collection<ShoppingList>) {
        val newItems: ArrayList<ListItem> = arrayListOf()
        shoppingLists.forEach {
            newItems.add(ListItem(it.id, it.name))
        }

        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater = parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = layoutInflater.inflate(R.layout.shopping_list_list_item, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun getItemCount() = items.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is ShoppingListViewHolder) return
        holder.shoppingListNameTextView.text = items[position].name
        holder.itemView.setOnClickListener {
            onItemSelectedListener?.invoke(items[position].id)
        }
    }

    class ShoppingListViewHolder(view: View): RecyclerView.ViewHolder(view) {
        val shoppingListNameTextView: TextView = view.findViewById(R.id.shopping_list_name_textView)
    }
}

class ListItem(val id: UUID, val name: String)
