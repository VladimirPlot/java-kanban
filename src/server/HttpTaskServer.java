package server;

import adapter.DurationAdapter;
import adapter.LocalDateTimeAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sun.net.httpserver.HttpServer;
import manager.Managers;
import manager.TaskManager;
import server.handler.*;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.Duration;
import java.time.LocalDateTime;

public class HttpTaskServer {
    private static final int PORT = 8080;
    private final HttpServer server;
    private final TaskManager taskManager;
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Duration.class, new DurationAdapter())  // Регистрация адаптера для Duration
            .registerTypeAdapter(LocalDateTime.class, new LocalDateTimeAdapter())  // Регистрация адаптера для LocalDateTime
            .create();

    public HttpTaskServer(TaskManager taskManager) throws IOException {
        this.taskManager = taskManager;
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);

        server.createContext("/tasks", new TaskHandler(taskManager, gson));
        server.createContext("/subtasks", new SubTaskHandler(taskManager, gson));
        server.createContext("/epics", new EpicHandler(taskManager, gson));
        server.createContext("/history", new HistoryHandler(taskManager, gson));
        server.createContext("/prioritized", new PrioritizedHandler(taskManager, gson));
    }

    public HttpTaskServer() throws IOException {
        this(Managers.getDefault());
    }

    public void start() {
        server.start();
        System.out.println("Сервер запущен на порту " + PORT);
    }

    public void stop() {
        server.stop(0);
        System.out.println("Сервер остановлен.");
    }

    public static Gson getGson() {
        return gson;
    }

    public static void main(String[] args) {
        try {
            HttpTaskServer server = new HttpTaskServer();
            server.start();
        } catch (IOException e) {
            System.err.println("Ошибка при запуске сервера: " + e.getMessage());
        }
    }
}