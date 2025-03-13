package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import resource.Task;

import java.io.IOException;
import java.util.Set;
import java.net.HttpURLConnection;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            if (method.equals("GET")) {
                handleGet(exchange);
            } else {
                writeResponse(exchange, convertToMessage("Метод " + method + " не поддерживается"), HttpURLConnection.HTTP_BAD_METHOD);
            }
        } catch (Exception e) {
            writeResponse(exchange, convertToMessage("Ошибка сервера: " + e.getMessage()), HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Set<Task> prioritizedTasks = taskManager.getPrioritizedTasks();
        writeResponse(exchange, gson.toJson(prioritizedTasks), HttpURLConnection.HTTP_OK);
    }
}