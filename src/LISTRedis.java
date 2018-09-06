import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class LISTRedis {
    /**
     * Realiza la ejecucion de la aplicacion, siempre esperando a que el usurio introduzca una opcion.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    public void execute(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        int option;

        //Menu de opciones para el usuario. Se mantiene ejecutando hasta que el usuario seleccione la opcion 5
        try {
            do {
                System.out.println("Welcome to your to do list! Select an option:");
                System.out.println("1) Push new task.");
                System.out.println("2) Pop completed task");
                System.out.println("3) Get a task");
                System.out.println("4) List all task.");
                System.out.println("5) Exit.");

                option = keyboard.nextInt();

                switch (option) {
                    case 1:
                        insertTask(jedis);
                        break;
                    case 2:
                        popTask(jedis);
                        break;
                    case 3:
                        getTask(jedis);
                        break;
                    case 4:
                        listTasks(jedis);
                        break;
                    case 5:
                        System.out.println("Good bye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Please choose a valid option.");
                }
            } while (option != 5);
        } catch (NumberFormatException e1) {
            System.out.println("Please choose a valid option.");
        } catch (InputMismatchException e2) {
            System.out.println("Please choose a valid option.");
        }
    }

    /**
     * Añade una nueva tarea al final de la lista.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void insertTask(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String task = "";

        System.out.println("\nINSERTING--------------------------------------------------");

        System.out.println("Insert a new task:");
        task = keyboard.nextLine();

        //Insertamos un nuevo trabajador al fondo de la lista
        jedis.rpush("task-list", task);

        System.out.println("--------------------------------------------------\n");

        listTasks(jedis);
    }

    /**
     * Elimina la tarea que se encuentra al tope de la lista.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void popTask(Jedis jedis) {
        System.out.println("\nPOP TASK--------------------------------------------------");

        //Sacamos el trabajador en el tope de la lista
        jedis.lpop("task-list");

        listTasks(jedis);

        System.out.println("--------------------------------------------------\n");
    }

    /**
     * Devuelve una tarea especifica dado el indice mostrado.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void getTask(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        try {
            System.out.println("\nGET TASK--------------------------------------------------");
            listTasks(jedis);
            System.out.println("Which task do you want? (Insert number):");

            int taskId = keyboard.nextInt();

            //Recuperamos un trabajador en el indice indicado por el usuario
            System.out.println("Your task: " + jedis.lindex("task-list", taskId-1));

            System.out.println("--------------------------------------------------\n");
        } catch (NumberFormatException e1) {
            System.out.println("Please enter a valid number");
        }
    }

    /**
     * Retorna una lista de cadenas que contienen las tareas que el usuario no ha eliminado.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     * @return List<String> con todas las tareas que el usuario tiene almacenadas.
     */
    private List<String> listTasks(Jedis jedis) {
        //Obtenemos una lista de todos los trabajadores
        List<String> tasks = jedis.lrange("task-list", 0 , -1);

        System.out.println("TASKS--------------------------------------------------");

        for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i+1) + " - " + tasks.get(i));
        }

        System.out.println("--------------------------------------------------\n");

        return tasks;
    }
}
