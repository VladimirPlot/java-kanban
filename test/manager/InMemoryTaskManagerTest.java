package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resource.Epic;
import resource.Status;
import resource.SubTask;
import resource.Task;

class InMemoryTaskManagerTest {
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
                Status.NEW
        );
        task2 = new Task(
                "Путешествие",
                "Собрать чемодан",
                Status.DONE
        );

        epic1 = new Epic(
                "Уборка по дому",
                "Ванна Кухня Спальня"
        );
        epic2 = new Epic(
                "Собеседование",
                "Подготовиться по теории"
        );
        taskManager = Managers.getDefault();
    }

    @Test
    void testCreateTask() {
        taskManager.createTask(task1);
        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}";
        String actually = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateTask() {
        taskManager.createTask(task1);
        String expected = "resource.Task{id=1, name='Путешествие', description='Собрать чемодан', status=DONE}";
        Task task = new Task(1, "Путешествие", "Собрать чемодан", Status.DONE);
        taskManager.updateTask(task);
        String actually = taskManager.getTaskById(1).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveTaskById() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeTaskById(2);
        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}]";
        String actually = taskManager.getAllTasks().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllTasks() {
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.removeAllTasks();

        Assertions.assertTrue(taskManager.getAllTasks().isEmpty());
    }

    @Test
    void testAddEpic() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[resource.Epic{id=1, name=Уборка по дому, subTasksIdList=[], status=NEW}, " +
                "resource.Epic{id=2, name=Собеседование, subTasksIdList=[], status=NEW}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "resource.Epic{id=2, name=Собеседование, subTasksIdList=[], status=NEW}";
        String actually = taskManager.getEpicById(2).toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        String expected = "[resource.Epic{id=1, name=Уборка по дому, subTasksIdList=[], status=NEW}, " +
                "resource.Epic{id=2, name=Собеседование, subTasksIdList=[], status=NEW}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testUpdateEpic() {
        taskManager.createEpic(epic1);
        Epic epic = taskManager.getEpicById(1);
        epic.setName("Новое имя");
        taskManager.updateEpic(epic);
        String expected = "[resource.Epic{id=1, name=Новое имя, subTasksIdList=[], status=NEW}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveEpicById() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeEpicById(2);
        String expected = "[resource.Epic{id=1, name=Уборка по дому, subTasksIdList=[], status=NEW}]";
        String actually = taskManager.getAllEpics().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveAllEpics() {
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);
        taskManager.removeAllEpics();

        Assertions.assertTrue(taskManager.getAllEpics().isEmpty());
    }

    @Test
    void testCreateSubTask() {
        taskManager.createEpic(epic1);
        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW
        );
        taskManager.createSubTask(subTask1);
        String expected = "[resource.SubTask{id=2, epicId=1, name=Помыть окна, status=NEW}]";
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
                Status.NEW
        );
        taskManager.createSubTask(subTask1);
        String expected = "resource.SubTask{id=2, epicId=1, name=Помыть окна, status=NEW}";
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
                Status.NEW
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW
        );
        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        String expected = "[resource.SubTask{id=3, epicId=1, name=Помыть окна, status=NEW}, " +
                "resource.SubTask{id=4, epicId=1, name=Помыть полы, status=NEW}, " +
                "resource.SubTask{id=5, epicId=2, name=Посмотреть теорию, status=NEW}]";
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
                Status.NEW
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        subTask1 = taskManager.getSubTaskById(3);
        subTask1.setName("Обновленная задача");
        taskManager.updateSubTask(subTask1);

        String expected = "[resource.SubTask{id=3, epicId=1, name=Обновленная задача, status=NEW}, " +
                "resource.SubTask{id=4, epicId=1, name=Помыть полы, status=NEW}, " +
                "resource.SubTask{id=5, epicId=2, name=Посмотреть теорию, status=NEW}]";
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
                Status.NEW
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.removeSubTaskById(3);

        String expected = "[resource.SubTask{id=4, epicId=1, name=Помыть полы, status=NEW}, " +
                "resource.SubTask{id=5, epicId=2, name=Посмотреть теорию, status=NEW}]";
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
                Status.NEW
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW
        );

        taskManager.createSubTask(subTask1);
        taskManager.createSubTask(subTask2);
        taskManager.createSubTask(subTask3);

        taskManager.removeAllSubTasks();

        Assertions.assertTrue(taskManager.getAllSubTasks().isEmpty());
    }

    @Test
    void testGetHistory() {
        final int historySize = 6;
        taskManager.createTask(task1);
        taskManager.createTask(task2);
        taskManager.createEpic(epic1);
        taskManager.createEpic(epic2);

        SubTask subTask1 = new SubTask(
                epic1,
                "Помыть окна",
                "На балконе",
                Status.NEW
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW
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
        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[5, 6], status=NEW}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[7], status=NEW}, " +
                "resource.SubTask{id=5, epicId=3, name=Помыть окна, status=NEW}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть теорию, status=NEW}]";
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
                Status.NEW
        );
        SubTask subTask2 = new SubTask(
                epic1,
                "Помыть полы",
                "Помыть шваброй",
                Status.NEW
        );

        SubTask subTask3 = new SubTask(
                epic2,
                "Посмотреть теорию",
                "Книги, видео",
                Status.NEW
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
        String expected = "[resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[5, 6], status=NEW}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть теорию, status=NEW}, " +
                "resource.SubTask{id=6, epicId=3, name=Помыть полы, status=NEW}, " +
                "resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}, " +
                "resource.SubTask{id=5, epicId=3, name=Помыть окна, status=NEW}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[7], status=NEW}]";
        String actually = taskManager.getHistory().toString();
        Assertions.assertEquals(expected, actually);

    }
}