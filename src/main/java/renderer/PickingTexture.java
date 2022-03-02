package renderer;

import static org.lwjgl.opengl.GL30.*;

/**
 * Store unique object ids as colors
 * Adding all the colors into 1 framebuffer
 */
public class PickingTexture {
    private int pickingTextureId;
    private int fbo;
    private int depthTexture; // depthTextureId actually

    public PickingTexture(int width, int height) {
        if (!init(width, height)) {
            assert false : "Error initializing picking texture";
        }
    }

    public boolean init(int width, int height) {
        // Generate framebuffer
        fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fbo);

        // Create the texture to render the data to, and attach it to our framebuffer
        pickingTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, pickingTextureId);
        // Use repeat to if the coordinates outside range of (0, 0) to (1, 1)
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        // Pixelate when stretching the texture
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB32F, width, height, 0, GL_RGB, GL_FLOAT, 0);
        // Draw to the GL_COLOR_ATTACHMENT0 (Like GL_TEXTURE0...)
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0,
                GL_TEXTURE_2D, this.pickingTextureId, 0);

        // Create the texture object for the depth buffer
        //glEnable(GL_DEPTH_TEST); // TODO: re-read framebuffer, depth testing
        glEnable(GL_TEXTURE_2D);
        depthTexture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, depthTexture);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_DEPTH_COMPONENT, width, height, 0,
                GL_DEPTH_COMPONENT, GL_FLOAT, 0);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_TEXTURE_2D, depthTexture, 0);

        // Disable the reading
        glReadBuffer(GL_NONE);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        if (glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE) {
            assert false : "Error: Framebuffer is not complete";
            return false;
        }

        // Unbind the texture and framebuffer
        glBindTexture(GL_TEXTURE_2D, 0);
        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return true;
    }

    public void enableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, fbo);
    }

    public void disableWriting() {
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);
    }

    public int readPixel(int x, int y) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fbo);
        glReadBuffer(GL_COLOR_ATTACHMENT0);

        float pixels[] = new float[3];
        glReadPixels(x, y, 1, 1, GL_RGB, GL_FLOAT, pixels);

        // We already
        // - cleared the color bit and fill the background with (0, 0, 0, 0)
        //   *in the Window class glClearColor(0.0f, 0.0f, 0.0f, 0.0f);*
        // - add 1 to the actual value of the unique id of object
        //
        // but we do not want to display the color of the background as 0 (because the id could be 0)
        // and we do not want to the background to have any color
        // -> minus 1 to get the actual color value for background as -1
        return (int)(pixels[0] - 1);
    }
}
