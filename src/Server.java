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


            Socket clientSocket = serverSocket.accept();
            System.out.println("New connection accepted " + clientSocket.getInetAddress() + ":" + clientSocket.getPort());
            while(true)
            {
                //Reception de la requête
                InputStreamReader inputReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(inputReader);

                //on récupère la première ligne c'est celle qui contient la méthode
                String httpRequest = reader.readLine();

                //On la sépare pour analyser la méthode
                String[] splitRequest = httpRequest.split(" ");

                //Si c'est un GET
                if(splitRequest[0].equals("GET"))
                {
                    System.out.println("C'est un GET");
                    System.out.println("Il veut " + splitRequest[1]);

                    try {
                        File fichier = new File(splitRequest[1]);

                        if(fichier.canRead())
                        {
                            byte [] mybytearray  = new byte [(int)fichier.length()];

                            FileInputStream fis = new FileInputStream(fichier);
                            BufferedInputStream bis = new BufferedInputStream(fis);

                            bis.read(mybytearray, 0, mybytearray.length);

                            OutputStream os = clientSocket.getOutputStream();

                            System.out.println("Sending " + splitRequest[1] + "(" + mybytearray.length + " bytes)");
                            os.write(mybytearray, 0, mybytearray.length);
                            os.flush();

                            System.out.println("Done.");
                        }
                        else
                        {
                            String httpResponse = "HTTP/1.1 418 Error\r\nError 418 I'm a teapot\r\n" + "Fichier introuvable connard";
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
