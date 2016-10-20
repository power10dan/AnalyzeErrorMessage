import CleanData.DataContainer;
import MySQLOperations.MySQLOperations;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.project.Project;

import java.sql.Connection;
import java.util.Random;

/**
 * Created by Daniel Lin on 2016/10/6.
 */
public class CompileAndAnalyzeMain extends AnAction {
    private static final int NUM_COMPILATION_DETAILS = 6;
    private MySQLOperations mySQLOperations;
    private Connection conn;

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project myProject = e.getData(PlatformDataKeys.PROJECT);

        // generate random session ID for each participant
        // during each start of the program
        int randomID = 0;
        Random r = new Random( System.currentTimeMillis() );
        randomID = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        final int randomIdentity = randomID;

        mySQLOperations = new MySQLOperations("com.mysql.jdbc.Driver", "jdbc:mysql://localhost:3306/USERCOMPILATIONDATA", "", "");

        final CompilerManager myCompilerManager = CompilerManager.getInstance(myProject);
        final long getCompilationStartTime = System.currentTimeMillis();

        myCompilerManager.make(myCompilerManager.createProjectCompileScope(myProject), new CompileStatusNotification() {
            @Override
            public void finished(final boolean aborted, final int errors, final int warnings, final CompileContext compileContext) {
                CompilerMessage [] errorMessage = new CompilerMessage[errors];
                CompilerMessage [] warningMessages = new CompilerMessage[warnings];

                int status = 0;

                if(errors > 0) {
                    errorMessage = compileContext.getMessages(CompilerMessageCategory.ERROR);
                }

                if(warnings > 0){
                    warningMessages = compileContext.getMessages(CompilerMessageCategory.WARNING);
                }

                if(warnings == 0 &&  errors == 0){
                    status = 1;
                }

                // get current time when compilation is finished
                final long compilationFinishTime = System.currentTimeMillis();
                // actual time solving problem minus compilation time
                final long timeThinking = compilationFinishTime - getCompilationStartTime;

                // gather additional data about the current compilation session
                int [] compilationDetails = new int[NUM_COMPILATION_DETAILS];
                int randomID = 0;
                randomID = randomIdentity;

                Random r = new Random( System.currentTimeMillis() );
                int warningID  = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
                int errorID = warningID;

                compilationDetails[0] = randomID;
                compilationDetails[1] = errors;
                compilationDetails[2] = warnings;
                compilationDetails[3] = status;
                compilationDetails[4] = warningID;
                compilationDetails[5] = errorID;


                DataContainer dataContainer = new DataContainer( errorMessage, warningMessages, timeThinking, compilationDetails);

                try {
                    conn = mySQLOperations.setUpConnection();
                    boolean success = mySQLOperations.sendToDatabase(dataContainer, false, conn);

                    if(success){
                        System.out.println("Data sent to database");
                    }
                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

    }
}
