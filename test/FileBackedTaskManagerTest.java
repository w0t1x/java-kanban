import manager.FileBackedTaskManager;
import task.*;

import org.junit.jupiter.api.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    File tempFile;
    FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void tearDown() {
        // Игнорируем результат delete(), это просто очистка
        tempFile.delete();
    }

    @Test
    @DisplayName("Сохранение и загрузка обычной задачи с датой и длительностью")
    void saveAndLoadSimpleTask() {
        RealizationTask task = new RealizationTask("Test Task", "Description");
        task.setStartTime(LocalDateTime.of(2025, 8, 1, 12, 0));
        task.setDuration(Duration.ofMinutes(90));

        manager.createTask(task);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> loadedTasks = new ArrayList<>(loaded.getAllTasks());
        assertEquals(1, loadedTasks.size());

        Task loadedTask = loadedTasks.get(0);
        assertEquals(task.getName(), loadedTask.getName());
        assertEquals(task.getDescription(), loadedTask.getDescription());
        assertEquals(task.getStartTime(), loadedTask.getStartTime());
        assertEquals(task.getDuration(), loadedTask.getDuration());
    }

    @Test
    @DisplayName("Сохранение и загрузка эпика с подзадачей")
    void saveAndLoadEpicWithSubtask() {
        Epic epic = new Epic("Epic 1", "Epic description");
        manager.createEpic(epic);

        Subtask subtask = new Subtask("Subtask 1", "Subtask description", Status.NEW, epic.getId());
        subtask.setStartTime(LocalDateTime.of(2025, 8, 2, 14, 0));
        subtask.setDuration(Duration.ofMinutes(60));
        manager.createSubtask(subtask);

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Epic> loadedEpics = new ArrayList<>(loaded.getAllEpics());
        List<Subtask> loadedSubs = new ArrayList<>(loaded.getAllSubtasks());

        assertEquals(1, loadedEpics.size());
        assertEquals(1, loadedSubs.size());

        Epic loadedEpic = loadedEpics.get(0);
        Subtask loadedSub = loadedSubs.get(0);

        assertTrue(loadedEpic.getSubtaskIds().contains(loadedSub.getId()));
        assertEquals(subtask.getStartTime(), loadedSub.getStartTime());
        assertEquals(subtask.getDuration(), loadedSub.getDuration());
    }

    @Test
    @DisplayName("Приоритетная сортировка задач по startTime")
    void prioritizedTasksSorted() {
        RealizationTask task1 = new RealizationTask("Task 1", "Desc 1");
        task1.setStartTime(LocalDateTime.of(2025, 8, 1, 10, 0));
        task1.setDuration(Duration.ofMinutes(30));

        RealizationTask task2 = new RealizationTask("Task 2", "Desc 2");
        task2.setStartTime(LocalDateTime.of(2025, 8, 1, 8, 0));
        task2.setDuration(Duration.ofMinutes(45));

        manager.createTask(task1);
        manager.createTask(task2);

        List<Task> sorted = new ArrayList<>(manager.getPrioritizedTasks());
        assertEquals("Task 2", sorted.get(0).getName());
        assertEquals("Task 1", sorted.get(1).getName());
    }

    @Test
    @DisplayName("Сохранение пустого файла и загрузка без ошибок")
    void loadEmptyFile() throws IOException {
        Files.write(tempFile.toPath(), "id,type,name,status,description,startTime,duration,epic\n".getBytes());
        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty());
        assertTrue(loaded.getAllEpics().isEmpty());
        assertTrue(loaded.getAllSubtasks().isEmpty());
    }
}
