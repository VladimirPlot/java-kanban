package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import manager.TaskManager;
import resource.Epic;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.net.HttpURLConnection;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager, Gson gson) {
        super(taskManager, gson);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();

        try {
            switch (method) {
                case "GET":
                    handleGet(exchange);
                    break;
                case "POST":
                    handlePost(exchange);
                    break;
                case "DELETE":
                    handleDelete(exchange);
                    break;
                default:
                    writeResponse(exchange, convertToMessage("Метод " + method + " не поддерживается"), HttpURLConnection.HTTP_BAD_METHOD);
            }
        } catch (Exception e) {
            writeResponse(exchange, convertToMessage("Ошибка сервера: " + e.getMessage()), HttpURLConnection.HTTP_INTERNAL_ERROR);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Epic> epic = getTaskManager().getEpicById(id.get());

            if (epic.isEmpty()) {
                writeResponse(exchange, convertToMessage("Эпик с id=" + id.get() + " не найден"), HttpURLConnection.HTTP_NOT_FOUND);
            } else {
                writeResponse(exchange, getGson().toJson(epic.get()), HttpURLConnection.HTTP_OK);
            }
        } else {
            List<Epic> epics = getTaskManager().getAllEpics();
            writeResponse(exchange, getGson().toJson(epics), HttpURLConnection.HTTP_OK);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        if (!isJsonRequest(exchange)) {
            writeResponse(exchange, convertToMessage("Некорректный заголовок Content-Type"), HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Epic epic = getGson().fromJson(body, Epic.class);

        if (epic == null) {
            writeResponse(exchange, convertToMessage("Ошибка десериализации эпика"), HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }

        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Epic> updatedEpic = getTaskManager().updateEpic(epic);

            if (updatedEpic.isEmpty()) {
                writeResponse(exchange, convertToMessage("Епик с id=" + id.get() + " не найден для обновления"), HttpURLConnection.HTTP_NOT_FOUND);
            } else {
                writeResponse(exchange, getGson().toJson(updatedEpic.get()), HttpURLConnection.HTTP_OK);
            }
        } else {
            Optional<Epic> createdEpic = getTaskManager().createEpic(epic);

            if (createdEpic.isEmpty()) {
                writeResponse(exchange, convertToMessage("Ошибка создания эпика"), HttpURLConnection.HTTP_INTERNAL_ERROR);
            } else {
                writeResponse(exchange, getGson().toJson(createdEpic.get()), HttpURLConnection.HTTP_CREATED);
            }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Boolean> result = getTaskManager().removeEpicById(id.get());

            if (result.isPresent()) {
                if (!result.get()) {
                    writeResponse(exchange, convertToMessage("Епик с id=" + id.get() + " не найден"), HttpURLConnection.HTTP_NOT_FOUND);
                } else {
                    exchange.sendResponseHeaders(HttpURLConnection.HTTP_NO_CONTENT, -1);
                    exchange.getResponseBody().close();
                }
            } else {
                writeResponse(exchange, convertToMessage("Неизвестная ошибка"), HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } else {
            writeResponse(exchange, convertToMessage("Не указан id подзадачи для удаления"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }
}