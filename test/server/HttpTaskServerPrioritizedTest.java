package server;

import manager.InMemoryTaskManager;
import manager.TaskManager;
import resource.Epic;
import resource.SubTask;
import resource.Task;
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

public class HttpTaskServerPrioritizedTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private HttpClient client;
    private Epic epic;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
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
    void shouldGetPrioritizedTasks() throws IOException, InterruptedException {
        Task task1 = new Task("Task 1", "Description of task 1", Status.NEW, Duration.ofMinutes(60), LocalDateTime.now().plusMinutes(60));
        SubTask subTask1 = new SubTask(epic, "Subtask 1", "Description", Status.NEW, Duration.ofMinutes(15), LocalDateTime.now());
        SubTask subTask2 = new SubTask(epic, "Subtask 2", "Description", Status.IN_PROGRESS, Duration.ofMinutes(15), LocalDateTime.now().plusMinutes(20));

        manager.createTask(task1);
        manager.createSubTask(subTask1);
        manager.createSubTask(subTask2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Task 1"));
        assertTrue(response.body().contains("Subtask 1"));
        assertTrue(response.body().contains("Subtask 2"));
    }

    @Test
    void shouldGetEmptyPrioritizedWhenNoTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}