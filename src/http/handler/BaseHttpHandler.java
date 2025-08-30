package http.handler;

import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    protected void sendResponse(HttpExchange exchange, int statusCode, String response) throws IOException {
        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(statusCode, bytes.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(bytes);
        }
    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 404, "{\"error\":\"Not Found\"}");
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        sendResponse(exchange, 406, "{\"error\":\"Task intersects with existing\"}");
    }

    protected void sendServerError(HttpExchange exchange, String message) throws IOException {
        sendResponse(exchange, 500, "{\"error\":\"" + message + "\"}");
    }
}
