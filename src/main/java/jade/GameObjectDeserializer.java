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
        Transform transform = context.deserialize(jsonObject.get("transform"), Transform.class);
        int zIndex = context.deserialize(jsonObject.get("zIndex"), int.class);

        GameObject go = new GameObject(name, transform, zIndex);
        // Deserialize all the components forming the GameObject
        for (JsonElement e : components) {
            Component c = context.deserialize(e, Component.class);
            go.addComponent(c);
        }

        return go;
    }
}
