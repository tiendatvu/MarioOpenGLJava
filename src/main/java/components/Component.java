package components;

import imgui.ImGui;
import jade.GameObject;
import org.joml.Vector3f;
import org.joml.Vector4f;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

// Like interface in c++. force implementation to do some abstract methods
public abstract class Component {
    /**
     * Global Id System for all the components on the scene
     */
    private static int ID_COUNTER = 0;

    /**
     * The id of a specific component
     */
    private int uid = -1;

    /**
     * - Show the component is currently belonged to which GameObject on the screen
     * - Different set of Components should form different type of GameObject
     * - GameObject is transient in the class Component because:
     *   + serialize GameObject is serializing Components
     *   + a Component contains the pointer to "mother" GameObject
     *   -> If serialize GameObject inside of Component -> cause the infinite loop
     */
    public transient GameObject gameObject = null;

    // override to rewrite
    public void start() {

    }

    // override if needed
    public void update(float dt) {

    }

    public void imgui() {
        try {
            // getField() can get a field inherited from a superclass but getDeclaredField() cannot
            Field[] fields = this.getClass().getDeclaredFields();
            for (Field field : fields) {
                boolean isTransient = Modifier.isTransient(field.getModifiers());
                if (isTransient) {
                    continue;
                }

                boolean isPrivate = Modifier.isPrivate(field.getModifiers());
                if (isPrivate) {
                    field.setAccessible(true);
                }

                Class type = field.getType();
                Object value = field.get(this);
                String name = field.getName();

                if (type == int.class) {
                    int val = (int)value;
                    int[] imInt = {val};
                    if (ImGui.dragInt(name + ": ", imInt)) {
                        field.set(this, imInt[0]);
                    }
                } else if (type == float.class) {
                    float val = (float)value;
                    float[] imFloat = {val};
                    if (ImGui.dragFloat(name + ": ", imFloat)) {
                        field.set(this, imFloat[0]);
                    }
                } else if (type == boolean.class) {
                    boolean val = (boolean)value;
                    if (ImGui.checkbox(name + ": ", val)) {
                        field.set(this, !val);
                    }
                } else if (type == Vector3f.class) {
                    Vector3f val = (Vector3f)value;
                    float[] imVec = {val.x, val.y, val.z};
                    if (ImGui.dragFloat3(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2]);
                    }
                } else if (type == Vector4f.class) {
                    Vector4f val = (Vector4f)value;
                    float[] imVec = {val.x, val.y, val.z, val.w};
                    if (ImGui.dragFloat4(name + ": ", imVec)) {
                        val.set(imVec[0], imVec[1], imVec[2], imVec[3]);
                    }
                }

                if (isPrivate) {
                    field.setAccessible(false);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * If the uid of the component has not been created
     * -> init id = current_max_id + 1
     */
    public void generateId() {
        if (this.uid == -1) {
            this.uid = ID_COUNTER++;
        }
    }

    public int uid() {
        return this.uid;
    }

    /**
     * Init the maximum value of the id counter
     * @param maxId
     */
    public static void init(int maxId) {
        ID_COUNTER = maxId;
    }
}
