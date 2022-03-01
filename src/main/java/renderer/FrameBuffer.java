package renderer;

import static org.lwjgl.opengl.GL30.*;

/**
 * The class should be treated as a texture
 * Its texture io could be passed into a ImGui to display on a view port
 * Look at GameViewWindow
 */
public class FrameBuffer {
    private int fboID = 0;
    private Texture texture = null;

    public FrameBuffer(int width, int height) {
        // Generate framebuffer
        fboID = glGenFramebuffers(); // give us the object id we can use
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);

        // Create the texture to render the data to, and attach it to our framebuffer
        this.texture = new Texture(width, height);
        // attach texture as a framebuffer
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture.getId(), 0);

        // Create renderbuffer to store the depth info
        int rboID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, rboID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT32, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, rboID);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Framebuffer id not complete";
        }

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, fboID);
    }

    public void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public int getFboID() {
        return fboID;
    }

    public int getTextureId() {
        return texture.getId();
    }
}
