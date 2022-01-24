package jade;

import org.joml.Vector2f;
import org.lwjgl.BufferUtils;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import renderer.Shader;
import util.Time;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class LevelEditorScene extends Scene {
    private int vertexID, fragmentID, shaderProgram;

    private float[] vertexArray = {
          // position                 // color
            100.5f, -0.5f,   0.0f,    1.0f, 0.0f, 0.0f, 1.0f, // bottom right 0
           -0.5f,    100.5f, 0.0f,    0.0f, 1.0f, 0.0f, 1.0f, // top    left  1
            100.5f,  100.5f, 0.0f,    1.0f, 0.0f, 1.0f, 1.0f, // top    right 2
           -0.5f,   -0.5f,   0.0f,    1.0f, 1.0f, 0.0f, 1.0f, // bottom left  3
    };

    // IMPORTANT: Must be in counter-clockwise order
    private int[] elementArray = {
        /*
           x     x

           x     x
        */
            2, 1, 0, // top right triangle
            0, 1, 3  // bottom left triangle
    };

    private int vaoID, vboID, eboID;

    private Shader defaultShader;

    public LevelEditorScene() {
    }

    @Override
    public void init() {
        // ============================================================
        // Compile and link shaders
        // ============================================================
        this.camera = new Camera(new Vector2f(-200, -300));
        defaultShader = new Shader("assets/shaders/default.glsl");
        defaultShader.compile();

        // ============================================================
        // Generate VAO, VBO, and EBO buffer objects, and send to GPU
        // ============================================================
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        //vertexBuffer.put(vertexArray); // TODO: try without flip
        vertexBuffer.put(vertexArray).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW);

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer(elementArray.length);
        elementBuffer.put(elementArray).flip();

        eboID = glGenBuffers();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW);

        // Add the vertex attribute pointers
        int positionsSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionsSize + colorSize) * floatSizeBytes;

        // index = 0: assign for position (location = 0 in the vertex shader)
        // pointer to offset = 0: offset in the array of data (vertex data)
        glVertexAttribPointer(0, positionsSize, GL_FLOAT, false, vertexSizeBytes, 0);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, colorSize, GL_FLOAT, false, vertexSizeBytes, positionsSize * floatSizeBytes);
        glEnableVertexAttribArray(1);
    }

    @Override
    public void update(float dt) {
//        camera.position.x -= dt * 50.0f;
//        camera.position.y -= dt * 20.0f;

        defaultShader.use();
        defaultShader.uploadMat4f("uProjection", camera.getProjectionMatrix());
        defaultShader.uploadMat4f("uView", camera.getViewMatrix());
        defaultShader.uploadFloat("uTime", Time.getTime());

        // Bind the VAO that we're using
        glBindVertexArray(vaoID);

        // Enable the vertex attribute pointers
        // TODO: try not to enable these IDs here
        // [checked]: still works
        glEnableVertexAttribArray(1);
        glEnableVertexAttribArray(0);

        glDrawElements(GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0);

        // TODO: try to not enable and disable here -> then check if the code works well
        // [checked]: still works if just comment "disable code" or both
        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);

        glBindVertexArray(0); // unbind vertex array
        glUseProgram(0); // unbind the program

        defaultShader.detach();
    }
}
