public class Task {

    private final String name;
    private final String description;
    private final int id;
    private Status status;

    public Task(String name, String description, int id) {
        this.name = name;
        this.description = description;
        this.id = id;
        this.status = Status.NEW;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", status=" + status +
                '}';
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public void upgradeStatus() {
        if (status == Status.NEW){
            setStatus(Status.IN_PROGRESS);
            System.out.println("Статус задачи изменен на 'В ПРОЦЕССЕ'");
        } else if (status == Status.IN_PROGRESS) {
            setStatus(Status.DONE);
            System.out.println("Статус задачи изменен на 'ЗАВЕРШЕН'");
        } else if (status == Status.DONE) {
            System.out.println("Статус задачи уже 'ЗАВЕРШЕН'");
        }
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getId() {
        return id;
    }

    public Status getStatus() {
        return status;
    }
}