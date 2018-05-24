import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client
{

    public static void main(String[] argc) {

        Scanner sc = new Scanner(System.in);
        boolean isConnected = true;
        String msg;

        Socket socketClient;
        InetAddress localAdress;
        InetAddress serveurAdress;

        String nomAdresseServeur = "www.facebook.com";

        //region Attribution des adresses
        try {
            localAdress =  InetAddress.getLocalHost();
            System.out.println("Votre adresse locale est " + localAdress);
            serveurAdress = InetAddress.getByName(nomAdresseServeur);
            System.out.println("L'adresse du serveur cible est " + serveurAdress);
        }
        catch ( UnknownHostException err ) {
            System.out.println("ERREUR : Adresses inconnus !");
            err.printStackTrace();
            return;
        }
        //endregion

        //region Création Socket client
        try {
            socketClient = new Socket(serveurAdress, 80);
            System.out.println("Socket crée sur le port 80 !");
        }
        catch (IOException err) {
            System.out.println("ERREUR création Socket client !");
            err.printStackTrace();
            return;
        }
        //endregion

        while (isConnected) {

            System.out.println("Entrez un message...");
            msg = sc.nextLine();

            System.out.println(msg);

            if ( msg.equals("quit")) {
                isConnected = false;
            }

        }

        //region Fermeture scanner et socket
        System.out.println("Arrêt du client...");
        sc.close();

        try {
            socketClient.close();
        }
        catch (IOException err) {
            System.out.println("ERREUR fermeture Socket Client");
            err.printStackTrace();
        }


    }


}
