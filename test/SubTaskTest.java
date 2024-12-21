import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class SubTaskTest {
    private SubTask subTask;

    @BeforeEach
    void init() {
        subTask = new SubTask(
                1,
                1,
                "Отжимания",
                "50 раз",
                Status.NEW
        );
    }

    @Test
    void testToString() {
        String expected = "SubTask{id=1, epicId=1, name=Отжимания, status=NEW}";
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
}