
package jarvis.modules.slotfill;

import java.util.HashMap;
import java.util.Collection;
import java.io.IOException;

import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.datastructs.Assignment;
import opendial.modules.Module;
import opendial.state.DialogueState;

/**
 * Slot Filler module used for domain management (jarvis).
 */
public class SlotFiller implements Module {

	// the dialogue system
	DialogueSystem system;
	
	// whether the module is paused or active
	boolean paused = true;

	/**
	 * Creates a new instance of the calendar updater module
	 * 
	 * @param system the dialogue system to which the module should be attached
	 */
	public SlotFiller(DialogueSystem system) {
		this.system = system;
                System.out.println("SlotFiller created!");
	}

	/**
	 * Starts the module.
	 */
	@Override
	public void start() throws DialException {
		paused = false;
	}

	@Override
	public void trigger(DialogueState state, Collection<String> updatedVars) {
            if (updatedVars.contains("a_u") && state.hasChanceNode("a_u")) {
                // get relevant vars
                String user_act =
                    state.queryProb("a_u").toDiscrete().getBest().toString();
                String ET = 
                    state.queryProb("ET").toDiscrete().getBest().toString();
                String CurrStep = 
                    state.queryProb("current_step").toDiscrete().getBest().toString();
                String system_act = 
                    state.queryProb("a_m").toDiscrete().getBest().toString();

                /*
                HashMap<String,String> newVars =
                    SlotMapper.map(user_act, ET, current_step, system_act);

                for(String key: newVars.keySet()){
                    system.addContent(new Assignment(key, newVars.get(key)));
                }
                */
            }
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
	 * @return whether the module is running or not.
	 */
	@Override
	public boolean isRunning() {
		return !paused;
	}

}
