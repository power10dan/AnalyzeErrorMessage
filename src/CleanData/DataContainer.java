package CleanData;

import com.intellij.openapi.compiler.*;

/**
 * Created by daniellin on 2016/10/14.
 */
public class DataContainer {
    private CompilerMessage[] errors;
    private CompilerMessage [] warnings;

    private long timeThinking;

    private int randomID;
    private int [] compileContext;
    private int warningIDs;
    private int errorIDs;
    private int numberOfErrors;
    private int numberOfWarnings;
    private int status;

    public DataContainer(CompilerMessage [] errors, CompilerMessage [] warnings,
                         long timeThinking,  int [] compileContext){

        this.errors = errors;
        this.warnings = warnings;
        this.timeThinking = timeThinking;
        this.randomID = compileContext[0];
        this.compileContext = compileContext;
        this.warningIDs = compileContext[4];
        this.errorIDs = compileContext[5];
        this.numberOfErrors = compileContext[1];
        this.numberOfWarnings = compileContext[2];
        this.status = compileContext[3];

    }

    // getter methods

    public CompilerMessage [] getErrors(){
        return this.errors;
    }

    public CompilerMessage [] getWarnings(){
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

    public int getNumberOfErrors() { return this.numberOfErrors; }
    public int getNumberOfWarnings() { return this.numberOfWarnings;}
    public int getStatus() { return this.status;}

    public void outPutToFile(){

    }

}
