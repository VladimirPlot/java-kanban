package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskTimeException;
import manager.TaskManager;
import resource.SubTask;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.net.HttpURLConnection;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(TaskManager taskManager, Gson gson) {
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
            Optional<SubTask> subTask = getTaskManager().getSubTaskById(id.get());

            if (subTask.isEmpty()) {
                writeResponse(exchange, convertToMessage("Подзадача с id=" + id.get() + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
            } else {
                writeResponse(exchange, getGson().toJson(subTask.get()), HttpURLConnection.HTTP_OK);
            }
        } else {
            List<SubTask> subTasks = getTaskManager().getAllSubTasks();
            writeResponse(exchange, getGson().toJson(subTasks), HttpURLConnection.HTTP_OK);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        if (!isJsonRequest(exchange)) {
            writeResponse(exchange, convertToMessage("Некорректный заголовок Content-Type"), HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        SubTask subTask = getGson().fromJson(body, SubTask.class);

        if (subTask == null) {
            writeResponse(exchange, convertToMessage("Ошибка десериализации задачи"), HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }

        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<SubTask> updatedSubTask = getTaskManager().updateSubTask(subTask);

            if (updatedSubTask.isEmpty()) {
                writeResponse(exchange, convertToMessage("Задача с id=" + id.get() + " не найдена для обновления"), HttpURLConnection.HTTP_NOT_FOUND);
            } else {
                writeResponse(exchange, getGson().toJson(updatedSubTask.get()), HttpURLConnection.HTTP_OK);
            }
        } else {
            try {
                Optional<SubTask> createdSubTask = getTaskManager().createSubTask(subTask);
                writeResponse(exchange, getGson().toJson(createdSubTask.get()), HttpURLConnection.HTTP_CREATED);
            } catch (InvalidTaskTimeException e) {
                writeResponse(exchange, convertToMessage("Задача пересекается с существующими: " + e.getMessage()), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
            }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            try {
                Optional<Boolean> result = getTaskManager().removeSubTaskById(id.get());

                if (result.isPresent()) {
                    if (!result.get()) {
                        writeResponse(exchange, convertToMessage("Подзадача с id=" + id.get() + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
                    } else {
                        writeResponse(exchange, "", HttpURLConnection.HTTP_NO_CONTENT);
                    }
                } else {
                    writeResponse(exchange, convertToMessage("Неизвестная ошибка"), HttpURLConnection.HTTP_INTERNAL_ERROR);
                }
            } catch (Exception e) {
                writeResponse(exchange, convertToMessage("Ошибка сервера: " + e.getMessage()), HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } else {
            writeResponse(exchange, convertToMessage("Не указан id подзадачи для удаления"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }
}