import java.io.Serializable;

public class Job implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1558957197426872413L;
	private String jobId;
	private JobType jobType;
	private String clientName;
	//Random rand = new Random();
	
	
	
	public Job(JobType jobType, String id, String name) {
		this.jobType = jobType;
		this.jobId = id;
		this.clientName = name;
	}
	
	public String getName() {
		return clientName;
	}

	public String getId() {
		return jobId;
	}

	public JobType getJobType() {
		return jobType;
	}

}
