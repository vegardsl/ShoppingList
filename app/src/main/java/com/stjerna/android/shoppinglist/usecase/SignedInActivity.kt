package com.stjerna.android.shoppinglist.usecase

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.stjerna.android.shoppinglist.CloudShoppingListGateway
import com.stjerna.android.shoppinglist.CloudUserGateway
import com.stjerna.android.shoppinglist.Repository

abstract class SignedInActivity : AppCompatActivity() {

    private lateinit var repository: Repository
    private val cloudShoppingListGateway = CloudShoppingListGateway.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = Repository.getInstance(
            cloudShoppingListGateway,
            CloudUserGateway()
        )
    }

}