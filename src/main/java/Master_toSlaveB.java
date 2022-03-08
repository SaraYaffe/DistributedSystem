import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Master_toSlaveB extends Thread {
    Object slaveBJobs_LOCK;
    ArrayList<Job> slaveBJobs;
    int portNumber = 34567;

    public Master_toSlaveB(ArrayList<Job> slaveBJobs, Object slaveBJobs_LOCK)
    {
        this.slaveBJobs = slaveBJobs;
        this.slaveBJobs_LOCK = slaveBJobs_LOCK;
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(portNumber);
             Socket clientSocket = serverSocket.accept();
             ObjectOutputStream sendNewJobs =
                     new ObjectOutputStream(clientSocket.getOutputStream());
        ) {
            Job sendingJob;
            //infinite loop sending jobs to slave
            while (true) {
                //sleep while no jobs
                while (slaveBJobs.isEmpty())
                {
                	try {
						this.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }

                //get job, remove from list
                synchronized (slaveBJobs_LOCK)
                {
                    sendingJob = slaveBJobs.get(0);
                    slaveBJobs.remove(sendingJob);
                }

                System.out.println("Sending job " + sendingJob.getId() + " of type " + sendingJob.getJobType() +
                        " to Slave B");

                //send job
                sendNewJobs.writeObject(sendingJob);
            }

        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to send data on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
