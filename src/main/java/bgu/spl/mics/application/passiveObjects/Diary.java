package bgu.spl.mics.application.passiveObjects;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

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
	private AtomicInteger total;

	/**
	 * Initialize a Diary
	 */
	private Diary(){
		this.reports = new LinkedList<>();
		total = new AtomicInteger(0);
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
		return reports;
	}

	/**
	 * adds a report to the diary
	 * @param reportToAdd - the report to add
	 */
	public void addReport(Report reportToAdd){
		reports.add(reportToAdd);
	}

	/**
	 *
	 * <p>
	 * Prints to a file name @filename a serialized object List<Report> which is a
	 * List of all the reports in the diary.
	 * This method is called by the main method in order to generate the output.
	 */
	public void printToFile(String filename) throws IOException {

		JsonArray reports = new JsonArray();
		for ( Report report : this.reports)
			reports.add(toJson(report));

		JsonObject jsonObject = new JsonObject();
		jsonObject.addProperty("total", total.get());
		jsonObject.addProperty("total reported", reports.size());
		jsonObject.add("reports", reports);

		FileWriter file = new FileWriter(filename);
		file.write(new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject));
		file.close();
	}

	/**
	 * Gets the total number of received missions (executed / aborted) be all the M-instances.
	 * @return the total number of received missions (executed / aborted) be all the M-instances.
	 */
	public int getTotal(){
		return total.get();
	}

	public void increment(){
		total.incrementAndGet();
	}

	/**
	 * Convert a report into a JsonObject with all the requiered fields
	 * @return jsonObject of the report
	 */
	private JsonObject toJson(Report report) {

		JsonArray serialsNumber = new JsonArray();
		for ( String serial : report.getAgentsSerialNumbers())
			serialsNumber.add(serial);

		JsonArray names = new JsonArray();
		for ( String name : report.getAgentsNames())
			names.add(name);

		JsonObject jsonReport = new JsonObject();
		jsonReport.addProperty("missionName", report.getMissionName());
		jsonReport.addProperty("M", report.getM());
		jsonReport.addProperty("MoneyPenny", report.getMoneypenny());
		jsonReport.add("agentsSerialNumbers", serialsNumber);
		jsonReport.add("agentsName", names);
		jsonReport.addProperty("gadget", report.getGadgetName());
		jsonReport.addProperty("timeIssued", report.getTimeIssued());
		jsonReport.addProperty("QTime", report.getQTime());
		jsonReport.addProperty("timeCreated", report.getTimeCreated());

		return jsonReport;
	}

	//todo delete this!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	public void clear() {
		reports.clear();
		total.set(0);
	}
}
