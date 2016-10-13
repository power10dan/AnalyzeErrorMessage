package MySQLOperations;

import com.intellij.openapi.compiler.CompilerMessage;

import java.util.Random;

/**
 * Created by daniellin on 2016/10/12.
 */
public class MySQLInteractor {

    public void SaveDataToMySQL(CompilerMessage[] errors, CompilerMessage[] warnings,
                                long timeDuration, int status) {

        // generate a random ID to act as the key to the data
        int studyParticipantId = 0;
        studyParticipantId = genPseudoRand();

        // organize the data into tuples
        for(CompilerMessage error : errors){




        }

    }

    private int genPseudoRand(){
        Random rand = new Random(System.currentTimeMillis());
        return 10000 + rand.nextInt(20000);
    }


    




}
