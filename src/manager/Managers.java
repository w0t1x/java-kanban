package manager;

public class Managers {
    public static TaskManager getDefault() {
        return new InMemoryTaskManager() {
            @Override
            public void deleteAllSubtasks() {

            }

            @Override
            public void deleteAllEpics() {

            }
        };
    }

    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}