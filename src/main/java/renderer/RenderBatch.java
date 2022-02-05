package renderer;

import components.SpriteRenderer;
import jade.Window;
import org.joml.Vector4f;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch {
    // Vertex
    // ======
    // Pos            Color
    // float, float,  float, float, float, float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 6;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;

    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize) {
        shader = new Shader("assets/shaders/default.glsl");
        shader.compile();
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
    }

    /**
     * Init a Batch:
     * - Create VAO to bind
     * - Create a placeholder (buffer) to hold the incoming data of the batch added in the future by calling function addSprite()
     * - Create indices for all element could be contained in this batch
     * - Enable attribute pointers
     */
    public void start() {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray(vaoID);

        // Allocate space for vertices
        vboID = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, vboID);

        // Init the buffer to hold actual data (that should be loaded after function addSprite())
        // It is different from basic example, where the data is loaded right after this function glBufferData()
        // with the already known array
        // FOR EXAMPLE:
        //         // Create a float buffer of vertices
        //        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer(vertexArray.length);
        //        vertexBuffer.put(vertexArray).flip();
        //
        //        // Create VBO upload the vertex buffer
        //        vboID = glGenBuffers();
        //        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        //        glBufferData(GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW); // Load data directly, in stead of, creating a placeholder
        glBufferData(GL_ARRAY_BUFFER, vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload indices buffer
        int eboID = glGenBuffers();
        int[] indices = generateIndices();
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, eboID);
        // Load the data directly because we can specify the indices of each element
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer(0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, POS_OFFSET);
        glEnableVertexAttribArray(0);

        glVertexAttribPointer(1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray(1);
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (this.numSprites >= this. maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        // For now, we will re-buffer all data every frame
        glBindBuffer(GL_ARRAY_BUFFER, vboID);
        glBufferSubData(GL_ARRAY_BUFFER, 0, vertices);

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0); // enable position attribute array
        glEnableVertexAttribArray(1); // enable color attribute array

        // Base on vertices data and indices data, draw the batch TRIANGLE by TRIANGLE
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0); // unbind everything

        shader.detach();
    }

    private void loadVertexProperties(int index) {
        // This component holds the properties of a QUAD to display on screen
        // This should have 4 vertices per sprite like the following
        // (0, 1)   (1, 1)
        // (0, 0)   (1, 0)
        SpriteRenderer sprite = this.sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();

        // Init the vertex to get the properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        // Loop through all 4 vertices of the sprite
        // -> Add vertices with the appropriate properties
        for (int i = 0; i < 4; i++) {
            if (i == 1) {
                yAdd = 0.0f;
            } else if (i == 2) {
                xAdd = 0.0f;
            } else if (i == 3) {
                yAdd = 1.0f;
            }

            // Remember, we are describe a passed-in vertex array like the followings
            // Vertex
            // ======
            // Pos            Color
            // float, float,  float, float, float, float
            // x      y       r      g      b      a

            // Load position
            // Get the actual position of a vertex by adding scale (edge-size) to the original position
            vertices[offset + 0] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x); // x position
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y); // y position

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // Move offset to the next vertices in the passed-in array
            // For example:
            //     private float[] vertexArray = {
            //        // position               // color
            //        100f,   0f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, -> vertex 0
            //          0f, 100f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, -> vertex 1
            //        100f, 100f, 0.0f ,      1.0f, 0.0f, 1.0f, 1.0f, -> vertex 2
            //          0f,   0f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f, -> vertex 3
            //    };
            //
            // => INCREASE OFFSET TO MOVE FROM VERTEX 0 TO VERTEX 1
            offset += VERTEX_SIZE;
        }
    }

    /**
     * Generate indices for all elements of this batch
     * I.E:
     * - MaxBatchSize = 1000
     * - Actual number of the elements in the batch < MaxBatchSize
     * => Still generate all the indices
     * @return
     */
    private int[] generateIndices() {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];
        for (int i = 0; i < maxBatchSize; i++){
            loadElementIndices(elements, i);
        }

        return elements;
    }

    /**
     * Load/Number the indices for each element
     *
     * Vertex example
     * vertices = {x0, y0, r0, g0, b0, a0, | x1, y1, r1, g1, b1, a1, | ...}
     *                  v0                         v1
     * elements = {3, 2, 0, 0, 2, 1, | 7, 6, 4, 4, 6, 5, | ...}
     *                 sprite 0            sprite 1
     * Counter clockwise to draw 2 TRIANGLES -> forming 1 QUAD
     *
     * Vertices of some sprites might be the same (i.e: v0 of sprite0 = v7 of sprite1
     * 3   0  |  7   4
     * 2   1  |  6   5
     *
     * Indices might be sequential, but the 2 sprites might not be right next to each other in the scene
     * Indices:
     * 3   0  |  7   4
     * 2   1  |  6   5
     * Positions:
     * (0,   0) (100,   0)  | (200, 0) (300,   0)
     * (0, 100) (100, 100)  | (300, 0) (300, 300)
     * => This depends on how we add sprite into batches (function add() in class Renderer)
     *
     * This data is generated by the function [loadVertexProperties()]
     * @param elements
     * @param index
     */
    private void loadElementIndices(int[] elements, int index) {
        // Element is TRIANGLE, QUAD, ... of the whole screen
        // We consider element in this certain case if QUAD
        // -> A Quad has 6 indices
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1        7, 6, 4, 4, 6 ,5
        // Triangle 1
        elements[offsetArrayIndex + 0] = offset + 3;
        elements[offsetArrayIndex + 1] = offset + 2;
        elements[offsetArrayIndex + 2] = offset + 0;

        // Triangle 2
        elements[offsetArrayIndex + 3] = offset + 0;
        elements[offsetArrayIndex + 4] = offset + 2;
        elements[offsetArrayIndex + 5] = offset + 1;
    }

    public boolean hasRoom() {
        return this.hasRoom;
    }
}