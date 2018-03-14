import java.io.Serializable;

public class Worker<T> implements Serializable, Comparable<Worker<T>> {
	private static final long serialVersionUID = 1808186439835345353L;
	private String name;
	private T dep;
	private int salary;

	public T getDep() {
		return dep;
	}

	public int getSalary() {
		return salary;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Worker(String name, T dep, int salary) {
		this.name = name;
		this.dep = dep;
		this.salary = salary;

	}

	public String getName() {
		return this.name;
	}
	@Override
	public String toString() {
		return String.format("%-20s\t %-46s\t %-10d", this.name, this.dep.toString(), this.salary);

	}


	@Override
	public int compareTo(Worker<T> o) {
		int check=this.getName().compareToIgnoreCase(o.getName());
		if(check ==0)
			return 1;
		return check ;

	}

}
