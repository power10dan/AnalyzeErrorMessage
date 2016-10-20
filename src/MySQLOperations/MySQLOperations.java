package MySQLOperations;

import CleanData.DataContainer;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessage;

import java.sql.*;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;

/**
 * Created by Daniel Lin  on 2016/10/15.
 *
 */

public class MySQLOperations {

    private static String mysqlDataDriverPath = "";
    private static String dbURL = "";
    private static String userName = "";
    private static String passWord = "";
    private Connection connection = null;

    private String mySQLCompilationAttemptUpdateStatementProfile = "";
    private String insertUserProfileStatement = "";
    private String insertErrorStatement = "";
    private String insertWarningStatement = "";

    public MySQLOperations(String mysqlDataDriverPath, String dbURL, String userName, String passWord){
        this.mysqlDataDriverPath = mysqlDataDriverPath;
        this.dbURL = dbURL;
        this.userName = userName;
        this.passWord = passWord;
    }

    public Connection setUpConnection() throws ClassNotFoundException, SQLException{

        System.out.println("Connecting to remote database");
        Class.forName(mysqlDataDriverPath);

        return DriverManager.getConnection(dbURL, userName, passWord);
    }

    public boolean sendToDatabase(DataContainer data, boolean isSnapShot, Connection connection) throws SQLException, ClassNotFoundException {
        try {

            if(connection != null){
                System.out.println("Connection to remote MySQL database established");
            } else{
                System.out.println("Database Connection Failed");
                return false;
            }

            Statement sqlStatement = connection.createStatement();
            System.out.println("database connected, creating table...");

            // query statements

            String userProfileTable = "CREATE TABLE IF NOT EXISTS userCompilationProfile" +
                        "(randomID INT NOT NULL, " +
                        "thinkTime BIGINT DEFAULT NULL, " +
                        "numberOfErrors INT(11) DEFAULT NULL, " +
                        "numberOfWarnings INT(11) DEFAULT NULL, " +
                        "compilationAttempt INT(11) DEFAULT 0, " +
                        "status INT(11) DEFAULT NULL, " +
                        "errorTableUserID INT(11) DEFAULT NULL, " +
                        "warningTableUserID INT(11) DEFAULT NULL, " +
                        "PRIMARY KEY (randomID)" +
                        ")ENGINE=InnoDB;";

            String errorTable = "CREATE TABLE IF NOT EXISTS errorsTable" +
                    "(errorTableID INT NOT NULL AUTO_INCREMENT, " +
                    "errorTableUserID INT(11) DEFAULT NULL, " +
                    "compilationErrorType VARCHAR(255) DEFAULT  NULL, " +
                    "compilationErrorMessage VARCHAR(255) DEFAULT NULL," +
                    "PRIMARY KEY (errorTableID)" +
                    ")ENGINE=InnoDB;";

            String warningTable = "CREATE TABLE IF NOT EXISTS warningsTable" +
                    "(warningID INT NOT NULL AUTO_INCREMENT, " +
                    "warningTableUserID INT (11) DEFAULT NULL, " +
                    "warningType VARCHAR(255) DEFAULT NULL, " +
                    "warningMessage VARCHAR(255) DEFAULT NULL, " +
                    "PRIMARY KEY (warningID)" +
                    ")ENGINE=InnoDB;";

            // create our tables in the mySQL database
            sqlStatement.execute(errorTable);
            sqlStatement.execute(warningTable);
            sqlStatement.execute(userProfileTable);


            if(data.getErrors() == null || data.getWarnings() == null){
                System.out.println("Both error arrays and warning arrays are  null, please check them");
                connection.close();
                return false;
            }

            if(data.getErrors().length > 0 && data.getWarnings().length > 0 ) {
                insertErrors(sqlStatement, data, isSnapShot, connection);
                insertWarnings(sqlStatement, data, isSnapShot, connection);
            }

            if(data.getNumberOfWarnings() > 0 ){
                insertWarnings(sqlStatement, data, isSnapShot, connection);
            }

            if(data.getNumberOfErrors() > 0){
                insertErrors(sqlStatement, data, isSnapShot, connection);
            }


            insertUserProfile(sqlStatement, data, isSnapShot, connection);

        } catch (Exception e){
            e.printStackTrace();

        }finally {
            connection.close();
            return true;
        }

    }

    public void insertUserProfile(Statement sqlInserter, DataContainer data, boolean isSnapShot, Connection conn) throws SQLException{
        mySQLCompilationAttemptUpdateStatementProfile = "UPDATE  userCompilationProfile" + " SET compilationAttempt = compilationAttempt + 1";
        // make some prepared statements
        // to stream line insertion and update operations
        PreparedStatement preparedInsertionStatement = null;

        insertUserProfileStatement = "INSERT INTO userCompilationProfile (randomID, thinkTime, numberOfErrors, numberOfWarnings, status, errorTableUserID, warningTableUserID)"
                                      + " VALUES (?, ?, ?, ?, ?, ?, ?);";

        preparedInsertionStatement = conn.prepareStatement(insertUserProfileStatement);

        int [] compileContext = data.getCompileContext();
        // insert our user compilation profile data into MySQL
        preparedInsertionStatement.setInt(1, data.getID());
        preparedInsertionStatement.setFloat(2, data.getTimeThinking());
        preparedInsertionStatement.setInt(3, data.getNumberOfErrors());
        preparedInsertionStatement.setInt(4, data.getNumberOfWarnings());
        preparedInsertionStatement.setInt(5, data.getStatus());
        // insert the error and warning ids, both are
        // foreign keys to warnings and errors tables
        preparedInsertionStatement.setInt(6, data.getErrorIDs());
        preparedInsertionStatement.setInt(7, data.getWarningIDs());
        preparedInsertionStatement.execute();

        if(isSnapShot == false) {
            sqlInserter.execute(mySQLCompilationAttemptUpdateStatementProfile);
        }
    }

    // insert user errors
    public void insertErrors(Statement sqlInserter, DataContainer data, boolean isSnapShot, Connection conn) throws SQLException{
        insertErrorStatement  = "INSERT INTO errorsTable (errorTableID, errorTableUserID, compilationErrorType, compilationErrorMessage) VALUES (?,?,?, ?)";

        PreparedStatement preparedInsertErrStatement = conn.prepareStatement(insertErrorStatement);

        CompilerMessage[] errors = data.getErrors();

        for(int i = 0; i < errors.length; ++i){
            Random r = new Random( System.currentTimeMillis() + i);
            int randomID = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));

            preparedInsertErrStatement.setInt(1, randomID);
            preparedInsertErrStatement.setInt(2, data.getErrorIDs());
            preparedInsertErrStatement.setString(3, "Compilation Error");
            preparedInsertErrStatement.setString(4, errors[i].toString());
            preparedInsertErrStatement.executeUpdate();

        }

    }

    // insert user warnings
    public void insertWarnings(Statement sqlInserter, DataContainer data, boolean isSnapShot, Connection conn) throws SQLException{
        insertWarningStatement = "INSERT INTO warningsTable (warningID, warningTableUserID, warningType, warningMessage) VALUES (?,?,?, ?)";

        PreparedStatement preparedInsertErrStatement = conn.prepareStatement(insertWarningStatement);
        CompilerMessage [] warnings = data.getWarnings();

        for(int i = 0; i < warnings.length; ++i){
            Random r = new Random( System.currentTimeMillis() + i);
            int randomID = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));

            preparedInsertErrStatement.setInt(1, randomID);
            preparedInsertErrStatement.setInt(2, data.getWarningIDs());
            preparedInsertErrStatement.setString(3, "Warnings");
            preparedInsertErrStatement.setString(4, warnings[i].toString());
            preparedInsertErrStatement.executeUpdate();
        }

    }

    public Queue getErrorForeignKey(Connection connection) throws SQLException{
        String getErrorIDStatement = "SELECT errorTableKeyID FROM errorsTable;";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(getErrorIDStatement);
        int errorID = 0;
        // put our queried items into a queue
        Queue errorQueue = new LinkedList();

        while(rs.next()){
            errorID = rs.getInt("errorTableKeyID");
            errorQueue.add(errorID);
        }

        return errorQueue;
    }

    public Queue getWarningForeignKey(Connection connection) throws SQLException{
        String getErrorIDStatement = "SELECT warningTableKeyID FROM warningsTable;";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(getErrorIDStatement);

        int warningID = 0;
        // put our queried items into a queue
        Queue warningQueue = new LinkedList();

        while(rs.next()){
            warningID = rs.getInt("warningTableKeyID");
            warningQueue.add(warningID);
        }

        return warningQueue;
    }
}
