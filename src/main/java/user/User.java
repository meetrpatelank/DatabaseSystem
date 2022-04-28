package user;

import state.State;
import utilities.ConsoleReader;
import utilities.FileReadWrite;
import java.math.BigInteger;
import java.security.*;

public class User {
    private String username;
    private String password;
    private String securityQuestion;
    private String securityAnswer;

    public static ConsoleReader reader = new ConsoleReader();
    public static FileReadWrite fileReadWrite = new FileReadWrite();

    public User(String username, String password, String securityQuestion, String securityAnswer) {
        try {
            this.username = username;
            this.password = hashPassword(password);
            this.securityQuestion = securityQuestion;
            this.securityAnswer = securityAnswer;
        } catch (Exception e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
    }

    public void save() {
        String userData = username + "," + password + "," + securityQuestion + "," + securityAnswer + "\n";
        fileReadWrite.writeFile("USER", userData);
    }

    public static void register () {

        System.out.print("\nEnter New Username: ");
        String username = reader.readString();
        System.out.print("Enter New Password: ");
        String password = reader.readString();
        System.out.print("Enter New Security Question: ");
        String securityQuestion = reader.readString();
        System.out.print("Enter New Security Answer: ");
        String securityAnswer = reader.readString();

        User user = new User(username, password, securityQuestion, securityAnswer);

        user.save();

        System.out.println("\nUser Registered Successfully");
    }

    public static Boolean login (State state) {

        System.out.print("\nEnter Username: ");
        String username = reader.readString();
        System.out.print("Enter Password: ");
        String password = reader.readString();

        String userData = fileReadWrite.readFile("USER");
        String[] userDataArray = userData.split("\n");

        String hashedPassword = hashPassword(password);
        for (String user : userDataArray) {
            String[] userArray = user.split(",");
            if (username.equals(userArray[0]) && hashedPassword.equals(userArray[1])) {

                System.out.print("Please answer the security question \"" + userArray[2] + "\": ");
                String securityAnswer = reader.readString();

                if (securityAnswer.equals(userArray[3])) {
                    state.setUserName(username);
                    System.out.println("\nLogin Successful");
                    return true;
                }
            }
        }

        System.out.println("\nLogin Failed");
        return false;
    }

    public static String hashPassword (String password) {
        try {
            byte[] bytesOfMessage = password.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] theMD5digest = md.digest(bytesOfMessage);
            BigInteger bigInt = new BigInteger(1,theMD5digest);
            String hashedPassword = bigInt.toString(16);
            while(hashedPassword.length() < 32 ){
                hashedPassword = "0"+hashedPassword;
            }
            return hashedPassword;
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
        return null;
    }
}
