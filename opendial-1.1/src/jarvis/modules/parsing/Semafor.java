
package jarvis.modules.parsing;

import opendial.datastructs.Assignment;
import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.state.DialogueState;
import opendial.modules.Module;

import com.google.api.client.http.HttpResponseException;

import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;

public class Semafor implements Module
{
    // test counter
    static int calledCount = 0;
    // whether the module is paused or active
    boolean paused = true;

    // the dialogue system
    DialogueSystem system;

    /**
     * Creates new instance of parsing module (makes API calls to semafor).
     *
     * @param system the dialogue system to which the module should be attached
     */
    public Semafor(DialogueSystem system) {
        this.system = system;
        System.out.println("Semafor Created!");
    }

    /**
     * Currently just prints to stdout. Checks whether the updated variables
     * contains the user utterance.
     *
     * @param state the current dialogue state
     * @param updatedVars the updated variables in the state
     */
    @Override
    public void trigger(DialogueState state, Collection<String> updatedVars) {
        if (updatedVars.contains("u_u") && state.hasChanceNode("u_u")) {
            // do parsing if user utterance ("u_u") has been updated
            String user_utterance =
                state.queryProb("u_u").toDiscrete().getBest().toString();
            System.out.println(user_utterance);

            // call semafor api
            try {
                try {
                    // parse user utterance (dependency and frame-based)
                    ParseResult parseResult = APISemafor.run(user_utterance);

                    /*
                    // interpret parse results, extract relevant information
                    ArrayList<UserAct> userActs =
                        ParseInterpreter.run(parseResult);
                    */
                    // get main ideas of the utterance, ordered by dependency
                    ArrayList<UtteranceTheme> ideas = 
                        ParseInterpreter.getUtteranceThemes(parseResult.getConll());

                    // classify as who/what/when/where and partition list
                    ArrayList<HashMap<String,UtteranceTheme>> actions =
                        ParseInterpreter.classify(ideas, parseResult.getFrames());
                   
                    // TESTING
                    System.out.println(ideas);
                    System.out.println(actions);

                    ArrayList<UserAct> userActs = new ArrayList<UserAct>();

                    for (UserAct ua: userActs){
                        System.out.println(ua);
                    }

                    if (userActs.size()>0){
                        // join all user acts into a string by comma
                        String a_u = userActs.get(0).toString();
                        for (int i=1; i<userActs.size(); i++){
                            a_u += "," + userActs.get(i).toString();
                        }
                        // return results to system
                        Assignment assign = new Assignment("a_u", a_u);
                        system.addContent(assign);
                    }
                    else {
                        // utterance not understood
                        system.addContent(new Assignment("a_u", "_?_"));
                    }
                    
                } catch (HttpResponseException e) {
                    System.err.println(e.getMessage());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        else {
            /*
            System.out.println(state);
            String user_act = state.queryProb("a_u").toDiscrete().getBest().toString();
            System.out.println(user_act);
            */
        }
        // end of trigger()
    }

    /**
     * Starts the module.
     */
    @Override
    public void start() throws DialException {
        paused = false;
    }
    
    /**
     * Pauses the module.
     * 
     * @param toPause whether to pause the module or not
     */
    @Override
    public void pause(boolean toPause) {
        paused = toPause;
    }

    /**
     * Returns whether the module is currently running or not.
     *
     * @return whether the module is running or not
     */
    @Override
    public boolean isRunning(){
        return !paused;
    }

    
}
