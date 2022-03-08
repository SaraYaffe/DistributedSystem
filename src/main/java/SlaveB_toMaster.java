import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;

public class SlaveB_toMaster extends Thread{
    private final ArrayList<Job> completedJobs;
    private final Object completedJobs_LOCK;

    public SlaveB_toMaster(ArrayList<Job> completedJobs, Object completedJobs_LOCK) {
        this.completedJobs = completedJobs;
        this.completedJobs_LOCK = completedJobs_LOCK;

    }

    public void run() {
    	String hostName = "127.0.0.1";
    	int portNumber = 40000;

        //connect to master
        try (
        		Socket socket = new Socket(hostName, portNumber);
                ObjectOutputStream sendCompletedJobs = new ObjectOutputStream(socket.getOutputStream())
        ) {
            Job completedJob;

            //infinite loop sending jobs to master from list
            while (true) {

                //sleep if no jobs to send
                while (completedJobs.isEmpty())
                {
                	try {
						sleep(5);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }

                //get job to send, remove from list
                synchronized (completedJobs_LOCK) {
                    completedJob = completedJobs.get(0);
                    completedJobs.remove(completedJob);
                }

                //send job
                System.out.println("Sending job " + completedJob.getId() + " to Master");
                sendCompletedJobs.writeObject(completedJob);
            }
        } catch (
                IOException e) {
            System.out.println(
                    "Exception caught when trying to send data on port " + portNumber + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
