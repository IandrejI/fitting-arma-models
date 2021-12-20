package test;
import assemblyLine.Job;
import assemblyLine.Successors;


public class Test {

	public static void main(String[] args) {
		Job A = new Job("A", 6);
		Job B = new Job("B", 2);
		Job C = new Job("C", 4);
		Job D = new Job("D", 6);
		Job E = new Job("E", 7);
		Job F = new Job("F", 3);
		
		Successors succsA = new Successors(A,B,C);
		Successors succsB = new Successors(B,D,E);
		Successors succsC = new Successors(C,E);
		Successors succsD = new Successors(D,F);
		Successors succsE = new Successors(E,F);
		
		Successors[] allSuccs = {succsA, succsB, succsC, succsD, succsE};
		for(int i = (allSuccs.length)-1; i >= 0; i--) {
			allSuccs[i].calcRPW();
		}


		
		
		System.out.print(D.getRPW());
		
		
		Job[] setOfJobs = {A,B,C,D,E,F};
		
		
		
		

	}

}
