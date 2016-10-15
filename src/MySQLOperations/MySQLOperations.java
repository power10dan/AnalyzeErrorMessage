package MySQLOperations;

import CleanData.DataContainer;

import java.sql.*;
/**
 * Created by Daniel Lin  on 2016/10/15.
 */
public class MySQLOperations {
    private static String mysqlDataDriverPath = "";
    private static String dbURL = "";
    private static String userName = "";
    private static String passWord = "";

    public MySQLOperations(String mysqlDataDriverPath, String dbURL, String userName, String passWord){
        this.mysqlDataDriverPath = mysqlDataDriverPath;
        this.dbURL = dbURL;
        this.userName = userName;
        this.passWord = passWord;
    }

    public void connectToDatabase(DataContainer data, int firstTime){
        try{
            System.out.println("Connecting to remote database");
            Connection connection = DriverManager.getConnection(dbURL, userName, passWord);
            Statement sqlStatement = connection.createStatement();

            // MySQL execution statement
            int key = data.getID();
            float thinkingTime = data.getTimeThinking();

            // if we are initiating
            // database for the first time,
            // create our table

            if(firstTime == 0){
                String mySQLStatement = "CREATE TABLE IF NOT EXISTS userData" +
                                        "(randomID INT(11) NOT NULL, " +
                                        "error VARCHAR(255) DEFAULT NULL, " +
                                        "warning VARCHAR(255) DEFAULT NULL,  " +
                                        "thinkTIME FLOAT(20) DEFAULT NULL, " +
                                        "numberOfErrors INT(11) DEFAULT NULL, " +
                                        "numberOfWarnings INT(11) DEFAULT NULL," +
                                        "compilationAttempt INT(11) DEFAULT NULL" +
                                        ");";

                sqlStatement.execute(mySQLStatement);
            }




        } catch(SQLException se){

        } finally {

        }

    }

}
