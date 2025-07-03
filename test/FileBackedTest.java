import manager.*;
import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileBackedTest {
    @Test
    void shouldSaveAndLoadFromFile() throws IOException {
        File tempFile = File.createTempFile("test_tasks", ".csv");

        TaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new Task("Test", "Desc");
        manager.createTask(task);

        TaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertEquals(manager.getTask(task.getId()), loaded.getTask(task.getId()));
    }
}
