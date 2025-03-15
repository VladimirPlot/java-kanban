package server;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.TaskManager;
import resource.Epic;
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

public class HttpTaskServerEpicsTest {
    private TaskManager manager;
    private HttpTaskServer taskServer;
    private Gson gson;
    private HttpClient client;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        taskServer = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();

        taskServer.start();
    }

    @AfterEach
    public void shutDown() {
        taskServer.stop();
    }

    @Test
    void shouldCreateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Task", "Description of epic", Duration.ofMinutes(60), LocalDateTime.now());
        String epicJson = gson.toJson(epic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(epicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertEquals(1, manager.getAllEpics().size());
        assertEquals("Epic Task", manager.getAllEpics().getFirst().getName());
    }

    @Test
    void shouldGetEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Task", "Description of epic", Duration.ofMinutes(60), LocalDateTime.now());
        manager.createEpic(epic);

        int epicId = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        Epic receivedEpic = gson.fromJson(response.body(), Epic.class);
        assertEquals(epicId, receivedEpic.getId());
        assertEquals("Epic Task", receivedEpic.getName());
    }

    @Test
    void shouldGetAllEpics() throws IOException, InterruptedException {
        Epic epic1 = new Epic("Epic Task 1", "Description of epic 1", Duration.ofMinutes(60), LocalDateTime.now());
        Epic epic2 = new Epic("Epic Task 2", "Description of epic 2", Duration.ofMinutes(90), LocalDateTime.now().plusMinutes(40));

        manager.createEpic(epic1);
        manager.createEpic(epic2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("Epic Task 1"));
        assertTrue(response.body().contains("Epic Task 2"));
    }

    @Test
    void shouldDeleteEpicById() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Task", "Description of epic", Duration.ofMinutes(60), LocalDateTime.now());
        manager.createEpic(epic);

        int epicId = epic.getId();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .DELETE()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(204, response.statusCode());
        assertTrue(manager.getEpicById(epicId).isEmpty());
    }

    @Test
    void shouldUpdateEpic() throws IOException, InterruptedException {
        Epic epic = new Epic("Epic Task", "Description of epic", Duration.ofMinutes(60), LocalDateTime.now());
        manager.createEpic(epic);

        int epicId = epic.getId();

        Epic updatedEpic = new Epic("Updated Epic Task", "Updated Description", Duration.ofMinutes(120), LocalDateTime.now().plusMinutes(10));
        updatedEpic.setId(epicId);
        String updatedEpicJson = gson.toJson(updatedEpic);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/epics/" + epicId))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(updatedEpicJson))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());

        Epic modifiedEpic = manager.getEpicById(epicId).orElseThrow();
        assertEquals("Updated Epic Task", modifiedEpic.getName());
        assertEquals(Duration.ofMinutes(120), modifiedEpic.getDuration());
    }
}