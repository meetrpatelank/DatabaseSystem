package logical;

import java.util.HashMap;
import java.util.List;
import utilities.ConsoleReader;
import utilities.FileReadWrite;

public class DataModel {

	public static ConsoleReader reader = new ConsoleReader();
	public static FileReadWrite fileReadWrite = new FileReadWrite();
	
	public static void menu() {
		while(true) {
			System.out.print("\nData Modelling - Please enter the database name>");
	        String query = reader.readString();
	        query = query.toUpperCase();
	        
	        if(query.contentEquals("EXIT")) {
	        	break;
	        }
	        
	        if(databaseExist(query)) {
	        	createModel(query);
	        }
	        else {
	        	System.out.print("\nTry again");;
	        }
		}
	}
	
	public static boolean databaseExist(String databaseName) {
		List<String> databaseList = fileReadWrite.getDirectories("databases");
		
		if (databaseList.contains(databaseName)) {
            return true;
        }
		else {
			System.out.println("Database " + databaseName + " does not exist");
            return false;
		}
	}
	
	public static void createModel(String databaseName) {
		String path = "databases/" + databaseName;
		String dataModelFile = "ERD/" + databaseName + "_ERD_MODEL";
		StringBuilder fileContent = new StringBuilder();
		fileContent.append("****************************************************Entity Relationship Diagram for database " + databaseName + "****************************************************" + "\n\n");
		List<String> tableNames = fileReadWrite.getDirectories(path);
		fileContent.append("Total number of relations/tables in the database " + databaseName + " = " + tableNames.size() + "\n\n");
		
		HashMap<String, String> tableFirstColumns = new HashMap<String, String>();
		HashMap<String, String> primaryKeys = new HashMap<String, String>();
		
		//Primary Key Implementation
		for(String tableName : tableNames) {
			int columnNumber = 0;
			String columnName = "";
			String tableMetaData = fileReadWrite.readFile(path + "/" + tableName + "/METADATA");
			String tableMetaDataParts[];
			tableMetaDataParts = tableMetaData.split("\n");

			for(String part : tableMetaDataParts) {
				if(part.contains("TABLE")) {
					continue;
				}
				else if(part.contains("COLUMN")) {
					columnName = part.substring(part.indexOf("^") + 1).substring(0, part.substring(part.indexOf("^") + 1).indexOf("^"));
					columnNumber++;
					
					if(columnNumber == 1) {
						tableFirstColumns.put(tableName, columnName.toLowerCase());
					}
				}
			}
		}
		
		for(String tableName : tableFirstColumns.keySet()) {
			if(!primaryKeys.keySet().contains(tableFirstColumns.get(tableName))) {
				primaryKeys.put(tableFirstColumns.get(tableName), tableName);
			}
			else {
				primaryKeys.put(tableFirstColumns.get(tableName), primaryKeys.get(tableFirstColumns.get(tableName)) + ", " +  tableName);
			}
			
		}
		
		//Foreign Key Implementation
		for(String pK : primaryKeys.keySet()) {
			if(!primaryKeys.get(pK).contains(",")) {
				for(String tableName : tableNames) {
					int columnNumber = 0;
					String columnName = "";
					String tableMetaData = fileReadWrite.readFile(path + "/" + tableName + "/METADATA");
					String tableMetaDataParts[];
					tableMetaDataParts = tableMetaData.split("\n");

					for(String part : tableMetaDataParts) {
						if(part.contains("TABLE")) {
							continue;
						}
						else if(part.contains("COLUMN")) {
							columnName = part.substring(part.indexOf("^") + 1).substring(0, part.substring(part.indexOf("^") + 1).indexOf("^"));
							columnNumber++;
							
							if(columnName.equalsIgnoreCase(pK) && columnNumber != 1) {
								primaryKeys.put(pK, primaryKeys.get(pK) + ", " + tableName);
							}
						}
					}
				}
			}
		}
		
		System.out.println(primaryKeys);
		
		for(String tableName : tableNames) {
			int columnNumber = 0;
			String columnName = "";
			String tableMetaData = fileReadWrite.readFile(path + "/" + tableName + "/METADATA");
			String tableMetaDataParts[];
			tableMetaDataParts = tableMetaData.split("\n");

			for(String part : tableMetaDataParts) {
				if(part.contains("TABLE")) {
					continue;
				}
				else if(part.contains("COLUMN")) {
					columnName = part.substring(part.indexOf("^") + 1).substring(0, part.substring(part.indexOf("^") + 1).indexOf("^"));
					columnNumber++;
					
					if(columnNumber == 1) {
						if(primaryKeys.get(columnName.toLowerCase()).contains(",")) {
							fileContent.append("TABLE NAME: " + tableName + " ::" + " COLUMN_NAME_" + columnNumber + " : "+ columnName + " (PRIMARY KEY)" + " [Primary/Foreign Key relationship on Column: " + columnName + " among tables: " + primaryKeys.get(columnName.toLowerCase()) +  "\n");
						}
						else {
							fileContent.append("TABLE NAME: " + tableName + " ::" + " COLUMN_NAME_" + columnNumber + " : "+ columnName + " (PRIMARY KEY)" +"\n");
						}
					}
					else {
						fileContent.append("TABLE NAME: " + tableName + " ::" + " COLUMN_NAME_" + columnNumber + " : "+ columnName + "\n");
					}
				}
				else if(part.contains("RELATION")) {
					String[] relation_parts = part.substring(part.indexOf("^") + 1).split(" ");
					String table1 = relation_parts[1];
					String table2 = relation_parts[4];
					String cardinality1 = relation_parts[0];
					String cardinality2 = relation_parts[3];
					
					String cardinality = "NOT FOUND";
					
					if(cardinality1.equalsIgnoreCase("many") && cardinality2.equalsIgnoreCase("one")) {
						cardinality = "N:1";
					}
					else if(cardinality1.equalsIgnoreCase("many") && cardinality2.equalsIgnoreCase("many")) {
						cardinality = "N:N";
					}
					else if(cardinality1.equalsIgnoreCase("one") && cardinality2.equalsIgnoreCase("many")) {
						cardinality = "1:N";
					}
					else if(cardinality1.equalsIgnoreCase("one") && cardinality2.equalsIgnoreCase("one")) {
						cardinality = "1:1";
					}
					fileContent.append("Cardinality = " + cardinality + " from TABLE-" + table1.toUpperCase() + " to TABLE-" + table2.toUpperCase() + "\n");
				}
			}
			fileContent.append("\n");
		}
		fileReadWrite.overWriteFile(dataModelFile, fileContent.toString());
		System.out.println("ERD generated successfully to the path: src/main/resources/" + dataModelFile + ".data");
	}	
}
