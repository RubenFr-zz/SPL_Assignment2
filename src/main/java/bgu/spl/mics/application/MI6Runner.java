package bgu.spl.mics.application;

import bgu.spl.mics.Publisher;
import bgu.spl.mics.Subscriber;
import bgu.spl.mics.application.passiveObjects.*;

import bgu.spl.mics.application.subscribers.Intelligence;
import bgu.spl.mics.application.publishers.TimeService;
import bgu.spl.mics.application.subscribers.M;
import bgu.spl.mics.application.subscribers.Moneypenny;
import bgu.spl.mics.application.subscribers.Q;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


/**
 * This is the Main class of the application. You should parse the input file,
 * create the different instances of the objects, and run the system.
 * In the end, you should output serialized objects.
 */
public class MI6Runner {

    private static CountDownLatch startSignal;
    private static Subscriber[] subscribers;
    private static Publisher[] publishers;
    private static String[] outputFilesName;
    private static LinkedList<M> MInstances;

    public static void main(String[] args) {

        if (args.length != 3) {
            System.out.println("Wrong arguments");
            return;
        }

        LinkedList<Thread> threadsList = new LinkedList<>();
        MInstances = new LinkedList<>();
        outputFilesName = new String[2];
        outputFilesName[0] = args[1];
        outputFilesName[1] = args[2];
        JsonParser parser = new JsonParser();

        try {
            JsonObject JsonObj = (JsonObject) parser.parse(new FileReader(args[0]));
            initInventory(JsonObj);
            initSquad(JsonObj);
            subscribers = initSubscribers(JsonObj);
            publishers = initPublishers(JsonObj);
        } catch (IOException e) {
            e.printStackTrace();
        }

        for (Subscriber subscriber : subscribers) {
            Thread thread = new Thread(subscriber);
            thread.start();
            threadsList.add(thread);
        }
        for (Publisher publisher : publishers) {
            Thread thread = new Thread(publisher);
            thread.setName("Time Service");
            thread.start();
            threadsList.add(thread);
        }

        for (Thread thread : threadsList) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        /**
         * When the program reaches this line the program is over !
         */
        printInventory();
        printDiary();

    }


    private static Subscriber[] initSubscribers(JsonObject jsonObj) {
        JsonObject services = jsonObj.get("services").getAsJsonObject();

        int m = services.get("M").getAsInt();
        int mp = services.get("Moneypenny").getAsInt();
        int q = 1;
        int i = services.get("intelligence").getAsJsonArray().size();
        Subscriber[] subscribers = new Subscriber[m + mp + q + i];
        startSignal = new CountDownLatch(m + mp + q + i);
        Q.getInstance().setLatch(startSignal);

        System.arraycopy(initM(m), 0, subscribers, 0, m);
        System.arraycopy(initMoneyPenny(mp), 0, subscribers, m, mp);
        System.arraycopy((new Subscriber[]{Q.getInstance()}), 0, subscribers, m + mp, q);
        System.arraycopy(initIntelligence(services), 0, subscribers, m + mp + q, i);

        return subscribers;
    }

    private static Publisher[] initPublishers(JsonObject jsonObj) {
        JsonObject services = jsonObj.get("services").getAsJsonObject();

        return (new Publisher[]{new TimeService(services.get("time").getAsInt(), 100, startSignal)});
    }

    private static Subscriber[] initM(int size) {
        Subscriber[] array = new M[size];

        for (int i = 0; i < size; i++) {
            M m = new M(Integer.toString(i + 1), startSignal);
            array[i] = m;
            MInstances.add(m);
        }
        return array;
    }

    private static Subscriber[] initMoneyPenny(int size) {
        Subscriber[] array = new Moneypenny[size];

        for (int i = 0; i < size; i++) {
            array[i] = new Moneypenny(Integer.toString(i + 1), startSignal);
        }
        return array;
    }

    /**
     * @param services is the section in the json of the service (publisher, subscribers)
     */
    private static Subscriber[] initIntelligence(JsonObject services) {
        JsonArray intelligenceJSON = (JsonArray) services.get("intelligence"); //Array of all intelligences
        Iterator<JsonElement> iterator = intelligenceJSON.iterator();
        Subscriber[] intelligences = new Intelligence[intelligenceJSON.size()];
        int i = 0;
        while (iterator.hasNext()) {
            JsonObject IntelligenceObject = iterator.next().getAsJsonObject(); //Intelligence
            JsonArray missionObject = (JsonArray) IntelligenceObject.get("missions");
            Intelligence intelligence = new Intelligence(String.valueOf(i + 1), startSignal);
            MissionInfo[] missions = new MissionInfo[missionObject.size()];
            int j = 0;

            Iterator<JsonElement> iterator1 = missionObject.iterator();
            while (iterator1.hasNext()) {
                MissionInfo mission = initMission(iterator1.next().getAsJsonObject());
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
    private static MissionInfo initMission(JsonObject missionObject) {
        MissionInfo mission = new MissionInfo();

        List<String> serialAgentsNumbers = extractSerialNumber(missionObject);
        mission.setSerialAgentsNumbers(serialAgentsNumbers);
        mission.setMissionName(missionObject.get("name").getAsString());
        mission.setGadget(missionObject.get("gadget").getAsString());
        mission.setDuration(missionObject.get("duration").getAsInt());
        mission.setTimeIssued(missionObject.get("timeIssued").getAsInt());
        mission.setTimeExpired(missionObject.get("timeExpired").getAsInt());
        return mission;
    }

    /**
     * Read the agent's serial numbers in Json
     *
     * @param missionObject
     * @return the list of agent's serial numbers required for the mission
     */
    private static List<String> extractSerialNumber(JsonObject missionObject) {
        JsonArray serialNumbers = (JsonArray) missionObject.get("serialAgentsNumbers");
        Iterator<JsonElement> iterator1 = serialNumbers.iterator();
        List<String> serialAgentsNumbers = new LinkedList<>();
        while (iterator1.hasNext()) {
            serialAgentsNumbers.add(iterator1.next().getAsString());
        }
        return serialAgentsNumbers;
    }


    /**
     * Extract the agents' list in the Json file and add it to the Squad (singleton)
     *
     * @param jsonObj
     */
    private static void initSquad(JsonObject jsonObj) {
        JsonArray squad = (JsonArray) jsonObj.get("squad");
        Iterator<JsonElement> iterator = squad.iterator();
        Agent[] agents = new Agent[squad.size()];
        int i = 0;
        while (iterator.hasNext()) {
            JsonObject agentObject = iterator.next().getAsJsonObject();
            Agent agent = new Agent();
            agent.setName(agentObject.get("name").getAsString());
            agent.setSerialNumber(agentObject.get("serialNumber").getAsString());
            agents[i++] = agent;
        }
        Squad.getInstance().load(agents);
    }


    /**
     * Extract the gadget's list in the Json file and add it to the inventory (singleton)
     *
     * @param jsonObj - Json file
     */
    private static void initInventory(JsonObject jsonObj) throws IOException {
        JsonArray inventory = (JsonArray) jsonObj.get("inventory");
        Iterator<JsonElement> iterator = inventory.iterator();
        String[] gadgets = new String[inventory.size()];
        int i = 0;
        while (iterator.hasNext()) {
            gadgets[i++] = iterator.next().getAsString();
        }
        Inventory.getInstance().load(gadgets);
    }

    private static void printInventory() {
        try{
            Inventory.getInstance().printToFile(outputFilesName[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void printDiary(){
        try {
            Diary.getInstance().printToFile(outputFilesName[1]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}


