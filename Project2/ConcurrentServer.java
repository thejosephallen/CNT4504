import java.net.*;
import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DateFormat;
import java.lang.Process;
import java.lang.Runtime;

class ServerThread implements Runnable{
   //IO and attributes for each thread
   Socket client;//needed?
   PrintWriter output;
   BufferedReader input;
   int request;
   String clientAddress;
   Process commandProcess;
   BufferedReader commandReader;
   String response;
   
   // Server Thread Constructor
   ServerThread(Socket client){
      try {
         this.client = client;
         this.output = new PrintWriter(client.getOutputStream(), true);
         this.input = new BufferedReader(new InputStreamReader(client.getInputStream()));
         this.clientAddress = client.getInetAddress().toString();
         System.out.println("Connection successful with user: " + this.clientAddress);
         (new Thread(this)).start();
      } catch (IOException e) {
         System.out.println("Exception caught when trying to set up IO.");
         System.out.println(e.getMessage());
      }
   }
   
   // What the threads do
   public void run(){
      try{
         // obtain and execute client's request
         request = Integer.parseInt(input.readLine());
         switch (request){
            case 1:// Host current Date and Time
               this.commandProcess = Runtime.getRuntime().exec("date");
               break;
            case 2:// Host uptime
               this.commandProcess = Runtime.getRuntime().exec("uptime");
               break;
            case 3:// Host memory use
               this.commandProcess = Runtime.getRuntime().exec("free -m");
               break;
            case 4:// Host Netstat
               this.commandProcess = Runtime.getRuntime().exec("netstat -a");
               break;
            case 5:// Host current users
               this.commandProcess = Runtime.getRuntime().exec("who");
               break;
            case 6:// Host running processes
               this.commandProcess = Runtime.getRuntime().exec("ps -aux");
               break;
            default:
         }
         
         // Send data to client
         this.commandReader = new BufferedReader(new InputStreamReader(commandProcess.getInputStream()));
         while ((response = commandReader.readLine()) != null){
            output.println(response);
         }
         
         // Close I/O and socket
         System.out.println("Closing connection with user: " + this.clientAddress);   
         this.input.close();
         this.output.close();
         this.client.close();
      }catch (IOException e){
         System.err.println("I/O error with the connection");
         System.exit(1);
      }   
   }
}

public class ConcurrentServer {
   public static void main(String[] args) throws IOException{
      if (args.length != 1){
         System.err.println("Usage: java Server <port number>");
         System.exit(1);
      }
      int portNumber = Integer.parseInt(args[0]);
      try{
         ServerSocket server = new ServerSocket(portNumber, 100);
         System.out.println("(Type CTRL+C to end the server program)");
         // Server loop
         while(true) new ServerThread(server.accept());
      } catch(IOException e) {
         System.out.println("Exception caught when trying to listen on port " + portNumber);
         System.out.println(e.getMessage());
      }
   }
}