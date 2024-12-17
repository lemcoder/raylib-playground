import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue

data class Color(
    var r: Int = 0,
    var g: Int = 0,
    var b: Int = 0,
    var a: Int = 255
) {
    @OptIn(ExperimentalForeignApi::class)
    fun asCValue() = cValue<raylib.Color> {
        this.r = this@Color.r.toUByte()
        this.g = this@Color.g.toUByte()
        this.b = this@Color.b.toUByte()
        this.a = this@Color.a.toUByte()
    }

    companion object
}

val Color.Companion.BLACK: Color
    get() = Color(0, 0, 0, 255)

val Color.Companion.WHITE: Color
    get() = Color(255, 255, 255, 255)

val Color.Companion.RED: Color
    get() = Color(255, 0, 0, 255)

val Color.Companion.GREEN: Color
    get() = Color(0, 255, 0, 255)

val Color.Companion.BLUE: Color
    get() = Color(0, 0, 255, 255)

val Color.Companion.SKY_BLUE: Color
    get() = Color(135, 206, 235, 255)

val Color.Companion.PURPLE: Color
    get() = Color(255, 0, 255, 255)

val Color.Companion.YELLOW: Color
    get() = Color(255, 255, 0, 255)

val Color.Companion.ORANGE: Color
    get() = Color(255, 165, 0, 255)

val Color.Companion.LIGHT_GRAY: Color
    get() = Color(200, 200, 200, 255)

val Color.Companion.DARK_GRAY: Color
    get() = Color(80, 80, 80, 255)

val Color.Companion.LIME: Color
    get() = Color(50, 205, 50, 255)

val Color.Companion.GOLD: Color
    get() = Color(255, 215, 0, 255)

val Color.Companion.DARK_PURPLE: Color
    get() = Color(128, 0, 128, 255)