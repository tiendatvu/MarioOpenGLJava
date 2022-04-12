package editor;

import components.NonPickable;
import components.SpriteRenderer;
import imgui.ImGui;
import jade.GameObject;
import jade.MouseListener;
import org.joml.Vector4f;
import physics2d.components.Box2DCollider;
import physics2d.components.CircleCollider;
import physics2d.components.Rigidbody2D;
import renderer.PickingTexture;
import scenes.Scene;

import javax.swing.*;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;

public class PropertiesWindow {
    // Show what GameObject is being modified
    private List<GameObject> activeGameObjects;
    private List<Vector4f> activeGameObjectsOgColor;
    private GameObject activeGameObject = null;
    private PickingTexture pickingTexture;

    /**
     * Time counter to 0 -> check if the user click and hold the button long enough.
     * Help to reduce number of checking times
     */
    private float debounce = 0.2f;

    public PropertiesWindow(PickingTexture pickingTexture) {
        this.activeGameObjects = new ArrayList<>();
        this.pickingTexture = pickingTexture;
        this.activeGameObjectsOgColor = new ArrayList<>();
    }

    public void imgui() {
        if (activeGameObjects.size() == 1 && activeGameObjects.get(0) != null) {
            activeGameObject = activeGameObjects.get(0);
            ImGui.begin("Properties");

            // Right click to popup a context window
            if (ImGui.beginPopupContextWindow("ComponentAdder")) {
                // Add menu item into the context window
                if (ImGui.menuItem("Add Rigid body")) {
                    if (activeGameObject.getComponent(Rigidbody2D.class) == null) {
                        activeGameObject.addComponent(new Rigidbody2D());
                    }
                }

                // Add a box collider if there is no box collider and circle collider exist
                if (ImGui.menuItem("Add Box Collider")) {
                    if (activeGameObject.getComponent(Box2DCollider.class) == null &&
                            activeGameObject.getComponent(CircleCollider.class) == null) {
                        activeGameObject.addComponent(new Box2DCollider());
                    }
                }

                // Add a circle collider if there is no circle collider and box collider exist
                if (ImGui.menuItem("Add Circle Collider")) {
                    if (activeGameObject.getComponent(CircleCollider.class) == null &&
                            activeGameObject.getComponent(Box2DCollider.class) == null) {
                        activeGameObject.addComponent(new CircleCollider());
                    }
                }

                ImGui.endPopup();
            }

            activeGameObject.imgui();
            ImGui.end();
        }
    }

    public GameObject getActiveGameObject() {
        return this.activeGameObjects.size() == 1 ? this.activeGameObjects.get(0) : null;
    }

    public List<GameObject> getActiveGameObjects() {
        return this.activeGameObjects;
    }

    public void clearSelected() {
        if (activeGameObjectsOgColor.size() > 0) {
            int i = 0;
            for (GameObject go : activeGameObjects) {
                SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
                if (spr != null) {
                    // set the color back to normal
                    spr.setColor(activeGameObjectsOgColor.get(i));
                }
                i++;
            }
        }
        this.activeGameObjects.clear(); // not select any objects
        this.activeGameObjectsOgColor.clear(); // clear the list of color
    }

    public void setActiveGameObject(GameObject go) {
        if (go != null) {
            // clear the current set of selected objects
            // set the first element in the array as active
            clearSelected();
            this.activeGameObjects.add(go);
        }
    }

    public void addActiveGameObject(GameObject go) {
        SpriteRenderer spr = go.getComponent(SpriteRenderer.class);
        if (spr != null) {
            this.activeGameObjectsOgColor.add(new Vector4f(spr.getColor()));
            spr.setColor(new Vector4f(0.8f, 0.8f, 0.0f, 0.8f));
        } else {
            this.activeGameObjectsOgColor.add(new Vector4f());
        }
        this.activeGameObjects.add(go);
    }

    public PickingTexture getPickingTexture() {
        return this.pickingTexture;
    }
}
