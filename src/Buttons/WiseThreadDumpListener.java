package Buttons;

import com.intellij.execution.process.CapturingProcessAdapter;
import com.intellij.execution.process.ProcessHandler;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.unscramble.ThreadDumpParser;
import com.intellij.unscramble.ThreadState;
import com.intellij.util.TimeoutUtil;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Created by Daniel Lin on 2016/10/27.
 *
 * This class is responsible for getting stderr
 * during user's dev session.
 *
 *
 */
public  class WiseThreadDumpListener {
    private final Project myProject;
    private final ProcessHandler myProcessHandler;
    private final CapturingProcessAdapter myListener;
    private final ArrayList<String> errorContainer = new ArrayList<>();


    public WiseThreadDumpListener(Project project, ProcessHandler processHandler) {
        myProject = project;
        myProcessHandler = processHandler;
        myListener = new CapturingProcessAdapter();
        myProcessHandler.addProcessListener(myListener);
    }

    public void  getRunTimeError(int userID) {
        if (myProject == null) {
            myProcessHandler.removeProcessListener(myListener);
            return;
        }

        ApplicationManager.getApplication().executeOnPooledThread(() -> {
            if (myProcessHandler.isProcessTerminated() || myProcessHandler.isProcessTerminating()){
                return;
            }

            final long start = System.currentTimeMillis();

            while ((System.currentTimeMillis() - start) < 2000) {
                final String stdout = myListener.getOutput().getStderr();
                errorContainer.add(stdout);
            }

            myProcessHandler.removeProcessListener(myListener);

            try {
                // remove duplicate entries
                // inside our error container
                Set<String> hs = new HashSet<>();
                hs.addAll(errorContainer);
                errorContainer.clear();
                errorContainer.addAll(hs);
                // write cleaned data to file
                writeToFile(userID);
            } catch(Exception e){
                e.printStackTrace();
            }

        });

    }

    public void writeToFile(int userID) throws IOException{
        final long timeStampErr = System.currentTimeMillis();

        String fileName = "/Users/daniellin/Desktop/RunSession" + Integer.toString(userID) + ".txt";
        File file = new File(fileName);
        BufferedWriter out = new BufferedWriter(new FileWriter(fileName, true));
        out.write(Long.toString(timeStampErr));
        out.write("\n");

        for (String err : errorContainer) {
            out.write(err);
        }

        out.close();

    }
}