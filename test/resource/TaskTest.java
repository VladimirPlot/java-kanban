package resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TaskTest {
    private static Task task1;
    private static Task task2;

    @BeforeEach
    void init() {
        task1 = new Task(
                1,
                "Купить продукты",
                "Яйца, молоко, хлеб",
                Status.NEW
        );

        task2 = new Task(
                1,
                "Выйти на пробежку",
                "Подъем в 7 утра",
                Status.DONE
        );
    }

    @Test
    void testToString() {
        String expected = "resource.Task{id=1, name='Купить продукты', description='Яйца, молоко, хлеб', status=NEW}";
        String actually = task1.toString();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testEquals() {
        boolean actually = task1.equals(task2);
        Assertions.assertTrue(actually);
    }

    @Test
    void testHashCode() {
        int expected = -506277013;
        int actually = task1.hashCode();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetId() {
        int expected = 1;
        int actually = task1.getId();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSetId() {
        int expected = 2;
        task1.setId(2);
        int actually = task1.getId();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetTaskName() {
        String expected = "Купить продукты";
        String actually = task1.getName();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSetTaskName() {
        String expected = "Новое имя";
        task1.setName(expected);
        String actually = task1.getName();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetDescription() {
        String expected = "Яйца, молоко, хлеб";
        String actually = task1.getDescription();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSetDescription() {
        String expected = "Новое примечание";
        task1.setDescription(expected);
        String actually = task1.getDescription();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testGetTaskStatus() {
        Status expected = Status.NEW;
        Status actually = task1.getStatus();
        Assertions.assertEquals(expected, actually);
    }

    @Test
    void testSetTaskStatus() {
        Status expected = Status.IN_PROGRESS;
        task1.setStatus(Status.IN_PROGRESS);
        Status actually = task1.getStatus();
        Assertions.assertEquals(expected, actually);
    }
}