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
        Server objServer1 = new Server("0001");
        Server objServer2 = new Server("0002");
        objNetwork.start();
        objClient1.start();
        objClient2.start();
        objServer1.start();
        objServer2.start();

        try {
            objNetwork.join();
            objClient1.join();
            objClient2.join();
            objServer1.join();
            objServer2.join();
        } catch (Exception e) {
            System.out.println(e.getMessage());
      }
      /*..............................................................................................................................................................*/

    }

 }
