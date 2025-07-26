import manager.*;
import task.*;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

public class FileBackedTest {

    @Test
    void shouldLoadAndSaveTasksFromFile() throws IOException {
        File tempFile = File.createTempFile("tasks_", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);
        Task task = new RealizationTask("Test", "Desc");
        task.setDuration(Duration.ofMinutes(45));
        task.setStartTime(LocalDateTime.of(2023, 1, 1, 12, 0));
        manager.createTask(task);
        manager.save();
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Task loadedTask = loaded.getTask(task.getId());
        assertNotNull(loadedTask);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStatus(), loadedTask.getStatus());
        assertEquals(task.getDuration(), loadedTask.getDuration());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getEndTime(), loadedTask.getEndTime());
    }

    @Test
    void shouldLoadAndSaveEpicFromFile() throws IOException {
        File tempFile = File.createTempFile("tasks_epic_", ".csv");
        tempFile.deleteOnExit();
        FileBackedTaskManager manager = new FileBackedTaskManager(tempFile);

        Epic epic = new Epic("Test Epic", "Description");
        manager.createEpic(epic);

        Subtask subtask1 = new Subtask("Sub1", "Desc1", Status.NEW, epic.getId());
        subtask1.setDuration(Duration.ofMinutes(30));
        subtask1.setStartTime(LocalDateTime.of(2023, 1, 1, 10, 0));
        manager.createSubtask(subtask1);

        Subtask subtask2 = new Subtask("Sub2", "Desc2", Status.NEW, epic.getId());
        subtask2.setDuration(Duration.ofMinutes(60));
        subtask2.setStartTime(LocalDateTime.of(2023, 1, 1, 11, 0));
        manager.createSubtask(subtask2);

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);
        Epic loadedEpic = loaded.getAllEpics().iterator().next();
        assertNotNull(loadedEpic);
        assertEquals(epic.getName(), loadedEpic.getName());
        assertEquals(epic.getDescription(), loadedEpic.getDescription());
        assertEquals(Duration.ofMinutes(120), loadedEpic.getDuration());

        assertEquals(LocalDateTime.of(2023, 1, 1, 10, 0), loadedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 1, 1, 12, 0), loadedEpic.getEndTime());
    }
}