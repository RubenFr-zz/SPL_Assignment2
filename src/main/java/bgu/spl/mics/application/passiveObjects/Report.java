package bgu.spl.mics.application.passiveObjects;

import bgu.spl.mics.application.subscribers.Moneypenny;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.LinkedList;
import java.util.List;

/**
 * Passive data-object representing a delivery vehicle of the store.
 * You must not alter any of the given public methods of this class.
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
public class Report {

	private String name = "";
	private int M;
	private int MoneyPenny;
	private List<String> agentsSerialNumbers = new LinkedList<>();
	private List<String> agentsName = new LinkedList<>();
	private String gadgetName = "";
	private int timeIssued;
	private int QTime;
	private int timeCreated;

	/**
     * Retrieves the mission name.
     */
	public String getname() {
		return this.name;
	}

	/**
	 * Sets the mission name.
	 */
	public void setname(String name) {
		this.name = name;
	}

	/**
	 * Retrieves the M's id.
	 */
	public int getM() {
		return this.M;
	}

	/**
	 * Sets the M's id.
	 */
	public void setM(int m) {
		this.M = m;
	}

	/**
	 * Retrieves the Moneypenny's id.
	 */
	public int getMoneypenny() {
		return this.MoneyPenny;
	}

	/**
	 * Sets the Moneypenny's id.
	 */
	public void setMoneypenny(int moneypenny) {
		this.MoneyPenny = moneypenny;
	}

	/**
	 * Retrieves the serial numbers of the agents.
	 * <p>
	 * @return The serial numbers of the agents.
	 */
	public List<String> getAgentsSerialNumbers() {
		return this.agentsSerialNumbers;
	}

	/**
	 * Sets the serial numbers of the agents.
	 */
	public void setAgentsSerialNumbers(List<String> agentsSerialNumbersNumber) {
		this.agentsSerialNumbers.addAll(agentsSerialNumbersNumber);
	}

	/**
	 * Retrieves the agents names.
	 * <p>
	 * @return The agents names.
	 */
	public List<String> getAgentsNames() {
		return this.agentsName;
	}

	/**
	 * Sets the agents names.
	 */
	public void setAgentsNames(List<String> agentsNames) {
		this.agentsName.addAll(agentsNames);
	}

	/**
	 * Retrieves the name of the gadget.
	 * <p>
	 * @return the name of the gadget.
	 */
	public String getGadgetName() {
		return gadgetName;
	}

	/**
	 * Sets the name of the gadget.
	 */
	public void setGadgetName(String gadgetName) {
		this.gadgetName = gadgetName;
	}

	/**
	 * Retrieves the time-tick in which Q Received the GadgetAvailableEvent for that mission.
	 */
	public int getQTime() {
		return this.QTime;
	}

	/**
	 * Sets the time-tick in which Q Received the GadgetAvailableEvent for that mission.
	 */
	public void setQTime(int qTime) {
		this.QTime = qTime;
	}

	/**
	 * Retrieves the time when the mission was sent by an Intelligence Publisher.
	 */
	public int getTimeIssued() {
		return this.timeIssued;
	}

	/**
	 * Sets the time when the mission was sent by an Intelligence Publisher.
	 */
	public void setTimeIssued(int timeIssued) {
		this.timeIssued = timeIssued;
	}

	/**
	 * Retrieves the time-tick when the report has been created.
	 */
	public int getTimeCreated() {
		return this.timeCreated;
	}

	/**
	 * Sets the time-tick when the report has been created.
	 */
	public void setTimeCreated(int timeCreated) {
		this.timeCreated = timeCreated;
	}
}
