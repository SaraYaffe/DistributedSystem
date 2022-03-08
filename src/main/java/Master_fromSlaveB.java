import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Master_fromSlaveB extends Thread {
    Counter currTimeB;
    Object currTimeB_LOCK;
    ArrayList<String> clientList;
    ArrayList<ArrayList<Job>> completedJobs_byClient;
    ArrayList<Object> completedJobsLock;

    int portNumber = 40000;

    public Master_fromSlaveB(Counter currTimeB, Object currTimeB_LOCK, ArrayList<String> clientList,
                             ArrayList<ArrayList<Job>> completedJobs_byClient,
                             ArrayList<Object> completedJobsLocks)
    {
        this.currTimeB = currTimeB;
        this.currTimeB_LOCK = currTimeB_LOCK;
        this.clientList = clientList;
        this.completedJobs_byClient = completedJobs_byClient;
        this.completedJobsLock = completedJobsLocks;

    }

    @Override
    public void run() {
        //connect to slave
        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             Socket clientSocket = serverSocket.accept();
             ObjectInputStream readNewJobs =
                     new ObjectInputStream(clientSocket.getInputStream()))
        {
            //infinite loop accepting completed jobs to return to client
            while (true) {
                //get job
                Job completeJob = (Job) readNewJobs.readObject();
                System.out.println("Received completed job " + completeJob.getId() + " of type " + completeJob.getJobType() +
                        " from Slave B");

                //calculate new time for slave (subtract time for completed job)
                synchronized (currTimeB_LOCK)
                {
                    if (completeJob.getJobType() == JobType.B)
                    {
                        currTimeB.subtract(2);
                    }
                    else
                    {
                        currTimeB.subtract(10);
                    }
                }

                //loop through list of clients to see which to return job to. If client matches, add to
                //client's job list (same index in other array) (list is shared with Master_toClient thread)
                for (int i = 0; i < clientList.size(); i++)
                {
                    if (completeJob.getName().equals(clientList.get(i)))
                    {
                        synchronized (completedJobsLock.get(i))
                        {
                            completedJobs_byClient.get(i).add(completeJob);
                        }
                    }
                }
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to listen on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
