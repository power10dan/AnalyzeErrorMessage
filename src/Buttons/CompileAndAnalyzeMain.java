package Buttons;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

/**
 * Created by Daniel Lin on 2016/10/6.
 */
public class CompileAndAnalyzeMain extends AnAction {

    @Override
    public void actionPerformed(AnActionEvent e) {

        Project myProject = e.getData(PlatformDataKeys.PROJECT);
        Listeners listeners = new Listeners(myProject);
        // gets editor lint messages
        listeners.addDaemonListener(e);
        // get build messages
        listeners.buildMessageListener(myProject);
        // listen to debugger events

        // make our project and obtain the compilation
        // errors during each session of compilation
        listeners.compilationListener(myProject);
    }


}
