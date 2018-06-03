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
        //String nomAdresseServeur = "192.168.43.4";
        String nomAdresseServeur = "127.0.0.1";

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


        //On écrit le msg à envoyer
        System.out.println("Entrez le nom du fichier à télécharger");
        msg = sc.nextLine();

        //System.out.println(msg);

        //Envoi
        try {
            outClient.writeBytes("GET " + msg + " HTTP/1.1\r\n\r\n");
            outClient.flush();
            System.out.println("Requête envoyée!");

        }
        catch (IOException err) {
            System.out.println("Erreur envoi de la requête !");
            err.printStackTrace();
        }

        //Reception
        try {

            //On prépare une large zone de buffer pour recevoir le fichier
            byte [] fileBytes = new byte[6022386];

            //On prépare les Stream pour la réception
            InputStream clientIn = socketClient.getInputStream();
            FileOutputStream fos = new FileOutputStream("received/" + msg);
            BufferedOutputStream bos = new BufferedOutputStream(fos);

            int bytesRead, current;
            //On lit tout.
            bytesRead = clientIn.read(fileBytes,0, fileBytes.length);
            current = bytesRead;

            //System.out.println("bytesRead = " + bytesRead + ", current = " + current);

            /*
            do {
                bytesRead = clientIn.read(fileBytes, current, (fileBytes.length-current));
                System.out.println("bytesRead = " + bytesRead + ", current = " + current);
                if (bytesRead >= 0)
                    current += bytesRead;
            } while(bytesRead > -1); */

            bos.write(fileBytes, 0 , current);
            bos.flush();
            System.out.println("Fichier " + msg + " téléchargé (" + current + " octets lues)");

            //msg = inClient.readLine();
            //System.out.println("Réponse reçue : \'" + msg + "\'");

        }
        catch (IOException err) {
            System.out.println("Erreur reception !");
            err.printStackTrace();
        }


        try {
            outClient.writeBytes("quit");
            outClient.flush();
        }
        catch (IOException err) {
            System.out.println("Erreur requête terminaison connexion !");
            err.printStackTrace();
            return;
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
