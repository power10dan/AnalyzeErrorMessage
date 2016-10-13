import com.intellij.compiler.impl.CompileContextImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.project.Project;

/**
 * Created by Daniel Lin on 2016/10/6.
 */
public class CompileAndAnalyzeMain extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        Project myProject = e.getData(PlatformDataKeys.PROJECT);
        CompilationStatusListener myCompilationStatusListener = new CompilationStatusListener() {
            // when compilation is finished,
            // we want to get the error messages from the compileContext
            // and analyze them.
            @Override
            public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                  CompilerMessage [] errorMessage = new CompilerMessage[errors];
                  CompilerMessage [] warningMessages = new CompilerMessage[warnings];
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

                  // get current time when finished...
                  // we define project complete duration as
                  // the entire time, including building time.
                  final long actualProblemSolvingTime = System.currentTimeMillis();
            }
        };

        final CompilerManager myCompilerManager = CompilerManager.getInstance(myProject);
        // add compilation listener to our project
        myCompilerManager.addCompilationStatusListener(myCompilationStatusListener, myProject);

    }
}
