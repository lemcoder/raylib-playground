import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlin.math.sqrt

data class Vector3(
    var x: Float = 0f,
    var y: Float = 0f,
    var z: Float = 0f
) {
    fun distance(other: Vector3): Float {
        return sqrt(
            (x - other.x) * (x - other.x) +
                    (y - other.y) * (y - other.y) +
                    (z - other.z) * (z - other.z)
        )
    }

    fun rotate(axis: Vector3, angle: Float): Vector3 {
        val v = this
        val c = kotlin.math.cos(angle)
        val s = kotlin.math.sin(angle)

        val dot = v.x * axis.x + v.y * axis.y + v.z * axis.z
        val crossX = axis.y * v.z - axis.z * v.y
        val crossY = axis.z * v.x - axis.x * v.z
        val crossZ = axis.x * v.y - axis.y * v.x

        return Vector3(
            v.x * c + crossX * s + axis.x * dot * (1 - c),
            v.y * c + crossY * s + axis.y * dot * (1 - c),
            v.z * c + crossZ * s + axis.z * dot * (1 - c)
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    fun asCValue() = cValue<raylib.Vector3> {
        this.x = this@Vector3.x
        this.y = this@Vector3.y
        this.z = this@Vector3.z
    }
}
