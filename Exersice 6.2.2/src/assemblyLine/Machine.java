package assemblyLine;

public class Machine {

	// the capacity of all bins
	public static double capacity = -1;

// the list of items in this bin
	public Job[] jobs;

// construct an empty bin
	public Machine(int maxItems) {
		this.jobs = new Job[maxItems];
	}

// calculate the remaining capacity
	public double remainingCapacity() {
		double capacity = Machine.capacity;
		for (Job job : jobs) {
			if (job != null) {
				capacity -= job.getProTime();
			}
		}
		return capacity;
	}

// try to pack an item , return true if it was
// packed , false otherwise
	public boolean pack(Job job) {
// first check the remaining capacity
		if (job.getProTime() > remainingCapacity()) {
// immediately return false
			return false;
		}

// check for an empty place in the array
		for (int i = 0; i < jobs.length; i++) {
			if (jobs[i] == null) {
				jobs[i] = job;
				return true;
			}
		}

		// could not find an empty place
		return false;
	}
}