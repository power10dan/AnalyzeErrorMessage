package UnitTests;

import CleanData.DataContainer;
import MySQLOperations.MySQLOperations;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessage;
import org.junit.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Created by daniellin on 2016/10/18.
 *
 * ConnectionToDatabaseTest contains unit tests
 * for methods found in MySQLOperations.
 *
 */
public class ConnectToTDatabaseTest {

    private CompilerMessage[] errors;
    private CompilerMessage[] warnings;
    private long timeThinking;
    private int randomID;
    private int [] compileContext;
    private int warningID = 123;
    private int errorID = 345;

    private boolean returnValue = true;
    private Connection connection = null;
    private DataContainer dataContainer = null;
    private MySQLOperations mysqlTester;

    @Test
    public void testDatabaseConnection() throws SQLException{
        dataContainer = populateWithDummyVariable();
        mysqlTester = new MySQLOperations("com.mysql.jdbc.Driver",
                                                          "jdbc:mysql://localhost:3306/USERCOMPILATIONDATA",
                                                           "root", "kec1148!");
        try {
             connection =  mysqlTester.setUpConnection();
        } catch(Exception e) {
            e.printStackTrace();
            assertNotEquals(connection, null);
        } finally{
            connection.close();
        }
    }

   /* @Test
    public void testInitDataBase() throws SQLException{
        DataContainer dataContainer = populateWithDummyVariable();
        mysqlTester = new MySQLOperations("com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/USERCOMPILATIONDATA",
                "root", "kec1148!");

        try {

            connection = mysqlTester.setUpConnection();
            returnValue = mysqlTester.initDatabase(dataContainer, false, connection);

        } catch(Exception e){
            assertEquals(returnValue, true);
            e.printStackTrace();
        } finally {
            connection.close();
        }

    }*/

    /*@Test
    public void testGetErrorForeignKey() throws SQLException{
        DataContainer dataContainer = populateWithDummyVariable();
        mysqlTester = new MySQLOperations("com.mysql.jdbc.Driver",
                "jdbc:mysql://localhost:3306/USERCOMPILATIONDATA",
                "root", "kec1148!");
        try {

            connection = mysqlTester.setUpConnection();
            returnValue = mysqlTester.sendToDatabase(dataContainer, false, connection);

            if(returnValue == true){
                //insertDummyVariablesIntoErrorAndWarningTable();
                getForeignKeyErrorTester(mysqlTester);
                getForeignKeyWarningTester(mysqlTester);
            }

        } catch(Exception e){
            e.printStackTrace();
            assertEquals(returnValue, true);
        } finally{
            connection.close();
        }
    }*/

    private DataContainer populateWithDummyVariable(){
        compileContext = new int[6];
        compileContext[0] = 23;
        compileContext[1] = 44;
        compileContext[2] = 55;
        compileContext[3] = 23;
        compileContext[4] = 2;
        compileContext[5] = 23;

        randomID = 123456;
        timeThinking = 233333;

        return new DataContainer(errors, warnings, timeThinking, compileContext);

    }


    private void getForeignKeyErrorTester(MySQLOperations mysqlTester) throws SQLException {

        Queue queue = mysqlTester.getErrorForeignKey(connection);

        assertNotEquals(queue, null);

        int foreignKeyErrorID = 0;

        String getErrorIDStatement = "SELECT errorTableKeyID FROM errorsTable;";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(getErrorIDStatement);

        while(rs.next()){
            foreignKeyErrorID = rs.getInt("errorTableKeyID");
            assertEquals(foreignKeyErrorID, queue.element());
        }
    }

    private void getForeignKeyWarningTester(MySQLOperations mysqlTester) throws SQLException {

        Queue queue = mysqlTester.getWarningForeignKey(connection);

        assertNotEquals(queue, null);

        int foreignKeyWarningID = 0;

        String getErrorIDStatement = "SELECT warningTableKeyID FROM warningsTable;";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(getErrorIDStatement);

        while(rs.next()){
            foreignKeyWarningID = rs.getInt("warningTableKeyID");
            assertEquals(foreignKeyWarningID, queue.element());
        }
    }

    private void insertDummyVariablesIntoErrorAndWarningTable() throws SQLException{
        String errorType= " \"Error\" ";
        String warningType = " \"Warning \" ";

        String testStatementOne = "INSERT INTO errorsTable(errorTableID, errorTableKeyID, compilationErrorType, compilationErrorMessage, compilationAttempt) "
                                    + "VALUES(23, 45," + errorType + ", \"java.NullPointerException\", 1);";

        String testStatementTwo = "INSERT INTO errorsTable(errorTableID, errorTableKeyID, compilationErrorType, compilationErrorMessage, compilationAttempt) "
                + "VALUES(24, 45," + errorType + ", \"java.NullPointerExceptionStuff\", 1);";

        String testStatementThree = "INSERT INTO errorsTable(errorTableID, errorTableKeyID, compilationErrorType, compilationErrorMessage, compilationAttempt) "
                + "VALUES(25, 45," + errorType + ", \"java.classNotFound\", 2);";

        String testStatementFour = "INSERT INTO warningsTable(warningID, warningTableKeyID, warningType , warningMessage, compilationAttempt) "
                + "VALUES(23, 45," + warningType + ", \"don't do this\", 1);";
        String testStatementFive = "INSERT INTO warningsTable(warningID, warningTableKeyID, warningType, warningMessage, compilationAttempt) "
                + "VALUES(24, 45," + warningType + ", \"don't do that\", 1);";
        String testStatementSix = "INSERT INTO warningsTable(warningID, warningTableKeyID, warningType, warningMessage, compilationAttempt) "
                + "VALUES(25, 45," + warningType + ", \"stop doing this\", 2);";


        Statement sqlStatement = connection.createStatement();

        sqlStatement.execute(testStatementOne);
        sqlStatement.execute(testStatementTwo);
        sqlStatement.execute(testStatementThree);
        sqlStatement.execute(testStatementFour);
        sqlStatement.execute(testStatementFive);
        sqlStatement.execute(testStatementSix);
    }
}
