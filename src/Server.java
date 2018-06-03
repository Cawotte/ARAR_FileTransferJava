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
            else if ( splitRequest != null && splitRequest[0].equals("quit") ) {

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
