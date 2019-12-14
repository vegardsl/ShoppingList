package com.stjerna.android.shoppinglist

import android.app.Application
import android.util.Log
import io.realm.Realm

class MyApplication : Application() {

    lateinit var repository: Repository

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Log.d(MyApplication::class.java.simpleName, "MyApplication, onCreate()")
    }
}