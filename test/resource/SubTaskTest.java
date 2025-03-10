package resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

class SubTaskTest {
    private SubTask subTask;

    @BeforeEach
    void init() {
        subTask = new SubTask(
                1,
                1,
                "Отжимания",
                "50 раз",
                Status.NEW,
                Duration.ofMinutes(15),
                LocalDateTime.of(LocalDate.of(2025, 2, 15), LocalTime.of(9, 0, 0))
        );
    }

    @Test
    void testToString() {
        String expected = "resource.SubTask{id=1, epicId=1, name=Отжимания, status=NEW, duration=15, startTime=09:00:00/15.02.2025, endTime=09:15:00/15.02.2025}";
        String actually = subTask.toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSetEpicId() {
        int expected = 2;
        subTask.setEpicId(2);
        int actually = subTask.getIdEpic();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetEpicId() {
        int expected = 1;
        int actually = subTask.getIdEpic();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSerializeToCsv() {
        String expected = "1,SUBTASK,Отжимания,NEW,50 раз,15,09:00:00/15.02.2025,09:15:00/15.02.2025,1\n";
        String actually = subTask.serializeToCsv();
        Assertions.assertEquals(expected, actually);
    }
}