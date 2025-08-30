package http;

import org.junit.jupiter.api.*;
import task.RealizationTask;
import task.Status;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerHistoryTest extends BaseHttpTest {
    @Test
    void shouldReturnHistoryAfterGetTask() throws Exception {
        RealizationTask task = new RealizationTask("TaskHistory", "desc");
        task.setStartTime(LocalDateTime.now());
        task.setDuration(Duration.ofMinutes(15));
        manager.createTask(task);

        manager.getTask(task.getId()); // чтобы попасть в историю

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertTrue(response.body().contains("TaskHistory"));
    }

    @Test
    void shouldReturnEmptyHistoryInitially() throws Exception {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/history"))
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(200, response.statusCode());
        assertEquals("[]", response.body());
    }
}
