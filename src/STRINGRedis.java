import redis.clients.jedis.Jedis;

import java.util.Scanner;

public class STRINGRedis {
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
                System.out.println("What is your favorite movie?");
                System.out.println("1) Write my favorite movie.");
                System.out.println("2) Delete my favorite movie.");
                System.out.println("3) Tell me wich is my favorite movie.");
                System.out.println("4) Exit.");

                option = keyboard.nextInt();

                switch (option) {
                    case 1:
                        setMovieName(jedis);
                        break;
                    case 2:
                        deleteMovieName(jedis);
                        break;
                    case 3:
                        getMovieName(jedis);
                        break;
                    case 4:
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
     * Recibe una cadena con el nombre de la pelicula a almacenar en Redis.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void setMovieName(Jedis jedis) {
        Scanner keyboard = new Scanner(System.in);
        String movieName = "";

        System.out.println("\nWhat is your favorite movie name?");
        movieName = keyboard.nextLine().toUpperCase();

        //Guardamos la cadena en Redis
        jedis.set("favorite-movie" , movieName);

        System.out.println("Inserted\n");
    }

    /**
     * Elimina la variable con el nombre de la pelicula.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     */
    private void deleteMovieName(Jedis jedis) {
        System.out.println("\n");
        //Elimina el contenido de la varible de Redis
        jedis.del("favorite-movie");
        System.out.println("Deleted!\n");
    }

    /**
     * Recupera el valor de la cadena con nombre de la pelicula.
     * @param jedis variable para manejar la conexion con Redis y realizar las operaciones adecuadas.
     * @return
     */
    private String getMovieName(Jedis jedis) {
        String movieName = "";

        System.out.println("\n");

        //Recupera el valor de la variable si existe, sino, es null
        movieName = jedis.get("favorite-movie");

        if (movieName != null) {
            System.out.println("Your favorite movie is: " + movieName + "\n");
        } else {
            System.out.println("You don't have a favorite movie yet!");
        }

        return movieName;
    }
}
