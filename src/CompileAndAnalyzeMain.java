import com.intellij.compiler.impl.CompileContextImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.project.Project;

/**
 * Created by Daniel Lin on 2016/10/6.
 */
public class CompileAndAnalyzeMain extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent e) {

        Project myProject = e.getData(PlatformDataKeys.PROJECT);

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
                    System.out.println(errorMessage[0].getMessage());
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

            }
        });



    }
}
