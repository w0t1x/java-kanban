import manager.*;
import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TaskManagerTest {
    private TaskManager taskManager;

    @BeforeEach
    public void setup() {
        taskManager = Managers.getDefault();
    }

    @Test
    void addNewTask() {
        Task task = new Task("Test addNewTask", "Test addNewTask description");
        taskManager.createTask(task);
        Task savedTask = taskManager.getTask(task.getId());

        assertNotNull(savedTask, "������ �� �������.");
        assertEquals(task, savedTask, "������ �� ���������.");
    }

    @Test
    void addTaskToHistory() {
        Task task = new Task("Test", "Test description");
        taskManager.createTask(task);
        taskManager.getTask(task.getId());

        List<Task> history = taskManager.getHistory();
        assertEquals(1, history.size(), "������� ������ ��������� ���� ������.");
        assertEquals(task, history.get(0), "������ � ������� �� ��������� � �����������.");
    }

    @Test
    void limitHistoryToTen() {
        for (int i = 1; i <= 12; i++) {
            Task task = new Task("Task " + i, "Description " + i);
            taskManager.createTask(task);
            taskManager.getTask(task.getId());
        }

        List<Task> history = taskManager.getHistory();
        assertEquals(10, history.size(), "������� ������ ��������� �������� 10 �����.");
    }

    @Test
    void addEpicAndSubtasks() {
        Epic epic = new Epic("Epic Task", "Epic description");
        taskManager.createEpic(epic);

        Subtask subtask1 = new Subtask("Subtask 1", "Subtask 1 description", Status.NEW, epic.getId());
        Subtask subtask2 = new Subtask("Subtask 2", "Subtask 2 description", Status.NEW, epic.getId());

        taskManager.createSubtask(subtask1);
        taskManager.createSubtask(subtask2);

        assertEquals(2, taskManager.getSubtasksByEpic(epic).size(), "���� ������ ��������� ��� ���������.");
    }
}
