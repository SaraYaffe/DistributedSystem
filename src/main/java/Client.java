import java.util.ArrayList;
import java.util.Scanner;

public class Client {
    public static void main(String[] args) {

        String clientName;
        ArrayList<Job> newJobList = new ArrayList<>();
        ArrayList<Job> currJobList = new ArrayList<>();
        ArrayList<Job> finishedJobList = new ArrayList<>();
        Object newJob_LOCK = new Object();
        Object currJob_LOCK = new Object();
        Object finish_LOCK = new Object();

        //get client name
        Scanner in = new Scanner(System.in);
        System.out.println("Enter Client Name: ");
        clientName = in.next();
        in.nextLine();

        //set up thread to master
        Thread ClientToMaster = new Client_toMaster(clientName, newJobList, newJob_LOCK, currJobList, currJob_LOCK,
                finishedJobList, finish_LOCK);
        ClientToMaster.start();

        //infinite loop taking new jobs & adding to list that thread will send
        while(true) {
        Job newJob;
        JobType jobType;
        
        //ask for job ID
        System.out.println("Enter job ID: ");
        String ID = in.next();
        in.nextLine();

        //get job type (validate it is 'a' or 'b')
        String jobT = "C";
        while (!jobT.equalsIgnoreCase("A") && !jobT.equalsIgnoreCase("B")) {
            System.out.println("Enter job type (A or B): ");
            jobT = in.next();
            in.nextLine();
        }

        if (jobT.equalsIgnoreCase("A")) {
            jobType = JobType.A;
        } else {
            jobType = JobType.B;
        }

        //create job
        newJob = new Job(jobType, ID, clientName);

        System.out.println("New job from client " + clientName + ": " + ID);

        //adds job to list of new jobs
        synchronized (newJob_LOCK) {
            newJobList.add(newJob);
        }
    }
    }


}
