package manager;

import exception.ManagerSaveException;
import resource.*;
import util.DataTimeFormat;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class FileBackedTaskManager extends InMemoryTaskManager {
    private final File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public void save() throws ManagerSaveException {
        final String title = "id,type,name,status,description,id_links\n";
        List<String> lines = new ArrayList<>();
        lines.add(title);

        getAllTasks().forEach(task -> lines.add(task.serializeToCsv()));
        getAllEpics().forEach(epic -> lines.add(epic.serializeToCsv()));
        getAllSubTasks().forEach(subTask -> lines.add(subTask.serializeToCsv()));

        saveToCsv(lines);
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager manager = new FileBackedTaskManager(file);
        List<String> lines = manager.loadFromCsv();

        if (!lines.isEmpty()) {
            lines.removeFirst();
            for (String line : lines) {
                manager.deSerialize(line);
            }
        }

        return manager;
    }

    @Override
    public Optional<Task> createTask(Task task) {
        Optional<Task> createdTask = super.createTask(task);
        createdTask.ifPresent(t -> save());
        return createdTask;
    }

    @Override
    public Optional<Task> updateTask(Task task) {
        Optional<Task> updatedTask = super.updateTask(task);
        updatedTask.ifPresent(t -> save());
        return updatedTask;
    }

    @Override
    public Optional<Boolean> removeTaskById(int id) {
        Optional<Boolean> result = super.removeTaskById(id);
        save();
        return result;
    }

    @Override
    public Optional<Void> removeAllTasks() {
        Optional<Void> result = super.removeAllTasks();
        save();
        return result;
    }

    @Override
    public Optional<Epic> createEpic(Epic epic) {
        Optional<Epic> createdEpic = super.createEpic(epic);
        createdEpic.ifPresent(e -> save());
        return createdEpic;
    }

    @Override
    public Optional<Epic> updateEpic(Epic epic) {
        Optional<Epic> updatedEpic = super.updateEpic(epic);
        updatedEpic.ifPresent(e -> save());
        return updatedEpic;
    }

    @Override
    public Optional<Boolean> removeEpicById(int id) {
        Optional<Boolean> result = super.removeEpicById(id);
        save();
        return result;
    }

    @Override
    public Optional<Void> removeAllEpics() {
        Optional<Void> result = super.removeAllEpics();
        save();
        return result;
    }

    @Override
    public Optional<SubTask> createSubTask(SubTask subTask) {
        Optional<SubTask> createdSubTask = super.createSubTask(subTask);
        createdSubTask.ifPresent(s -> save());
        return createdSubTask;
    }

    @Override
    public Optional<SubTask> updateSubTask(SubTask subTask) {
        Optional<SubTask> updatedSubTask = super.updateSubTask(subTask);
        updatedSubTask.ifPresent(s -> save());
        return updatedSubTask;
    }

    @Override
    public Optional<Boolean> removeSubTaskById(int id) {
        Optional<Boolean> result = super.removeSubTaskById(id);
        save();
        return result;
    }

    @Override
    public Optional<Void> removeAllSubTasks() {
        Optional<Void> result = super.removeAllSubTasks();
        save();
        return result;
    }

    private void saveToCsv(List<String> lines) {
        if (file == null) {
            throw new ManagerSaveException("Невозможно сохранить данные в файл.");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в CSV: " + e.getMessage());
        }
    }

    private List<String> loadFromCsv() {
        if (file == null) {
            throw new ManagerSaveException("Невозможно загрузить данные из файла.");
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при загрузке данных из CSV: " + e.getMessage());
        }

        return lines;
    }

    private void deSerialize(String line) {
        if (line.isBlank()) {
            return;
        }

        String[] lines = line.trim().split(",");

        if (lines.length < 5) {
            System.out.println("Ошибка: некорректный формат строки - " + line);
            return;
        }

        TaskType taskType = TaskType.valueOf(lines[1]);

        switch (taskType) {
            case TASK -> super.createTask(
                    new Task(
                            Integer.parseInt(lines[0]),
                            lines[2],
                            lines[4],
                            getTaskStatusFromString(lines[3]),
                            Duration.ofMinutes(Long.parseLong(lines[5])),
                            LocalDateTime.parse(lines[6], DataTimeFormat.getDataTimeFormat())
                    ));
            case EPIC -> super.createEpic(
                    new Epic(
                            Integer.parseInt(lines[0]),
                            lines[2],
                            lines[4],
                            getTaskStatusFromString(lines[3]),
                            Duration.ofMinutes(Long.parseLong(lines[5])),
                            LocalDateTime.parse(lines[6], DataTimeFormat.getDataTimeFormat())
                    ));

            case SUBTASK -> {
                int subTaskId = Integer.parseInt(lines[0]);
                int epicId = Integer.parseInt(lines[lines.length - 1]);
                super.createSubTask(
                        new SubTask(
                                subTaskId,
                                epicId,
                                lines[2],
                                lines[4],
                                getTaskStatusFromString(lines[3]),
                                Duration.ofMinutes(Long.parseLong(lines[5])),
                                LocalDateTime.parse(lines[6], DataTimeFormat.getDataTimeFormat())
                        ));
            }
        }
    }

    private Status getTaskStatusFromString(String line) {
        return switch (line) {
            case "NEW" -> Status.NEW;
            case "IN_PROGRESS" -> Status.IN_PROGRESS;
            case "DONE" -> Status.DONE;
            default -> throw new IllegalStateException("Неизвестное значение: " + line);
        };
    }
}