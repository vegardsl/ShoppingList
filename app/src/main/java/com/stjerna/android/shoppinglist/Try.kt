package com.stjerna.android.shoppinglist

sealed class Try<out T> {

    abstract fun isSuccess(): Boolean

    abstract fun isFailure(): Boolean

}

data class Success<out T>(val value: T) : Try<T>() {

    override fun isSuccess(): Boolean = true

    override fun isFailure(): Boolean = false

}

data class Failure<out T>(val e: Throwable) : Try<T>() {

    override fun isSuccess(): Boolean = false

    override fun isFailure(): Boolean = true

}