import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import kotlinx.cinterop.memScoped
import raylib.*
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

@OptIn(ExperimentalForeignApi::class)
val black = cValue<Color> {
    r = 0.toUByte()
    g = 0.toUByte()
    b = 0.toUByte()
    a = 255.toUByte()
}

@OptIn(ExperimentalForeignApi::class)
val white = cValue<Color> {
    r = 255.toUByte()
    g = 255.toUByte()
    b = 255.toUByte()
    a = 255.toUByte()
}

@OptIn(ExperimentalForeignApi::class)
val red = cValue<Color> {
    r = 255.toUByte()
    g = 0.toUByte()
    b = 0.toUByte()
    a = 255.toUByte()
}

@OptIn(ExperimentalForeignApi::class)
val green = cValue<Color> {
    r = 0.toUByte()
    g = 255.toUByte()
    b = 0.toUByte()
    a = 255.toUByte()
}

const val GRID_SIZE = 10
const val CUBE_SIZE = 1.0f

data class Vector3(var x: Float = 0f, var y: Float = 0f, var z: Float = 0f) {
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

data class Food(var position: Vector3 = Vector3())

data class Snake(
    val segments: MutableList<Vector3> = mutableListOf(Vector3()),
    var direction: Vector3 = Vector3(1f, 0f, 0f)
) {
    val length: Int
        get() = segments.size
}

fun spawnFood(food: Food, snake: Snake) {
    var validPosition = false
    while (!validPosition) {
        validPosition = true
        val random = Random(41)
        food.position = Vector3(
            random.nextInt(-GRID_SIZE / 2 + 1, GRID_SIZE / 2 - 1).toFloat(),
            random.nextInt(-GRID_SIZE / 2 + 1, GRID_SIZE / 2 - 1).toFloat(),
            random.nextInt(-GRID_SIZE / 2 + 1, GRID_SIZE / 2 - 1).toFloat()
        )
        for (segment in snake.segments) {
            if (food.position.distance(segment) < 0.1f) {
                validPosition = false
                break
            }
        }
    }
}

fun moveSnake(snake: Snake) {
    for (i in snake.length - 1 downTo 1) {
        snake.segments[i] = snake.segments[i - 1]
    }
    val head = snake.segments[0]
    head.x += snake.direction.x
    head.y += snake.direction.y
    head.z += snake.direction.z
}

fun checkCollision(snake: Snake, food: Food): Boolean {
    return snake.segments[0].distance(food.position) < 0.5f
}

fun checkSelfCollision(snake: Snake): Boolean {
    val head = snake.segments[0]
    for (i in 1 until snake.length) {
        if (head.distance(snake.segments[i]) < 0.1f) {
            return true
        }
    }
    return false
}

fun checkWallCollision(snake: Snake): Boolean {
    val head = snake.segments[0]
    return abs(head.x) > GRID_SIZE / 2 || abs(head.y) > GRID_SIZE / 2 || abs(head.z) > GRID_SIZE / 2
}

val upAxis = Vector3(0f, 1f, 0f) // Y-axis for rotation
val rightAxis = Vector3(1f, 0f, 0f) // X-axis for lateral rotation


@OptIn(ExperimentalForeignApi::class)
fun changeDirection(snake: Snake, directionKey: Int) {
}

//@OptIn(ExperimentalForeignApi::class)
@OptIn(ExperimentalForeignApi::class)
fun main() {
    memScoped {
        val screenWidth = 800
        val screenHeight = 600

        InitWindow(screenWidth, screenHeight, "Snake 3D - Kotlin Native")

        val camera = cValue<Camera3D> {
            position.x = 20f
            position.y = 20f
            position.z = 20f

            target.x = 0f
            target.y = 0f
            target.z = 0f

            up.x = 0f
            up.y = 1f
            up.z = 0f

            fovy = 45f
            projection = CAMERA_PERSPECTIVE.toInt()
        }

        SetTargetFPS(10)

        val snake = Snake()
        val food = Food()
        spawnFood(food, snake)

        var gameOver = false

        while (!WindowShouldClose()) {
            if (!gameOver) {
                // Handle input

                if (IsKeyDown(KEY_UP.toInt())) {
                    snake.direction = snake.direction.rotate(rightAxis, (-90f * DEG2RAD)) // Rotate upward
                }
                if (IsKeyDown(KEY_DOWN.toInt())) {
                    snake.direction = snake.direction.rotate(rightAxis, (90f * DEG2RAD)) // Rotate downward
                }
                if (IsKeyDown(KEY_LEFT.toInt())) {
                    snake.direction = snake.direction.rotate(upAxis, (90f * DEG2RAD)) // Rotate left
                }
                if (IsKeyDown(KEY_RIGHT.toInt())) {
                    snake.direction = snake.direction.rotate(upAxis, (-90f * DEG2RAD)) // Rotate right
                }

                PollInputEvents()

                // Move snake
                moveSnake(snake)

                // Check collisions
//                if (checkCollision(snake, food)) {
//                    snake.segments.add(Vector3(food.position.x, food.position.y, food.position.z))
//                    spawnFood(food, snake)
//                }
//                if (checkSelfCollision(snake) || checkWallCollision(snake)) {
//                    gameOver = true
//                }
            }

            // Draw
            BeginDrawing()
            ClearBackground(white)

            BeginMode3D(camera)

            // Draw game boundaries (cube)
            DrawCubeWires(Vector3().asCValue(), GRID_SIZE.toFloat(), GRID_SIZE.toFloat(), GRID_SIZE.toFloat(), black)

            // Draw snake
            for (segment in snake.segments) {
                DrawCube(segment.asCValue(), CUBE_SIZE, CUBE_SIZE, CUBE_SIZE, green)
            }

            // Draw food
            DrawCube(food.position.asCValue(), CUBE_SIZE, CUBE_SIZE, CUBE_SIZE, red)

            EndMode3D()

            if (gameOver) {
                DrawText("Game Over!", screenWidth / 2 - MeasureText("Game Over!", 40) / 2, screenHeight / 2 - 20, 40, red)
            }

            EndDrawing()
        }

        CloseWindow()
    }
}