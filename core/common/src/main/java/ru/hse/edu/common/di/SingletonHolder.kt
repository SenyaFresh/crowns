package ru.hse.edu.common.di

/**
 * Singleton holder for Dagger dependency injection.
 */
open class SingletonHolder<out T>(private val constructor: ( ) -> T) {

    @Volatile private var instance: T? = null

    fun getInstance(): T {
        return when {
            instance != null -> instance!!
            else -> synchronized(this) {
                if (instance == null) {
                    instance = constructor()
                }
                instance!!
            }
        }
    }
}