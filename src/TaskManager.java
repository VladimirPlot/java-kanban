import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class TaskManager {
    Scanner scanner = new Scanner(System.in);
    private final Map<Integer, Task> taskMap = new HashMap<>();
    private final Map<Integer, Epic> epicMap = new HashMap<>();
    private final Map<Integer, SubTask> subTaskMap = new HashMap<>();
    private int id;

    public void createTask() {
        System.out.println("Введите название задачи: ");
        String name = scanner.next();
        System.out.println("Введите описание задачи: ");
        String description = scanner.next();
        Task task = new Task(name, description, getNewId());
        taskMap.put(task.getId(), task);
        System.out.println("Задача '" + task.getName() + "' создана, ID-" + task.getId());
    }

    public void createEpic() {
        System.out.println("Введите название Эпика: ");
        String name = scanner.next();
        System.out.println("Введите описание Эпика: ");
        String description = scanner.next();
        Epic epic = new Epic(name, description, getNewId());
        boolean isCreating = true;

        while (isCreating) {
            System.out.println("1 - Добавить задачу в Эпик");
            System.out.println("2 - Завершить создание Эпика");
            System.out.println("Введите пункт: ");
            int input = scanner.nextInt();

            switch (input) {
                case 1:
                    epic.addSubTask(getNewId());
                    break;
                case 2:
                    System.out.println("Создание Епика завершено!");
                    isCreating = false;
                    break;
                default:
                    System.out.println("Такого пункта нет!");
                    break;
            }
        }
        epicMap.put(epic.getId(), epic);
        addSubTuskInMap(epic);
    }

    public void createSubTask() {
        System.out.println("Введите ID Эпика куда добавить новую Подзадачу: ");
        int id = scanner.nextInt();

        if (epicMap.containsKey(id)) {
            epicMap.get(id).addSubTask(getNewId());
            addSubTuskInMap(epicMap.get(id));
            epicMap.get(id).upgradeStatusEpic();
        } else {
            System.out.println("Такого Эпика нет!");
        }
    }

    public void showTasks() {
        for (HashMap.Entry<Integer, Task> entry : taskMap.entrySet()) {
            Integer key = entry.getKey();
            Task value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    public void showEpics() {
        for (HashMap.Entry<Integer, Epic> entry : epicMap.entrySet()) {
            Integer key = entry.getKey();
            Epic value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    public void showSubTasks() {
        for (HashMap.Entry<Integer, SubTask> entry : subTaskMap.entrySet()) {
            Integer key = entry.getKey();
            SubTask value = entry.getValue();
            System.out.println("Key: " + key + ", Value: " + value);
        }
    }

    public void removeTaskById() {
        System.out.println("Введите ID задачи: ");
        int id = scanner.nextInt();

        if (taskMap.containsKey(id)) {
            taskMap.remove(id);
            System.out.println("Задача удалена!");
        } else {
            System.out.println("Задачи с таким ID нет!");
        }
    }

    public void removeEpicById() {
        System.out.println("Введите ID Эпика: ");
        int id = scanner.nextInt();

        if (epicMap.containsKey(id)) {
            for (SubTask subTask : epicMap.get(id).getSubTasks()) {
                subTaskMap.remove(subTask.getId());
            }
            epicMap.remove(id);
            System.out.println("Эпик удален!");
        } else {
            System.out.println("Эпика с таким ID нет!");
        }
    }

    public void removeSubTaskById() {
        System.out.println("Введите ID Подзадачи: ");
        int id = scanner.nextInt();

        if (subTaskMap.containsKey(id)) {
            epicMap.get(subTaskMap.get(id).getIdEpic()).removeSubTaskInList(subTaskMap.get(id));
            epicMap.get(subTaskMap.get(id).getIdEpic()).upgradeStatusEpic();
            subTaskMap.remove(id);
            System.out.println("Подзадача удалена!");
        } else {
            System.out.println("Подзадачи с таким ID нет!");
        }
    }

    public void showInfoById() {
        System.out.println("Введите ID задачи/эпика/подзадачи: ");
        int id = scanner.nextInt();

        if (taskMap.containsKey(id)) {
            System.out.println(taskMap.get(id).toString());
        } else if (subTaskMap.containsKey(id)) {
            System.out.println(subTaskMap.get(id).toString());
        } else if (epicMap.containsKey(id)) {
            System.out.println(epicMap.get(id).toString());
        } else {
            System.out.println("Задачи с таким ID нет!");
        }
    }

    public void removeAllTasks() {
        taskMap.clear();
    }

    public void removeAllEpics() {
        subTaskMap.clear();
        epicMap.clear();
    }

    public void removeAllSubtasks() {
        clearEpicSubTasks();
        subTaskMap.clear();

        for (Epic epic : epicMap.values()) {
            epic.upgradeStatusEpic();
        }
    }

    public void changeStatusTask() {
        System.out.println("Введите ID задачи: ");
        int id = scanner.nextInt();

        if (taskMap.containsKey(id)) {
            taskMap.get(id).upgradeStatus();
        } else if (subTaskMap.containsKey(id)) {
            subTaskMap.get(id).upgradeStatus();
            epicMap.get(subTaskMap.get(id).getIdEpic()).upgradeStatusEpic();
        } else if (epicMap.containsKey(id)) {
            System.out.println("У Эпика нельзя менять статус");
        } else {
            System.out.println("Задачи с таким ID нет!");
        }
    }

    public void updateTask(Task task) {
        if (taskMap.containsKey(task.getId())) {
            taskMap.replace(task.getId(), task);
        }
    }

    public void updateEpic(Epic epic) {
        if (epicMap.containsKey(epic.getId())) {
            epicMap.replace(epic.getId(), epic);
        }
    }

    public void updateSubTask(SubTask subTask) {
        if (subTaskMap.containsKey(subTask.getId())) {
            subTaskMap.replace(subTask.getId(), subTask);
        }
        epicMap.get(subTask.getIdEpic()).upgradeStatusEpic();
    }

    private void clearEpicSubTasks() {
        for (Epic epic : epicMap.values()) {
            epic.clearSubTaskList();
        }
    }

    private int getNewId() {
        return id = id + 1;
    }

    private void addSubTuskInMap(Epic epic) {
        for (SubTask subTask : epic.getSubTasks()) {
            if (!subTaskMap.containsKey(subTask.getId())) {
                subTaskMap.put(subTask.getId(), subTask);
            }
        }
    }
}