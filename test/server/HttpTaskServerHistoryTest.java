package server;

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

public class HttpTaskServerHistoryTest {
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
    void shouldGetHistory() throws IOException, InterruptedException {
        SubTask subTask = new SubTask(epic, "Subtask", "Description", Status.NEW, Duration.ofMinutes(30), LocalDateTime.now());
        manager.createSubTask(subTask);

        int subTaskId = subTask.getId();
        manager.getSubTaskById(subTaskId);
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains(String.valueOf(subTaskId)));
    }

    @Test
    void shouldGetEmptyHistoryWhenNoTasks() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}