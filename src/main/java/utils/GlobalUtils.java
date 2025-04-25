package utils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

public class GlobalUtils {
    private static final Gson GSON = new Gson();
    
    public static Map<String, String> parseJsonRequest(HttpServletRequest request) throws IOException {
        try (BufferedReader reader = request.getReader()) {
            String jsonBody = reader.lines().collect(Collectors.joining());
            
            if (jsonBody.isEmpty()) {
                return Collections.emptyMap();
            }
            
            return GSON.fromJson(
                jsonBody, 
                new TypeToken<Map<String, String>>(){}.getType()
            );
        } catch (JsonSyntaxException e) {
            throw new IllegalArgumentException("Invalid JSON format", e);
        }
    }
}