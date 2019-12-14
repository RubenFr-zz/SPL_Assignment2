package bgu.spl.mics.application;

import bgu.spl.mics.application.passiveObjects.Agent;
import bgu.spl.mics.application.passiveObjects.Inventory;
import bgu.spl.mics.application.passiveObjects.MissionInfo;
import bgu.spl.mics.application.passiveObjects.Squad;

import bgu.spl.mics.application.publishers.Intelligence;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {

        if (args.length != 1) {
            System.out.println("Wrong arguments");
        }

        JSONParser parser = new JSONParser();

        try {
            JSONObject JsonObj = (JSONObject) parser.parse(new FileReader(args[0]));
            initInventory(JsonObj);
            initSquad(JsonObj);
            Intelligence[] intelligences = initServices(JsonObj);
            System.out.println(intelligences);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private static Intelligence[] initServices(JSONObject jsonObj) {
        JSONObject services = (JSONObject) jsonObj.get("services");
        initM(services);
        initMonneyPenny(services);
        return initIntelligence(services);
    }

    private static void initM(JSONObject services) {
        M m1 = new M(String.valueOf(services.get("M")));
    }

    private static void initMonneyPenny(JSONObject services) {
        Moneypenny mp1 = new Moneypenny(String.valueOf(services.get("Moneypenny")));
    }

    /**
     * @param services is the section in the json of the service (publisher, subscribers)
     */
    private static Intelligence[] initIntelligence(JSONObject services) {
        JSONArray intelligenceJSON = (JSONArray) services.get("intelligence"); //Array of all intelligences
        Iterator<JSONObject> iterator = intelligenceJSON.iterator();
        Intelligence[] intelligences = new Intelligence[intelligenceJSON.size()];
        int i = 0;
        while (iterator.hasNext()) {
            JSONObject IntelligenceObject = iterator.next(); //Intelligence
            JSONArray missionObject = (JSONArray) IntelligenceObject.get("missions");
            Intelligence intelligence = new Intelligence(String.valueOf(i+1));
            MissionInfo[] missions = new MissionInfo[missionObject.size()];
            int j = 0;

            Iterator<JSONObject> iterator1 = missionObject.iterator();
            while(iterator1.hasNext()) {
                MissionInfo mission = initMission(iterator1.next());
                missions[j++] = mission;
            }

            intelligence.loadMission(missions);
            intelligences[i++] = intelligence;
        }
        return intelligences;

    }

    /**
     * Create and initialize every field of a  {@link MissionInfo}
     *
     * @param missionObject
     * @return the new mission initialized
     */
    private static MissionInfo initMission(JSONObject missionObject) {
        MissionInfo mission = new MissionInfo();

        List<String> serialAgentsNumbers = extractSerialNumber(missionObject);
        mission.setSerialAgentsNumbers(serialAgentsNumbers);
        mission.setMissionName((String) missionObject.get("missionName"));
        mission.setGadget((String) missionObject.get("gadget"));
        mission.setDuration(((Long) missionObject.get("duration")).intValue());
        mission.setTimeIssued(((Long) missionObject.get("timeIssued")).intValue());
        mission.setTimeExpired(((Long) missionObject.get("timeExpired")).intValue());
        return mission;
    }

    /**
     * Read the agent's serial numbers in Json
     *
     * @param missionObject
     * @return the list of agent's serial numbers required for the mission
     */
    private static List<String> extractSerialNumber(JSONObject missionObject) {
        JSONArray serialNumbers = (JSONArray) missionObject.get("serialAgentsNumbers");
        Iterator<String> iterator1 = serialNumbers.iterator();
        List<String> serialAgentsNumbers = new LinkedList<>();
        while (iterator1.hasNext()) {
            serialAgentsNumbers.add((String) iterator1.next());
        }
        return serialAgentsNumbers;
    }


    /**
     * Extract the agents' list in the Json file and add it to the Squad (singleton)
     *
     * @param jsonObj
     */
    private static void initSquad(JSONObject jsonObj) {
        JSONArray squad = (JSONArray) jsonObj.get("squad");
        Iterator<JSONObject> iterator = squad.iterator();
        Agent[] agents = new Agent[squad.size()];
        int i = 0;
        while (iterator.hasNext()) {
            JSONObject agentObject = iterator.next();
            Agent agent = new Agent();
            agent.setName((String) agentObject.get("name"));
            agent.setSerialNumber((String) agentObject.get("serialNumber"));
            agents[i++] = agent;
        }
        Squad.getInstance().load(agents);
    }


    /**
     * Extract the gadget's list in the Json file and add it to the inventory (singleton)
     *
     * @param jsonObj - Json file
     */
    private static void initInventory(JSONObject jsonObj) throws IOException {
        JSONArray inventory = (JSONArray) jsonObj.get("inventory");
        Iterator<String> iterator = inventory.iterator();
        String[] gadgets = new String[inventory.size()];
        int i = 0;
        while (iterator.hasNext()) {
            gadgets[i++] = iterator.next();
        }
        Inventory.getInstance().load(gadgets);
        Inventory.getInstance().printToFile("/home/rubenf/IdeaProjects/SPL_Assignment2/output.txt");
    }

}


