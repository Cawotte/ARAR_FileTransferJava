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
        String nomAdresseServeur = "192.168.43.4";

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

        //region get Input/Output Streams
        try {
            inClient = new DataInputStream(socketClient.getInputStream());
            bufferIn = new BufferedInputStream(inClient);
            outClient = new DataOutputStream(socketClient.getOutputStream());
            bufferOut = new BufferedOutputStream(outClient);
        }
        catch (IOException err) {
            System.out.println("Erreur création In/Out Streams !");
            err.printStackTrace();
            return;
        }
        //endregion

        msg = "Salut?";

        /*
        while (isConnected) {

            try {
                outClient.writeBytes("GET " + msg + " HTTP/1.1\r\n\n");
                outClient.flush();
                System.out.println("Message envoyé!");

            }
            catch (IOException err) {
                System.out.println("Erreur envoie GET");
                err.printStackTrace();
            }


        } */


        try {
            outClient.writeBytes("GET " + msg + " HTTP/1.1\r\n\n");
            outClient.flush();
            System.out.println("Message envoyé!");

        }
        catch (IOException err) {
            System.out.println("Erreur envoie GET");
            err.printStackTrace();
        }

        //inClient.read();

        //region boucle simple
        /*
        while (isConnected) {

            System.out.println("Entrez un message...");
            msg = sc.nextLine();

            System.out.println(msg);

            if ( msg.equals("quit")) {
                isConnected = false;
            }

        }
        */
        //endregion

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
