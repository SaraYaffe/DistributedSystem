import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Master_toSlaveA extends Thread {
    Object slaveAJobs_LOCK;
    ArrayList<Job> slaveAJobs;
    int portNumber = 23456;

    public Master_toSlaveA(ArrayList<Job> slaveAJobs, Object slaveAJobs_LOCK)
    {
        this.slaveAJobs = slaveAJobs;
        this.slaveAJobs_LOCK = slaveAJobs_LOCK;
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
                //sleep while no jobs to send
                while (slaveAJobs.isEmpty())
                {
                	try {
						this.sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }

                //get job, remove from list
                synchronized (slaveAJobs_LOCK)
                {
                    sendingJob = slaveAJobs.get(0);
                    slaveAJobs.remove(sendingJob);
                }

                System.out.println("Sending job " + sendingJob.getId() + " of type " + sendingJob.getJobType() +
                        " to Slave A");

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
