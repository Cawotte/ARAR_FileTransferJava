import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String args[])
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(80);
            System.out.println("Serveur démarré ! En attente de clients...");

            Socket clientSocket = serverSocket.accept();
            System.out.println("New connection accepted " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            while(true)
            {
                //Reception de la requête
                InputStreamReader inputReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(inputReader);

                //on récupère la première ligne c'est celle qui contient la méthode
                String httpRequest = reader.readLine();
                //System.out.println("HTTP REQUEST FULL : "+ httpRequest);

                //On la sépare pour analyser la méthode
                String[] splitRequest = httpRequest.split(" ");

                //Si c'est un GET
                if(splitRequest[0].equals("GET"))
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

                            //On va envoyer tout ce qu'on a lu vers le client.

                            //OutputStream os = clientSocket.getOutputStream();

                            OutputStream clientOut = clientSocket.getOutputStream();

                            System.out.println("Sending " + splitRequest[1] + "(" + fileBytes.length + " bytes)");
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
                    catch(Exception e)
                    {
                        System.out.println(e);
                    }


                    /*
                    String httpResponse = "HTTP/1.1 200 OK\r\nError 418 I'm a teapot\r\n" + "Voilà ton fichier connard";
                    clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
                    */

                    System.out.println("Waiting new request ...");
                }
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
