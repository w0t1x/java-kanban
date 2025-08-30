package http;

import org.junit.jupiter.api.*;
import task.Epic;
import task.Subtask;
import task.Status;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerSubtasksTest extends BaseHttpTest {
    @Test
    void shouldCreateSubtaskSuccessfully() throws Exception {
        Epic epic = manager.createEpic(new Epic("Epic1", "desc"));
        Subtask sub = new Subtask("Sub1", "Sub desc", Status.NEW, epic.getId());
        String body = gson.toJson(sub);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks"))
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .header("Content-Type", "application/json")
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        assertTrue(response.body().contains("Sub1"));
    }

    @Test
    void shouldReturn404WhenSubtaskNotFound() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/subtasks/999"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }
}
