import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        TaskManager taskManager = new TaskManager();
        Scanner scanner = new Scanner(System.in);
        boolean isMenu = true;

        while (isMenu) {
            System.out.println("Введите нужный пункт");
            System.out.println("1 - Создать задачу");
            System.out.println("2 - Создать Эпик");
            System.out.println("3 - Создать Подзадачу");
            System.out.println("4 - Изменить статус задачи по ID");
            System.out.println("5 - Показать все Эпики");
            System.out.println("6 - Показать все подзадачи");
            System.out.println("7 - Показать все задачи");
            System.out.println("8 - Показать задачу/эпик/подзадачу по ID");
            System.out.println("9 - Удалить все задачи");
            System.out.println("10 - Удалить все Эпики");
            System.out.println("11 - Удалить все подзадачи");
            System.out.println("12 - Удалить задачу по ID");
            System.out.println("13 - Удалить эпик по ID");
            System.out.println("14 - Удалить подзадачу по ID");
            System.out.println("15 - Выход");

            int input = scanner.nextInt();

            switch (input) {
                case 1 :
                    taskManager.createTask();
                    break;
                case 2 :
                    taskManager.createEpic();
                    break;
                case 3 :
                    taskManager.createSubTask();
                    break;
                case 4 :
                    taskManager.changeStatusTask();
                    break;
                case 5 :
                    taskManager.showEpics();
                    break;
                case 6 :
                    taskManager.showSubTasks();
                    break;
                case 7 :
                    taskManager.showTasks();
                    break;
                case 8 :
                    taskManager.showInfoById();
                    break;
                case 9 :
                    taskManager.removeAllTasks();
                    break;
                case 10 :
                    taskManager.removeAllEpics();
                    break;
                case 11 :
                    taskManager.removeAllSubtasks();
                    break;
                case 12 :
                    taskManager.removeTaskById();
                    break;
                case 13 :
                    taskManager.removeEpicById();
                    break;
                case 14 :
                    taskManager.removeSubTaskById();
                    break;
                case 15 :
                    isMenu = false;
                    break;
            }
        }
    }
}