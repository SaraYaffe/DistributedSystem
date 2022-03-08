import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Master_fromClient_receiveData extends Thread{

    Object pendingJobs_LOCK;
    ArrayList<Job> pendingJobs;
    ObjectInputStream inputStream;

    public Master_fromClient_receiveData(ObjectInputStream inputStream, Object pendingJobs_LOCK, ArrayList<Job> pendingJobs) {
        this.pendingJobs_LOCK = pendingJobs_LOCK;
        this.pendingJobs = pendingJobs;
        this.inputStream = inputStream;
    }

    public void run() {
        //infinite loop accepting new jobs from client
        try {
            while (true) {
                //read job
                Job newJob = (Job) inputStream.readObject();

                //add to list of jobs to be processed by master
                synchronized (pendingJobs_LOCK) {
                    pendingJobs.add(newJob);
                }

                System.out.println("Received Job " + newJob.getId() + " of type " + newJob.getJobType()
                        + " from client " + newJob.getName());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port"
                    + " or listening for a connection");
            e.printStackTrace();
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}