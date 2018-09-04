import com.google.gson.Gson;
import redis.clients.jedis.Jedis;

import java.util.Scanner;
import java.util.Set;

public class Main {



    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        int option;
        //Conectandose al servidor
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //Revisa si el servidor se esta ejecutando o no
        System.out.println("Server is running: " + jedis.ping());

        //Menu de opciones para el usuario. Se mantiene ejecutando hasta que el usuario seleccione la opcion 4
        try {
            do {
                System.out.println("Welcome! Select an option:");
                System.out.println("1) Register new worker.");
                System.out.println("2) Delete worker register.");
                System.out.println("3) Edit worker register.");
                System.out.println("4) List all workers.");
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
                        getWorkers(jedis);
                        break;
                    case 5:
                        System.out.println("Good bye!");
                        System.exit(0);
                    default:
                        System.out.println("Please choose a valid option.");
                }
            } while (option != 5);
        } catch (NumberFormatException e1) {
            System.out.println("Please choose a valid option.");
        }
    }

    /**
     * Crea el registro de un nuevo trabajador. Toma los datos como cadenas y los convierte a JSON para poder
     * insertarlos como cadenas JSON a Redis. Hace uso de SET. Al finalizar imprime el contenido del SET.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    public static void insertWorker(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        Worker worker = new Worker();
        Gson gson = new Gson();

        System.out.println("\nINSERTING--------------------------------------------------");

        try {
            //Recuperamos la informacion del trabajador a insertar
            System.out.println("What is the worker full name?");
            worker.setName(keyboard.nextLine().toUpperCase());
            System.out.println("How old it the worker?");
            worker.setAge(keyboard.nextInt());
            System.out.println("How much the worker gets paid?");
            worker.setSalary(keyboard.nextDouble());

            //Convertimos el objeto de tipo Worker a un String JSON y lo insertamos al SET
            jedis.sadd("worker-set", gson.toJson(worker));

            System.out.println("Inserted!");

            //Imprimimos el contenido de SET despues de la insercion
            getWorkers(jedis);

            System.out.println("\n--------------------------------------------------");
        } catch (NumberFormatException e1) {
            System.out.println("Please enter a valid number");
        }
    }

    /**
     * Elimina el registro de un nuevo trabajador. El usuario introduce el nombre del trabajador que quiere borrar.
     * Hace uso de SET. Al finalizar imprime el contenido del SET.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    public static void deleteWorker(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String workerName = "";

        //Recuperamos el contenido del SET que tiene toda la informacion de los trabajadores en cadenas JSON
        Set<String> workers = jedis.smembers("worker-set");

        System.out.println("\nDELETING--------------------------------------------------");
        System.out.println("Type the name of the worker to delete:");

        //Recuperamos el nombre del trabajador a borrar
        workerName = keyboard.nextLine().toUpperCase();

        for (String w: workers) {
            //Si el nombre que introdujo el usuario se encuentra en Redis, lo eliminamos
            if (w.contains(workerName)) {
                jedis.srem("worker-set", w);
                break;
            }
        }

        System.out.println("Deleted!");

        //Imprimimos el contenido de SET despues de eliminar.
        getWorkers(jedis);

        System.out.println("--------------------------------------------------");
    }

    /**
     * Elimina el registro de un nuevo trabajador. El usuario introduce el nombre del trabajador que quiere borrar.
     * Hace uso de SET. Al finalizar imprime el contenido del SET.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    public static void editWorker(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String workerName = "";
        char answer = 'N';

        //Recuperamos el contenido del SET que tiene toda la informacion de los trabajadores en cadenas JSON
        Set<String> workers = jedis.smembers("worker-set");

        Worker worker = new Worker();
        Gson gson = new Gson();

        System.out.println("\nEDITING--------------------------------------------------");
        System.out.println("Type the name of the worker to edit:");

        //Recuperamos el nombre del trabajador a editar
        workerName = keyboard.nextLine().toUpperCase();

        for (String w: workers) {
            if (w.contains(workerName)) {
                //Copiamos la cadena JSON con la informacion del trabajador a un objeto Worker para manipularlo
                worker = gson.fromJson(w, Worker.class);
                //Eliminamos el viejo registro de Redis
                jedis.srem("worker-set", w);

                //Mostramos la informacion del usuario para ser editada
                worker.printInformation();

                //Realizamos la edicion en todos los campos
                System.out.println("Edit name? (Y/N)");
                answer = keyboard.nextLine().toUpperCase().charAt(0);
                if (answer == 'Y') {
                    System.out.println("What is the worker full name?");
                    worker.setName(keyboard.nextLine().toUpperCase());
                    answer = 'N';
                }

                System.out.println("Edit age? (Y/N)");
                answer = keyboard.nextLine().toUpperCase().charAt(0);
                if (answer == 'Y') {
                    System.out.println("How old it the worker?");
                    worker.setAge(keyboard.nextInt());
                    answer = 'N';
                }

                System.out.println("Edit salary? (Y/N)");
                answer = keyboard.nextLine().toUpperCase().charAt(0);
                if (answer == 'Y') {
                    System.out.println("How much the worker gets paid?");
                    worker.setSalary(keyboard.nextDouble());
                    answer = 'N';
                }

                //Convertimos el objeto de tipo Worker a un String JSON y lo insertamos al SET
                jedis.sadd("worker-set", gson.toJson(worker));

                System.out.println("Edited!");

                break;
            }
        }

        getWorkers(jedis);

        System.out.println("\n--------------------------------------------------");
    }

    /**
     * Imprime el contenido del SET de trabajadores en formato JSON
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    public static void getWorkers(Jedis jedis) {
        Set<String> workers = jedis.smembers("worker-set");

        System.out.println("\nALL WORKERS--------------------------------------------------");

        for (String w: workers) {
            System.out.println(w);
        }

        System.out.println("\n--------------------------------------------------");
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
