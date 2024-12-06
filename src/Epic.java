import java.util.ArrayList;
import java.util.Scanner;

public class Epic extends Task {

    private final ArrayList<SubTask> subTasks = new ArrayList<>();
    private final Scanner scanner = new Scanner(System.in);

    public Epic(String name, String description, int id) {
        super(name, description, id);
    }

    @Override
    public String toString() {
        return "Epic{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", description='" + getDescription() + '\'' +
                ", status=" + getStatus() +
                '}';
    }

    public void addSubTask(int id) {
        System.out.println("Введите название задачи: ");
        String name = scanner.next();
        System.out.println("Введите описание задачи: ");
        String description = scanner.next();
        SubTask subTask = new SubTask(name, description, id, getId());
        subTasks.add(subTask);
        System.out.println("Задача '" + subTask.getName() + "' создана, ID-" + subTask.getId());
    }

    public void removeSubTaskInList(SubTask subTask) {
        for (SubTask sub : subTasks) {
            if (subTask.equals(sub)) {
                subTasks.remove(subTask);
            }
        }
    }

    public void upgradeStatusEpic() {
        if (subTasks.isEmpty()) {
            setStatus(Status.NEW);
        } else {
            int numberOfTask = subTasks.size();
            int currentNumber = 0;
            int currentNumber2 = 0;
            for (SubTask subTask : subTasks) {
                if (subTask.getStatus() == Status.IN_PROGRESS) {
                    setStatus(Status.IN_PROGRESS);
                } else if (subTask.getStatus() == Status.DONE) {
                    setStatus(Status.IN_PROGRESS);
                    currentNumber++;
                } else if (subTask.getStatus() == Status.NEW) {
                    currentNumber2++;
                }
            }

            if (currentNumber == numberOfTask) {
                setStatus(Status.DONE);
            } else if (currentNumber2 == numberOfTask) {
                setStatus(Status.NEW);
            }
        }
    }

    public void clearSubTaskList() {
        subTasks.clear();
    }

    public ArrayList<SubTask> getSubTasks() {
        return subTasks;
    }
}