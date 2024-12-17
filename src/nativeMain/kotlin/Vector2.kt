import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlin.math.sqrt

data class Vector2(
    var x: Float = 0f,
    var y: Float = 0f,
) {
    fun distance(other: Vector2): Float {
        return sqrt(
            (x - other.x) * (x - other.x) +
                    (y - other.y) * (y - other.y)
        )
    }

    @OptIn(ExperimentalForeignApi::class)
    fun asCValue() = cValue<raylib.Vector2> {
        this.x = this@Vector2.x
        this.y = this@Vector2.y
    }
}
