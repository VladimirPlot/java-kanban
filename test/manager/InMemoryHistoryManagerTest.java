package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resource.Epic;
import resource.Status;
import resource.SubTask;
import resource.Task;

class InMemoryHistoryManagerTest {
    private HistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;
    private SubTask subTask3;

    @BeforeEach
    void SetUp() {
        historyManager = Managers.getDefaultHistoryManager();

        task1 = new Task(
                1,
                "Купить продукты",
                "Молоко и яйца",
                Status.NEW
        );

        task2 = new Task(
                2,
                "Путешествие",
                "Собрать чемодан",
                Status.DONE
        );

        epic1 = new Epic(
                3,
                "Уборка по дому",
                "Ванна Кухня Спальня",
                Status.NEW
        );
        epic2 = new Epic(
                4,
                "Собеседование",
                "Подготовиться по теории",
                Status.NEW
        );

        subTask1 = new SubTask(
                5,
                3,
                "Сухая уборка",
                "Подмести",
                Status.NEW
        );
        subTask2 = new SubTask(
                6,
                3,
                "Влажная уборка",
                "Помыть полы шваброй",
                Status.NEW
        );

        subTask3 = new SubTask(
                7,
                4,
                "Посмотреть видео на ютуб",
                "Записи собеседований",
                Status.NEW
        );
    }

    @Test
    void testAddTask() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        historyManager.add(subTask3);

        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[], status=NEW}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[], status=NEW}, " +
                "resource.SubTask{id=5, epicId=3, name=Сухая уборка, status=NEW}, " +
                "resource.SubTask{id=6, epicId=3, name=Влажная уборка, status=NEW}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть видео на ютуб, status=NEW}]";
        String actually = historyManager.getHistory().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subTask2);
        historyManager.add(subTask1);
        historyManager.add(subTask3);

        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[], status=NEW}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[], status=NEW}, " +
                "resource.SubTask{id=6, epicId=3, name=Влажная уборка, status=NEW}, " +
                "resource.SubTask{id=5, epicId=3, name=Сухая уборка, status=NEW}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть видео на ютуб, status=NEW}]";
        String actually = historyManager.getHistory().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void thereShouldntBeNullInHistory() {
        Task task = null;
        Epic epic = null;
        SubTask subTask = null;

        historyManager.add(task);
        historyManager.add(epic);
        historyManager.add(subTask);

        Assertions.assertTrue(historyManager.getHistory().isEmpty());
    }

    @Test
    void sizeOfStoriesShouldNotExceedTenElements() {
        final int historySize = 7;

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subTask2);
        historyManager.add(subTask1);
        historyManager.add(subTask3);
        historyManager.add(subTask3);
        historyManager.add(subTask1);
        historyManager.add(epic2);

        Assertions.assertEquals(historySize, historyManager.getHistory().size());

        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);

        Assertions.assertEquals(historySize, historyManager.getHistory().size());
    }
}