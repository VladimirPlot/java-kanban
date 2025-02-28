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

    public void load() {
        List<String> lines;
        try {
            lines = loadFromCsv();
        } catch (ManagerSaveException e) {
            throw new RuntimeException(e);
        }

        lines.removeFirst();

        for (String line : lines) {
            deSerialize(line);
        }
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

        try {
            FileWriter fileWriter = new FileWriter(file, StandardCharsets.UTF_8, false);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);

            for (String line : lines) {
                bufferedWriter.write(line);
            }

            bufferedWriter.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> loadFromCsv() throws ManagerSaveException {
        if (file == null) {
            throw new ManagerSaveException("Невозможно загрузить данные из файла.");
        }

        List<String> lines = new ArrayList<>();

        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            while (bufferedReader.ready()) {
                lines.add(bufferedReader.readLine());
            }

            bufferedReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
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