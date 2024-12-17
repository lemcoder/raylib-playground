import kotlinx.cinterop.*
import raylib.*
import kotlin.random.Random

@OptIn(ExperimentalForeignApi::class)
fun main() {

    val screenWidth = 800
    val screenHeight = 450

    InitWindow(screenWidth, screenHeight, "raylib [core] example - 3d camera modes")

    memScoped {
        val MAX_COLUMNS = 20

        // Initialization

        // Define the camera to look into our 3D world
        val camera = alloc<Camera3D> {
            position.x = 0f
            position.y = 2f
            position.z = 4f

            target.x = 0f
            target.y = 2f
            target.z = 0f

            up.x = 0f
            up.y = 1f
            up.z = 0f

            fovy = 60f                              // Camera field-of-view Y
            projection = CAMERA_PERSPECTIVE.toInt() // Perspective projection
        }

        // Variables for camera control
        var cameraMode = CAMERA_FIRST_PERSON

        // Generate random columns
        val heights = FloatArray(MAX_COLUMNS) { Random.nextFloat() * 11 + 1 }
        val positions = Array(MAX_COLUMNS) {
            Vector3(
                Random.nextFloat() * 30 - 15,
                heights[it] / 2.0f,
                Random.nextFloat() * 30 - 15
            )
        }
        val colors = Array(MAX_COLUMNS) {
            Color(
                Random.nextInt(20, 255),
                Random.nextInt(10, 55),
                30,
                255
            )
        }

        DisableCursor() // Limit cursor to relative movement inside the window

        SetTargetFPS(60) // Set the game to run at 60 frames-per-second

        // Main game loop
        while (!WindowShouldClose()) {
            // Update
            if (IsKeyPressed(KEY_ONE.toInt())) cameraMode = CAMERA_FREE
            if (IsKeyPressed(KEY_TWO.toInt())) cameraMode = CAMERA_FIRST_PERSON
            if (IsKeyPressed(KEY_THREE.toInt())) cameraMode = CAMERA_THIRD_PERSON
            if (IsKeyPressed(KEY_FOUR.toInt())) cameraMode = CAMERA_ORBITAL

            if (IsKeyPressed(KEY_P.toInt())) {
                camera.projection = if (camera.projection == CAMERA_PERSPECTIVE.toInt()) {
                    camera.fovy = 20f
                    CAMERA_ORTHOGRAPHIC.toInt()
                } else {
                    camera.fovy = 60f
                    CAMERA_PERSPECTIVE.toInt()
                }
            }

            // Update camera based on current mode
            UpdateCamera(camera.ptr, cameraMode.toInt())

            // Draw
            BeginDrawing()
            ClearBackground(Color(255, 255, 255, 255).asCValue())

            BeginMode3D(camera.readValue())

            // Draw ground and walls
            DrawPlane(Vector3(0f, 0f, 0f).asCValue(), Vector2(32f, 32f).asCValue(), Color.LIGHT_GRAY.asCValue())
            DrawCube(Vector3(-16f, 2.5f, 0f).asCValue(), 1f, 5f, 32f, Color.BLUE.asCValue())
            DrawCube(Vector3(16f, 2.5f, 0f).asCValue(), 1f, 5f, 32f, Color.LIME.asCValue())
            DrawCube(Vector3(0f, 2.5f, 16f).asCValue(), 32f, 5f, 1f, Color.GOLD.asCValue())

            // Draw random columns
            for (i in 0 until MAX_COLUMNS) {
                DrawCube(positions[i].asCValue(), 2f, heights[i], 2f, colors[i].asCValue())
                DrawCubeWires(positions[i].asCValue(), 2f, heights[i], 2f, Color.DARK_GRAY.asCValue())
            }

            // Draw a player cube in third -person mode
            if (cameraMode == CAMERA_THIRD_PERSON) {
                DrawCube(camera.target.readValue(), 0.5f, 0.5f, 0.5f, Color.PURPLE.asCValue())
                DrawCubeWires(camera.target.readValue(), 0.5f, 0.5f, 0.5f, Color.DARK_PURPLE.asCValue())
            }

            EndMode3D()

            // UI
            DrawRectangle(5, 5, 330, 100, Fade(Color.SKY_BLUE.asCValue(), 0.5f))
            DrawRectangleLines(5, 5, 330, 100, Color.BLUE.asCValue())
            DrawText("Camera controls:", 15, 15, 10, Color.BLACK.asCValue())
            DrawText("- Move keys: W, A, S, D, Space, Left-Ctrl", 15, 30, 10, Color.BLACK.asCValue())
            DrawText("- Look around: Arrow keys or mouse", 15, 45, 10, Color.BLACK.asCValue())
            DrawText("- Camera mode keys: 1, 2, 3, 4", 15, 60, 10, Color.BLACK.asCValue())
            DrawText("- Zoom keys: Num-Plus, Num-Minus or mouse scroll", 15, 75, 10, Color.BLACK.asCValue())
            DrawText("- Camera projection key: P", 15, 90, 10, Color.BLACK.asCValue())

            DrawRectangle(600, 5, 195, 100, Fade(Color.SKY_BLUE.asCValue(), 0.5f))
            DrawRectangleLines(600, 5, 195, 100, Color.BLUE.asCValue())
            DrawText("Camera status:", 610, 15, 10, Color.BLUE.asCValue())
            DrawText(
                "- Mode: ${
                    when (cameraMode) {
                        CAMERA_FREE         -> "FREE"
                        CAMERA_FIRST_PERSON -> "FIRST_PERSON"
                        CAMERA_THIRD_PERSON -> "THIRD_PERSON"
                        CAMERA_ORBITAL      -> "ORBITAL"
                        else                -> "CUSTOM"
                    }
                }", 610, 30, 10, Color.BLACK.asCValue()
            )
            DrawText("- Projection: ${if (camera.projection == CAMERA_PERSPECTIVE.toInt()) "PERSPECTIVE" else "ORTHOGRAPHIC"}", 610, 45, 10, Color.BLACK.asCValue())
            DrawText("- Position: (${camera.position.x}, ${camera.position.y}, ${camera.position.z})", 610, 60, 10, Color.BLACK.asCValue())
            DrawText("- Target: (${camera.target.x}, ${camera.target.y}, ${camera.target.z})", 610, 75, 10, Color.BLACK.asCValue())
            DrawText("- Up: (${camera.up.x}, ${camera.up.y}, ${camera.up.z})", 610, 90, 10, Color.BLACK.asCValue())

            EndDrawing()
        }
    }

    CloseWindow()
}
