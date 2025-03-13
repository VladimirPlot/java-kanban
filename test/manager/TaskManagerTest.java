package manager;

import exception.InvalidTaskTimeException;
import resource.Epic;
import resource.SubTask;
import resource.Task;
import resource.Status;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

public abstract class TaskManagerTest<T extends TaskManager> {
    protected abstract T createManager();

    private TaskManager taskManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;

    @BeforeEach
    void setUp() {

        task1 = new Task(
                "Купить продукты",
                "Молоко и яйца",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(8, 0))
        );
        task2 = new Task(
                "Путешествие",
                "Собрать чемодан",
                Status.DONE,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(9, 0))
        );

        epic1 = new Epic(
                "Уборка по дому",
                "Ванна Кухня Спальня",
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(10, 0))
        );
        epic2 = new Epic(
                "Собеседование",
                "Подготовиться по теории",
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(11, 0))
        );
        taskManager = Managers.getDefault();
    }

    @Test
    void testCreateTask() {
        taskManager.createTask(task1);
        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "Optional[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}]";
        String actually = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateTask() {
        taskManager.createTask(task1);
        String expected = "Optional[resource.Task{id=1, name='Путешествие', description='Собрать чемодан', status=DONE, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}]";
        Task task = new Task(
                1,
                "Путешествие",
                "Собрать чемодан",
                Status.DONE,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(9, 0)));
        taskManager.updateTask(task);
        String actually = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeTaskById(2);
        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();

        assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void testAddEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[resource.Epic{id=1, name=Уборка по дому, subTasksIdList=[], status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}, " +
                "resource.Epic{id=2, name=Собеседование, subTasksIdList=[], status=NEW, duration=15, startTime=11:00:00/15.02.2025, endTime=11:15:00/15.02.2025}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "Optional[resource.Epic{id=2, name=Собеседование, subTasksIdList=[], status=NEW, duration=15, startTime=11:00:00/15.02.2025, endTime=11:15:00/15.02.2025}]";
        String actually = taskManager.getEpicById(2).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[resource.Epic{id=1, name=Уборка по дому, subTasksIdList=[], status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}, " +
                "resource.Epic{id=2, name=Собеседование, subTasksIdList=[], status=NEW, duration=15, startTime=11:00:00/15.02.2025, endTime=11:15:00/15.02.2025}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateEpic() {
        taskManager.createEpic(epic1);
        Epic epic = taskManager.getEpicById(1).orElseThrow(RuntimeException::new);
        epic.setName("Новое имя");
        taskManager.updateEpic(epic);
        String expected = "[resource.Epic{id=1, name=Новое имя, subTasksIdList=[], status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeEpicById(2);
        String expected = "[resource.Epic{id=1, name=Уборка по дому, subTasksIdList=[], status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeAllEpics();

        assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void testCreateSubTask() {
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(16, 0))
        );
        taskManager.createSubTask(subTask1);
        String expected = "[resource.SubTask{id=2, epicId=1, name=Помыть окна, status=NEW, duration=15, startTime=16:00:00/15.02.2025, endTime=16:15:00/15.02.2025}]";
        String actually = taskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetSubTaskById() {
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(14, 0))
        );
        taskManager.createSubTask(subTask1);
        String expected = "Optional[resource.SubTask{id=2, epicId=1, name=Помыть окна, status=NEW, duration=15, startTime=14:00:00/15.02.2025, endTime=14:15:00/15.02.2025}]";
        String actually = taskManager.getSubTaskById(2).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetAllSubTasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(12, 0))
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(13, 0))
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(14, 0))
        );
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        String expected = "[resource.SubTask{id=3, epicId=1, name=Помыть окна, status=NEW, duration=15, startTime=12:00:00/15.02.2025, endTime=12:15:00/15.02.2025}, " +
                "resource.SubTask{id=4, epicId=1, name=Помыть полы, status=NEW, duration=15, startTime=13:00:00/15.02.2025, endTime=13:15:00/15.02.2025}, " +
                "resource.SubTask{id=5, epicId=2, name=Посмотреть теорию, status=NEW, duration=15, startTime=14:00:00/15.02.2025, endTime=14:15:00/15.02.2025}]";
        String actually = taskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateSubTask() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(16, 0))
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(17, 0))
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(18, 0))
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        subTask1 = taskManager.getSubTaskById(3).orElseThrow(RuntimeException::new);
        subTask1.setName("Обновленная задача");
        taskManager.updateSubTask(subTask1);

        String expected = "[resource.SubTask{id=3, epicId=1, name=Обновленная задача, status=NEW, duration=15, startTime=16:00:00/15.02.2025, endTime=16:15:00/15.02.2025}, " +
                "resource.SubTask{id=4, epicId=1, name=Помыть полы, status=NEW, duration=15, startTime=17:00:00/15.02.2025, endTime=17:15:00/15.02.2025}, " +
                "resource.SubTask{id=5, epicId=2, name=Посмотреть теорию, status=NEW, duration=15, startTime=18:00:00/15.02.2025, endTime=18:15:00/15.02.2025}]";
        String actually = taskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveSubTaskById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(18, 30))
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(19, 0))
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(20, 0))
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.removeSubTaskById(3);

        String expected = "[resource.SubTask{id=4, epicId=1, name=Помыть полы, status=NEW, duration=15, startTime=19:00:00/15.02.2025, endTime=19:15:00/15.02.2025}, " +
                "resource.SubTask{id=5, epicId=2, name=Посмотреть теорию, status=NEW, duration=15, startTime=20:00:00/15.02.2025, endTime=20:15:00/15.02.2025}]";
        String actually = taskManager.getAllSubTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllSubtasks() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(8, 0))
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(10, 0))
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.now(), LocalTime.of(15, 0))
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.removeAllSubTasks();

        assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    void testGetHistory() {
        final int historySize = 7;
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(21, 0))
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(22, 0))
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(10, 0))
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(7);

        Assertions.assertEquals(historySize, taskManager.getHistory().size());
        String expected = "[resource.SubTask{id=6, epicId=3, name=Помыть полы, status=NEW, duration=15, startTime=22:00:00/15.02.2025, endTime=22:15:00/15.02.2025}, " +
                "resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[5, 6], status=NEW, duration=30, startTime=21:00:00/15.02.2025, endTime=21:30:00/15.02.2025}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[7], status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}, " +
                "resource.SubTask{id=5, epicId=3, name=Помыть окна, status=NEW, duration=15, startTime=21:00:00/15.02.2025, endTime=21:15:00/15.02.2025}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть теорию, status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}]";
        String actually = taskManager.getHistory().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testHistoryShouldNotExceedTenEentries() {
        final int historySize = 7;
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(13, 0))
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(14, 0))
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(15, 0))
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.getTaskById(1);
        taskManager.getTaskById(2);
        taskManager.getEpicById(3);
        taskManager.getEpicById(4);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(7);
        taskManager.getEpicById(4);
        taskManager.getSubTaskById(5);
        taskManager.getSubTaskById(6);
        taskManager.getTaskById(1);
        taskManager.getSubTaskById(5);
        taskManager.getEpicById(4);

        Assertions.assertEquals(historySize, taskManager.getHistory().size());
        String expected = "[resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[5, 6], status=NEW, duration=30, startTime=13:00:00/15.02.2025, endTime=13:30:00/15.02.2025}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть теорию, status=NEW, duration=15, startTime=15:00:00/15.02.2025, endTime=15:15:00/15.02.2025}, " +
                "resource.SubTask{id=6, epicId=3, name=Помыть полы, status=NEW, duration=15, startTime=14:00:00/15.02.2025, endTime=14:15:00/15.02.2025}, " +
                "resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}, " +
                "resource.SubTask{id=5, epicId=3, name=Помыть окна, status=NEW, duration=15, startTime=13:00:00/15.02.2025, endTime=13:15:00/15.02.2025}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[7], status=NEW, duration=15, startTime=15:00:00/15.02.2025, endTime=15:15:00/15.02.2025}]";
        String actually = taskManager.getHistory().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void TestPrioritizedTasks() {
        // Первая задача (основа для проверки)
        Task task1 = new Task(1, "Task 1", "Base Task", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 3, 10, 0));
        assertTrue(taskManager.createTask(task1).isPresent());

        // Полное совпадение
        Task task2 = new Task(2, "Task 2", "Overlap Same", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 3, 10, 0));
        assertThrows(InvalidTaskTimeException.class, () -> taskManager.createTask(task2));

        // Полное вложение
        Task task3 = new Task(3, "Task 3", "Fully Inside", Status.NEW, Duration.ofMinutes(30), LocalDateTime.of(2025, 3, 3, 10, 15));
        assertThrows(InvalidTaskTimeException.class, () -> taskManager.createTask(task3));

        // Пересечение слева
        Task task4 = new Task(4, "Task 4", "Overlap Left", Status.NEW, Duration.ofMinutes(45), LocalDateTime.of(2025, 3, 3, 9, 45));
        assertThrows(InvalidTaskTimeException.class, () -> taskManager.createTask(task4));

        // Пересечение справа
        Task task5 = new Task(5, "Task 5", "Overlap Right", Status.NEW, Duration.ofMinutes(45), LocalDateTime.of(2025, 3, 3, 10, 45));
        assertThrows(InvalidTaskTimeException.class, () -> taskManager.createTask(task5));

        // Без пересечения (успешно добавляется)
        Task task6 = new Task(6, "Task 6", "No Overlap", Status.NEW, Duration.ofMinutes(60), LocalDateTime.of(2025, 3, 3, 11, 10));
        assertTrue(taskManager.createTask(task6).isPresent());
    }
}