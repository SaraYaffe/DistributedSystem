import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Master_fromClient_establishConnection extends Thread {
    ArrayList<Job> pendingJobs;
    Object pendingJobs_LOCK;
    int serverPortNumber = 12345, portNumber = 12347;
    ArrayList<String> clientList;
    ArrayList<ArrayList<Job>> completedJobs_byClient;
    ArrayList<Object> completedJobsLocks;

    public Master_fromClient_establishConnection(ArrayList<String> clientList, ArrayList<ArrayList<Job>> completedJobs_byClient,
                                                 ArrayList<Object> completedJobsLocks, ArrayList<Job> pendingJobs, Object pendingJobs_LOCK) {
        this.clientList = clientList;
        this.completedJobs_byClient = completedJobs_byClient;
        this.completedJobsLocks = completedJobsLocks;
        this.pendingJobs = pendingJobs;
        this.pendingJobs_LOCK = pendingJobs_LOCK;
    }

    @Override
    public void run() {
            try (ServerSocket serverSocket = new ServerSocket(serverPortNumber)
            ){
                //infinite loop accepting clients
                while (true) {
                    //create client socket for new client
                    Socket socket = serverSocket.accept();
                    ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());

                    //get client name, store client's name, list of jobs, and lock for that list in 2d arrays
                    String clientName = (String) inStream.readObject();
                    clientList.add(clientName);
                    ArrayList<Job> clientJobs = new ArrayList<>();
                    completedJobs_byClient.add(clientJobs);
                    Object jobsLock = new Object();
                    completedJobsLocks.add(jobsLock);

                    //start threads to send to client and receive from client
                    Thread newToClient = new Master_toClient(outStream, clientJobs, jobsLock, clientName);
                    newToClient.start();
                    Thread newFromClient = new Master_fromClient_receiveData(inStream, pendingJobs_LOCK, pendingJobs);
                    newFromClient.start();

                }
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("Exception caught when trying to listen on port " + serverPortNumber
                        + " or listening for a connection");
                System.out.println(e.getMessage());
                System.exit(1);
            }
        }

    }
