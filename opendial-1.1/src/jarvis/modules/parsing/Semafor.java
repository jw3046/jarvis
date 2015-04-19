
package jarvis.modules.parsing;

import opendial.datastructs.Assignment;
import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.state.DialogueState;
import opendial.modules.Module;
import opendial.bn.values.Value;

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

    // the parse interpreter
    ParseInterpreter parseInterpreter;

    /**
     * Creates new instance of parsing module (makes API calls to semafor).
     *
     * @param system the dialogue system to which the module should be attached
     */
    public Semafor(DialogueSystem system) {
        this.system = system;
        parseInterpreter = new ParseInterpreter();
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
            String user_utterance = "None";
            for (Value val: state.queryProb("u_u").toDiscrete().getValues()){
                if (!(val.toString().equals("None"))){
                    user_utterance = val.toString();
                    break;
                }
            }
            System.out.println(user_utterance);

            // call semafor api
            try {
                try {
                    // parse user utterance (dependency and frame-based)
                    ParseResult parseResult = APISemafor.run(user_utterance);

                    // get main ideas of the utterance, ordered by dependency
                    ArrayList<UtteranceTheme> ideas = 
                        parseInterpreter.getUtteranceThemes(parseResult.getConll());

                    // partition list into sets of who/what/when/where
                    ArrayList<HashMap<String,UtteranceTheme>> actions =
                        parseInterpreter.classify(ideas, parseResult.getFrames());

                    if (actions.size()>0){
                        // join user acts into (key,value)
                        // where key={Person,Place,Date,Object}
                        String user_act = "";
                        for (HashMap<String,UtteranceTheme> action: actions){
                            for (String key: action.keySet()){
                                UtteranceTheme idea = action.get(key);
                                if (idea!=null){
                                    user_act += "("+key+","+idea+"),";
                                }
                            }
                        }
                        system.addContent(new Assignment("a_u",user_act));
                    }
                    else {
                        // utterance not understood
                        system.addContent(new Assignment("a_u", "_?_"));
                    }
                    // TESTING
                    System.out.println(ideas);
                    System.out.println(actions);
                    
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
