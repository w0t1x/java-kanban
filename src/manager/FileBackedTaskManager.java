package manager;

import task.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
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
    public void deleteAllTasks() {
        super.deleteAllTasks();
        save();
    }

    @Override
    public void deleteEpic(int id) {
        super.deleteEpic(id);
        save();
    }

    public void save() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
            writer.write("id,type,name,status,description,epic");
            writer.newLine();

            for (Task task : getAllTasks()) {
                writer.write(toString(task));
                writer.newLine();
            }
            for (Epic epic : getAllEpics()) {
                writer.write(toString(epic));
                writer.newLine();
                for (Subtask subtask : getSubtasksByEpic(epic)) {
                    writer.write(toString(subtask));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении файла");
        }
    }

    private String toString(Task task) {
        if (task instanceof Subtask) {
            return String.join(",",
                    String.valueOf(task.getId()),
                    "SUBTASK",
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    String.valueOf(((Subtask) task).getEpicId())
            );
        } else if (task instanceof Epic) {
            return String.join(",",
                    String.valueOf(task.getId()),
                    "EPIC",
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    ""
            );
        } else {
            return String.join(",",
                    String.valueOf(task.getId()),
                    "TASK",
                    task.getName(),
                    task.getStatus().toString(),
                    task.getDescription(),
                    ""
            );
        }
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);

        try {
            List<String> lines = Files.readAllLines(file.toPath());
            if (lines.size() <= 1) return manager;

            for (int i = 1; i < lines.size(); i++) {
                Task task = fromString(lines.get(i));
                if (task instanceof Task && !(task instanceof Epic || task instanceof Subtask)) {
                    manager.createTask(task);
                } else if (task instanceof Epic) {
                    manager.createEpic((Epic) task);
                } else if (task instanceof Subtask) {
                    manager.createSubtask((Subtask) task);
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при чтении файла");
        }

        return manager;
    }

    private static Task fromString(String value) {
        String[] parts = value.split(",");
        int id = Integer.parseInt(parts[0]);
        String type = parts[1];
        String name = parts[2];
        Status status = Status.valueOf(parts[3]);
        String description = parts[4];

        switch (type) {
            case "TASK":
                return new Task(id, name, description, status);
            case "EPIC":
                return new Epic(id, name, description);
            case "SUBTASK":
                return new Subtask(name, description, status, Integer.parseInt(parts[5]));
            default:
                throw new IllegalArgumentException("Неизвестный тип задачи: " + type);
        }
    }
}