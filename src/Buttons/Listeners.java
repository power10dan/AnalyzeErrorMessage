package Buttons;

import CleanData.DataContainer;
import com.intellij.codeInsight.daemon.impl.DaemonCodeAnalyzerImpl;
import com.intellij.codeInsight.daemon.impl.HighlightInfo;
import com.intellij.execution.Executor;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.execution.ui.RunContentDescriptor;
import com.intellij.execution.ui.RunContentManager;
import com.intellij.execution.ui.RunContentWithExecutorListener;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.compiler.*;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Random;

/**
 * Created by Daniel Lin on 2016/10/28.
 *
 * Listeners attached to the IDE to collect user data
 *
 */
public class Listeners {

    private Project myProject;
    private static final int NUM_COMPILATION_DETAILS = 6;

    public Listeners(Project project){
        myProject = project;
    }

    // get run time error listener,
    // listens for stderr output on the run tab console
    public void buildMessageListener(Project myProject){
        Random r = new Random( System.currentTimeMillis() );
        int randomID = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));

        myProject.getMessageBus().connect().subscribe(RunContentManager.TOPIC, new RunContentWithExecutorListener() {
            @Override
            public void contentSelected(@Nullable RunContentDescriptor runContentDescriptor, @NotNull Executor executor) {
                ProcessHandler processHandler =  runContentDescriptor.getProcessHandler();
                WiseThreadDumpListener wiseThreadDumpListener = new WiseThreadDumpListener(myProject, processHandler);
                wiseThreadDumpListener.getRunTimeError(randomID);
            }

            @Override
            public void contentRemoved(@Nullable RunContentDescriptor runContentDescriptor, @NotNull Executor executor) {

            }
        });
    }

    // if user initiate debugger session, this method will
    // listen for debugger events.
    public void debuggerListener(Project myProject, String existingFileName){





    }

    // listens for errors caused when running make and output
    // them to a file
    public void compilationListener(Project myProject){
        // generate random session ID for each participant
        // during each start of the program
        int randomID = 0;
        Random r = new Random( System.currentTimeMillis() );
        randomID = ((1 + r.nextInt(2)) * 10000 + r.nextInt(10000));
        final int randomIdentity = randomID;

        final MessageBusConnection conn = myProject.getMessageBus().connect();

        // listen for COMPILATION_STATUS...
        // When Compilation is done, conn will be able
        // to get the compilation error messages and warnings.
        conn.subscribe(CompilerTopics.COMPILATION_STATUS, new CompilationStatusListener() {
            @Override
            public void compilationFinished(boolean aborted, int errors, int warnings, CompileContext compileContext) {
                CompilerMessage[] errorMessage = new CompilerMessage[errors];
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

                // Time took to produce a compiled code
                final long timeFinish = System.currentTimeMillis();

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

                DataContainer dataContainer = new DataContainer( errorMessage, warningMessages, timeFinish, compilationDetails);
                // output to file
                dataContainer.outPutToFile();
            }
        });

    }

    // listens for errors and warnings that are generated
    // by the daemon code analyzer ran in the background
    // process
    public void addDaemonListener(AnActionEvent anActionEvent){
        Project myProject = anActionEvent.getData(PlatformDataKeys.PROJECT);
        Editor editor = anActionEvent.getData(PlatformDataKeys.EDITOR);

        Document document = editor.getDocument();

        if(document == null){
            return;
        }

        final Disposable connection = Disposer.newDisposable();
        final MessageBusConnection bus = myProject.getMessageBus().connect(connection);

        bus.subscribe(com.intellij.codeInsight.daemon.DaemonCodeAnalyzer.DAEMON_EVENT_TOPIC,
                new com.intellij.codeInsight.daemon.DaemonCodeAnalyzer.DaemonListenerAdapter(){
                    @Override
                    public void daemonFinished() {
                        getLintOnEditor(myProject, document);

                    }
                });
    }

    // method to get the lint errors and warnings and output them to a file
    private void getLintOnEditor(Project myProject, Document document){
        List<HighlightInfo> errors = DaemonCodeAnalyzerImpl.getHighlights(document, HighlightSeverity.ERROR, myProject);
        List<HighlightInfo> warnings = DaemonCodeAnalyzerImpl.getHighlights(document, HighlightSeverity.WARNING, myProject);
        // sanitize data and remove duplicates in both the errors and warnings List
        for(HighlightInfo err : errors){
            System.out.println(err.getDescription());
        }

    }

}
