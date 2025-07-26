import manager.*;

public class InMemoryTaskManagerTest extends TaskManagerTest<InMemoryTaskManager> {

    @Override
    protected InMemoryTaskManager createTaskManager() {
        return new InMemoryTaskManager();
    }

    @Override
    protected HistoryManager createHistoryManager() {
        return Managers.getDefaultHistory();
    }
}