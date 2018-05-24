import java.io.*;
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

        //Streams et buffers
        DataInputStream inClient;
        DataOutputStream outClient;
        BufferedInputStream bufferIn;
        BufferedOutputStream bufferOut;

        //Socket et adresses
        Socket socketClient;
        InetAddress localAdress;
        InetAddress serveurAdress;

        //"http://192.168.43.4/";
        //Adresse fabien : "192.168.43.4"
        String nomAdresseServeur = "192.168.43.4";

        //region Initialisation
        try {

            //Init adresses
            localAdress =  InetAddress.getLocalHost();
            System.out.println("Votre adresse locale est " + localAdress);
            serveurAdress = InetAddress.getByName(nomAdresseServeur);
            System.out.println("L'adresse du serveur cible est " + serveurAdress);

            //Creation socket Client
            socketClient = new Socket(serveurAdress, 80);
            System.out.println("Socket crée sur le port 80 !");

            //Creation Streams
            inClient = new DataInputStream(socketClient.getInputStream());
            bufferIn = new BufferedInputStream(inClient);
            outClient = new DataOutputStream(socketClient.getOutputStream());
            bufferOut = new BufferedOutputStream(outClient);
        }
        catch ( UnknownHostException err ) {
            System.out.println("ERREUR : Adresses inconnus !");
            err.printStackTrace();
            return;
        }
        catch (IOException err) {
            System.out.println("ERREUR création Socket client OU Streams !");
            err.printStackTrace();
            return;
        }
        //endregion Initialisation



        while (isConnected) {


            //On écrit le msg à envoyer
            System.out.println("Entrez un message...");
            msg = sc.nextLine();

            //System.out.println(msg);

            //Envoie
            try {
                outClient.writeBytes("GET " + msg + " HTTP/1.1\r\n\r\n");
                outClient.flush();
                System.out.println("Message envoyé!");

            }
            catch (IOException err) {
                System.out.println("Erreur envoie GET");
                err.printStackTrace();
            }

            //Reception
            try {
                msg = inClient.readLine();
                System.out.println("Réponse reçue : \'" + msg + "\'");
            }
            catch (IOException err) {
                System.out.println("Erreur reception !");
                err.printStackTrace();
            }

            //Arrêt de la connexion si le msg = "quit"
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
        //endregion


    }


}
