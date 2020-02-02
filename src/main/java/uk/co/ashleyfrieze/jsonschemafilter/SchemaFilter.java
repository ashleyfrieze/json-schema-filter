package uk.co.ashleyfrieze.jsonschemafilter;

import org.everit.json.schema.ArraySchema;
import org.everit.json.schema.ObjectSchema;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Iterator;

public class SchemaFilter {
    /**
     * Load the JSON schema from a JSON string
     * @param schemaJson the string of the schema
     * @return a {@link Schema} representation
     */
    public static Schema load(String schemaJson) {
        JSONObject schemaObject = new JSONObject(schemaJson);
        return SchemaLoader.builder()
                .draftV7Support()
                .useDefaults(true)
                .schemaJson(schemaObject)
                .build()
                .load()
                .build();
    }

    public static String normalize(String json, Schema schema) {
        if (json == null) {
            throw new IllegalArgumentException("Input json cannot be null");
        }
        if (schema == null) {
            throw new IllegalArgumentException("Schema cannot be null");
        }

        // convert to JSON
        JSONObject jsonObject = new JSONObject(json);

        // remove extra fields
        filter(jsonObject, schema);

        schema.validate(jsonObject);

        return jsonObject.toString();
    }

    private static void filter(JSONObject jsonObject, Schema schema) {
        Iterator<String> keys = jsonObject.keys();
        keys.forEachRemaining(key -> removeOrKeep(key, keys, jsonObject, schema));
    }

    private static void removeOrKeep(String key, Iterator<String> keys, JSONObject jsonObject, Schema schema) {
        if (!schema.definesProperty(key)) {
            keys.remove();
            return;
        }

        Schema propertySchema = ((ObjectSchema)schema).getPropertySchemas().get(key);
        if (propertySchema instanceof ObjectSchema && jsonObject.get(key) instanceof JSONObject) {
            filter(jsonObject.getJSONObject(key), propertySchema);
        } else if (propertySchema instanceof ArraySchema && jsonObject.get(key) instanceof JSONArray) {
            filterArrayMembers(key, jsonObject, (ArraySchema) propertySchema);
        }
    }

    private static void filterArrayMembers(String key, JSONObject jsonObject, ArraySchema propertySchema) {
        Schema arrayObjectSchema = propertySchema.getAllItemSchema();
        if (arrayObjectSchema instanceof ObjectSchema) {
            for (Object o : jsonObject.getJSONArray(key)) {
                if (o instanceof JSONObject) {
                    filter((JSONObject)o, arrayObjectSchema);
                }
            }
        }
    }
}
