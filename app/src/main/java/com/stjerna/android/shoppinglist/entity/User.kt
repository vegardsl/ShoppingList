package com.stjerna.android.shoppinglist.entity

import java.util.*

class User(val id: String, val nick: String, val email: String, val lists: MutableList<UUID>)
