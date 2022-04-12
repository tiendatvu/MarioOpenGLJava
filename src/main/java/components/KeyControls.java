package components;

import editor.PropertiesWindow;
import jade.GameObject;
import jade.KeyListener;
import jade.Window;
import util.Settings;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.glfw.GLFW.*;

public class KeyControls extends Component {
    @Override
    public void editorUpdate(float dt) {
        PropertiesWindow propertiesWindow = Window.getImGuiLayer().getPropertiesWindow();
        GameObject activeGameObject = propertiesWindow.getActiveGameObject();
        List<GameObject> activeGameObjects = propertiesWindow.getActiveGameObjects();

        if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) &&
                activeGameObject != null) {
            // Duplicate an object
            GameObject newObj = activeGameObject.copy();
            Window.getScene().addGameObjectToScene(newObj);
            // init the new object at the right next grid
            newObj.transform.position.add(Settings.GRID_WIDTH, 0.0f);
            propertiesWindow.setActiveGameObject(newObj);
        } else if (KeyListener.isKeyPressed(GLFW_KEY_LEFT_CONTROL) &&
                KeyListener.keyBeginPress(GLFW_KEY_D) &&
                activeGameObjects.size() > 1) {
            // Duplicate a set of objects
            List<GameObject> gameObjects = new ArrayList<>(activeGameObjects);
            propertiesWindow.clearSelected();
            for (GameObject go : gameObjects) {
                GameObject copy = go.copy();
                Window.getScene().addGameObjectToScene(copy);
                propertiesWindow.addActiveGameObject(copy);
            }
        } else if (KeyListener.keyBeginPress(GLFW_KEY_DELETE)) {
            // Delete selected objects
            for (GameObject go : activeGameObjects) {
                go.destroy();
            }
            propertiesWindow.clearSelected();
        }
    }
}
