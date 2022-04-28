package logGenerator;

import org.json.JSONArray;
import org.json.JSONObject;
import state.State;
import utilities.FileReadWrite;

import java.util.List;

public class Logger {
    public static FileReadWrite fileReadWrite = new FileReadWrite();

    public void log(JSONObject log, String logType) {
        fileReadWrite.addToJsonArray("logs/" + logType + "/logs" , log);
    }

    public void generalLog(String query, State state){
        if (state.getActiveDatabase() != null) {
            JSONObject log = new JSONObject();

            List<String> tableList = fileReadWrite.getDirectories("databases/" + state.getActiveDatabase());

            JSONArray tables = new JSONArray();
            for (String table : tableList) {
                JSONObject tableObject = new JSONObject();

                String tableDataContent = fileReadWrite.readFile("databases/" + state.getActiveDatabase().toUpperCase() + "/" + table + "/DATA");

                if(tableDataContent == null){
                    return;
                }

                String[] tableDataParts = tableDataContent.split("\n");

                tableObject.put("totalRows", tableDataParts.length);
                tableObject.put("name", table);
                tables.put(tableObject);
            }

            log.put("tables", tables);
            log.put("query", query);
            log.put("database", state.getActiveDatabase());
            log.put("timestamp", System.currentTimeMillis());
            log.put("user", state.getUserName());
            this.log(log, "GENERAL");
        }
    }

    public void eventLog(String eventType, String eventDescription, State state){
        JSONObject log = new JSONObject();
        log.put("eventType", eventType);
        log.put("eventDescription", eventDescription);
        log.put("timestamp", System.currentTimeMillis());
        log.put("user", state.getUserName());
        this.log(log, "EVENT");
    }

    public void queryLog(String query, State state) {
        JSONObject log = new JSONObject();
        log.put("query", query);
        log.put("database", state.getActiveDatabase());
        log.put("table", state.getLastUsedTable());
        log.put("timestamp", System.currentTimeMillis());
        log.put("user", state.getUserName());
        this.log(log, "QUERY");
    }
}
