import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class Server
{
    public static void main(String args[])
    {
        try
        {
            ServerSocket serverSocket = new ServerSocket(80);
            while(true)
            {
                System.out.println("Waiting new Request ...");

                //Reception de la requête
                Socket clientSocket = serverSocket.accept();
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
                }

                String line = reader.readLine();

                while(!line.isEmpty())
                {
                    line = reader.readLine();
                }

                String httpResponse = "HTTP/1.1 200 OK\r\n\r\n" + "Reçu Reçu";
                clientSocket.getOutputStream().write(httpResponse.getBytes("UTF-8"));
            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
