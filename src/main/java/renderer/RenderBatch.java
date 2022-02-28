package renderer;

import components.SpriteRenderer;
import jade.Window;
import org.joml.Vector2f;
import org.joml.Vector4f;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.glGenBuffers;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch implements Comparable<RenderBatch> {
    // Vertex
    // ======
    // Pos            Color                        Texture coords  Texture Id
    // float, float,  float, float, float, float,  float, float,   float
    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE * Float.BYTES;
    private final int VERTEX_SIZE = 9;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0, 1, 2, 3, 4, 5, 6, 7}; // place holders for textures loaded in ONE BATCH

    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;
    private int zIndex;

    public RenderBatch(int maxBatchSize, int zIndex) {
        this.zIndex = zIndex;

        /*
          This is the old version. This code makes us
          shader = new Shader("assets/shaders/default.glsl");
          shader.compile();

          Below is the new version
         */
        shader = AssetPool.getShader("assets/shaders/default.glsl");
        this.sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        this.numSprites = 0;
        this.hasRoom = true;
        this.textures = new ArrayList<>();
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

        glVertexAttribPointer(2, TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray(2);

        glVertexAttribPointer(3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET);
        glEnableVertexAttribArray(3);
    }

    public void addSprite(SpriteRenderer spr) {
        // Get index and add renderObject
        int index = this.numSprites;
        this.sprites[index] = spr;
        this.numSprites++;

        if (spr.getTexture() != null) {
            if (!textures.contains(spr.getTexture())) {
                textures.add(spr.getTexture());
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if (this.numSprites >= this. maxBatchSize) {
            this.hasRoom = false;
        }
    }

    public void render() {
        boolean reBufferData = false;
        for (int i = 0; i < this.numSprites; i++) {
            SpriteRenderer spr = sprites[i];
            if (spr.isDirty()) {
                loadVertexProperties(i);
                spr.setClean();
                reBufferData = true;
            }
        }

        if (reBufferData) {
            glBindBuffer(GL_ARRAY_BUFFER, vboID);
            glBufferSubData(GL_ARRAY_BUFFER, 0, this.vertices);
        }

        // Use shader
        shader.use();
        shader.uploadMat4f("uProjection", Window.getScene().camera().getProjectionMatrix());
        shader.uploadMat4f("uView", Window.getScene().camera().getViewMatrix());
        for (int i = 0; i < textures.size(); i++) {
            // TODO: bind the first texture to 1 instead of 0, 2nd to 2 ...
            //       this should hold the first slot in List textures to no texture
            //       -> just draw color when that slot is called
            glActiveTexture(GL_TEXTURE0 + i + 1);
            textures.get(i).bind();
        }
        // Because texture is bound with the (GL_TEXTURE0 + i + 1),
        // just pass the texture id from texSlots array into shader.
        // This should automatically load the texture
        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray(vaoID);
        glEnableVertexAttribArray(0); // enable position attribute array
        glEnableVertexAttribArray(1); // enable color attribute array

        // Base on vertices data and indices data, draw the batch TRIANGLE by TRIANGLE
        glDrawElements(GL_TRIANGLES, this.numSprites * 6, GL_UNSIGNED_INT, 0);

        glDisableVertexAttribArray(0);
        glDisableVertexAttribArray(1);
        glBindVertexArray(0); // unbind everything

        for (int i = 0; i < textures.size(); i++) {
            textures.get(i).unbind();
        }
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
        Vector2f[] texCoords = sprite.getTexCoords();

        // TODO: bind the first texture to 1 instead of 0, 2nd to 2 ...
        //       this should hold the first slot in List textures to no texture
        //       -> just draw color when that slot is called
        int texId = 0;
        if (sprite.getTexture() != null) {
            for (int i = 0; i < textures.size(); i++) {
                // use operator == : compare addresses of the actual objects
                // use function equals: compare the actual objects
                if (textures.get(i).equals(sprite.getTexture())) {
                    texId = i  + 1;
                    break;
                }
            }
        }

        // Init the vertex to get the properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;
        // + Loop through all 4 vertices of the sprite
        // + Loop through with the considered coordinates order like in the function SpriteRenderer.getTexCoords()
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
            // Pos            Color                        Texture coords  Texture Id
            // float, float,  float, float, float, float,  float, float,   float
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

            // Load texture coordinates
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;

            // Load texture id
            vertices[offset + 8] = texId;

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

    public boolean hasTextureRoom() {
        return this.textures.size() < 8;
    }

    public boolean hasTexture(Texture tex) {
        return this.textures.contains(tex);
    }

    public int zIndex() {
        return this.zIndex;
    }

    @Override
    public int compareTo(RenderBatch o) {
        return Integer.compare(this.zIndex, o.zIndex());
    }
}