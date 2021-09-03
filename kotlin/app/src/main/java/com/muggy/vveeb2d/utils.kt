package com.muggy.vveeb2d

import android.content.Context
import kotlin.math.pow
import kotlin.math.sqrt


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

class Vector3 (
    val x: Float,
    val y: Float,
    val z: Float,
) {

    fun distanceFrom(otherVector: Vector3): Float{
        return sqrt(
            (otherVector.x - x).pow(2)
            + (otherVector.y - y).pow(2)
            + (otherVector.z - z).pow(2)
        )
    }

    fun middlePointFrom(otherVector: Vector3): Vector3{
        return Vector3(
            (x + otherVector.x) / 2,
            (y + otherVector.y) / 2,
            (z + otherVector.z) / 2,
        )
    }

    override fun toString(): String {
        return "[x: ${x}, y: ${y}, z: ${z},]"
    }

    operator fun plus(other: Vector3): Vector3 {
        return Vector3(
            x + other.x,
            y + other.y,
            z + other.z,
        )
    }

    operator fun minus(other: Vector3): Vector3 {
        return Vector3(
            x - other.x,
            y - other.y,
            z - other.z,
        )
    }

    operator fun unaryMinus(): Vector3 {
        return Vector3(-x, -y, -z,)
    }

    companion object {
        fun pointAvarage(vararg points: Vector3):Vector3{
            var sumX = 0f
            var sumY = 0f
            var sumZ = 0f
            val pointsLength = points.size

            for (point:Vector3 in points){
                sumX += point.x
                sumY += point.y
                sumZ += point.z
            }

            return Vector3(sumX / pointsLength, sumY / pointsLength, sumZ / pointsLength)
        }
    }
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

fun mapNumber(
    value: Float,
    istart: Float,
    istop: Float,
    ostart: Float,
    ostop: Float
): Float {
    return ostart + (ostop - ostart) * ((value - istart) / (istop - istart))
}

fun logisticBias (input:Float, a: Float = 40f, c: Float = 1f, k:Float = 14f): Float {
    return c / (1f + (a * Math.E.toFloat().pow(-k).pow(input)))
}
fun logisticBias (input:Double, a: Double = 40.0, c: Double = 1.0, k:Double = 14.0): Double {
    return c / (1 + (a * Math.E.toFloat().pow(-k.toFloat()).pow(input.toFloat())))
}