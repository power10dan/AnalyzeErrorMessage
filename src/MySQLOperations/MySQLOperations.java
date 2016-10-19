package MySQLOperations;

import CleanData.DataContainer;
import com.intellij.openapi.compiler.CompileContext;

import java.sql.*;

/**
 * Created by Daniel Lin  on 2016/10/15.
 *
 */

public class MySQLOperations {

    private static String mysqlDataDriverPath = "";
    private static String dbURL = "";
    private static String userName = "";
    private static String passWord = "";
    private Connection connection;

    private String mySQLCompilationAttemptUpdateStatement = "";
    private String insertUserProfileStatement = "";
    private String insertErrorStatement = "";
    private String insertWarningStatement = "";

    public MySQLOperations(String mysqlDataDriverPath, String dbURL, String userName, String passWord){
        this.mysqlDataDriverPath = mysqlDataDriverPath;
        this.dbURL = dbURL;
        this.userName = userName;
        this.passWord = passWord;
    }

    public void connectToDatabase(DataContainer data, boolean isSnapShot) throws SQLException, ClassNotFoundException {
        try {

            System.out.println("Connecting to remote database");

            Class.forName(mysqlDataDriverPath);
            connection = DriverManager.getConnection(dbURL, userName, passWord);

            Statement sqlStatement = connection.createStatement();
            System.out.println("database connected, creating table...");

            // query statements

            String userProfileTable = "CREATE TABLE IF NOT EXISTS userCompilationProfile" +
                        "(randomID INT NOT NULL, " +
                        "thinkTime FLOAT(20) DEFAULT NULL, " +
                        "numberOfErrors INT(11) DEFAULT NULL, " +
                        "numberOfWarnings INT(11) DEFAULT NULL, " +
                        "compilationAttempt INT(11) DEFAULT 0, " +
                        "errorTableKey INT(11) DEFAULT NULL, " +
                        "warningTableKey INT(11) DEFAULT NULL, " +
                        "PRIMARY KEY (randomID), " +
                        "FOREIGN KEY (errorTableKey) REFERENCES errorsTable(errorTableID) " +
                        "ON UPDATE CASCADE ON DELETE RESTRICT," +
                        "FOREIGN KEY (warningTableKey) REFERENCES warningsTABLE(warningID) " +
                        "ON UPDATE CASCADE ON DELETE RESTRICT" +
                        ")ENGINE=InnoDB;";

            String errorTable = "CREATE TABLE IF NOT EXISTS errorsTable" +
                    "(errorTableID INT NOT NULL, " +
                    "compilationErrorType VARCHAR(255) DEFAULT  NULL, " +
                    "compilationErrorMessage VARCHAR(255) DEFAULT NULL," +
                    "PRIMARY KEY(errorTableID)" +
                    ")ENGINE=InnoDB;";

            String warningTable = "CREATE TABLE IF NOT EXISTS warningsTable" +
                    "(warningID INT NOT NULL, " +
                    "warningType VARCHAR(255) DEFAULT NULL, " +
                    "warningMessage VARCHAR(255) DEFAULT NULL, " +
                    "PRIMARY KEY (warningID)" +
                    ")ENGINE=InnoDB;";

            // create our tables in the mySQL database
            sqlStatement.execute(errorTable);
            sqlStatement.execute(warningTable);
            sqlStatement.execute(userProfileTable);

            // insert user profile data into mySQL
            insertUserProfile(sqlStatement, data, isSnapShot);

            if(data.getErrors().length > 0 && data.getWarnings().length > 0 ) {
                insertErrors(sqlStatement, data);
                insertWarnings(sqlStatement, data);
            }

            if(data.getWarnings().length > 0 && data.getErrors().length == 0){
                insertWarnings(sqlStatement, data);

            }

            if(data.getErrors().length > 0 && data.getWarnings().length == 0){
                insertErrors(sqlStatement, data);

            }

        } catch (Exception e){
            e.printStackTrace();

        }finally {
            connection.close();
        }

    }


    private void insertUserProfile(Statement sqlInserter, DataContainer data, boolean isSnapShot) throws SQLException{
        mySQLCompilationAttemptUpdateStatement = "UPDATE userData" + " SET compilationAttempt = compilationAttempt + 1";

        // make some prepared statements
        // to stream line insertion and update operations
        PreparedStatement preparedInsertionStatement = null;

        insertUserProfileStatement = "INSERT INTO  userCompilationDataProfile  VALUES(?,?,?,?,?, ?, ?)";
        preparedInsertionStatement = connection.prepareStatement(insertUserProfileStatement);

        int [] compileContext = data.getCompileContext();
        // insert our user compilation profile data into MySQL
        preparedInsertionStatement.setInt(1, data.getID());
        preparedInsertionStatement.setFloat(2, data.getTimeThinking());
        preparedInsertionStatement.setInt(3, compileContext[1]);
        preparedInsertionStatement.setInt(4, compileContext[2]);
        preparedInsertionStatement.setInt(5, compileContext[3]);
        // insert the error and warning ids
        preparedInsertionStatement.setInt(6, data.getErrorIDs());
        preparedInsertionStatement.setInt(7, data.getWarningIDs());

        if(isSnapShot == true) {
            sqlInserter.execute(mySQLCompilationAttemptUpdateStatement);
        }


    }

    // insert user errors
    private void insertErrors(Statement sqlInserter, DataContainer data) throws SQLException{
        insertWarningStatement  = "INSERT INTO warnings VALUES(?,?,?)";
        PreparedStatement preparedInsertErrStatement = connection.prepareStatement(insertWarningStatement);

        CompileContext [] errors = data.getErrors();

        for(int i = 0; i < errors.length; ++i){
            preparedInsertErrStatement.setInt(1, data.getErrorIDs());
            preparedInsertErrStatement.setString(2, "Compilation Error");
            preparedInsertErrStatement.setString(3, errors[i].toString());
        }
    }

    // insert user warnings
    private void insertWarnings(Statement sqlInserter, DataContainer data) throws SQLException{
        insertErrorStatement = "INSERT INTO warnings VALUES(?,?,?)";
        PreparedStatement preparedInsertErrStatement = connection.prepareStatement(insertWarningStatement);
        CompileContext [] warnings = data.getWarnings();

        for(int i = 0; i < warnings.length; ++i){
            preparedInsertErrStatement.setInt(1, data.getErrorIDs());
            preparedInsertErrStatement.setString(2, "Warnings");
            preparedInsertErrStatement.setString(3, warnings[i].toString());
        }
    }
}
