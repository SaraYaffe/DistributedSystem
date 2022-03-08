

import java.util.ArrayList;

public class Master {

    public static void main(String[] args)
    {
        ArrayList<Job> pendingJobs = new ArrayList<>();
        Object pendingJobs_LOCK = new Object();
        ArrayList<Job> slaveAJobs = new ArrayList<>();
        Object slaveAJobs_LOCK = new Object();
        ArrayList<Job> slaveBJobs = new ArrayList<>();
        Object slaveBJobs_LOCK = new Object();
        Counter currTimeA = new Counter();
        Object currTimeA_LOCK = new Object();
        Counter currTimeB = new Counter();
        Object currTimeB_LOCK = new Object();
        ArrayList<String> clientList = new ArrayList<>();
        ArrayList<ArrayList<Job>> completedJobs_byClient = new ArrayList<>();
        ArrayList<Object> completedJobsLocks = new ArrayList<>();

        //create all threads
        Thread establishConnection = new Master_fromClient_establishConnection(clientList, completedJobs_byClient, completedJobsLocks, pendingJobs, pendingJobs_LOCK);
        Thread master_toSlaveA = new Master_toSlaveA(slaveAJobs, slaveAJobs_LOCK);
        Thread master_fromSlaveA = new Master_fromSlaveA(currTimeA,currTimeA_LOCK, clientList, completedJobs_byClient,
                completedJobsLocks);
        Thread master_toSlaveB = new Master_toSlaveB(slaveBJobs, slaveBJobs_LOCK);
        Thread master_fromSlaveB = new Master_fromSlaveB(currTimeB,currTimeB_LOCK, clientList, completedJobs_byClient,
                completedJobsLocks);

        //start all threads
        establishConnection.start();
        master_fromSlaveA.start();
        master_toSlaveA.start();
        master_fromSlaveB.start();
        master_toSlaveB.start();

        Job currJob;
        JobType type;

        //infinite loop processing jobs from list (jobs added by Master_fromClient_receiveData)
        while (true) {
            //sleep while no jobs to process
            while (pendingJobs.isEmpty())
            {
            	try {
					Thread.sleep(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
            }

            //get info of current job to process
            currJob = pendingJobs.get(0);
            type = currJob.getJobType();

            //load balancing, figure out which slave to send it to and add to list
            synchronized (currTimeA_LOCK) {
                synchronized (currTimeB_LOCK) {
                    if (type == JobType.A) {
                        if (currTimeA.getCurrentValue() + 2 <= 10 + currTimeB.getCurrentValue()) {
                            synchronized (slaveAJobs_LOCK) {
                                slaveAJobs.add(currJob);
                            }
                            currTimeA.add(2);
                            System.out.println("Assigning job " + currJob.getId() + "to slave A");
                        } else {
                            synchronized (slaveBJobs_LOCK) {
                                slaveBJobs.add(currJob);
                            }
                            currTimeB.add(10);
                            System.out.println("Assigning job " + currJob.getId() + "to slave B");
                        }
                    } else {
                        if (currTimeB.getCurrentValue() + 2 <= 10 + currTimeA.getCurrentValue()) {
                            synchronized (slaveBJobs_LOCK) {
                                slaveBJobs.add(currJob);
                            }
                            currTimeB.add(2);
                            System.out.println("Assigning job " + currJob.getId() + " to slave B");
                        } else {
                            synchronized (slaveAJobs_LOCK) {
                                slaveAJobs.add(currJob);
                            }
                            currTimeA.add(10);
                            System.out.println("Assigning job " + currJob.getId() + " to slave A");
                        }
                    }
                    synchronized (pendingJobs_LOCK) {
                        pendingJobs.remove(0);
                    }
                }
            }
        }
    }
}
