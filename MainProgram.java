import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.TreeMap;
import java.util.TreeSet;

//name:Tal Cohen
//id: 308275429
//include: worker.java,Department.java,FixedLengthStringIO.java,workerCompartor.java
public class HW2_TalCohen {
	final static int COMPANYSIZE = 6; // number of employees
	final static String WORKER_FILE = "worker.dat";
	final static int NAME_SIZE = 20;
	final static int DEP_NAME_SIZE = 40;
	final static int DEP_HEAD_SIZE = 10;
	final static int RECORD_SIZE_DEPARTMENT = ((NAME_SIZE + DEP_NAME_SIZE + DEP_HEAD_SIZE) * Character.BYTES)
			+ Integer.BYTES;
	final static int RECORD_SIZE_STRING = ((NAME_SIZE + DEP_NAME_SIZE) * Character.BYTES) + Integer.BYTES;
	static Scanner s = new Scanner(System.in);

	public static void main(String[] args) throws IOException, ClassNotFoundException {
		// create data for example
		Department[] depArray = new Department[COMPANYSIZE];
		depArray[0] = new Department("Software Engineering", "Boss1");
		depArray[1] = new Department("Mechanical Engineering", "Boss2");
		depArray[2] = new Department("Industrial And Medical Engineering", "Boss3");
		depArray[3] = new Department("Electrical Engineering", "Boss4");
		String[] names = { "Elvis", "Samba", "Bamba", "Bisli", "Kinder Bueono" };
		String[] depNames = { depArray[0].getdepName(), depArray[1].getdepName(), depArray[2].getdepName(),
				depArray[3].getdepName() };
		int[] salaries = { 1000, 2000, 3000, 4000, 9999 };
		// end initialized data
		
		Boolean longMode = false;
		// flag for long mode- dep as a department or false to dep as a string
		System.out.println("Press 1 for dep as a class Department, any other key for dep as a string");
		String choice = s.nextLine();
		
		ArrayList<Worker<?>> workerList = new ArrayList<Worker<?>>();
		Map<?, ?> workerMap = new TreeMap<>();
		
		if (!(choice.equals("1"))) {
			workerList = HW2_TalCohen.<String>toArrList(names, depNames, salaries); // T
			workerMap = HW2_TalCohen.createMap(workerList);
		} else {
			longMode = true; // dep as a department, this is the only place this
								// boolean change
			workerList = HW2_TalCohen.<Department>toArrList(names, depArray, salaries);
			workerMap = HW2_TalCohen.createMap(workerList);

		}
		try {
			File workerFile = new File(WORKER_FILE);
			
			System.out.println("Array List Content:");
			printList(workerList);

			System.out.println("\nMap content backward, order by worker's name:");
			printMapBackWord(workerMap);

			saveMapToFile(workerMap, workerFile, longMode);
			System.out.println("\nFile Content : ");
			readFromFile(workerFile, longMode);

			sortBySalary(workerFile, new workerComparator(), longMode);
			System.out.println("\nFile Content After Sorting");
			readFromFile(workerFile, longMode);

			System.out.println("checkIterator:");
			ListIterator<Worker<?>> testIterator = listIterator(new RandomAccessFile(workerFile, "rw"), longMode);
			checkIterator(testIterator);
		} catch (FileNotFoundException ex) {
			ex.printStackTrace();
		} catch (IOException ex) {
			ex.printStackTrace();
		}

	}

	private static <T extends Worker<?>> Map<?, ?> createMap(ArrayList<T> workerList) {
		TreeSet<T> tempSet = new TreeSet<>(workerList);
		Map<Integer, T> workerMap = new TreeMap<>();
		Iterator<T> it = tempSet.iterator();
		for (int i = 1; it.hasNext(); i++)
			workerMap.put(i, it.next());

		return workerMap;

	}

	private static <T extends Worker<?>> void printMapBackWord(Map<?, ?> workerMap) {
		ArrayList<?> keyList = new ArrayList<>(workerMap.keySet());
		ListIterator<?> keyIt = keyList.listIterator(keyList.size());
		int index;
		while (keyIt.hasPrevious()) {
			index = (int) keyIt.previous();
			System.out.println((index) + ": " + workerMap.get(index));
		}

	}

	private static void saveMapToFile(Map<?, ?> workerMap, File workFile, Boolean longMode)
			throws FileNotFoundException, IOException {
		ArrayList<?> keyList = new ArrayList<>(workerMap.keySet());
		ListIterator<?> keyIt = keyList.listIterator();
		try (DataOutputStream out = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(workFile)))) {
			while (keyIt.hasNext()) {
				saveOneWorkerToFile((Worker<?>) workerMap.get(keyIt.next()), out, longMode);
			}

		}

	}

	private static void saveOneWorkerToFile(Worker<?> worker1, DataOutput out, Boolean longMode) throws IOException {
		FixedLengthStringIO.writeFixedLengthString(worker1.getName(), NAME_SIZE, out);
		if (longMode) {
			FixedLengthStringIO.writeFixedLengthString(((Department) worker1.getDep()).getdepName(), DEP_NAME_SIZE,
					out);
			FixedLengthStringIO.writeFixedLengthString(((Department) worker1.getDep()).getDepHead(), DEP_HEAD_SIZE,
					out);
		} else
			FixedLengthStringIO.writeFixedLengthString(((String) worker1.getDep()), DEP_NAME_SIZE, out);

		out.writeInt(worker1.getSalary());
	}

	private static void readFromFile(File workFile, Boolean longMode) throws FileNotFoundException, IOException {
		BufferedInputStream buf;
		try (DataInputStream in = new DataInputStream(buf = new BufferedInputStream(new FileInputStream(workFile)))) {
			while (buf.available() > 0) {
				Worker<?> newWorker = readOneWorkerFromFile(in, longMode);
				System.out.println(newWorker.toString());
			}
		}

	}

	private static Worker<?> readOneWorkerFromFile(DataInput input, Boolean longMode) throws IOException {
		String name = FixedLengthStringIO.readFixedLengthString(NAME_SIZE, input);
		String depName = FixedLengthStringIO.readFixedLengthString(DEP_NAME_SIZE, input);
		if (longMode) {
			String depHead = FixedLengthStringIO.readFixedLengthString(DEP_HEAD_SIZE, input);
			int salary = input.readInt();
			return new Worker<Department>(name, new Department(depName, depHead), salary);
		} else {
			int salary = input.readInt();
			return new Worker<String>(name, depName, salary);
		}

	}

	private static void sortBySalary(File workerFile, workerComparator wComp, Boolean longMode)
			throws FileNotFoundException, IOException {
		int recSize = (longMode) ? RECORD_SIZE_DEPARTMENT : RECORD_SIZE_STRING;
		long pointerW2, pointerNewW;
		try (RandomAccessFile raf = new RandomAccessFile(workerFile, "rw")) {
			long numOfRec = raf.length() / recSize;
			for (int i = 1; i < numOfRec; i++)// Insertion Sort
			{
				pointerNewW = i * recSize;
				raf.seek(pointerNewW);
				Worker<?> newWorker = readOneWorkerFromFile(raf, longMode);
				pointerW2 = pointerNewW;
				raf.seek(pointerW2 - recSize);
				Worker<?> worker2 = readOneWorkerFromFile(raf, longMode);
				while (pointerW2 > 0 && wComp.compare(newWorker, worker2) < 0) {
					raf.seek(pointerW2 - recSize);
					worker2 = readOneWorkerFromFile(raf, longMode);
					raf.seek(pointerW2);
					saveOneWorkerToFile(worker2, raf, longMode);
					pointerW2 -= recSize;
					if (pointerW2 > 0) {// EOF
						raf.seek(pointerW2 - recSize);
						worker2 = readOneWorkerFromFile(raf, longMode);
					}
				}
				raf.seek(pointerW2);
				saveOneWorkerToFile(newWorker, raf, longMode);
			}

		}
	}

	public static <T> ArrayList<Worker<?>> toArrList(String[] names, T[] listDep, int[] salaries) {
		// create an array list with generic type, while running the type will
		// be decided.
		ArrayList<Worker<?>> arrl = new ArrayList<>();
		arrl.add(new Worker<>(names[0], listDep[0], salaries[0]));
		arrl.add(new Worker<>(names[1], listDep[1], salaries[1]));
		arrl.add(new Worker<>(names[2], listDep[2], salaries[2]));
		arrl.add(new Worker<>(names[3], listDep[3], salaries[3]));
		arrl.add(new Worker<>(names[4], listDep[3], salaries[0]));
		arrl.add(new Worker<>(names[0], listDep[0], salaries[4]));
		return arrl;
	}

	private static void printList(ArrayList<Worker<?>> workerList) {

		for (int i = 0; i < workerList.size(); i++)
			System.out.println(workerList.get(i).toString());
	}

	private static void checkIterator(ListIterator<Worker<?>> myIt) {
		System.out.println("\nFile content FORWARD with ListIterator:");
		while (myIt.hasNext()) {
			System.out.println(myIt.next());
		}
		System.out.println("\nFile content BACKWARD with ListIterator:");
		while (myIt.hasPrevious()) {
			System.out.println(myIt.previous());
		}
	}

	public static ListIterator<Worker<?>> listIterator(RandomAccessFile raf, Boolean longMode) throws IOException {
		return new MyListIterator(0, raf, longMode);

	}

	public static ListIterator<Worker<?>> listIterator(int index, RandomAccessFile raf, Boolean longMode)
			throws IOException {
		return new MyListIterator(index, raf, longMode);

	}

	private static class MyListIterator implements ListIterator<Worker<?>> {
		private RandomAccessFile raf;
		private int recSize;
		private int cur = 0;
		private int last = -1;
		private long numOfRec;
		private boolean longMode;

		public MyListIterator(int index, RandomAccessFile raf, Boolean longMode) throws IOException {
			this.cur = index;
			this.raf = raf;
			recSize = (longMode) ? RECORD_SIZE_DEPARTMENT : RECORD_SIZE_STRING;
			this.numOfRec = raf.length() / recSize;
			this.longMode = longMode;
		}

		@Override
		public boolean hasNext() {
			return (cur < numOfRec);
		}

		@Override
		public Worker<?> next() {
			if (!hasNext())
				throw new NoSuchElementException();
			try {
				raf.seek(cur * recSize);
				Worker<?> temp = readOneWorkerFromFile(raf, longMode);
				last = cur;
				cur++;
				return temp;

			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public boolean hasPrevious() {
			return cur > 0;
		}

		@Override
		public Worker<?> previous() {
			if (!hasPrevious())
				throw new NoSuchElementException();
			cur--;
			last = cur;
			try {
				raf.seek(cur * recSize);
				Worker<?> temp = readOneWorkerFromFile(raf, longMode);
				return temp;
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}

		@Override
		public int nextIndex() {
			return cur;
		}

		@Override
		public int previousIndex() {
			return cur - 1;
		}

		@Override
		public void remove() {
			if (last == -1)
				throw new IllegalStateException();
			ArrayList<Worker<?>> list = new ArrayList<>();
			try {
				raf.seek(last * recSize);
				fromFileToList(raf, numOfRec - last, list);
				list.remove(0);
				list.trimToSize();
				numOfRec--;
				raf.seek(last * recSize);
				fromListToFile(raf, list);
				raf.setLength(numOfRec * recSize);
				cur = last;
				last = -1;

			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		@Override
		public void set(Worker<?> newWorker) {
			if (last == -1)
				throw new IllegalStateException();
			try {
				raf.seek(last * recSize);
				saveOneWorkerToFile(newWorker, raf, longMode);
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

		private void fromFileToList(RandomAccessFile raf, long numOfEl, ArrayList<Worker<?>> list) {
			try {
				while (raf.getFilePointer() < raf.length())
					list.add(readOneWorkerFromFile(raf, longMode));
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

		private void fromListToFile(RandomAccessFile raf, ArrayList<Worker<?>> list) {
			try {
				int index = 0;
				while (index < list.size()) {
					saveOneWorkerToFile(list.get(index), raf, longMode);
					index++;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void add(Worker<?> worker) {
			if (cur < 0)
				throw new IndexOutOfBoundsException();
			try {
				raf.seek(cur * recSize);
				ArrayList<Worker<?>> tempList = new ArrayList<>();
				fromFileToList(raf, numOfRec - cur - 1, tempList);
				raf.seek(cur * recSize);
				saveOneWorkerToFile(worker, raf, longMode);
				fromListToFile(raf, tempList);
				numOfRec++;
				cur++;
				last = -1;
			} catch (IOException e1) {
				e1.printStackTrace();
			}

		}

	}

}
