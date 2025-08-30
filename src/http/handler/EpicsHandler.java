package http.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import manager.TaskManager;
import task.Epic;
import task.Subtask;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;

public class EpicsHandler extends BaseHttpHandler implements HttpHandler {
    private final TaskManager manager;
    private final Gson gson;

    public EpicsHandler(TaskManager manager, Gson gson) {
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
                    Collection<Epic> epics = manager.getAllEpics();
                    sendResponse(exchange, 200, gson.toJson(epics));
                } else if (segments.length == 3) {
                    int id = Integer.parseInt(segments[2]);
                    Epic epic = manager.getAllEpics().stream()
                            .filter(e -> e.getId() == id)
                            .findFirst()
                            .orElse(null);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        sendResponse(exchange, 200, gson.toJson(epic));
                    }
                } else if (segments.length == 4 && "subtasks".equals(segments[3])) {
                    int id = Integer.parseInt(segments[2]);
                    Epic epic = manager.getAllEpics().stream()
                            .filter(e -> e.getId() == id)
                            .findFirst()
                            .orElse(null);
                    if (epic == null) {
                        sendNotFound(exchange);
                    } else {
                        List<Subtask> subs = manager.getSubtasksByEpic(epic);
                        sendResponse(exchange, 200, gson.toJson(subs));
                    }
                }
            } else if ("POST".equals(method)) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), StandardCharsets.UTF_8);
                Epic epic = gson.fromJson(body, Epic.class);
                Epic created = manager.createEpic(epic);
                sendResponse(exchange, 201, gson.toJson(created));
            } else if ("DELETE".equals(method) && segments.length == 3) {
                int id = Integer.parseInt(segments[2]);
                manager.deleteEpic(id);
                sendResponse(exchange, 200, "{\"status\":\"deleted\"}");
            } else {
                sendNotFound(exchange);
            }
        } catch (Exception e) {
            sendServerError(exchange, e.getMessage());
        }
    }
}
