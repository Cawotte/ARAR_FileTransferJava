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
                Socket clientSocket = serverSocket.accept();
                InputStreamReader inputReader = new InputStreamReader(clientSocket.getInputStream());
                BufferedReader reader = new BufferedReader(inputReader);

                String httpRequest = reader.readLine();

                String[] splitRequest = httpRequest.split(" ");

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

            }
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
}
