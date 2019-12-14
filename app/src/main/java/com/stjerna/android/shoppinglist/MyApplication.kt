package com.stjerna.android.shoppinglist

import android.app.Application
import android.util.Log
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.stjerna.android.shoppinglist.entity.ShoppingListGateway
import io.realm.Realm

class MyApplication : Application() {

    lateinit var shoppingListGateway: ShoppingListGateway
    lateinit var foregroundBackgroundListener: ForegroundBackgroundListener

    override fun onCreate() {
        super.onCreate()

        Realm.init(this)
        Log.d(MyApplication::class.java.simpleName, "MyApplication, onCreate()")

        ProcessLifecycleOwner.get()
            .lifecycle
            .addObserver(
                ForegroundBackgroundListener()
                    .also { foregroundBackgroundListener = it })
    }
}

class ForegroundBackgroundListener : LifecycleObserver {

    private val shoppingListGateway = CloudShoppingListGateway.getInstance()

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun startSomething() {
        Log.v("ProcessLog", "APP IS ON FOREGROUND")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun stopSomething() {
        Log.v("ProcessLog", "APP IS IN BACKGROUND")
        shoppingListGateway.unsubscribeAll()
    }
}