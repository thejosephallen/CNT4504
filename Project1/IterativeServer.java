import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.lang.Process;
import java.lang.Runtime;

public class IterativeServer {
   public static void main(String[] args) throws IOException{
      if (args.length != 1){
         System.err.println("Usage: java Server <port number>");
         System.exit(1);
      }
      int portNumber = Integer.parseInt(args[0]);
      try{
         ServerSocket server = new ServerSocket(portNumber, 100);
         System.out.println("(Type CTRL+C to end the server program)");
         while(true){
            try (
               Socket client = server.accept();
               PrintWriter output = new PrintWriter(client.getOutputStream(), true);
               BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
            ){
               System.out.println("Connection successful with user: " + client.getInetAddress());
               String command = input.readLine();
   	         int menuOp = command.charAt(0);
   	         switch (menuOp){
   	            case 49://Host current Date and Time
   	               DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
   	               Calendar cal = Calendar.getInstance();
   	               String timestamp = dateFormat.format(cal.getTime());
   	               output.println(timestamp);
   	               System.out.println("Processed Date and Time request for user.");
                     break;
   	            case 50://Host uptime
                     Process uptimeProcess = Runtime.getRuntime().exec("uptime");
                     BufferedReader utreader = new BufferedReader(new InputStreamReader(uptimeProcess.getInputStream()));
                     String uptime = utreader.readLine();
                     output.println(uptime);
                     System.out.println("Processed uptime request for user.");
                     break;
   	            case 51://Host memory use
                     Runtime memory = Runtime.getRuntime();
                     output.println("Total memory: " + memory.maxMemory()/1000000.0 + " MB");
                     output.println("Free memory: " + memory.freeMemory()/1000000.0 + " MB"); 
                     output.println("Memory in use: " + (memory.maxMemory() - memory.freeMemory())/1000000.0 + " MB");
                     System.out.println("Processed memory use request for user.");
                     break;
   	            case 52://Host Netstat
                     Process netstatProcess = Runtime.getRuntime().exec("netstat -a");
                     BufferedReader netreader = new BufferedReader(new InputStreamReader(netstatProcess.getInputStream()));
                     String line;
                     while ((line = netreader.readLine()) != null){
                        output.println(line);
                     }
                     System.out.println("Processed netstat request for user");
                     break;
   	            case 53://Host current users
                     Process usersProcess = Runtime.getRuntime().exec("who");
                     BufferedReader usersReader = new BufferedReader(new InputStreamReader(usersProcess.getInputStream()));
                     String user;
                     while ((user = usersReader.readLine()) != null){
                        output.println(user);
                     }
                     System.out.println("Processed current users request for user.");
                     break;
   	            case 54://Host running processes
                     Process runProcess = Runtime.getRuntime().exec("ps -aux");
                     BufferedReader runReader = new BufferedReader (new InputStreamReader(runProcess.getInputStream()));
                     String run;
                     while ((run = runReader.readLine()) != null){
                        output.println(run);
                     }
                     System.out.println("Processed running processes request for user.");
                     break;
   	            case 55://Quit
                     break;
               }//end of switch statement for requests
               //close IO and the client socket
               System.out.println("Closing connection for user " + client.getInetAddress());
               input.close();
               output.close();
               client.close();
            } catch (IOException e) {
               System.out.println("Exception caught when handling a connection.");
               System.out.println(e.getMessage());
            }
            System.out.println("Waiting for next connection...");
         }//end of server loop
      } catch(IOException e) {
         System.out.println("Exception caught when trying to listen on port " + portNumber);
         System.out.println(e.getMessage());
      }
   }
}