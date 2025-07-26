package manager;

import task.*;
import java.io.*;
import java.nio.file.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

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
    public void updateTask(Task task) {
        super.updateTask(task);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void updateSubtask(Subtask subtask) {
        super.updateSubtask(subtask);
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            // Обновлен заголовок для новых полей
            writer.write("id,type,name,status,description,epic,duration,startTime");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(taskToString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(epicToString(epic));
                writer.newLine();
            }
            for (Subtask subtask : getAllSubtasks()) {
                writer.write(subtaskToString(subtask));
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл", e);
        }
    }

    private String taskToString(Task task) {
        return String.join(",",
                String.valueOf(task.getId()),
                task.getType().name(),
                task.getName(),
                task.getStatus().name(),
                task.getDescription(),
                "", // epic
                task.getDuration() != null ? String.valueOf(task.getDuration().toMinutes()) : "",
                task.getStartTime() != null ? task.getStartTime().toString() : ""
        );
    }

    private String epicToString(Epic epic) {
        return String.join(",",
                String.valueOf(epic.getId()),
                epic.getType().name(),
                epic.getName(),
                epic.getStatus().name(),
                epic.getDescription(),
                "", // epic
                "", // duration
                ""  // startTime
        );
    }

    private String subtaskToString(Subtask subtask) {
        return String.join(",",
                String.valueOf(subtask.getId()),
                subtask.getType().name(),
                subtask.getName(),
                subtask.getStatus().name(),
                subtask.getDescription(),
                String.valueOf(subtask.getEpicId()),
                subtask.getDuration() != null ? String.valueOf(subtask.getDuration().toMinutes()) : "",
                subtask.getStartTime() != null ? subtask.getStartTime().toString() : ""
        );
    }
    // ----------------------------

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() < 2) return manager;

            for (int i = 1; i < lines.size(); i++) {
                String line = lines.get(i);
                if (line.trim().isEmpty()) continue;
                Task task = fromString(line);

                switch (task.getType()) {
                    case TASK:
                        manager.tasks.put(task.getId(), task);
                        break;
                    case EPIC:
                        Epic epic = (Epic) task;
                        manager.epics.put(epic.getId(), epic);
                        break;
                    case SUBTASK:
                        Subtask subtask = (Subtask) task;
                        manager.subtasks.put(subtask.getId(), subtask);
                        Epic epicOwner = manager.epics.get(subtask.getEpicId());
                        if (epicOwner != null) {
                            epicOwner.addSubtaskId(subtask.getId());
                        }
                        break;
                    default:
                        throw new IllegalArgumentException("Неизвестный тип задачи: " + task.getType());
                }
            }

            // После загрузки всех задач, обновляем статусы и временные данные эпиков
            for (Epic epic : manager.epics.values()) {
                manager.updateEpicTimeAndStatus(epic);
            }

        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка чтения из файла", e);
        }
        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",", -1); // -1 to include trailing empty strings
        if (parts.length < 8) {
            throw new IllegalArgumentException("Неверный формат строки задачи: " + value);
        }

        int id = Integer.parseInt(parts[0]);
        TaskType type = TaskType.valueOf(parts[1]);
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];
        String durationStr = parts[6]; // Изменено индексирование
        String startTimeStr = parts[7]; // Изменено индексирование

        Duration duration = null;
        if (!durationStr.isEmpty()) {
            try {
                duration = Duration.ofMinutes(Long.parseLong(durationStr));
            } catch (NumberFormatException e) {
                // Log or handle parsing error if needed
            }
        }

        LocalDateTime startTime = null;
        if (!startTimeStr.isEmpty()) {
            try {
                startTime = LocalDateTime.parse(startTimeStr);
            } catch (Exception e) {
                // Log or handle parsing error if needed
            }
        }

        switch (type) {
            case TASK:
                RealizationTask task = new RealizationTask(id, name, description, status);
                task.setDuration(duration);
                task.setStartTime(startTime);
                return task;
            case EPIC:
                return new Epic(id, name, description);
            case SUBTASK:
                int epicId = Integer.parseInt(parts[5]);
                Subtask subtask = new Subtask(id, name, description, status, epicId);
                subtask.setDuration(duration);
                subtask.setStartTime(startTime);
                return subtask;
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}