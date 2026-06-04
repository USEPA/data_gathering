package gov.epa.exp_data_gathering.parse;

/**
* @author TMARTI02
*/

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.annotations.SerializedName;

import java.io.Reader;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JsonFieldChecker {

    /**
     * Loads the JSON file into a JsonArray and iterates each JsonObject, returning
     * the union of top-level keys that are NOT mapped by the provided class.
     *
     * Behavior:
     * - If root is a JSON array: iterate every object element.
     * - If root is a single JSON object: it is wrapped into an array of one.
     * - Honors @SerializedName (value and alternates).
     * - Skips static, transient, and synthetic fields.
     * - Only checks top-level fields of each object.
     * - No exceptions are thrown; on error it returns what it has (usually an empty set).
     *
     * @param jsonFilePath path to the JSON file (UTF-8)
     * @param targetClass  the class to compare against
     * @return set of unknown field names (deduplicated; preserves first-seen order)
     */
    public static Set<String> findUnknownFields(String jsonFilePath, Class<?> targetClass) {
        // Known JSON names that the class can map
        Set<String> knownFieldNames = collectJsonNamesForClass(targetClass);

        // Use LinkedHashSet to preserve insertion order while satisfying Set<String> return type
        LinkedHashSet<String> unknown = new LinkedHashSet<>();

        try (Reader reader = Files.newBufferedReader(Paths.get(jsonFilePath), StandardCharsets.UTF_8)) {
            JsonElement root = JsonParser.parseReader(reader);

            // Normalize to a JsonArray and iterate JsonObjects
            JsonArray array = null;
            if (root != null && root.isJsonArray()) {
                array = root.getAsJsonArray();
            } else if (root != null && root.isJsonObject()) {
                array = new JsonArray();
                array.add(root.getAsJsonObject());
            } else {
                return unknown; // nothing to process
            }

            for (JsonElement el : array) {
                if (el != null && el.isJsonObject()) {
                    JsonObject obj = el.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> e : obj.entrySet()) {
                        String key = e.getKey();
                        if (!knownFieldNames.contains(key)) {
                            unknown.add(key);
                        }
                    }
                }
            }
        } catch (Exception ignored) {
            // Swallow exceptions and return whatever we collected (likely empty)
        }

        return unknown;
    }

    // Builds the set of JSON names that Gson can map for the given class.
    // Honors @SerializedName (value and alternates) and skips static/transient/synthetic fields.
    private static Set<String> collectJsonNamesForClass(Class<?> clazz) {
        Set<String> names = new HashSet<>();

        for (Class<?> c = clazz; c != null && c != Object.class; c = c.getSuperclass()) {
            for (Field f : c.getDeclaredFields()) {
                int mods = f.getModifiers();
                if (Modifier.isStatic(mods) || Modifier.isTransient(mods) || f.isSynthetic()) {
                    continue;
                }

                SerializedName sn = f.getAnnotation(SerializedName.class);
                if (sn != null) {
                    names.add(sn.value());
                    for (String alt : sn.alternate()) {
                        names.add(alt);
                    }
                } else {
                    // Default: Gson maps field name as-is unless a FieldNamingPolicy is configured.
                    names.add(f.getName());
                }
            }
        }

        return names;
    }

} 