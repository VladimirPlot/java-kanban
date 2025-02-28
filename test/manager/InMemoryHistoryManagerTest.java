package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import resource.Epic;
import resource.Status;
import resource.SubTask;
import resource.Task;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

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
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(8, 0))
        );

        task2 = new Task(
                2,
                "Путешествие",
                "Собрать чемодан",
                Status.DONE,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(9, 0))
        );

        epic1 = new Epic(
                3,
                "Уборка по дому",
                "Ванна Кухня Спальня",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(10, 0))
        );
        epic2 = new Epic(
                4,
                "Собеседование",
                "Подготовиться по теории",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(11, 0))
        );

        subTask1 = new SubTask(
                5,
                3,
                "Сухая уборка",
                "Подмести",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(12, 0))
        );
        subTask2 = new SubTask(
                6,
                3,
                "Влажная уборка",
                "Помыть полы шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(13, 0))
        );

        subTask3 = new SubTask(
                7,
                4,
                "Посмотреть видео на ютуб",
                "Записи собеседований",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(14, 0))
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

        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[], status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[], status=NEW, duration=15, startTime=11:00:00/15.02.2025, endTime=11:15:00/15.02.2025}, " +
                "resource.SubTask{id=5, epicId=3, name=Сухая уборка, status=NEW, duration=15, startTime=12:00:00/15.02.2025, endTime=12:15:00/15.02.2025}, " +
                "resource.SubTask{id=6, epicId=3, name=Влажная уборка, status=NEW, duration=15, startTime=13:00:00/15.02.2025, endTime=13:15:00/15.02.2025}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть видео на ютуб, status=NEW, duration=15, startTime=14:00:00/15.02.2025, endTime=14:15:00/15.02.2025}]";
        String actually = historyManager.getHistory().toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetHistory() {
        historyManager.add(task1);
        historyManager.add(epic1);
        historyManager.add(task2);
        historyManager.add(epic2);
        historyManager.add(subTask1);
        historyManager.add(subTask2);
        historyManager.add(subTask3);

        String expected = "[resource.Task{id=1, name='Купить продукты', description='Молоко и яйца', status=NEW, duration=15, startTime=08:00:00/15.02.2025, endTime=08:15:00/15.02.2025}, " +
                "resource.Epic{id=3, name=Уборка по дому, subTasksIdList=[], status=NEW, duration=15, startTime=10:00:00/15.02.2025, endTime=10:15:00/15.02.2025}, " +
                "resource.Task{id=2, name='Путешествие', description='Собрать чемодан', status=DONE, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}, " +
                "resource.Epic{id=4, name=Собеседование, subTasksIdList=[], status=NEW, duration=15, startTime=11:00:00/15.02.2025, endTime=11:15:00/15.02.2025}, " +
                "resource.SubTask{id=5, epicId=3, name=Сухая уборка, status=NEW, duration=15, startTime=12:00:00/15.02.2025, endTime=12:15:00/15.02.2025}, " +
                "resource.SubTask{id=6, epicId=3, name=Влажная уборка, status=NEW, duration=15, startTime=13:00:00/15.02.2025, endTime=13:15:00/15.02.2025}, " +
                "resource.SubTask{id=7, epicId=4, name=Посмотреть видео на ютуб, status=NEW, duration=15, startTime=14:00:00/15.02.2025, endTime=14:15:00/15.02.2025}]";
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