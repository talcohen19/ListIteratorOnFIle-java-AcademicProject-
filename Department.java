public class Department   {
	private String depName;
	private String depHead;

	public Department(String depName, String depHead) {
		this.depHead = depHead;
		this.depName = depName;
	}

	public Department(Department dep) {
		this.depName = dep.depName;
		this.depHead = dep.depHead;

	}
	public String getdepName(){
		return this.depName;
	}
	public String getDepHead() {
		return depHead;
	}

	@Override
	public String toString(){
		return String.format("%-40s %-10s",this.depName,this.depHead);
	}

}