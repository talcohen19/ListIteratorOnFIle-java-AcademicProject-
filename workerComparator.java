import java.util.Comparator;

public class workerComparator implements Comparator<Worker<?>> {


	public int compare(Worker<?> o1, Worker<?> o2) {
		
		if (o1.getSalary() ==o2.getSalary())
			return 0;
		else if(o1.getSalary()>o2.getSalary())
			return 1;
		else
			return -1;
	}

}
