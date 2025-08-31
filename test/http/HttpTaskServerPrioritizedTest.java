package http;

import org.junit.jupiter.api.*;
import task.RealizationTask;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerPrioritizedTest extends BaseHttpTest {
    @Test
    void shouldReturnTasksInOrder() throws Exception {
        RealizationTask t1 = new RealizationTask("T1", "desc1");
        t1.setStartTime(LocalDateTime.now().plusHours(1));
        t1.setDuration(Duration.ofMinutes(30));
        manager.createTask(t1);

        RealizationTask t2 = new RealizationTask("T2", "desc2");
        t2.setStartTime(LocalDateTime.now());
        t2.setDuration(Duration.ofMinutes(30));
        manager.createTask(t2);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().indexOf("T2") < response.body().indexOf("T1")); // T2 должен идти первым
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/prioritized"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}

