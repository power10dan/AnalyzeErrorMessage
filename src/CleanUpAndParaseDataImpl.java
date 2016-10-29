package CleanData;

import java.util.ArrayList;

/**
 * Created by Daniel Lin  on 2016/10/14.
 *
 * Description: puts data into a data container
 * and sends the container to mySQL
 */
public class CleanUpAndParaseDataImpl {
    private ArrayList<String> dirtyErr;

    public CleanUpAndParaseDataImpl(ArrayList<String> dataDump){
        dirtyErr = dataDump;
    }

    public ArrayList<String> cleanedThreadDump(){
        ArrayList<Integer> numStackTraces = new ArrayList<Integer>();
        String pattern = "";

        for(String errToParse : dirtyErr){



        }

        return dirtyErr;
    }
}
