
package jarvis.modules.parsing;

import java.util.Collection;

import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.state.DialogueState;
import opendial.modules.Module;

public class Semafor implements Module
{
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
    }

    /**
     * Currently just prints to stdout.
     *
     * @param state the current dialogue state
     * @param updatedVars the updated variables in the state
     */
    @Override
    public void trigger(DialogueState state, Collection<String> updatedVars) {
        System.out.println("Semafor was triggered!");
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
