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

// copypasted from https://android.googlesource.com/platform/external/jmonkeyengine/+/59b2e6871c65f58fdad78cd7229c292f6a177578/engine/src/core/com/jme3/math/Quaternion.java
fun toAngles(x:Float, y:Float, z:Float, w:Float): FloatArray {
    var angles = FloatArray(3)

    val sqw: Float = w * w
    val sqx = (x * x).toFloat()
    val sqy = (y * y).toFloat()
    val sqz: Float = z * z
    val unit = sqx + sqy + sqz + sqw // if normalized is one, otherwise
    // is correction factor
    val test: Float = x * y + z * w
    if (test > 0.499 * unit) { // singularity at north pole
        angles[1] = 2 * FastMath.atan2(x, w)
        angles[2] = FastMath.HALF_PI
        angles[0] = 0f
    } else if (test < -0.499 * unit) { // singularity at south pole
        angles[1] = -2 * FastMath.atan2(x, w)
        angles[2] = -FastMath.HALF_PI
        angles[0] = 0f
    } else {
        angles[1] = FastMath.atan2(2 * y * w - 2 * x * z, sqx - sqy - sqz + sqw) // roll or heading
        angles[2] = FastMath.asin(2 * test / unit) // pitch or attitude
        angles[0] = FastMath.atan2(2 * x * w - 2 * y * z, -sqx + sqy - sqz + sqw) // yaw or bank
    }
    return angles
}