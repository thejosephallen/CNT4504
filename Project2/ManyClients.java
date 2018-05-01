import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

class ClientThread extends Thread{
   //IO and attributes for each thread
   Socket clientSocket;
   PrintWriter out;
   BufferedReader in;
   int option;
   int reqNum;
   
   //Time attributes
   static ArrayList<Long> times = new ArrayList<Long>();
   long reqTime;
   
   //Client Thread Constuctor   
   ClientThread(String hostName, int portNumber, int option, int i){
      try{
         this.clientSocket = new Socket(hostName, portNumber);
         this.out = new PrintWriter(clientSocket.getOutputStream(), true);
         this.in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
         this.option = option;
         this.reqTime = 0;
         this.reqNum = i;
      }catch (UnknownHostException e){
         System.err.println("Dont know about host " + hostName);
         System.exit(1);
      }catch (IOException e){
         System.err.println("Couldn't get I/O for the connection to " + hostName);
         System.exit(1);
      }
   }
   
   //The code that the threads execute when started
   public void run(){
      String response;
      int linesRead = 0;
      long startTime = System.currentTimeMillis();
      out.println(this.option); //menu option sent to server
      //Recieve the server's response
      try{
         while ((response = in.readLine()) != null){
            response = null;
            linesRead++;
            //Uncomment the below print statement to print to screen
   	      //System.out.println(response); //reading from socket
         }
         this.in.close();
         this.out.close();
         this.clientSocket.close();
      }catch (IOException e){
         System.err.println("I/O error with the connection");
         System.exit(1);
      }
      //calculate the time it took and add it to the times list
      this.reqTime = (System.currentTimeMillis() - startTime);
      times.add(this.reqTime);
      //System.out.println("Request number: "+this.reqNum+" took: "+reqTime+" ms");
   }
}

public class ManyClients{
   public static void main(String[] args) throws IOException{
      //check that necessary info is provided
      if (args.length != 2){
         System.out.println("Must use format: 'java myClient (Hostname) (Port Number)'\nExiting Program.....");
         System.exit(1);
      }
      String hostName = args[0];
      int portNumber = Integer.parseInt(args[1]);
      
      Scanner sc = new Scanner(System.in);
      int menuOp;
      int clients = 1;
      ClientThread[] threads = new ClientThread[100];          //array for the client threads

      // Print the menu
      System.out.println("Enter one of the following commands:");
      System.out.println("1 - Host Current Date/Time");
      System.out.println("2 - Host Uptime");
      System.out.println("3 - Host Memory Use");
      System.out.println("4 - Host Netstat");
      System.out.println("5 - Host Current Users");
      System.out.println("6 - Host Running Processes");
      System.out.println("7 - Exit");

      while(true){

         // Get the menu choice
         while (true){
            try{
               System.out.print("Command: ");
               menuOp = Integer.parseInt(sc.next());
            }catch(NumberFormatException nfe){
               System.out.println("Please enter a number between 1 and 7");
               continue;
            }
            if (menuOp < 1 || menuOp > 7)
               System.out.println("Please enter a number between 1 and 7");
            else break;
         }
         
         // Prompt for number of clients for commands 1 and 4; default = 1
         clients = 1;
         if (menuOp == 1 || menuOp == 4){
            System.out.println("How many clients are you testing for? (1 - 100)");
            while (true) {
               try{
                  System.out.print("Clients: ");
                  clients = Integer.parseInt(sc.next());
               }catch (NumberFormatException nfe){
                  System.out.println("Please enter a number between 1 and 100");
                  continue;
               }
               if (clients < 1 || clients > 100)
                  System.out.println("Please enter a number between 1 and 100");
               else break;
            }
         }
         
         // Check if user wants to quit
         if (menuOp == 7){
            System.out.println("Exiting program");
            System.exit(1);
         }
         
         System.out.println("Command: " + menuOp);
         System.out.println("Number of clients: " + clients);
         
         //create the threads
         for (int i = 0;i < clients;i++){
            threads[i] = new ClientThread(hostName, portNumber, menuOp, i);
         }
        
         //start the threads
         for (int j = 0;j < clients;j++){
            threads[j].start();
         }
        
         //join after all threads have started so that the program waits for all of them to finish
         for (int k = 0; k < clients; k++){
            try{
               threads[k].join();
            }catch (InterruptedException ie){
               System.out.println(ie.getMessage());
            }
         }
        
         //calculate the average server response time
         long sumOfTimes = 0;
         for(long x: ClientThread.times){
            sumOfTimes += x;
         }
         //System.out.println("Sum of times: "+sumOfTimes+" ms");
         double avgTime = sumOfTimes / (double)clients;
         ClientThread.times.clear();
         System.out.println("Average time of response = " + avgTime + "ms\n");
      }
   }//end main
}