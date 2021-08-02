package com.muggy.vveeb2d

import android.content.Context

class JavascriptBindings(private val context: Context) {
    private var listeners: HashMap<String, MutableList<(Any)-> Any>> = HashMap<String, MutableList<(Any)-> Any>>()

    fun on(event:String, callback:(Any)-> Any): () -> Boolean? {
        if (listeners[event] == null){
            listeners[event] = mutableListOf()
            listeners[event]?.add(callback)
        }
        else{
            listeners[event]?.add(callback)
        }

        return { listeners[event]?.remove(callback) }
    }

    fun emit(event: String, data: Any){
        listeners[event]?.forEach({ callback ->
            try {
                callback(data)
            }
            catch (ouf: Error){
                listeners[event]?.remove(callback)
            }
        })
    }
}
