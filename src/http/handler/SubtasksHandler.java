package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

public class SubtasksHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public SubtasksHandler(TaskManager manager, Gson gson) {
        this.manager = manager;
        this.gson = gson;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        try {
            String method = exchange.getRequestMethod();
            String path = exchange.getRequestURI().getPath();
            String[] segments = path.split("/");

            if ("GET".equals(method)) {
                if (segments.length == 2) {
                    Collection<Subtask> subs = manager.getAllSubtasks();
                    sendResponse(exchange, 200, gson.toJson(subs));
                } else if (segments.length == 3) {
                    int id = Integer.parseInt(segments[2]);
                    Subtask sub = manager.getAllSubtasks().stream()
                            .filter(s -> s.getId() == id)
                            .findFirst()
                            .orElse(null);
                    if (sub == null) {
                        sendNotFound(exchange);
                    } else {
                        sendResponse(exchange, 200, gson.toJson(sub));
                    }
                }
            } else if ("POST".equals(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Subtask subtask = gson.fromJson(body, Subtask.class);
                try {
                    Subtask created = manager.createSubtask(subtask);
                    sendResponse(exchange, 201, gson.toJson(created));
                } catch (IllegalArgumentException e) {
                    sendHasInteractions(exchange);
                }
            } else if ("DELETE".equals(method) && segments.length == 3) {
                int id = Integer.parseInt(segments[2]);
                manager.getAllSubtasks().removeIf(s -> s.getId() == id);
                sendResponse(exchange, 200, "{\"status\":\"deleted\"}");
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
