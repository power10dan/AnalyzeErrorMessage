package UnitTests;

import CleanData.DataContainer;
import MySQLOperations.MySQLOperations;
import com.intellij.openapi.compiler.CompileContext;
import org.junit.Test;

/**
 * Created by daniellin on 2016/10/18.
 */
public class ConnectToTDatabaseTest {

    private CompileContext[] errors;
    private CompileContext [] warnings;
    private float timeThinking;
    private int randomID;
    private int [] compileContext;
    private int warningID = 123;
    private int errorID = 345;


    @Test
    public void connectToDatabaseTest(){
        DataContainer dataContainer = new DataContainer(errors, warnings, timeThinking, randomID, compileContext, warningID, errorID);
        MySQLOperations mysqlTester = new MySQLOperations("com.mysql.jdbc.Driver",
                                                          "jdbc:mysql://localhost:3306/USERCOMPILATIONDATA",
                                                           "root", "kec1148!");
        try {
            mysqlTester.connectToDatabase(dataContainer, false);
        } catch(Exception e){
            e.printStackTrace();
        }

    }

    //@Test
    //public void createTableAndInsertIntoTableTest(){




    //}

}
