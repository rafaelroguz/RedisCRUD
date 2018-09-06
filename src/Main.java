import redis.clients.jedis.Jedis;

import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner keyboard = new Scanner(System.in);
        int option;

        //Conectandose al servidor
        Jedis jedis = new Jedis("localhost");
        System.out.println("Connection to server sucessfully");
        //Revisa si el servidor se esta ejecutando o no
        System.out.println("Server is running: " + jedis.ping());

        //Menu de opciones para el usuario. Se mantiene ejecutando hasta que el usuario seleccione la opcion 5
        //Cada opcion dirige a un nuevo submenu que actua como su propio CRUD
        //Cada CRUD maneja diferente tematica con el objetivo de ilustrar el uso de los comandos basicos de las
        //estructuras de datos de Redis
        try {
            do {
                System.out.println("\nBienvenido!");
                System.out.println("Seleccione una opcion:");
                System.out.println("1) Strings");
                System.out.println("2) Lists");
                System.out.println("3) Sets");
                System.out.println("4) Hashes");
                System.out.println("5) Sorted sets");
                System.out.println("6) Exit");

                option = keyboard.nextInt();

                switch (option) {
                    case 1:
                        STRINGRedis stringRedis = new STRINGRedis();
                        stringRedis.execute(jedis);
                        break;
                    case 2:
                        LISTRedis listRedis = new LISTRedis();
                        listRedis.execute(jedis);
                        break;
                    case 3:
                        SETRedis setRedis = new SETRedis();
                        setRedis.execute(jedis);
                        break;
                    case 4:
                        HASHRedis hashRedis = new HASHRedis();
                        hashRedis.execute(jedis);
                        break;
                    case 5:
                        ZSETRedis zsetRedis = new ZSETRedis();
                        zsetRedis.execute(jedis);
                        break;
                    case 6:
                        System.out.println("Good bye!");
                        System.exit(0);
                        break;
                    default:
                        System.out.println("Please choose a valid option.");
                }
            } while (option != 6);
        } catch (NumberFormatException e1) {
            System.out.println("Please choose a valid option.");
        }
    }
}
