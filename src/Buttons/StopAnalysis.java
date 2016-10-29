package Buttons;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

import java.util.ArrayList;

/**
 * Created by Daniel Lin  on 2016/10/28.
 */
public class StopAnalysis extends AnAction {
    @Override
    public void actionPerformed(AnActionEvent anActionEvent) {
        // if this button is pressed, this means that the study
        // has concluded. We create another copy of the data
        // dump, sanitize them, and output to a new file
        // called sanitized data dump for analysis.






    }

    private void cleanUpData(ArrayList<String> data){


    }
}
