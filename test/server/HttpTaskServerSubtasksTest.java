package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import resource.Epic;
import resource.SubTask;
import resource.Status;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerSubtasksTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;
    private Epic epic;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();

        taskServer.start();

        epic = new Epic("Epic Task", "Description of epic", Duration.ofMinutes(60), LocalDateTime.now());
        manager.createEpic(epic);
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void shouldCreateSubtask() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(epic, "Subtask", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        String subTaskJson = gson.toJson(subTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(subTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllSubTasks().size());
        assertEquals("Subtask", manager.getAllSubTasks().getFirst().getName());
    }

    @Test
    void shouldGetSubtaskById() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(epic, "Subtask", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subTask);

        int subTaskId = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTaskId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        SubTask receivedSubTask = gson.fromJson(response.body(), SubTask.class);
        assertEquals(subTaskId, receivedSubTask.getId());
        assertEquals("Subtask", receivedSubTask.getName());
    }

    @Test
    void shouldGetAllSubtasks() throws IOException, InterruptedException {
        // Создаём подзадачи
        SubTask subTask1 = new SubTask(epic, "Subtask1", "Description1", Status.NEW, Duration.ofMinutes(20), LocalDateTime.now());
        SubTask subTask2 = new SubTask(epic, "Subtask2", "Description2", Status.IN_PROGRESS, Duration.ofMinutes(30), LocalDateTime.now().plusMinutes(40));

        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Subtask1"));
        assertTrue(response.body().contains("Subtask2"));
    }

    @Test
    void shouldDeleteSubtaskById() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(epic, "Subtask", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subTask);

        int subTaskId = subTask.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTaskId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        assertTrue(manager.getSubTaskById(subTaskId).isEmpty());
    }

    @Test
    void shouldUpdateSubtask() throws IOException, InterruptedException {
        // Создаём подзадачу
        SubTask subTask = new SubTask(epic, "Subtask", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subTask);

        int subTaskId = subTask.getId();

        // Обновляем подзадачу
        SubTask updatedSubTask = new SubTask(epic, "Updated Subtask", "Updated Description", Status.DONE, Duration.ofMinutes(45), LocalDateTime.now().plusMinutes(10));
        updatedSubTask.setId(subTaskId);
        String updatedSubTaskJson = gson.toJson(updatedSubTask);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/" + subTaskId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedSubTaskJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        // Проверяем, что изменения сохранились
        SubTask modifiedSubTask = manager.getSubTaskById(subTaskId).orElseThrow();
        assertEquals("Updated Subtask", modifiedSubTask.getName());
        assertEquals(Status.DONE, modifiedSubTask.getStatus());
        assertEquals(Duration.ofMinutes(45), modifiedSubTask.getDuration());
    }
}