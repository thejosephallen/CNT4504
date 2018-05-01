

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
         this.reqNum = i;
         this.reqTime = 0;
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
      long startTime = System.currentTimeMillis();
      out.println(this.option); //menu option sent to server
      //Recieve the server's response
      try{
         while ((response = in.readLine()) != null){
            //Uncomment the below print statement to view the data on your screen
            response = null;
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
      int clients;
      //array for the client threads
      ClientThread[] threads = new ClientThread[100];

      do{
         //menu
         System.out.println("Enter one of the following commands:");
         System.out.println("1 - Host Current Date/Time");
         System.out.println("2 - Host Uptime");
         System.out.println("3 - Host Memory Use");
         System.out.println("4 - Host Netstat");
         System.out.println("5 - Host Current Users");
         System.out.println("6 - Host Running Processes");
         System.out.println("7 - Exit");

         menuOp = sc.next().charAt(0);
         while (menuOp < 49 || menuOp > 55){
            System.out.println("Please enter a number between 1 - 7");
            menuOp = sc.next().charAt(0);
         }
         clients = 1;

         switch (menuOp){
            case 49: //1, ASCII Value
               System.out.println("How many clients are you testing for?");
               clients = sc.nextInt();
               menuOp = 1;
               break;
            case 50: //2
               menuOp = 2;
               break;
            case 51: //3
               menuOp = 3;
               break;
            case 52: //4
               System.out.println("How many clients are you testing for?");
               clients = sc.nextInt();
               menuOp = 4;
               break;
            case 53: //5
               menuOp = 5;
               break;
            case 54: //6
               menuOp = 6;
               break;
            case 55: //7
               System.out.println("Exiting Program");
               menuOp = 7;
               break;
            default:
               System.out.println("INPUT ERROR: Your input must be a value between 1 - 7.\n");
         }//end switch
         System.out.println("menuOp " + menuOp);
         System.out.println("Clients " + clients);
         
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
         double avgTime = sumOfTimes / (double)clients;
         ClientThread.times.clear();
         System.out.println("Average time of response = " + avgTime + "ms\n");
      }while (menuOp != 7);
      System.exit(1);
   }//end main
}