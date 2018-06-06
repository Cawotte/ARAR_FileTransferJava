import java.io.*;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class Client
{

    public static void main(String[] argc)
    {

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

        while(isConnected)
        {
            System.out.println("Entrez votre requête");
            msg = sc.nextLine();
            System.out.println(msg);

            try {
                String[] message = msg.split(" ");
                System.out.println(message[1]);
                if(message[0].equals("GET"))
                    get(outClient,message[1], socketClient);
                if(message[0].equals("PUT"))
                    put(outClient, message[1], socketClient);
                if(message[0].equals("quit"))
                {
                    try {
                        outClient.writeBytes("quit");
                        outClient.flush();
                    }
                    catch (IOException err) {
                        System.out.println("Erreur requête terminaison connexion !");
                        err.printStackTrace();
                        return;
                    }
                }

            }
            catch (IOException err) {
                System.out.println("Erreur envoi de la requête !");
                err.printStackTrace();
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

    public static void get(DataOutputStream outClient, String msg, Socket socketClient) throws IOException
    {
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
            //region old
            /*
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

            bos.write(fileBytes, 0 , current);
            bos.flush();
            System.out.println("Fichier " + msg + " téléchargé (" + current + " octets lues)");*/
            //endregion

            int bytesRead;
            int totalBytesRead = 0;
            InputStream clientIn = socketClient.getInputStream();
            FileOutputStream fos = new FileOutputStream("received/" + msg);

            //On lira dans le stream par coup de 1024 octets.
            byte[] buffer = new byte[1024];

            clientIn.read(buffer, 0, 17); //On ignore les 17 octets du header

            do {
                bytesRead = clientIn.read(buffer); //On lit le stream et on compte le nombre d'octets lues.
                totalBytesRead += bytesRead;
                //System.out.println("bytesRead = " + bytesRead); //Debug
                fos.write(buffer, 0, bytesRead); //On écrit ce qu'on a lue dans le fichier.

                //Tant que le buffer est plein, il y a encore du contenu à lire ! On boucle donc.
            } while ( bytesRead != -1 && bytesRead == buffer.length );


            System.out.println("Fichier " + msg + " téléchargé ! (" + totalBytesRead + " octets lues)");

            fos.close();
        }
        catch (IOException err) {
            System.out.println("Erreur reception !");
            err.printStackTrace();
        }
    }

    public static void put(DataOutputStream outClient, String fic, Socket socketClient) throws IOException
    {

        File fichier = new File("toSend/" + fic);

        if(fichier.canRead())
        {
            byte [] fileBytes  = new byte [(int)fichier.length()];

            //On prépare les Stream.
            FileInputStream fis = new FileInputStream(fichier);
            BufferedInputStream bis = new BufferedInputStream(fis);

            bis.read(fileBytes, 0, fileBytes.length);
            outClient.writeBytes("PUT " + fic + " HTTP/1.1\r\n\r\n");

            outClient.write(fileBytes, 0, fileBytes.length);
            System.out.println( (outClient.size() - ("PUT " + fic + " HTTP/1.1\r\n\r\n").getBytes().length) + " octets envoyés.");
            outClient.flush();
            System.out.println("Fichier envoyé !");

            InputStream clientIn = socketClient.getInputStream();
            InputStreamReader inputReader = new InputStreamReader(clientIn);
            BufferedReader reader = new BufferedReader(inputReader);
            String reponse = reader.readLine();
            //System.out.println("réponse serveur : " + reponse);
            String[] reponseSplit = reponse.split(" ");
            if (reponseSplit[1].equals("200"))
            {
                System.out.println("Transfert réussi ! (Accusé de réception reçu)");
            }
        }
        else
            System.out.println("fichier introuvable");
    }
}
