package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import org.junit.jupiter.api.*;
import resource.Task;
import resource.Status;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class HttpTaskServerTasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();

        taskServer.start();
    }

    @AfterEach
    void tearDown() {
        taskServer.stop();
    }

    @Test
    void shouldCreateTask() throws IOException, InterruptedException {
        Task task = new Task("Test Task", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String taskJson = gson.toJson(task);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(taskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test Task", tasks.getFirst().getName());
    }

    @Test
    void shouldGetTaskById() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description of task", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);

        int taskId = task.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Task receivedTask = gson.fromJson(response.body(), Task.class);
        assertEquals(taskId, receivedTask.getId());
        assertEquals("Task", receivedTask.getName());
    }

    @Test
    void shouldReturnAllTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        Task task2 = new Task("Task 2", "Description", Status.DONE, Duration.ofMinutes(20), LocalDateTime.now().plusMinutes(30));
        manager.createTask(task1);
        manager.createTask(task2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, response.statusCode());

        Task[] tasks = gson.fromJson(response.body(), Task[].class);
        assertEquals(2, tasks.length);
    }

    @Test
    void shouldUpdateTask() throws IOException, InterruptedException {
        Task task = new Task("Task", "Description of task", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createTask(task);

        int taskId = task.getId();

        Task updatedTask = new Task("Updated Task", "Updated Description", Status.IN_PROGRESS, Duration.ofMinutes(45), LocalDateTime.now().plusMinutes(10));
        updatedTask.setId(taskId);
        String updatedTaskJson = gson.toJson(updatedTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + taskId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Task modifiedTask = manager.getTaskById(taskId).orElseThrow();
        assertEquals("Updated Task", modifiedTask.getName());
        assertEquals(Status.IN_PROGRESS, modifiedTask.getStatus());
        assertEquals(Duration.ofMinutes(45), modifiedTask.getDuration());
    }

    @Test
    void shouldDeleteTask() throws IOException, InterruptedException {
        Task task = new Task("Task to Delete", "Description", Status.NEW, Duration.ofMinutes(10), LocalDateTime.now());
        manager.createTask(task);

        HttpRequest deleteRequest = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks/" + task.getId()))
                .DELETE()
                .build();

        HttpResponse<String> deleteResponse = client.send(deleteRequest, HttpResponse.BodyHandlers.ofString());
        assertEquals(200, deleteResponse.statusCode());

        assertTrue(manager.getAllTasks().isEmpty());
    }
}