import java.util.ArrayList;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {
    private final List<Task> historyTasks = new ArrayList<>();

    @Override
    public void add(Task task) {
        if (task != null) {
            historyTasks.add(task);
            if (historyTasks.size() > 10) {
                historyTasks.removeFirst();
            }
        }
    }

    @Override
    public List<Task> getHistory() {
        return List.copyOf(historyTasks);
    }
}