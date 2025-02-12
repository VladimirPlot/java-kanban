package manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

class ManagersTest {

    @Test
    void getDefault() {
        TaskManager actually = Managers.getDefault();
        Assertions.assertInstanceOf(InMemoryTaskManager.class, actually);
    }

    @Test
    void getDefaultHistoryManager() {
        HistoryManager actually = Managers.getDefaultHistoryManager();
        Assertions.assertInstanceOf(InMemoryHistoryManager.class, actually);
    }

    @Test
    void getFileBackedTaskManager() {
        Path filePath = null;
        try {
            filePath = Files.createTempFile("data-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TaskManager actually = Managers.getFileBackedTaskManager(filePath.toFile());
        Assertions.assertInstanceOf(FileBackedTaskManager.class, actually);
    }

    @Test
    void loadFromFile() {
        Path filePath = null;
        try {
            filePath = Files.createTempFile("data-", ".csv");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        TaskManager actually = Managers.getFileBackedTaskManager(filePath.toFile());
        Assertions.assertInstanceOf(FileBackedTaskManager.class, actually);
    }
}