package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import resource.Task;

import java.io.IOException;
import java.util.List;
import java.net.HttpURLConnection;

public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager, Gson gson) {
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
        List<Task> history = taskManager.getHistory();
        writeResponse(exchange, gson.toJson(history), HttpURLConnection.HTTP_OK);
    }
}