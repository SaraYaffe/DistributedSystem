import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

public class Master_toClient extends Thread {
    Object finishedJobs_LOCK;
    ArrayList<Job> finishedJobs; //list of jobs specific to client,
                        // initialized when established connection & added to by Master_fromSlave threads
    ObjectOutputStream outputStream; //gets output stream as parameter in constructor;
                        // initialized when established connection
    String clientName;

    public Master_toClient(ObjectOutputStream outputStream, ArrayList<Job> finishedJobs, Object finishedJobs_LOCK, String clientName)
    {
        this.finishedJobs = finishedJobs;
        this.finishedJobs_LOCK = finishedJobs_LOCK;
        this.outputStream = outputStream;
        this.clientName = clientName;

    }

    @Override
    public void run() {
        try {
            Job sendingJob;
            //infinite loop sending jobs
            while (true) {
                //sleep if no jobs to send
                while (finishedJobs.isEmpty())
                {
                    sleep(5);
                }

                //get job to send, remove from list
                synchronized (finishedJobs_LOCK)
                {
                    sendingJob = finishedJobs.get(0);
                    finishedJobs.remove(sendingJob);
                }

                //send job
                System.out.println("Sending completed job " + sendingJob.getId() + " of type " + sendingJob.getJobType() +
                        " to Client " + clientName);
                outputStream.writeObject(sendingJob);
            }
        } catch (IOException e) {
            System.out.println(
                    "Exception caught when trying to send data on port " + " or listening for a connection");
            System.out.println(e.getMessage());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
