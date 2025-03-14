package server.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import exception.InvalidTaskTimeException;
import manager.TaskManager;
import resource.Task;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.net.HttpURLConnection;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager, Gson gson) {
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
            Optional<Task> task = getTaskManager().getTaskById(id.get());

            if (task.isEmpty()) {
                writeResponse(exchange, convertToMessage("Задача с id=" + id.get() + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
            } else {
                writeResponse(exchange, getGson().toJson(task.get()), HttpURLConnection.HTTP_OK);
            }
        } else {
            List<Task> tasks = getTaskManager().getAllTasks();
            writeResponse(exchange, getGson().toJson(tasks), HttpURLConnection.HTTP_OK);
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        if (!isJsonRequest(exchange)) {
            writeResponse(exchange, convertToMessage("Некорректный заголовок Content-Type"), HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }

        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        Task task = getGson().fromJson(body, Task.class);

        if (task == null) {
            writeResponse(exchange, convertToMessage("Ошибка десериализации задачи"), HttpURLConnection.HTTP_BAD_REQUEST);
            return;
        }

        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Task> updatedTask = getTaskManager().updateTask(task);

            if (updatedTask.isEmpty()) {
                writeResponse(exchange, convertToMessage("Задача с id=" + id.get() + " не найдена для обновления"), HttpURLConnection.HTTP_NOT_FOUND);
            } else {
                writeResponse(exchange, getGson().toJson(updatedTask.get()), HttpURLConnection.HTTP_OK);
            }
        } else {
            try {
                Optional<Task> createdTask = getTaskManager().createTask(task);
                writeResponse(exchange, getGson().toJson(createdTask.get()), HttpURLConnection.HTTP_CREATED);
            } catch (InvalidTaskTimeException e) {
                writeResponse(exchange, convertToMessage("Задача пересекается с существующими: " + e.getMessage()), HttpURLConnection.HTTP_NOT_ACCEPTABLE);
            }
        }
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        Optional<Integer> id = getId(exchange);

        if (id.isPresent()) {
            Optional<Boolean> result = getTaskManager().removeTaskById(id.get());

            if (result.isPresent()) {
                if (!result.get()) {
                    writeResponse(exchange, convertToMessage("Задача с id=" + id.get() + " не найдена"), HttpURLConnection.HTTP_NOT_FOUND);
                } else {
                    writeResponse(exchange, convertToMessage("Задача удалена"), HttpURLConnection.HTTP_OK);
                }
            } else {
                writeResponse(exchange, convertToMessage("Неизвестная ошибка"), HttpURLConnection.HTTP_INTERNAL_ERROR);
            }
        } else {
            writeResponse(exchange, convertToMessage("Не указан id задачи для удаления"), HttpURLConnection.HTTP_BAD_REQUEST);
        }
    }
}