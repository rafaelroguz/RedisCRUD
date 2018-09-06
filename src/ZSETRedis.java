import redis.clients.jedis.Jedis;

import java.util.Scanner;
import java.util.Set;

public class ZSETRedis {
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
                System.out.println("Welcome to the videogame scores dashboard! Select an option:");
                System.out.println("1) Register a new score and player.");
                System.out.println("2) Delete a player.");
                System.out.println("3) Show the best players.");
                System.out.println("4) Show players in a score range.");
                System.out.println("5) Exit.");

                option = keyboard.nextInt();

                switch (option) {
                    case 1:
                        insertScore(jedis);
                        break;
                    case 2:
                        deleteScore(jedis);
                        break;
                    case 3:
                        showAllScores(jedis);
                        break;
                    case 4:
                        showScoresInRange(jedis);
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
     *
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void insertScore(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String user = "";
        int score = 0;

        System.out.println("\nType the nick name:");

        user = keyboard.nextLine();

        System.out.println("Type de score:");

        score = keyboard.nextInt();

        jedis.zadd("score-zset", score, user);

        System.out.println("New score inserted!\n");
    }

    /**
     *
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void deleteScore(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String user = "";

        System.out.println("\nType de nick name to delete from the score list:");

        user = keyboard.nextLine();

        jedis.zrem("score-zset", user);

        System.out.println("Deleted!\n");
    }

    /**
     *
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void showAllScores(Jedis jedis) {
        Set<String> players = jedis.zrange("score-zset", 0, -1);
        int i = players.size();

        System.out.println("\nTop players:");

        for (String player : players) {
            System.out.println(i + ") " + player);
            i--;
        }

        System.out.println("\n");
    }

    /**
     *
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void showScoresInRange(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        int beggining = 0;
        int ending = 0;

        Set<String> players;

        System.out.println("\nType de lowest score you want to see:");

        beggining = keyboard.nextInt();

        System.out.println("Type de highest score you want to see:");

        ending = keyboard.nextInt();

        System.out.println("Here are the players with a score between that range:");

        players = jedis.zrangeByScore("score-zset", beggining, ending);

        for (String player : players) {
            System.out.println(player);
        }

        System.out.println("\n");
    }

}
