package resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

class EpicTest {
    private Epic epic1;
    private Epic epic2;
    private SubTask subTask1;
    private SubTask subTask2;

    @BeforeEach
    void init() {
        epic1 = new Epic(
                1,
                "Уборка",
                "Ванна, спальня, кухня",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(9, 0, 0))
        );

        epic2 = new Epic(
                "Уборка",
                "Ванна, спальня, кухня",
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(10, 0, 0))
        );

        subTask1 = new SubTask(
                1,
                1,
                "Сухая уборка",
                "Подмести",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.now(), LocalTime.now().plus(Duration.ofMinutes(10)))
        );

        subTask2 = new SubTask(
                2,
                1,
                "Влажная уборка",
                "Помыть полы шваброй",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.now(), LocalTime.now().plus(Duration.ofMinutes(10)))
        );
    }

    @Test
    void testToString() {
        String expected = "resource.Epic{id=1, name=Уборка, subTasksIdList=[], status=NEW, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}";
        String actually = epic1.toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testAddSubTaskId() {
        List<Integer> expected = List.of(1, 2);
        epic1.addSubTaskId(subTask1);
        epic1.addSubTaskId(subTask2);
        List<Integer> actually = epic1.getSubTasksList();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testRemoveSubTaskId() {
        List<Integer> expected = List.of(1);
        epic1.addSubTaskId(subTask1);
        epic1.addSubTaskId(subTask2);
        epic1.removeSubTaskInList(2);
        List<Integer> actually = epic1.getSubTasksList();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetSubTasksIdList() {
        List<Integer> expected = List.of(1, 2);
        epic1.addSubTaskId(subTask1);
        epic1.addSubTaskId(subTask2);
        List<Integer> actually = epic1.getSubTasksList();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testClearSubTasksList() {
        List<Integer> expected = List.of();
        epic1.addSubTaskId(subTask1);
        epic1.addSubTaskId(subTask2);
        epic1.clearSubTasksList();
        List<Integer> actually = epic1.getSubTasksList();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSerializeToCsv() {
        String expected = "1,EPIC,Уборка,NEW,Ванна, спальня, кухня,15,09:00:00/15.02.2025,09:15:00/15.02.2025,[]\n";
        String actually = epic1.serializeToCsv();
        Assertions.assertEquals(expected, actually);
    }
}