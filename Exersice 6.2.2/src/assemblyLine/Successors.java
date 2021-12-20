package assemblyLine;

public class Successors {
	
	
	private Job job;
	private Job succ1;
	private Job succ2 = null;
	
	
	public Successors(Job job, Job succ1, Job succ2) {
		this.job = job;
		this.succ1  = succ1;
		this.succ2 = succ2;
	}
	
	public Successors(Job job, Job succ1) {
		this.job = job;
		this.succ1  = succ1;
	}
	
	public void calcRPW() {	
		if(succ2 != null) {
			job.setRPW(job.getRPW()+succ1.getRPW()+succ2.getRPW());
		} else {
			job.setRPW(job.getRPW()+succ1.getRPW());
		}
	}
	

}
