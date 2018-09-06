import redis.clients.jedis.Jedis;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class HASHRedis {
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
                System.out.println("Welcome to the workers managing system! Select an option:");
                System.out.println("1) Register new worker.");
                System.out.println("2) Delete worker register.");
                System.out.println("3) Edit worker register.");
                System.out.println("4) Find a worker.");
                System.out.println("5) Exit.");

                option = keyboard.nextInt();

                switch (option) {
                    case 1:
                        insertWorker(jedis);
                        break;
                    case 2:
                        deleteWorker(jedis);
                        break;
                    case 3:
                        editWorker(jedis);
                        break;
                    case 4:
                        getWorker(jedis);
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
        }
    }

    /**
     * Crea el registro de un nuevo trabajador. Toma los datos y los pasa a un hashmap para poder enviarlos a Redis.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void insertWorker(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String name = "";
        int age = 0;
        double salary = 0.0;
        String redisName = "";

        System.out.println("\nINSERTING--------------------------------------------------");

        try {
            //Recuperamos la informacion del trabajador a insertar
            System.out.println("What is the worker full name?");
            name = keyboard.nextLine().toUpperCase();
            System.out.println("How old it the worker?");
            age = keyboard.nextInt();
            System.out.println("How much the worker gets paid?");
            salary = keyboard.nextDouble();

            Map<String, String> userProperties = new HashMap<String, String>();
            userProperties.put("name", name);
            userProperties.put("age", Integer.toString(age));
            userProperties.put("salary", Double.toString(salary));

            redisName = "worker:" + name.replaceAll("\\s+","");

            System.out.println(jedis.hmset(redisName, userProperties));

            System.out.println("Inserted!");

            //Imprimimos el contenido del hashmap despues de la insercion
            getWorker(jedis, redisName);

            System.out.println("\n--------------------------------------------------");
        } catch (NumberFormatException e1) {
            System.out.println("Please enter a valid number");
        }
    }

    /**
     * Si el trabajador existe, lo elimina del hasmap de Redis
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void deleteWorker(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String workerName = "";
        String redisName = "";
        System.out.println("\nDELETING--------------------------------------------------");
        System.out.println("Type the name of the worker to delete:");

        //Recuperamos el nombre del trabajador a borrar
        workerName = keyboard.nextLine().toUpperCase();
        redisName = "worker:" + workerName.replaceAll("\\s+","");

        jedis.hdel(redisName, "name", "age", "salary");

        System.out.println("Deleted!");

        //Comprobamos que hubo eliminacion
        getWorker(jedis, redisName);

        System.out.println("--------------------------------------------------");
    }

    /**
     * Recupera la informacion del trabajador y permite su edicion para registrar los cambios en Redis
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void editWorker(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String workerName = "";
        String redisName = "";
        String name;
        int age;
        double salary;

        System.out.println("\nEDITING--------------------------------------------------");
        System.out.println("Type the name of the worker to edit:");

        //Recuperamos el nombre del trabajador a editar
        workerName = keyboard.nextLine().toUpperCase();
        redisName = "worker:" + workerName.replaceAll("\\s+","");

        Map<String, String> workerMap = jedis.hgetAll(redisName);

        if (!workerMap.isEmpty()) {
            //Realizamos la edicion en todos los campos
            System.out.println("Edit name (" + workerMap.get("name") + ")");
            name = keyboard.nextLine().toUpperCase();

            System.out.println("Edit age (" + workerMap.get("age") + ")");
            age = keyboard.nextInt();

            System.out.println("Edit salary (" + workerMap.get("salary") + ")");
            salary = keyboard.nextDouble();

            jedis.hset(redisName, "name", name);
            jedis.hset(redisName, "age", Integer.toString(age));
            jedis.hset(redisName, "salary", Double.toString(salary));
        }

        getWorker(jedis, redisName);

        System.out.println("\n--------------------------------------------------");
    }

    /**
     * Recupera la informacion de un trabajador dado su nombre
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private Map<String, String> getWorker(Jedis jedis, String name) {
        //Recuperamos del hash a un trabajador especifico dado su nombre
        Map<String, String> worker = jedis.hgetAll(name);

        System.out.println("Your worker: ");
        System.out.println("Name: " + worker.get("name"));
        System.out.println("Age; " + worker.get("age"));
        System.out.println("Salary: " + worker.get("salary"));
        System.out.println("\n");

        return worker;
    }

    /**
     * Recupera la informacion de un trabajador dado su nombre.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void getWorker(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String workerName = "";
        String redisName = "";

        System.out.println("What is the workers name?:");
        workerName = keyboard.nextLine().toUpperCase();
        redisName = "worker:" + workerName.replaceAll("\\s+","");

        //Recuperamos todos los trabajadores del hash
        Map<String, String> worker = jedis.hgetAll(redisName);

        if (!worker.isEmpty()) {
            System.out.println("Your worker: ");
            System.out.println("Name: " + worker.get("name"));
            System.out.println("Age; " + worker.get("age"));
            System.out.println("Salary: " + worker.get("salary"));
            System.out.println("\n");
        } else {
            System.out.println("Worker not found!");
        }
    }

    /**
     * Clase que representa la informacion del trabajador.
     */
    public static class Worker {
        private String name;
        private int age;
        private double salary;

        public Worker() {
            this.name = "";
            this.age = 0;
            this.salary = 0.0;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public double getSalary() {
            return salary;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }

        public void setSalary(double salary) {
            this.salary = salary;
        }

        public void printInformation() {
            System.out.println("Name: " + name);
            System.out.println("Age: " + age);
            System.out.println("Salary: " + salary);
        }
    }
}
