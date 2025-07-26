import manager.*;

import static org.junit.jupiter.api.Assertions.*;

import task.*; // Импортируем классы задач
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public abstract class TaskManagerTest<T extends TaskManager> {

    protected abstract T createTaskManager();

    protected abstract HistoryManager createHistoryManager();

    protected T taskManager;

    @BeforeEach
    void setUp() {
        taskManager = createTaskManager();
    }

    @Test
    void shouldCalculateEndTime() {
        Task task = new RealizationTask("Test Task", "Description");
        LocalDateTime start = LocalDateTime.of(2023, 10, 26, 10, 0);
        Duration duration = Duration.ofMinutes(90);
        task.setStartTime(start);
        task.setDuration(duration);

        LocalDateTime expectedEnd = start.plus(duration);
        assertEquals(expectedEnd, task.getEndTime());
    }

    @Test
    void shouldReturnNullEndTimeIfStartOrDurationIsNull() {
        Task task1 = new RealizationTask("Test Task 1", "Description");
        task1.setStartTime(LocalDateTime.now());
        assertNull(task1.getEndTime());

        Task task2 = new RealizationTask("Test Task 2", "Description");
        task2.setDuration(Duration.ofMinutes(60));
        assertNull(task2.getEndTime());

        Task task3 = new RealizationTask("Test Task 3", "Description");
        assertNull(task3.getEndTime());
    }

    @Test
    void shouldCalculateEpicStatus_NEW() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", Status.NEW, epic.getId());
        taskManager.createSubtask(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc2", Status.NEW, epic.getId());
        taskManager.createSubtask(sub2);

        Epic updatedEpic = findEpicInManager(epic.getId());
        assertNotNull(updatedEpic);
        assertEquals(Status.NEW, updatedEpic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatus_DONE() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", Status.DONE, epic.getId());
        taskManager.createSubtask(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc2", Status.DONE, epic.getId());
        taskManager.createSubtask(sub2);

        Epic updatedEpic = findEpicInManager(epic.getId());
        assertNotNull(updatedEpic);
        assertEquals(Status.DONE, updatedEpic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatus_IN_PROGRESS_mixed() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", Status.NEW, epic.getId());
        taskManager.createSubtask(sub1);
        Subtask sub2 = new Subtask("Sub2", "Desc2", Status.DONE, epic.getId());
        taskManager.createSubtask(sub2);

        Epic updatedEpic = findEpicInManager(epic.getId());
        assertNotNull(updatedEpic);
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldCalculateEpicStatus_IN_PROGRESS_direct() {
        Epic epic = new Epic("Epic", "Desc");
        taskManager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", Status.IN_PROGRESS, epic.getId());
        taskManager.createSubtask(sub1);

        Epic updatedEpic = findEpicInManager(epic.getId());
        assertNotNull(updatedEpic);
        assertEquals(Status.IN_PROGRESS, updatedEpic.getStatus());
    }

    @Test
    void shouldHandleEpicWithNoSubtasks() {
        Epic epic = new Epic("Empty Epic", "No subtasks");
        taskManager.createEpic(epic);

        Epic updatedEpic = findEpicInManager(epic.getId());
        assertNotNull(updatedEpic);
        assertEquals(Status.NEW, updatedEpic.getStatus());
    }

    @Test
    void shouldCalculateEpicTimeAndStatus() {
        Epic epic = new Epic("Test Epic", "Description");
        taskManager.createEpic(epic);

        Subtask sub1 = new Subtask("Sub1", "Desc1", Status.NEW, epic.getId());
        sub1.setStartTime(LocalDateTime.of(2023, 10, 26, 10, 0));
        sub1.setDuration(Duration.ofMinutes(30));
        taskManager.createSubtask(sub1);

        Subtask sub2 = new Subtask("Sub2", "Desc2", Status.NEW, epic.getId());
        sub2.setStartTime(LocalDateTime.of(2023, 10, 26, 11, 0));
        sub2.setDuration(Duration.ofMinutes(60));
        taskManager.createSubtask(sub2);

        Epic updatedEpic = findEpicInManager(epic.getId());
        assertNotNull(updatedEpic);

        assertEquals(Status.NEW, updatedEpic.getStatus());
        assertEquals(LocalDateTime.of(2023, 10, 26, 10, 0),
                updatedEpic.getStartTime());
        assertEquals(LocalDateTime.of(2023, 10, 26, 12, 0),
                updatedEpic.getEndTime());
        assertEquals(Duration.ofMinutes(120), updatedEpic.getDuration());
    }

    @Test
    void shouldReturnPrioritizedTasksSorted() {
        Task task1 = new RealizationTask("Task1", "Desc1");
        task1.setStartTime(LocalDateTime.of(2023, 10, 26, 11, 0));
        taskManager.createTask(task1);

        Task task2 = new RealizationTask("Task2", "Desc2");
        task2.setStartTime(LocalDateTime.of(2023, 10, 26, 10, 0));
        taskManager.createTask(task2);

        Task task3 = new RealizationTask("Task3", "Desc3");
        taskManager.createTask(task3); // No start time

        List<Task> prioritized = taskManager.getPrioritizedTasks();
        assertEquals(2, prioritized.size());
        assertEquals(task2, prioritized.get(0));
        assertEquals(task1, prioritized.get(1));
    }

    @Test
    void shouldDetectTimeIntersection_SameTime() {
        Task task1 = new RealizationTask("Task1", "Desc1");
        task1.setStartTime(LocalDateTime.of(2023, 10, 26, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        taskManager.createTask(task1);

        Task task2 = new RealizationTask("Task2", "Desc2");
        task2.setStartTime(LocalDateTime.of(2023, 10, 26, 10, 30));
        task2.setDuration(Duration.ofMinutes(60));

        ManagerSaveException exception = assertThrows(ManagerSaveException.class, () -> {
            taskManager.createTask(task2);
        });
        assertTrue(exception.getMessage().contains("пересекается"));
    }

    @Test
    void shouldAllowNonIntersectingTasks() {
        Task task1 = new RealizationTask("Task1", "Desc1");
        task1.setStartTime(LocalDateTime.of(2023, 10, 26, 10, 0));
        task1.setDuration(Duration.ofMinutes(60));
        // Заменяем assertDoesNotThrow на try-catch
        try {
            taskManager.createTask(task1);
        } catch (Exception e) {
            fail("Создание задачи без пересечений не должно вызывать исключений, но было выброшено: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
        }

        Task task2 = new RealizationTask("Task2", "Desc2");
        task2.setStartTime(LocalDateTime.of(2023, 10, 26, 11, 0));
        task2.setDuration(Duration.ofMinutes(60));
        // Заменяем assertDoesNotThrow на try-catch
        try {
            taskManager.createTask(task2);
        } catch (Exception e) {
            fail("Создание задачи без пересечений не должно вызывать исключений, но было выброшено: "
                    + e.getClass().getSimpleName() + " - " + e.getMessage());
        }
    }

    @Test
    void shouldAddTaskToHistoryOnGet() {
        Task task = new RealizationTask("History Task", "For history test");
        taskManager.createTask(task);

        assertTrue(taskManager.getHistory().isEmpty());

        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

    private Epic findEpicInManager(int epicId) {
        return taskManager.getAllEpics().stream()
                .filter(e -> e.getId() == epicId)
                .findFirst()
                .orElse(null);
    }
}