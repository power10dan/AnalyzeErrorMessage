package CleanData;

import com.intellij.openapi.compiler.CompileContext;
import com.sun.org.apache.xalan.internal.xsltc.cmdline.Compile;

/**
 * Created by daniellin on 2016/10/14.
 */
public class DataContainer {
    private CompileContext [] errors;
    private CompileContext [] warnings;
    private float timeThinking;
    private int randomID;
    private int [] compileContext;
    private int warningIDs;
    private int errorIDs;

    public DataContainer(CompileContext [] errors, CompileContext [] warnings,
                         float timeThinking, int randomID, int [] compileContext, int warningIDs, int errorIDs){
        this.errors = errors;
        this.warnings = warnings;
        this.timeThinking = timeThinking;
        this.randomID = randomID;
        this.compileContext = compileContext;
        this.warningIDs = warningIDs;
        this.errorIDs = errorIDs;


    }

    // getter methods

    public CompileContext [] getErrors(){
        return this.errors;
    }

    public CompileContext [] getWarnings(){
        return this.warnings;
    }

    public float getTimeThinking(){
        return this.timeThinking;
    }

    public int getID(){
        return this.randomID;
    }

    public int [] getCompileContext(){return this.compileContext;}

    public int getWarningIDs() {return this.warningIDs;}
    public int getErrorIDs() { return this.errorIDs;}

}
