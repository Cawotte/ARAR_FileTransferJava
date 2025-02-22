import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String args[])
    {
        ServerSocket serverSocket;
        Socket clientSocket;
        String[] splitRequest;
        //region creation serveur
        try {
            serverSocket = new ServerSocket(80);
            System.out.println("Serveur démarré ! En attente de clients...");
        }
        catch(IOException err) {
            System.out.println("Erreur création Socket Serveur !");
            err.printStackTrace();
            return;
        }
        //endregion

        //region connexion client
        try {
            clientSocket = serverSocket.accept();
            System.out.println("New connection accepted " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
        }
        catch(IOException err) {
            System.out.println("Erreur connexion avec le client !");
            err.printStackTrace();
            return;
        }
        //endregion

        while(true)
        {
            splitRequest = null;
            try {
                //Reception de la requête
                InputStreamReader inputReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(inputReader);

                //on récupère la première ligne c'est celle qui contient la méthode
                String httpRequest = reader.readLine();
                //System.out.println("HTTP REQUEST FULL : "+ httpRequest);

                //On la sépare pour analyser la méthode
                if ( httpRequest != null )
                    splitRequest = httpRequest.split(" ");
            }
            catch (IOException err) {
                System.out.println("Erreur Lecture Stream client !");
                err.printStackTrace();
                return;
            }

            //Si c'est un GET
            if( splitRequest != null && splitRequest[0].equals("GET") )
            {
                System.out.println("Requête GET reçu, fichier demandé : " + splitRequest[1]);

                try {
                    File fichier = new File("toSend/" + splitRequest[1]);

                    if(fichier.canRead())
                    {
                        //On lit l'entièreté du fichier qu'on enregistre dans un buffer.
                        byte [] fileBytes  = new byte [(int)fichier.length()];

                        FileInputStream fis = new FileInputStream(fichier);
                        BufferedInputStream bis = new BufferedInputStream(fis);

                        bis.read(fileBytes, 0, fileBytes.length);

                        //On envoie tout vers le client
                        OutputStream clientOut = clientSocket.getOutputStream();

                        System.out.println("Envoi du fichier " + splitRequest[1] + "(" + fileBytes.length + " octets)");
                        String httpResponse = "HTTP/1.1 200 OK\r\n";
                        clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                        clientOut.write(fileBytes, 0, fileBytes.length);
                        clientOut.flush();

                        System.out.println("Fichier envoyé.");

                    }
                    else
                    {
                        System.out.println("Fichier introuvable!");
                        String httpResponse = "HTTP/1.1 418 Error\r\nFichier introuvable!";
                        clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                        clientSocket.getOutputStream().flush();
                    }

                }
                catch(IOException err)
                {
                    System.out.println("Erreur écriture/lecture du fichier !");
                    err.printStackTrace();
                }

                System.out.println("En attente d'une nouvelle requête...");
            }

            if(splitRequest[0].equals("PUT"))
            {
                //On prépare une large zone de buffer pour recevoir le fichier
                byte [] fileBytes = new byte[6022386];

                try
                {
                    //region old
                    /*
                    //On prépare les Stream pour la réception
                    InputStream clientIn = clientSocket.getInputStream();
                    FileOutputStream fos = new FileOutputStream(splitRequest[1]);
                    BufferedOutputStream bos = new BufferedOutputStream(fos);

                    int bytesRead, current;
                    //On lit tout.
                    bytesRead = clientIn.read(fileBytes,0,fileBytes.length);
                    current = bytesRead;

                    bos.write(fileBytes, 0 , current);
                    bos.flush(); */
                    //endregion

                    int bytesRead;
                    int totalBytesRead = 0;
                    InputStream clientIn = clientSocket.getInputStream();
                    FileOutputStream fos = new FileOutputStream(splitRequest[1]);

                    //On lira dans le stream par coup de 1024 octets.
                    byte[] buffer = new byte[1024];

                    do {
                        bytesRead = clientIn.read(buffer); //On lit le stream et on compte le nombre d'octets lues.
                        totalBytesRead += bytesRead;
                        //System.out.println("bytesRead = " + bytesRead); //Debug
                        fos.write(buffer, 0, bytesRead); //On écrit ce qu'on a lue dans le fichier.

                        //Tant que le buffer est plein, il y a encore du contenu à lire ! On boucle donc.
                    } while ( bytesRead != -1 && bytesRead == buffer.length );


                    System.out.println("Fichier " + splitRequest[1] + " téléchargé ! (" + totalBytesRead + " octets lues)");

                    fos.close();

                    String httpResponse = "HTTP/1.1 200 OK\r\n";
                    clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                    clientSocket.getOutputStream().flush();
                }
                catch(Exception e)
                {
                    System.out.println(e);
                }
            }

            if ( splitRequest != null && splitRequest[0].equals("quit") ) {

                System.out.println("Arrêt de la connexion avec le client...");
                try {
                    clientSocket.close();
                } catch (IOException err) {
                    System.out.println("Erreur arrêt de la connexion client !");
                    err.printStackTrace();
                    return;
                }

                try {
                    System.out.println("En attente d'une nouvelle connexion client...");
                    clientSocket = serverSocket.accept();
                    System.out.println("New connection accepted " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
                } catch (IOException err)
                {
                    System.out.println("Erreur arrêt de la connexion client !");
                    err.printStackTrace();
                    return;
                }
            }
        }
    }
}
