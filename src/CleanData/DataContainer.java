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

    public DataContainer(CompileContext [] errors, CompileContext [] warnings,
                         float timeThinking, int randomID){
        this.errors = errors;
        this.warnings = warnings;
        this.timeThinking = timeThinking;
        this.randomID = randomID;

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

}
