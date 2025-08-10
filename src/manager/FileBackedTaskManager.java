package manager;

import task.*;

import java.io.*;
import java.nio.file.Files;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    @Override
    public Task createTask(Task task) {
        Task created = super.createTask(task);
        save();
        return created;
    }

    @Override
    public Epic createEpic(Epic epic) {
        Epic created = super.createEpic(epic);
        save();
        return created;
    }

    @Override
    public Subtask createSubtask(Subtask subtask) {
        Subtask created = super.createSubtask(subtask);
        save();
        return created;
    }

    @Override
    public void deleteTask(int id) {
        super.deleteTask(id);
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    @Override
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteAllSubtasks() {
        super.getAllSubtasks().forEach(s -> prioritizedTasks.remove(s));
        super.getAllSubtasks().clear();
        save();
    }

    @Override
    public void deleteAllEpics() {
        super.getAllEpics().clear();
        super.getAllSubtasks().clear();
        prioritizedTasks.clear();
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,startTime,duration,epic");
            writer.newLine();

            for (Task task : tasks.values()) {
                writer.write(taskToString(task));
                writer.newLine();
            }
            for (Epic epic : epics.values()) {
                writer.write(taskToString(epic));
                writer.newLine();
            }
            for (Subtask subtask : subtasks.values()) {
                writer.write(taskToString(subtask));
                writer.newLine();
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        String startTimeStr = task.getStartTime() != null ? task.getStartTime().toString() : "";
        String durationStr = task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "";
        String epicIdStr = "";

        if (task instanceof Subtask sub) {
            epicIdStr = String.valueOf(sub.getEpicId());
        }

        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                startTimeStr,
                durationStr,
                epicIdStr
        );
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) return manager; // пустой файл

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i).trim();
                if (!line.isEmpty()) {
                    Task task = fromString(line);
                    switch (task.getType()) {
                        case TASK -> manager.tasks.put(task.getId(), task);
                        case EPIC -> manager.epics.put(task.getId(), (Epic) task);
                        case SUBTASK -> {
                            Subtask sub = (Subtask) task;
                            manager.subtasks.put(sub.getId(), sub);
                            Epic epicOwner = manager.epics.get(sub.getEpicId());
                            if (epicOwner != null) {
                                epicOwner.addSubtaskId(sub.getId());
                            }
                        }
                    }
                    if (task.getStartTime() != null) {
                        manager.prioritizedTasks.add(task);
                    }
                }
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла", e);
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",", -1);
        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        LocalDateTime startTime = parts[5].isEmpty() ? null : LocalDateTime.parse(parts[5]);
        Duration duration = parts[6].isEmpty() ? null : Duration.ofMinutes(Long.parseLong(parts[6]));

        switch (type) {
            case TASK -> {
                RealizationTask task = new RealizationTask(id, name, description, status);
                task.setStartTime(startTime);
                task.setDuration(duration);
                return task;
            }
            case EPIC -> {
                Epic epic = new Epic(id, name, description);
                epic.setStatus(status);
                epic.setStartTime(startTime);
                epic.setDuration(duration);
                return epic;
            }
            case SUBTASK -> {
                int epicId = Integer.parseInt(parts[7]);
                Subtask subtask = new Subtask(id, name, description, status, epicId);
                subtask.setStartTime(startTime);
                subtask.setDuration(duration);
                return subtask;
            }
            default -> throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}
