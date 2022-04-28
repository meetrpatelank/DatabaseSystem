package export;

import utilities.ConsoleReader;
import utilities.FileReadWrite;
import utilities.Utility;
import state.State;
import queries.Queries;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.text.SimpleDateFormat;
import java.util.Date;


public class Export {


    private final static String EXPORT_PATH = System.getProperty("user.dir") + "/centdb-exports";

    String sqlExport ="";

    public static ConsoleReader reader = new ConsoleReader();
    public static FileReadWrite fileReadWrite = new FileReadWrite();

    public static void show(State state) {
        System.out.println("-------------");
        while (true) {

            System.out.println("Use the schema to perform SQL Dumb");
            System.out.print("\nQUERY>");
            String query = reader.readString();
            String[] queryParts = query.split(" ");

            if (queryParts[0].equalsIgnoreCase("export") && queryParts[1].equalsIgnoreCase("database")) {
                useDatabase(queryParts[2], state);
                String queryTemp = reader.readString();
                if (queryTemp.equalsIgnoreCase("yes")) {
                    System.out.println("Performed SQL Dump");
                    showtable(state);

                }
            } else if (queryParts[0].equalsIgnoreCase("exit")) {
                break;
            } else {
                System.out.println("Entered Schema does not exist. Please re-enter the Schema name");
            }

        }

    }

    private static void useDatabase(String databaseName, State state) {
        List<String> databaseList = fileReadWrite.getDirectories("databases");

        if (!databaseList.contains(databaseName.toUpperCase())) {
            System.out.println("Database does not exist. Please create a database first.");
            return;
        }
        state.setActiveDatabase(databaseName);
        System.out.println("Now using database: " + state.getActiveDatabase());

        System.out.println("Please enter yes to perform the SQL Dump");

    }

    private static void showtable(State state) {

        if (state.getActiveDatabase() == null) {
            System.out.println("Please use a database first.");
            return;
        }
        String sqlExport = "";

        List<String> tableList = fileReadWrite.getDirectories("databases/" + state.getActiveDatabase());

        System.out.println(tableList);
        System.out.format("%20s%20s%20s\n", "Table Number", "Database name", "Table Name");
        for (int i = 0; i < tableList.size(); i++) {
            System.out.format("%20s%20s%20s\n", i + 1, state.getActiveDatabase(), tableList.get(i));
        }
        String[] tableName = new String[tableList.size()];

        for (int i = 0; i < tableList.size(); i++) {
            tableName[i] = tableList.get(i);
            try{
            String metadata = fileReadWrite.readFile("databases/" + state.getActiveDatabase() + "/" + tableName[i] + "/metadata");
            String data = fileReadWrite.readFile("databases/" + state.getActiveDatabase() + "/" + tableName[i] + "/data");
            sqlExport += "USE DATABASE " +state.getActiveDatabase();
            sqlExport += "\n" +"CREATE DATABASE "+state.getActiveDatabase();
            sqlExport += generateSqlCreateQuery(tableName[i], state, metadata);
            sqlExport += "\n";
            sqlExport += generateSqlInsertQuery(tableName[i], state, metadata, data);
            }
            catch (Exception ex){
                return;
            }
        }
        sqlExport += "-- Dump completed on " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());

        try{
            File exportsDir = new File(EXPORT_PATH);
            if(!exportsDir.exists()){
                exportsDir.mkdir();
            }
            File sql = new File(EXPORT_PATH + "/" + state.getActiveDatabase() + ".sql");
            
            if (sql.createNewFile()) {
                System.out.println("File created: " + state.getActiveDatabase() + ".sql");
            } else {
                System.out.println("File already exists.");
            }
            FileWriter fileWriter = new FileWriter(sql);
            fileWriter.write(sqlExport);
            fileWriter.close();
            
        }
        catch (IOException e) {
            e.printStackTrace();
        }
 
    }

   
    private static String generateSqlCreateQuery(String tableName, State state, String metadata) {

        String sqlCreateQuery = "\nCREATE TABLE "+ tableName +" (";
        List<String> metaDataLineByLine = Utility.readStringByLines(metadata);
        metaDataLineByLine.remove(0);
        for(int i=0;i<metaDataLineByLine.size();i++){
            String[] metadatasplit = metaDataLineByLine.get(i).split("\\^");
            
            sqlCreateQuery += metadatasplit[1] + " ";
            sqlCreateQuery += Utility.getEquivalentSqlDatatype(metadatasplit[2]);

            if(i != metaDataLineByLine.size()-1 ){
                sqlCreateQuery += ",";
            }

            
        }
        sqlCreateQuery += ")";
        
        return sqlCreateQuery;
    }
    private static String generateSqlInsertQuery(String tableName, State state, String metadata, String data){


        String sqlInsertQuery = "";
        List<String> dataLineByLine = Utility.readStringByLines(data);

        for(int i = 0; i < dataLineByLine.size(); i++){
            String[] dataSplit = dataLineByLine.get(i).split("\\^");
            sqlInsertQuery += "INSERT INTO " +tableName +" VALUES ";
            sqlInsertQuery += "(";

            for(int j=0; j < dataSplit.length; j++){
                sqlInsertQuery += dataSplit[j] + " ";
                if(j != dataSplit.length -1 ){
                    sqlInsertQuery += ",";
                }
            }
            sqlInsertQuery += ")" ; 
            if (i != dataLineByLine.size() -1){
                sqlInsertQuery += "\n";
            }
        }
        sqlInsertQuery += "\n\n";
      
        return sqlInsertQuery;
    }

    


}
