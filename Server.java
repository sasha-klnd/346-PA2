import java.util.Scanner;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.io.FileWriter;
import java.io.IOException;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*package comp546pa1w2020;*/

/**
 * Server class
 *
 * @author Kerly Titus
 */

public class Server extends Thread {

    /* NEW : Shared member variables are now static for the 2 receiving threads */
    private static int numberOfTransactions; /* Number of transactions handled by the server */
    private static int numberOfAccounts; /* Number of accounts stored in the server */
    private static int maxNbAccounts; /* maximum number of transactions */
    private static Accounts[] account; /* Accounts to be accessed or updated */
    /*
     * NEW : member variabes to be used in PA2 with appropriate accessor and mutator
     * methods
     */
    private String serverThreadId; /* Identification of the two server threads - Thread1, Thread2 */
    private static String serverThreadRunningStatus1; /* Running status of thread 1 - idle, running, terminated */
    private static String serverThreadRunningStatus2; /* Running status of thread 2 - idle, running, terminated */

    /**
     * Constructor method of Client class
     *
     * @return
     * @param stid
     */
    Server(String stid) {
        if (!(Network.getServerConnectionStatus().equals("connected"))) {
            System.out.println("\n Initializing the server ...");
            numberOfTransactions = 0;
            numberOfAccounts = 0;
            maxNbAccounts = 100;
            serverThreadId = stid; /* unshared variable so each thread has its own copy */
            serverThreadRunningStatus1 = "idle";
            account = new Accounts[maxNbAccounts];
            System.out.println("\n Inializing the Accounts database ...");
            initializeAccounts();
            System.out.println("\n Connecting server to network ...");
            if (!(Network.connect(Network.getServerIP()))) {
                System.out.println("\n Terminating server application, network unavailable");
                System.exit(0);
            }
        } else {
            serverThreadId = stid; /* unshared variable so each thread has its own copy */
            serverThreadRunningStatus2 = "idle";
            System.out.println("\n Server " + getServerThreadId() + " idle");

        }
    }

    /**
     * Accessor method of Server class
     *
     * @return numberOfTransactions
     * @param
     */
    public int getNumberOfTransactions() {
        return numberOfTransactions;
    }

    /**
     * Mutator method of Server class
     *
     * @return
     * @param nbOfTrans
     */
    public void setNumberOfTransactions(int nbOfTrans) {
        numberOfTransactions = nbOfTrans;
    }

    /**
     * Accessor method of Server class
     *
     * @return numberOfAccounts
     * @param
     */
    public int getNumberOfAccounts() {
        return numberOfAccounts;
    }

    /**
     * Mutator method of Server class
     *
     * @return
     * @param nbOfAcc
     */
    public void setNumberOfAccounts(int nbOfAcc) {
        numberOfAccounts = nbOfAcc;
    }

    /**
     * Accessor method of Server class
     *
     * @return maxNbAccounts
     * @param
     */
    public int getMxNbAccounts() {
        return maxNbAccounts;
    }

    /**
     * Mutator method of Server class
     *
     * @return
     * @param nbOfAcc
     */
    public void setMaxNbAccounts(int nbOfAcc) {
        maxNbAccounts = nbOfAcc;
    }

    /**
     * Accessor method of Server class
     *
     * @return serverThreadId
     * @param
     */
    public String getServerThreadId() {
        return serverThreadId;
    }

    /**
     * Mutator method of Server class
     *
     * @return
     * @param tId
     */
    public void setServerThreadId(String stid) {
        serverThreadId = stid;
    }

    /**
     * Accessor method of Server class
     *
     * @return serverThreadRunningStatus1
     * @param
     */
    public String getServerThreadRunningStatus1() {
        return serverThreadRunningStatus1;
    }

    /**
     * Mutator method of Server class
     *
     * @return
     * @param runningStatus
     */
    public void setServerThreadRunningStatus1(String runningStatus) {
        serverThreadRunningStatus1 = runningStatus;
    }

    /**
     * Accessor method of Server class
     *
     * @return serverThreadRunningStatus2
     * @param
     */
    public String getServerThreadRunningStatus2() {
        return serverThreadRunningStatus2;
    }

    /**
     * Mutator method of Server class
     *
     * @return
     * @param runningStatus
     */
    public void setServerThreadRunningStatus2(String runningStatus) {
        serverThreadRunningStatus2 = runningStatus;
    }

    /**
     * Initialization of the accounts from an input file
     *
     * @return
     * @param
     */
    public void initializeAccounts() {
        Scanner inputStream = null; /* accounts input file stream */
        int i = 0; /* index of accounts array */

        try {
            inputStream = new Scanner(new FileInputStream("account.txt"));
        } catch (FileNotFoundException e) {
            System.out.println("File account.txt was not found");
            System.out.println("or could not be opened.");
            System.exit(0);
        }
        while (inputStream.hasNextLine()) {
            try {
                account[i] = new Accounts();
                account[i].setAccountNumber(inputStream.next()); /* Read account number */
                account[i].setAccountType(inputStream.next()); /* Read account type */
                account[i].setFirstName(inputStream.next()); /* Read first name */
                account[i].setLastName(inputStream.next()); /* Read last name */
                account[i].setBalance(inputStream.nextDouble()); /* Read account balance */
            } catch (InputMismatchException e) {
                System.out.println("Line " + i + "file account.txt invalid input");
                System.exit(0);
            }
            i++;
        }
        setNumberOfAccounts(i); /* Record the number of accounts processed */

        // System.out.println("\n DEBUG : Server.initializeAccounts() " + getNumberOfAccounts() + " accounts processed");

        inputStream.close();
    }

    /**
     * Find and return the index position of an account
     *
     * @return account index position or -1
     * @param accNumber
     */
    public int findAccount(String accNumber) {
        int i = 0;

        /* Find account */
        while (!(account[i].getAccountNumber().equals(accNumber)))
            i++;
        if (i == getNumberOfAccounts())
            return -1;
        else
            return i;
    }

    /**
     * Processing of the transactions
     *
     * @return
     * @param trans
     */
    public boolean processTransactions(Transactions trans) {
        int accIndex; /* Index position of account to update */
        double newBalance; /* Updated account balance */


        // System.out.println("\n DEBUG : Server.processTransactions() " + getServerThreadId() );


        /* Process the accounts until the client disconnects */
        while ((!Network.getClientConnectionStatus().equals("disconnected"))) {
            // Busy-waiting
            /* while ( (Network.getInBufferStatus().equals("empty") && !Network.getClientConnectionStatus().equals("disconnected")) ) {
                if (Network.getClientConnectionStatus().equals("disconnected"))
                    return true;
                Thread.yield();
            } */

            if (!Network.getInBufferStatus().equals("empty")) {
                    // System.out.println("\n DEBUG : Server.processTransactions() - transferring in account " + trans.getAccountNumber());

                    Network.transferIn(trans); /* Transfer a transaction from the network input buffer */

                    accIndex = findAccount(trans.getAccountNumber());
                    synchronized(account[accIndex]) {
                        /* Process deposit operation */
                        if (trans.getOperationType().equals("DEPOSIT")) {
                                newBalance = deposit(accIndex, trans.getTransactionAmount());
                                trans.setTransactionBalance(newBalance);
                                trans.setTransactionStatus("done");
                                // System.out.println("\n DEBUG : Server.processTransactions() - Deposit of " + trans.getTransactionAmount() + " in account " + trans.getAccountNumber());

                        } else
                        /* Process withdraw operation */
                        if (trans.getOperationType().equals("WITHDRAW")) {
                                newBalance = withdraw(accIndex, trans.getTransactionAmount());
                                trans.setTransactionBalance(newBalance);
                                trans.setTransactionStatus("done");
                                // System.out.println("\n DEBUG : Server.processTransactions() - Withdrawal of " + trans.getTransactionAmount() + " from account " + trans.getAccountNumber());

                        } else
                        /* Process query operation */
                        if (trans.getOperationType().equals("QUERY")) {
                            newBalance = query(accIndex);
                            trans.setTransactionBalance(newBalance);
                            trans.setTransactionStatus("done");
                            // System.out.println("\n DEBUG : Server.processTransactions() - Obtaining balance from account" + trans.getAccountNumber());
                        }

                        // Busy-waiting
                       /*  while (Network.getOutBufferStatus().equals("full")) {
                            if (Network.getClientConnectionStatus().equals("disconnected"))
                                return true;
                            Thread.yield();
                        } */

                        /*
                        * System.out.
                        * println("\n DEBUG : Server.processTransactions() - transferring out account "
                        * + trans.getAccountNumber());
                        */

                        try {
                            Network.transferOut(trans); /*
                                                         * Transfer a completed transaction from the server to the network output
                                                         * buffer
                                                         */
                        } catch (InterruptedException e) {
                            System.out.println("Thread interrupted during Network.transferOut: " + e.getMessage());
                            Thread.currentThread().interrupt(); // Preserve interrupt status
                        }
                        setNumberOfTransactions((getNumberOfTransactions() + 1)); /*
                                                                                * Count the number of transactions processed
                                                                                */
                    }
            }
        }

        // System.out.println("\n DEBUG : Server.processTransactions() - " + getNumberOfTransactions() + " accounts updated");

        return true;
    }

    /**
     * Processing of a deposit operation in an account
     *
     * @return balance
     * @param i, amount
     */
    public double deposit(int i, double amount) {
        double curBalance; /* Current account balance */

        curBalance = account[i].getBalance(); /* Get current account balance */

        /*
         * NEW : A server thread is blocked before updating the 10th , 20th, ... 70th
         * account balance in order to simulate an inconsistency situation
         */
        /* if (((i + 1) % 10) == 0) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
        } */

        /* System.out.println("\n DEBUG : Server.deposit - " + "i " + i + " Current balance " + curBalance + " Amount "
                + amount + " " + getServerThreadId()); */

        account[i].setBalance(curBalance + amount); /* Deposit amount in the account */
        return account[i].getBalance(); /* Return updated account balance */
    }

    /**
     * Processing of a withdrawal operation in an account
     *
     * @return balance
     * @param i, amount
     */
    public double withdraw(int i, double amount) {
        double curBalance; /* Current account balance */

        curBalance = account[i].getBalance(); /* Get current account balance */

        /* System.out.println("\n DEBUG : Server.withdraw - " + "i " + i + " Current balance " + curBalance + " Amount "
                + amount + " " + getServerThreadId()); */

        account[i].setBalance(curBalance - amount); /* Withdraw amount in the account */
        return account[i].getBalance(); /* Return updated account balance */

    }

    /**
     * Processing of a query operation in an account
     *
     * @return balance
     * @param i
     */
    public double query(int i) {
        double curBalance; /* Current account balance */

        curBalance = account[i].getBalance(); /* Get current account balance */

        // System.out.println("\n DEBUG : Server.query - " + "i " + i + " Current balance " + curBalance + " " + getServerThreadId());

        return curBalance; /* Return current account balance */
    }

    /**
     * Create a String representation based on the Server Object
     *
     * @return String representation
     */
    public String toString() {
        return ("\n server IP " + Network.getServerIP() + "connection status " + Network.getServerConnectionStatus()
                + "Number of accounts " + getNumberOfAccounts());
    }

    /**
     * Code for the run method
     *
     * @return
     * @param
     */
    public void run() {

        Boolean server1Running = false;
        Boolean server2Running = false;
        Boolean server3Running = false;
        Transactions trans = new Transactions();

        if (getServerThreadId().equals("0001")) {
            server1Running = true;
            long serverStartTime1, serverEndTime1;

            // System.out.println("\n DEBUG : Server.run() - starting server thread " + Network.getServerConnectionStatus());

            serverStartTime1 = System.currentTimeMillis();
            processTransactions(trans);
            serverEndTime1 = System.currentTimeMillis();
            server1Running = false;
            System.out.println("\nTerminating server thread 0001 - " + " Running time " + (serverEndTime1 - serverStartTime1) + " milliseconds");
        }

        else if (getServerThreadId().equals("0002")) {
            server2Running = true;
            long serverStartTime2, serverEndTime2;

            // System.out.println("\n DEBUG : Server.run() - starting server thread " + Network.getServerConnectionStatus());

            serverStartTime2 = System.currentTimeMillis();
            processTransactions(trans);
            serverEndTime2 = System.currentTimeMillis();
            server2Running = false;
            System.out.println("\nTerminating server thread 0002 - " + " Running time " + (serverEndTime2 - serverStartTime2) + " milliseconds");
        }

       /* else  if (getServerThreadId().equals("0003")) {
            server3Running = true;
            long serverStartTime3, serverEndTime3;

            // System.out.println("\n DEBUG : Server.run() - starting server thread " + Network.getServerConnectionStatus());

            serverStartTime3 = System.currentTimeMillis();
            processTransactions(trans);
            serverEndTime3 = System.currentTimeMillis();
            server3Running = false;
            System.out.println("\nTerminating server thread 0003 - " + " Running time " + (serverEndTime3 - serverStartTime3) + " milliseconds");
        } */

        while (true) {
            if (server1Running || server2Running || server3Running) {
                Thread.yield();
            }
            else
                break;
        }

        // System.out.println(" DEBUG : Both server threads finished execution, disconnecting from network");
        Network.disconnect(Network.getServerIP());
    }
}