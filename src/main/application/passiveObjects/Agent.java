package main.application.passiveObjects;

/**
 * Passive data-object representing a information about an agent in MI6.
 * You must not alter any of the given public methods of this class. 
 * <p>
 * You may add ONLY private fields and methods to this class.
 */
// This class is supposed to be a singleton check if need to implement it with arguments !!!???
public class Agent {

	private String serialNumber;
	private String name;
	private Boolean available;

	/**
	 * empty constructor to initialize the instance of the class
	 */
	public Agent() {}

	/**
	 * Initialize an agent
	 * @param serialNumber
	 *                     any non null String object
	 * @param name
	 *             any non null String object
	 */
	public Agent(String serialNumber, String name){
		this.serialNumber = serialNumber;
		this.name = name;
		this.available = true;
	}

	/**
	 * Static inner class (Bill Push singleton method)
	 * That way we are sure the class instance is only defined once !
	 */
	private static class AgentHolder {
		private static Agent instance = new Agent();
	}

	/**
	 * Retrieves the single instance of this class.
	 */
	public static Agent getInstance() {
		return AgentHolder.instance;
	}

	/**
	 * Sets the serial number of an agent. (command)
	 * @param serialNumber
	 *                     any non null String object
	 * @pre: none.
	 * @post: this.serialNumber == @param
	 */
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}

	/**
     * Retrieves the serial number of an agent. (query)
     * <p>
     * @return The serial number of an agent.
     */
	public String getSerialNumber() {
		return this.serialNumber;
	}

	/**
	 * Sets the name of the agent. (command)
	 * @param name
	 *             any non null String object
	 * @pre: none.
	 * @post: this.name == @param;
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
     * Retrieves the name of the agent. (query)
     * <p>
     * @return the name of the agent.
     */
	public String getName() {
		return this.name;
	}

	/**
     * Retrieves if the agent is available. (query)
     * <p>
     * @return if the agent is available.
     */
	public boolean isAvailable() {
		return this.available;
	}

	/**
	 * Acquires an agent. (command)
	 * @pre: this.available == true
	 */
	public void acquire(){
		this.available = false;
	}

	/**
	 * Releases an agent. (command)
	 * @pre: this.available == false
	 */
	public void release(){
		this.available = true;
	}

	/**
	 * toString method (query)
	 * @return A string of this
	 */
	public String toString() {
		String str ="Serial Number: " + this.serialNumber + ", Name: " + this.name;
		if (available) str += ", Available";
		else str += ", Not Available";
		return str;
	}
}
