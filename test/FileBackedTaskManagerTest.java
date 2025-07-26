import manager.*;
import java.io.File;

public class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    File tempDir;

    @Override
    protected FileBackedTaskManager createTaskManager() {
        try {
            File tempFile = new File(tempDir, "tasks_test.csv");
            return new FileBackedTaskManager(tempFile);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось создать FileBackedTaskManager для теста", e);
        }
    }

    @Override
    protected HistoryManager createHistoryManager() {
        return Managers.getDefaultHistory();
    }
}