package http;

import com.google.gson.Gson;
import manager.InMemoryTaskManager;
import manager.Managers;
import manager.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;
import java.net.http.HttpClient;

public abstract class BaseHttpTest {
    protected TaskManager manager;
    protected HttpTaskServer server;
    protected Gson gson;
    protected HttpClient client;

    @BeforeEach
    void startServer() throws IOException {
        manager = Managers.getDefault();
        server = new HttpTaskServer(manager);
        gson = HttpTaskServer.getGson();
        client = HttpClient.newHttpClient();
        server.start();
    }

    @AfterEach
    void stopServer() {
        server.stop();
    }
}
