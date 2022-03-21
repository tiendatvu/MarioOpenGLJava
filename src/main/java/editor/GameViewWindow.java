package editor;

import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import jade.MouseListener;
import jade.Window;
import observers.EventSystem;
import observers.events.Event;
import observers.events.EventType;
import org.joml.Vector2f;

/**
 * The view just to edit GameObjects and play game
 */
public class GameViewWindow {

    private float leftX, rightX, topY, bottomY;
    private boolean isPlaying = false;

    public void imgui() {
        // Open an ImGui window
        ImGui.begin("Game Viewport",
                    ImGuiWindowFlags.NoScrollbar |
                            ImGuiWindowFlags.NoScrollWithMouse |
                            ImGuiWindowFlags.MenuBar);

        ImGui.beginMenuBar();
        // Add the menu item to the menubar
        // selected: true -> select the menu item
        // enabled: true -> enable the menu item
        // Here, if selected -> disable the menu item
        if (ImGui.menuItem("Play", "", isPlaying, !isPlaying)) {
            isPlaying = true;
            EventSystem.notify(null, new Event(EventType.GameEngineStartPlay));
        }
        if (ImGui.menuItem("Stop", "", !isPlaying, isPlaying)) {
            isPlaying = false;
            EventSystem.notify(null, new Event(EventType.GameEngineStopPlay));
        }
        ImGui.endMenuBar();

        ImVec2 windowSize = getLargestSizeForViewport();
        ImVec2 windowPos = getCenteredPositionForViewport(windowSize);

        // Draw the ImGui window at this position
        // Cursor position in window coordinates (relative to window position)
        // (Coordinates on the region of game view port only)
        ImGui.setCursorPos(windowPos.x, windowPos.y);

        ImVec2 topLeft = new ImVec2();

        // Cursor position in ABSOLUTE screen coordinates [0..io.DisplaySize]
        // (Coordinates on the out most screen)
        ImGui.getCursorScreenPos(topLeft);
        topLeft.x -= ImGui.getScrollX();
        topLeft.y -= ImGui.getScrollY();
//        System.out.println("windowPos: " + windowPos);
//        System.out.println("windowSize: " + windowSize);
//        System.out.println("topLeft: " + topLeft);

        // Assign the absolute coordinates of viewport on the main screen
        leftX = topLeft.x;
        bottomY = topLeft.y;
        rightX = topLeft.x + windowSize.x;
        topY = topLeft.y + windowSize.y;

        int textureId = Window.getFramebuffer().getTextureId();
        // Pass
        // - the framebuffer with its texture id (consider each frame buffer as a texture)
        // - window size to work with
        // - how to sample the texture: from (0, 1) top-left to (1, 0) bottom-right
        ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);

        // Set the properties to calculate the orthogonal/perspective coordinates
        MouseListener.setGameViewportPos(new Vector2f(topLeft.x, topLeft.y));
        MouseListener.setGameViewportSize(new Vector2f(windowSize.x, windowSize.y));

        // Close an ImGui window
        ImGui.end();
    }

    public boolean getWantCaptureMouse() {
        return MouseListener.getX() >= leftX && MouseListener.getX() <= rightX &&
                MouseListener.getY() >= bottomY && MouseListener.getY() <= topY;
    }

    private ImVec2 getLargestSizeForViewport() {
        ImVec2 windowSize = new ImVec2();
        // assign the available region to the buffer
        // windowSize = GetContentRegionMax() - GetCursorPos()
        ImGui.getContentRegionAvail(windowSize);

        // GetContentRegionMax() = Current content boundaries (typically window boundaries including scrolling,
        //                         or current column boundaries), in windows coordinates
        // In the case the window accidentally has scroll bar on its side
        // -> minus the size of its scroll (if the window does not have -> scroll bar size = 0)
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        // Calculate which direction (horizontally or vertically) should have black bars on both sides
        // Case 1: display all the width of the view port
        float aspectWidth = windowSize.x;
        // getTargetAspectRatio() = 16/9 should be for the general screen ratio
        float aspectHeight = aspectWidth / Window.getTargetAspectRatio();
        if (aspectHeight > windowSize.y) {
            // We must switch to pillar box mode
            // Case 2: display all the height of the view port
            aspectHeight = windowSize.y;
            aspectWidth = aspectHeight * Window.getTargetAspectRatio();
        }

        return new ImVec2(aspectWidth, aspectHeight);
    }

    /**
     * The author may name this function wrongly.
     * This function should calculate the position top-left to draw the Game viewport
     * Remember the snippet
     * // - how to sample the texture: from (0, 1) top-left to (1, 0) bottom-right
     *      ImGui.image(textureId, windowSize.x, windowSize.y, 0, 1, 1, 0);
     *
     * (windowSize.x / 2.0f): the center of the whole viewport window
     * (aspectSize.x / 2.0f): the center of the smaller viewport window where sprites would be drawn
     * viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f): specify the position to start draw the viewport
     *
     * @param aspectSize
     * @return
     */
    private ImVec2 getCenteredPositionForViewport(ImVec2 aspectSize) {
        ImVec2 windowSize = new ImVec2();
        ImGui.getContentRegionAvail(windowSize);
        windowSize.x -= ImGui.getScrollX();
        windowSize.y -= ImGui.getScrollY();

        float viewportX = (windowSize.x / 2.0f) - (aspectSize.x / 2.0f);
        float viewportY = (windowSize.y / 2.0f) - (aspectSize.y / 2.0f);

        return new ImVec2(viewportX + ImGui.getCursorPosX(),
                          viewportY + ImGui.getCursorPosY());
    }
}
