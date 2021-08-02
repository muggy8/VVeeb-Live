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

class EulerAngles(
    val heading:Double,
    val attitude:Double,
    val bank:Double,
){
    val yaw get() = heading
    val pitch get() = bank
    val roll get() = attitude

    val yawDeg get() = Math.toDegrees(yaw)
    val pitchDeg get() = Math.toDegrees(pitch)
    val rollDeg get() = Math.toDegrees(roll)
}

fun toAngles (x:Double, y:Double, z:Double, w:Double):EulerAngles {
    val sqw = w*w;
    val sqx = x*x;
    val sqy = y*y;
    val sqz = z*z;
    val unit = sqx + sqy + sqz + sqw; // if normalised is one, otherwise is correction factor
    val test = x*y + z*w;

    var heading:Double
    var attitude:Double
    var bank:Double

    if (test > 0.499*unit) { // singularity at north pole
        heading = 2 * Math.atan2(x,w)
        attitude = Math.PI/2
        bank = 0.0
    }
    if (test < -0.499*unit) { // singularity at south pole
        heading = -2 * Math.atan2(x,w)
        attitude = -Math.PI/2
        bank = 0.0
    }
    else {
        heading = Math.atan2(2*y*w-2*x*z , sqx - sqy - sqz + sqw)
        attitude = Math.asin(2*test/unit)
        bank = Math.atan2(2*x*w-2*y*z , -sqx + sqy - sqz + sqw)
    }

    return EulerAngles(heading, attitude, bank)
}