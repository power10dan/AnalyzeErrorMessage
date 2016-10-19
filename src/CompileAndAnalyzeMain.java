import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.project.Project;

import java.util.Random;

/**
 * Created by Daniel Lin on 2016/10/6.
 */
public class CompileAndAnalyzeMain extends AnAction {
    private static final int NUM_COMPILATION_DETAILS = 4;

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project myProject = e.getData(PlatformDataKeys.PROJECT);
        // generate random session ID for each participant
        // during each start of the program
        int randomID = 0;
        Random r = new Random( System.currentTimeMillis() );
        randomID = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        final int randomIdentity = randomID;

        final CompilerManager myCompilerManager = CompilerManager.getInstance(myProject);
        final long getCompilationStartTime = System.currentTimeMillis();

        myCompilerManager.make(myCompilerManager.createProjectCompileScope(myProject), new CompileStatusNotification() {
            @Override
            public void finished(final boolean aborted, final int errors, final int warnings, final CompileContext compileContext) {
                CompilerMessage [] errorMessage = new CompilerMessage[errors];
                CompilerMessage [] warningMessages = new CompilerMessage[warnings];
                // store number of errors, warnings, and the number of compilation attempts
                // attempted so far
                int status = 0;

                if(errors > 0) {
                    errorMessage = compileContext.getMessages(CompilerMessageCategory.ERROR);
                }

                if(warnings > 0){
                    warningMessages = compileContext.getMessages(CompilerMessageCategory.WARNING);
                }

                if(warnings == 0 || errors == 0){
                    status = 1;
                }

                // get current time when compilation is finished
                final long compilationFinishTime = System.currentTimeMillis();
                // actual time solving problem minus compilation time
                final long timeDiff = compilationFinishTime - getCompilationStartTime;

                // gather additional data about the current compilation session
                int [] compilationDetails = new int[NUM_COMPILATION_DETAILS];
                int randomIDFinished = 0;
                randomIDFinished = randomIdentity;
                compilationDetails[0] = randomIDFinished;
                compilationDetails[1] = errors;
                compilationDetails[2] = warnings;
                compilationDetails[3] = status;





            }
        });

    }
}
