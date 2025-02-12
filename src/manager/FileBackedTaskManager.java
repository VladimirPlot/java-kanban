package manager;

import exception.ManagerSaveException;
import resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

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
            lines.removeFirst(); // Убираем заголовок
            for (String line : lines) {
                manager.deSerialize(line);
            }
        }

        return manager;
    }

    @Override
    public void createTask(Task task) {
        super.createTask(task);
        save();
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);
        save();
        return updatedTask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);
        save();
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();
        save();
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);
        save();
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);
        save();
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);
        save();
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();
        save();
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);
        save();
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);
        save();
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);
        save();
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();
        save();
    }

    private void saveToCsv(List<String> lines) throws ManagerSaveException {
        if (file == null) {
            throw new ManagerSaveException("Невозможно сохранить данные в файл.");
        }

        try (BufferedWriter writer = Files.newBufferedWriter(file.toPath(), StandardCharsets.UTF_8)) {
            for (String line : lines) {
                writer.write(line);
                writer.newLine(); // Добавляем перенос строки
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка при сохранении данных в CSV: " + e.getMessage());
        }
    }

    private List<String> loadFromCsv() throws ManagerSaveException {
        if (file == null) {
            throw new ManagerSaveException("Невозможно загрузить данные из файла.");
        }

        List<String> lines = new ArrayList<>();

        try (BufferedReader reader = Files.newBufferedReader(file.toPath(), StandardCharsets.UTF_8)) {
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
        if (line.isBlank()) { // Проверяем, что строка не пустая
            return;
        }

        String[] lines = line.trim().split(",");

        if (lines.length < 5) { // Минимальная длина для корректных записей
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
                            getTaskStatusFromString(lines[3])
                    ));
            case EPIC -> super.createEpic(
                    new Epic(
                            Integer.parseInt(lines[0]),
                            lines[2],
                            lines[4],
                            getTaskStatusFromString(lines[3])
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
                                getTaskStatusFromString(lines[3])
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