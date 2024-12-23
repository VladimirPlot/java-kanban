package resource;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
                Status.NEW
        );

        epic2 = new Epic(
                "Уборка",
                "Ванна, спальня, кухня"
        );

        subTask1 = new SubTask(
                1,
                1,
                "Сухая уборка",
                "Подмести",
                Status.NEW
        );

        subTask2 = new SubTask(
                2,
                1,
                "Влажная уборка",
                "Помыть полы шваброй",
                Status.NEW
        );
    }

    @Test
    void testToString() {
        String expected = "resource.Epic{id=1, name=Уборка, subTasksIdList=[], status=NEW}";
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
}