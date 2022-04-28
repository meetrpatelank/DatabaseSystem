package utilities;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;
import java.io.*;

public class FileReadWrite {

    public final String rootPath = "src/main/resources/";

    public String readFile(String path) {

        StringBuilder stringBuilder = new StringBuilder();
        String fileName = rootPath + path + ".data";

        File file = new File(fileName);
        if(!file.exists()){
            return null;
        }

        try {

            FileReader fileReader = new FileReader(fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String currentLine = null;

            while ((currentLine = bufferedReader.readLine()) != null) {
                stringBuilder.append(currentLine).append("\n");
            }

            bufferedReader.close();
        } catch (Exception e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
        }
        return stringBuilder.toString();
    }

    public void writeFile(String path, String content) {

        String fileName = rootPath + path + ".data";

        try {

            File file = new File(fileName);
            file.getParentFile().mkdirs();

            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.print(content);
            printWriter.close();

            fileWriter.close();

        } catch (Exception e) {
            System.out.println("Error writing file: " + fileName);
            e.printStackTrace();
        }
    }

    public List<String> getDirectories (String path) {
        List<String> directories = new ArrayList<>();
        File directory = new File(rootPath + path);
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                directories.add(file.getName());
            }
        }
        return directories;
    }

    public void overWriteFile (String path, String content) {
        String fileName = rootPath + path + ".data";

        try {

            File file = new File(fileName);
            file.getParentFile().mkdirs();

            FileWriter fileWriter = new FileWriter(file, false);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.print(content);
            printWriter.close();

            fileWriter.close();

        } catch (Exception e) {
            System.out.println("Error writing file: " + fileName);
            e.printStackTrace();
        }
    }

    public void deleteDirectory (String path) {
        File directory = new File(rootPath + path);
        File[] files = directory.listFiles();
        assert files != null;
        for (File file : files) {
            if (file.isDirectory()) {
                deleteDirectory(path + "/" + file.getName());
            } else {
                file.delete();
            }
        }
        directory.delete();
    }

    public void addToJsonArray (String path, JSONObject object) {
        String fileName = rootPath + path + ".json";

        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();


            StringBuilder stringBuilder = new StringBuilder();
            JSONObject fileContents = null;

            if(file.exists()){
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String currentLine = null;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(currentLine).append("\n");
                }
                bufferedReader.close();
                if(stringBuilder.length() > 0) {
                    fileContents = new JSONObject(stringBuilder.toString());
                }
            }

            if(fileContents == null || fileContents.length() == 0){
                fileContents = new JSONObject();
                fileContents.put("logs", new JSONArray());
            }

            JSONArray jsonArray = fileContents.getJSONArray("logs");
            jsonArray.put(object);

            FileWriter fileWriter = new FileWriter(file);
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.print(fileContents.toString());

            printWriter.close();
            fileWriter.close();
        } catch (Exception e) {
            System.out.println("Error writing file: " + fileName);
            e.printStackTrace();
        }
    }

    public JSONObject readLogFile(String path){
        String fileName = rootPath + path + ".json";

        try {
            File file = new File(fileName);
            file.getParentFile().mkdirs();

            StringBuilder stringBuilder = new StringBuilder();
            JSONObject fileContents = null;

            if (file.exists()) {
                FileReader fileReader = new FileReader(fileName);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                String currentLine = null;
                while ((currentLine = bufferedReader.readLine()) != null) {
                    stringBuilder.append(currentLine).append("\n");
                }
                bufferedReader.close();
                if (stringBuilder.length() > 0) {
                    fileContents = new JSONObject(stringBuilder.toString());
                }

            }

            return fileContents;

        } catch (Exception e) {
            System.out.println("Error reading file: " + fileName);
            e.printStackTrace();
        }

        return null;
    }
    
    public boolean checkDirectory(String path) {
    	
        File directory = new File(rootPath + path);
        File[] files = directory.listFiles();
        if(files != null) {
        	return true;
        }
        else {
        	return false;
        }  
    }
}
