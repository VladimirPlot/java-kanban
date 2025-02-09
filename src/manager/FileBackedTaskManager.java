package manager;

import exception.ManagerSaveException;
import resource.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
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

    @Override
    public void createTask(Task task) {
        super.createTask(task);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения задачи: " + task.getName());
        }
    }

    @Override
    public Task updateTask(Task task) {
        Task updatedTask = super.updateTask(task);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения задачи: " + task.getName());
        }

        return updatedTask;
    }

    @Override
    public void removeTaskById(int id) {
        super.removeTaskById(id);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения задачи c id=" + id);
        }
    }

    @Override
    public void removeAllTasks() {
        super.removeAllTasks();

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения задач в файл при удалении.");
        }
    }

    @Override
    public void createEpic(Epic epic) {
        super.createEpic(epic);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения эпика: " + epic.getName());
        }
    }

    @Override
    public void updateEpic(Epic epic) {
        super.updateEpic(epic);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения эпика: " + epic.getName());
        }
    }

    @Override
    public void removeEpicById(int id) {
        super.removeEpicById(id);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения эпика с id=" + id);
        }
    }

    @Override
    public void removeAllEpics() {
        super.removeAllEpics();

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения эпиков в файл при удалении.");
        }
    }

    @Override
    public void createSubTask(SubTask subTask) {
        super.createSubTask(subTask);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения подзадачи: " + subTask.getName());
        }
    }

    @Override
    public void updateSubTask(SubTask subTask) {
        super.updateSubTask(subTask);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения подзадачи: " + subTask.getName());
        }
    }

    @Override
    public void removeSubTaskById(int id) {
        super.removeSubTaskById(id);

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения эпика с id=" + id);
        }
    }

    @Override
    public void removeAllSubTasks() {
        super.removeAllSubTasks();

        try {
            save();
        } catch (ManagerSaveException e) {
            throw new RuntimeException("Произошла ошибка сохранения подзадач в файл при удалении.");
        }
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
        String[] lines = line.trim().split(",");
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