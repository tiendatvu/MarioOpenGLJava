package components;

import com.google.gson.*;
import components.Component;

import java.lang.reflect.Type;

public class ComponentDeserializer implements JsonSerializer<Component>,
        JsonDeserializer<Component> {
    @Override
    public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = json.getAsJsonObject();
        String type = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(type));
        } catch (ClassNotFoundException e) {
            throw new JsonParseException("Unknown element type: " + type, e);
        }
    }

    @Override
    public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        // serialize class name with the property named: type
        // getCanonicalName(): get name without the full package name (just the class name)
        result.add("type", new JsonPrimitive(src.getClass().getCanonicalName()));
        // serialize all the properties inside of the class with the built in function of gson
        // context.serialize()
        // src.getClass(): get the full class name with class name
        result.add("properties", context.serialize(src, src.getClass()));
        return result;
    }
}