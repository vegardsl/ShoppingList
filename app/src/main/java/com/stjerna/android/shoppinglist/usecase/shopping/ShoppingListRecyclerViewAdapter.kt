package com.stjerna.android.shoppinglist.usecase.shopping

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.stjerna.android.shoppinglist.R
import com.stjerna.android.shoppinglist.entity.Item
import java.util.*

class ShoppingRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var items: List<ListItem> = listOf()
    private var onItemSelectedListener: ((id: UUID, isChecked: Boolean) -> Unit)? = null

    fun onListItemSelected(onSelected: (id: UUID, isChecked: Boolean) -> Unit) {
        onItemSelectedListener = onSelected
    }

    fun setListItems(shoppingLists: Collection<Item>) {
        val newItems: ArrayList<ListItem> = arrayListOf()
        shoppingLists.forEach {
            newItems.add(ListItem(it.id, it.name, it.isChecked))
        }

        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val layoutInflater: LayoutInflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        val view = layoutInflater.inflate(R.layout.shopping_activity_list_item, parent, false)
        return ShoppingListViewHolder(view)
    }

    override fun getItemCount() = items.count()

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder !is ShoppingListViewHolder) return
        holder.shoppingListNameTextView.text = items[position].name
        holder.checkBox.isChecked = items[position].isChecked
        holder.itemId = items[position].id
    }

    inner class ShoppingListViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val shoppingListNameTextView: TextView = view.findViewById(R.id.shopping_list_name_textView)
        val checkBox: CheckBox = view.findViewById(R.id.item_found_checkBox)
        var itemId: UUID? = null

        init {
            checkBox.setOnClickListener {
                itemId?.let { onItemSelectedListener?.invoke(it, checkBox.isChecked) }
            }
        }
    }
}

class ListItem(val id: UUID, val name: String, val isChecked: Boolean)
