package manager;

import resource.Epic;
import resource.SubTask;
import resource.Task;
import resource.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class FileBackedTaskManagerTest {
    private Path filePath;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;
    private TaskManager taskManager;

    @BeforeEach
    void setUp() {
        try {
            filePath = Files.createTempFile("data-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        taskManager = Managers.getFileBackedTaskManager(filePath.toFile());

        task1 = new Task(
                "Приготовить кофе",
                "добавить сливки",
                Status.NEW
        );
        task2 = new Task(
                "Купить хлеб",
                "половину буханки",
                Status.DONE
        );

        epic1 = new Epic(
                "Уборка по дому",
                "произвести уборку по всему дому"
        );
        epic2 = new Epic(
                "Хомяк",
                "покормить хомяка"
        );

        subTask1 = new SubTask(
                5,
                3,
                "Пропылесосить комнаты",
                "тщательно",
                Status.NEW
        );
        subTask2 = new SubTask(
                6,
                3,
                "Помыть полы",
                "мыть с чистящим средством",
                Status.NEW
        );
        subTask3 = new SubTask(
                7,
                3,
                "Разобрать посудомойку",
                "протереть посуду",
                Status.NEW
        );

    }

    @Test
    void testLoad() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllTasks() + " " + taskManager.getAllEpics() + " " + taskManager.getAllSubTasks();
        String actually = loadedTaskManager.getAllTasks() + " " + loadedTaskManager.getAllEpics() + " " + taskManager.getAllSubTasks();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testCreateTask() {
        taskManager.createTask(task2);
        taskManager.createTask(task1);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedTaskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateTask() {
        taskManager.createTask(task1);
        Task task = taskManager.getTaskById(1);
        task.setName("Test task");
        taskManager.updateTask(task);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedTaskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveTaskById() {
        taskManager.createTask(task1);
        taskManager.removeTaskById(1);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedTaskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllTasks().toString();
        String actually = loadedTaskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testCreateEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllEpics().toString();
        String actually = loadedTaskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateEpic() {
        taskManager.createEpic(epic2);
        Epic epic = taskManager.getEpicById(1);
        epic.setName("Test name");
        taskManager.updateEpic(epic);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllEpics().toString();
        String actually = loadedTaskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveEpicById() {
        taskManager.createEpic(epic1);
        taskManager.removeEpicById(1);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllEpics().toString();
        String actually = loadedTaskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeAllEpics();
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllEpics().toString();
        String actually = loadedTaskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testCreateSubTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllSubTasks().toString();
        String actually = loadedTaskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateSubTask() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        SubTask subTask = taskManager.getSubTaskById(6);
        subTask.setName("Test subTask");
        taskManager.updateSubTask(subTask);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllSubTasks().toString();
        String actually = loadedTaskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveSubTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        taskManager.removeSubTaskById(7);
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllSubTasks().toString();
        String actually = loadedTaskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllSubtasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);
        taskManager.removeAllSubTasks();
        TaskManager loadedTaskManager = Managers.loadFromFile(filePath.toFile());
        String expected = taskManager.getAllSubTasks().toString();
        String actually = loadedTaskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }
}