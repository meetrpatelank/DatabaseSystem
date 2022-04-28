package analytics;

import org.json.JSONArray;
import org.json.JSONObject;
import state.State;
import utilities.ConsoleReader;
import utilities.FileReadWrite;

import java.util.HashSet;
import java.util.Set;

public class Analytics {

    public static ConsoleReader reader = new ConsoleReader();
    public static FileReadWrite fileReadWrite = new FileReadWrite();
    public static State state;

    public static void menu(State newState) {

        while (true) {
            state = newState;
            System.out.print("\nAnalytics Query>");
            String query = reader.readString();

            if (query.equalsIgnoreCase("exit")) {
                break;
            } else {
                processQuery(query);
            }
        }
    }

    public static void processQuery(String query) {
        String[] queryParts = query.split(" ");

        if (queryParts[0].equalsIgnoreCase("count") && queryParts[1].equalsIgnoreCase("queries")) {
            countQueries(queryParts[2]);
        } else if (queryParts[0].equalsIgnoreCase("count") && queryParts[1].equalsIgnoreCase("update")) {
            countUpdates(queryParts[2]);
        }
    }

    public static void countQueries(String dbName) {
        JSONObject logs = fileReadWrite.readLogFile("logs/QUERY/logs");
        JSONArray queries = logs.getJSONArray("logs");

        int count = 0;
        for (int i = 0; i < queries.length(); i++) {
            JSONObject query = queries.getJSONObject(i);

            if (query.has("database") && query.getString("database").equalsIgnoreCase(dbName)
                    && query.has("user") && query.getString("user").equalsIgnoreCase(state.getUserName())) {
                count++;
            }
        }

        System.out.println("user '" + state.getUserName() + "' has executed " + count + " queries.");
    }

    public static void countUpdates (String dbName) {
        JSONObject logs = fileReadWrite.readLogFile("logs/QUERY/logs");
        JSONArray queries = logs.getJSONArray("logs");

        Set<String> tables = new HashSet<>();
        for (int i = 0; i < queries.length(); i++) {
            JSONObject query = queries.getJSONObject(i);
            if(query.has("table") && query.getString("table").length() > 2) {
                tables.add(query.getString("table"));
            }
        }

        for (String table : tables) {
            int count = 0;
            for (int i = 0; i < queries.length(); i++) {
                JSONObject query = queries.getJSONObject(i);
                if (query.has("database") && query.getString("database").equalsIgnoreCase(dbName)
                        && query.has("table") && query.getString("table").equalsIgnoreCase(table)
                        && query.has("user") && query.getString("user").equalsIgnoreCase(state.getUserName())
                        && query.has("query") && query.getString("query").contains("UPDATE")) {
                    count++;
                }
            }
            System.out.println("user '" + state.getUserName() + "' has executed " + count + " updates to table '" + table + "'.");
        }
    }
}