
package jarvis.modules.parsing;

import opendial.datastructs.Assignment;
import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.state.DialogueState;
import opendial.modules.Module;

import com.google.api.client.http.HttpResponseException;

import java.util.Collection;
import java.util.ArrayList;

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

                    // interpret parse results, extract relevant information
                    // TODO: change to UserAct class
                    ArrayList<UserAct> userActs =
                        ParseInterpreter.run(parseResult);

                    for (UserAct ua: userActs){
                        System.out.println(ua);
                    }
                    // TODO:return full list of interpreted results
                    if (userActs.size()>0){
                        Assignment assign =
                            new Assignment("a_u", userActs.get(0).toString());
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
