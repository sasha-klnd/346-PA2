/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.rmi.server.ServerCloneException;

import javax.sql.rowset.serial.SerialArray;

/**
 *
 * @author Kerly Titus
 */
public class Driver {

    /** 
     * main class
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        Network objNetwork = new Network( );            /* Activate the network */
        Client objClient1 = new Client("sending");          /* Start the sending client thread */
        Client objClient2 = new Client("receiving");        /* Start the receiving client thread */
        Server objServer = new Server("0001");
        objNetwork.start();
        objClient1.start();
        objClient2.start();
        objServer.start();

        try {
            objNetwork.join();
            objClient1.join();
            objClient2.join();
            objServer.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
      }
      /*..............................................................................................................................................................*/
       
    }
    
 }
