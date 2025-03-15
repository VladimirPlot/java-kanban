package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

public abstract class BaseHttpHandler implements HttpHandler {
    private final TaskManager taskManager;
    private final Gson gson;

    public BaseHttpHandler(TaskManager taskManager, Gson gson) {
        this.taskManager = taskManager;
        this.gson = gson;
    }

    protected void writeResponse(HttpExchange exchange, String responseString, int responseCode) throws IOException {
        exchange.getResponseHeaders().set("Content-Type", "application/json;charset=utf-8");

        if (responseCode == HttpURLConnection.HTTP_NO_CONTENT) {
            exchange.sendResponseHeaders(responseCode, -1); // Указываем, что тела нет
        } else {
            byte[] responseBytes = responseString.getBytes(StandardCharsets.UTF_8);
            exchange.sendResponseHeaders(responseCode, responseBytes.length);

            try (OutputStream os = exchange.getResponseBody()) {
                os.write(responseBytes);
            }
        }
        exchange.close();
    }

    protected Optional<Integer> getId(HttpExchange exchange) {
        String[] pathParts = exchange.getRequestURI().getPath().split("/");
        if (pathParts.length < 3) {
            return Optional.empty();
        }
        try {
            return Optional.of(Integer.parseInt(pathParts[2]));
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            System.err.println("Ошибка при разборе ID из URL: " + e.getMessage());
            return Optional.empty();
        }
    }

    protected boolean isJsonRequest(HttpExchange exchange) {
        Headers headers = exchange.getRequestHeaders();
        List<String> contentTypeValues = headers.get("Content-Type");
        return contentTypeValues != null && contentTypeValues.contains("application/json");
    }

    protected String convertToMessage(String message) {
        return gson.toJson(new Message(message), Message.class);
    }

    protected TaskManager getTaskManager() {
        return taskManager;
    }

    protected Gson getGson() {
        return gson;
    }
}