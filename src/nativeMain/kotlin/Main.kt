@file: OptIn(ExperimentalForeignApi::class)
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.cValue
import platform.posix.rand
import platform.posix.srand
import platform.posix.time
import raylib.*

const val TILE_SIZE = 20
const val ROWS = 20
const val COLS = 20

var foodPos = shortArrayOf(0, 0)
var snakePos = shortArrayOf(0, 0)
var snakeDirection: Short = 0
var lost: Short = 0
var gameRunCooldown: Short = 0
val snakePartTime = Array(ROWS) { ShortArray(COLS) }
var creationPower: Short = 2

val black= cValue<Color> {
    r = 0.toUByte()
    g = 0.toUByte()
    b = 0.toUByte()
    a = 255.toUByte()
}

val red = cValue<Color> {
    r = 255.toUByte()
    g = 0.toUByte()
    b = 0.toUByte()
    a = 255.toUByte()
}

val green = cValue<Color> {
    r = 0.toUByte()
    g = 255.toUByte()
    b = 0.toUByte()
    a = 255.toUByte()
}

fun main() {
    srand(time(null).toUInt())
    InitWindow(ROWS * TILE_SIZE, COLS * TILE_SIZE, "Snake")
    SetTargetFPS(10)

    foodPos[0] = (rand() % ROWS).toShort()
    foodPos[1] = (rand() % COLS).toShort()
    snakePos[0] = (ROWS / 2).toShort()
    snakePos[1] = (COLS / 2).toShort()

    snakePartTime.forEach { row -> row.fill(0) }

    while (!WindowShouldClose()) {
        if (gameRunCooldown == 2.toShort()) {
            snakePos[0] = (snakePos[0] + if (snakeDirection == 0.toShort()) 1 else if (snakeDirection == 1.toShort()) -1 else 0).toShort()
            snakePos[1] = (snakePos[1] + if (snakeDirection == 2.toShort()) 1 else if (snakeDirection == 3.toShort()) -1 else 0).toShort()

            if (snakePartTime[snakePos[0].toInt()][snakePos[1].toInt()] > 0) lost = 1
            snakePartTime[snakePos[0].toInt()][snakePos[1].toInt()] = creationPower

            for (i in 0 until ROWS) {
                for (j in 0 until COLS) {
                    if (snakePartTime[i][j] > 0) snakePartTime[i][j]--
                }
            }
            gameRunCooldown = 0
        }
        gameRunCooldown++

        snakeDirection = when {
            IsKeyDown(KEY_LEFT.toInt())  -> 1
            IsKeyDown(KEY_RIGHT.toInt()) -> 0
            IsKeyDown(KEY_UP.toInt())    -> 3
            IsKeyDown(KEY_DOWN.toInt())  -> 2
            else                         -> snakeDirection
        }.toShort()

        if (snakePos[0] < 0 || snakePos[1] < 0 || snakePos[0] >= ROWS || snakePos[1] >= COLS) lost = 1

        if (snakePos[0] == foodPos[0] && snakePos[1] == foodPos[1]) {
            do {
                foodPos[0] = (rand() % ROWS).toShort()
                foodPos[1] = (rand() % COLS).toShort()
            } while (snakePartTime[foodPos[0].toInt()][foodPos[1].toInt()] > 0)
            creationPower++
        }

        BeginDrawing()
        ClearBackground(black)

        if (lost == 0.toShort()) {
            DrawRectangle(foodPos[0] * TILE_SIZE, foodPos[1] * TILE_SIZE, TILE_SIZE, TILE_SIZE, red)
            for (i in 0 until ROWS) {
                for (j in 0 until COLS) {
                    if (snakePartTime[i][j] > 0) {
                        DrawRectangle(i * TILE_SIZE, j * TILE_SIZE, TILE_SIZE, TILE_SIZE, green)
                    }
                }
            }
        } else {
            val message = "You lost"
            val textWidth = MeasureText(message, 50)
            DrawText(
                message,
                GetScreenWidth() / 2 - textWidth / 2,
                GetScreenHeight() / 2 - 50 / 2,
                50,
                red
            )
        }

        EndDrawing()
    }
    CloseWindow()
}