package utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Utility {
    public static List<String> readStringByLines(String string) {      
        return new ArrayList<>(Arrays.asList(string.split("\n")));
    } 

    public static String getEquivalentSqlDatatype(String centDbDatatype) {
        switch (centDbDatatype) {
            case "int":
                return "int";
            case "varchar":
                return "varchar(255)";
            default:
                return null;
        }
    }
}
