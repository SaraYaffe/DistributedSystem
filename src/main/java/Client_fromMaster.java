import java.io.ObjectInputStream;
import java.util.ArrayList;

public class Client_fromMaster extends Thread {

    ObjectInputStream inputStream;    //gets input stream as parameter in constructor;
                                        // initialized when established connection
    private ArrayList<Job> currJobList;
    private ArrayList<Job> finishedJobList;
    private Object currJob_LOCK;
    private Object finish_LOCK;

    public Client_fromMaster(ObjectInputStream inputStream, ArrayList<Job> currJobList, ArrayList<Job> finishedJobList, Object currJob_LOCK,
                             Object finish_LOCK) {
        this.inputStream = inputStream;
        this.currJobList = currJobList;
        this.finishedJobList = finishedJobList;
        this.currJob_LOCK = currJob_LOCK;
        this.finish_LOCK = finish_LOCK;
    }

    @Override
    public void run() {
        try {
            //infinite loop receiving jobs
            while (true) {
                //read job
                Job doneJob = (Job) inputStream.readObject();

                //remove job from list of current jobs
                synchronized (currJob_LOCK) {
                    currJobList.remove(doneJob);
                }

                //add job to list of completed jobs
                synchronized (finish_LOCK) {
                    finishedJobList.add(doneJob);
                }
                System.out.println("Received completed Job " + doneJob.getId() + " of type " + doneJob.getJobType() + " from Master");
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.exit(1);
        }
    }
}