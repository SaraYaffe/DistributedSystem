import java.util.ArrayList;


public class SlaveA {
    public static void main(String[] args) throws InterruptedException {
        ArrayList<Job> pendingJobs = new ArrayList<>();
        Object pendingJobs_LOCK = new Object();
        ArrayList<Job> completedJobs = new ArrayList<>();
        Object completedJobs_LOCK = new Object();
        int optimalSleepTime = 2000;
        int nonOptimalSleepTime = 10000;
        SlaveType slaveType = SlaveType.A;

        //create threads to communicate with master
        Thread slave_fromMaster = new SlaveA_fromMaster(pendingJobs, pendingJobs_LOCK);
        Thread slave_toMaster = new SlaveA_toMaster(completedJobs, completedJobs_LOCK);

        slave_fromMaster.start();
        slave_toMaster.start();

        Job currJob;

        //infinite loop "completing" jobs (by sleeping)
        while (true) {
            //sleep if no jobs
            while (pendingJobs.isEmpty())
            {
            	Thread.sleep(5);
            }

            //get job
            synchronized (pendingJobs_LOCK)
            {
                currJob = pendingJobs.get(0);
                pendingJobs.remove(0);
            }

            //sleep as necessary
            if (currJob.getJobType().toString().equals(slaveType.toString())) {
                Thread.sleep(optimalSleepTime);
            } else {
                Thread.sleep(nonOptimalSleepTime);
            }

            System.out.println("Completed job " + currJob.getId());

            //add job to list of completed jobs (to be sent back to master)
            synchronized (completedJobs_LOCK)
            {
                completedJobs.add(currJob);
            }
        }
    }
}
