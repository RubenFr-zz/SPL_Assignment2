package bgu.spl.mics.application.passiveObjects;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.LinkedList;

/**
 * Passive object representing the diary where all reports are stored.
 * <p>
 * This class must be implemented safely as a thread-safe singleton.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You can add ONLY private fields and methods to this class as you see fit.
 */
public class Diary {

	private List<Report> reports;
	private int total = 0;

	/**
	 * Initialize a Diary
	 */
	private Diary(){// For the singleton
		this.reports = new LinkedList<>();
		this.total = 0;
	}

	/**
	 * Static inner class (Bill Push singleton method)
	 * That way we are sure the class instance is only defined once !
	 */
	private static class DiaryHolder {
		private static Diary instance = new Diary();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Diary getInstance() {
		return DiaryHolder.instance;
	}

	public List<Report> getReports() {
		return this.reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		this.reports.add(reportToAdd);
		this.total++;// Should we increment the number of reports here?
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) throws IOException {
		FileWriter write = new FileWriter(filename, false);
		PrintWriter print_line = new PrintWriter(write);
		for (Report report : this.reports) {
			print_line.printf(report.toString());
		}
		print_line.close();
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return this.total;
	}

	public void incrementTotal(){
		this.total++;
	}
}
