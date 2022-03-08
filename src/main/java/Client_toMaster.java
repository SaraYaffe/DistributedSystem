import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

public class Client_toMaster extends Thread {

    private final String clientName;
    private final ArrayList<Job> newJobList;
    private final ArrayList<Job> currJobList;
    private final ArrayList<Job> finishedJobList;
    private final Object newJob_LOCK;
    private final Object finish_LOCK;
    private final Object currJob_LOCK;

    public Client_toMaster(String name, ArrayList<Job> newJobList, Object newJob_LOCK, ArrayList<Job> currJobList,
                           Object currJob_LOCK, ArrayList<Job> finishedJobList, Object finish_LOCK) {
        this.clientName = name;
        this.newJobList = newJobList;
        this.newJob_LOCK = newJob_LOCK;
        this.currJobList = currJobList;
        this.currJob_LOCK = currJob_LOCK;
        this.finishedJobList = finishedJobList;
        this.finish_LOCK = finish_LOCK;
    }

    @Override
    public void run() {
        String hostName = "127.0.0.1";
        int portNumber = 12345;
        try(Socket socket = new Socket(hostName, portNumber);
            ObjectOutputStream outStream = new ObjectOutputStream(socket.getOutputStream());
            ObjectInputStream inStream = new ObjectInputStream(socket.getInputStream())
        ){
            //send client name to master
            outStream.writeObject(clientName);
            //create thread to read finished jobs sent by master
            Thread ClientFromMaster = new Client_fromMaster(inStream, currJobList, finishedJobList, currJob_LOCK, finish_LOCK);
            ClientFromMaster.start();

            //infinite loop sending jobs on list
            while (true) {
                Job sendingJob;

                //sleep if no jobs to send
                while (newJobList.isEmpty())
                {
                    sleep(5);
                }

                //get job to send & remove from list
                synchronized (newJob_LOCK) {
                    sendingJob = newJobList.get(0);
                    newJobList.remove(sendingJob);
                }

                //send
                outStream.writeObject(sendingJob);

                //add job to list of jobs currently by master
                synchronized (currJob_LOCK) {
                    currJobList.add(sendingJob);
                }

                System.out.println("Sending job " + sendingJob.getId() + " to master.");
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
