import manager.*;
import task.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

public class FileBackedTest {
    @Test
    void shouldLoadAndSaveTasksFromFile() throws IOException {
        File tempFile = File.createTempFile("tasks_", ".csv");
        tempFile.deleteOnExit();

        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new RealizationTask("Test", "Desc");
        manager.createTask(task);
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        assertNotNull(loaded.getTask(task.getId()));
    }
}
