package jade;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import components.Component;
import components.ComponentDeserializer;
import components.Sprite;
import components.SpriteRenderer;
import imgui.ImGui;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

/**
 * Describe everything in the scene depending on which COMPONENTS is added into it.
 * i.e:
 * - Just add a SpriteRender class -> GameObject is just a SPRITE on the screen
 */
public class GameObject {

    private static int ID_COUNTER = 0;
    private int uid = -1;

    public String name;
    private List<Component> components;
    public transient Transform transform;
    private boolean doSerialization = true;
    private boolean isDead = false;

    public GameObject(String name) {
        this.name = name;
        this.components = new ArrayList<>();

        this.uid = ID_COUNTER++;
    }

    // Get the component with passed-in class
    // TODO: in case there are 2 components with the same class -> how can we decide which to be returned
    public <T extends Component> T getComponent(Class<T> componentClass) {
        for (Component c : components) {
            if (componentClass.isAssignableFrom(c.getClass())) {
                try {
                    return componentClass.cast(c);
                } catch (ClassCastException e) {
                    e.printStackTrace();
                    assert false : "Error: Casting component.";
                }
            }
        }

        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass) {
        for (int i = 0; i < components.size(); i++) {
            Component c = components.get(i);
            if (componentClass.isAssignableFrom(c.getClass())) {
                components.remove(c);
                return;
            }
        }
    }

    public void addComponent(Component c) {
        c.generateId();
        this.components.add(c);
        // assign back the game object using this component
        // -> different components could communicate:
        //    1. through some common state variables
        //    2. sending messages
        c.gameObject = this;
    }

    public void update(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).update(dt);
        }
    }

    public void editorUpdate(float dt) {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).editorUpdate(dt);
        }
    }

    public void start() {
        for (int i = 0; i < components.size(); i++) {
            components.get(i).start();
        }
    }

    public void imgui() {
        for (Component c : components) {
            // Create collapsing header for each component
            // i.e:
            // - GameObject contains Transform, SpriteRenderer
            // -> Create 2 header (automatically) containing all properties of the Component class
            //if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
            if (ImGui.collapsingHeader(c.getClass().getSimpleName()))
                c.imgui();
        }
    }

    public int getUid() {
        return this.uid;
    }

    public void destroy() {
        this.isDead = true;
        for (int i = 0; i < components.size(); i++) {
            components.get(i).destroy();
        }
    }

    /**
     * Duplicate a GameObject.
     * Copy using GSon
     * -> cannot copy transient properties (like texture Id texId)
     * @return
     */
    public GameObject copy() {
        // TODO: come up with cleaner solution

        Gson gson = new GsonBuilder()
                .registerTypeAdapter(Component.class, new ComponentDeserializer())
                .registerTypeAdapter(GameObject.class, new GameObjectDeserializer())
                .create();
        // Serialize object into gson source
        String objAsJson = gson.toJson(this);
        // Create new object from gson source
        GameObject obj = gson.fromJson(objAsJson, GameObject.class);

        obj.generateUid();
        for (Component c : obj.getAllComponents()) {
            c.generateId();
        }

        SpriteRenderer sprite = obj.getComponent(SpriteRenderer.class);
        if (sprite != null && sprite.getTexture() != null) {
            sprite.setTexture(AssetPool.getTexture(sprite.getTexture().getFilePath()));
        }

        return obj;
    }

    public boolean isDead() {
        return this.isDead;
    }

    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }

    public List<Component> getAllComponents() {
        return this.components;
    }

    public void setNoSerialize() {
        this.doSerialization = false;
    }

    public void generateUid(){
        this.uid = ID_COUNTER++;
    }

    /**
     * if doSerialization == true, save this with the file saving (serialize GameObjects)
     * @return
     */
    public boolean doSerialization() {
        return this.doSerialization;
    }
}
