import manager.*;
import task.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TaskManagerTest {
    private HistoryManager historyManager;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
    }

    @Test
    void shouldAddAndGetHistory() {
        Task task = new Task("Test", "Desc");
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
        assertEquals(task, historyManager.getHistory().get(0));
    }

    @Test
    void shouldRemoveDuplicates() {
        Task task = new Task("Test", "Desc");
        historyManager.add(task);
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size());
    }

    @Test
    void shouldRemoveById() {
        Task task = new Task("Test", "Desc");
        historyManager.add(task);
        historyManager.remove(task.getId());
        assertTrue(historyManager.getHistory().isEmpty());
    }
}