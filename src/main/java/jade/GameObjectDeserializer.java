package jade;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;
import java.util.List;

public class GameObjectDeserializer implements JsonDeserializer<GameObject> {
    @Override
    public GameObject deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();

        // The definition of a GameObject:
//        private String name;
//        private List<Component> components;
//        public Transform transform;
//        private int zIndex;
        // That the reason why jsonObject.get("xxx") as followings:
        String name = jsonObject.get("name").getAsString();
        JsonArray components = jsonObject.getAsJsonArray("components");

        GameObject go = new GameObject(name);
        // Deserialize all the components forming the GameObject
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }
        // The Transform class is deserialized from file, and added to the component list
        // The property "transform" of the class GameObject is still not initialized
        // -> Have to init here
        go.transform = go.getComponent(Transform.class);
        return go;
    }
}
