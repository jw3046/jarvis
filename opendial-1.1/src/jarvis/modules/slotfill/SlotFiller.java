
package jarvis.modules.slotfill;

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
	public CalendarUpdate(DialogueSystem system) {
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

	/**
	 * Checks whether the updated variables contains the system action and (if yes)
	 * whether the system action value is "FindOffer" or "Book".  If the value is 
	 * "FindOffer", checks the price of the order (faked here to 179 or 299 EUR) 
	 * and adds the new action "MakeOffer(price)" to the dialogue state.  If the
	 * value is "Book", simply write down the order on the system output.
	 * 
	 * @param state the current dialogue state
	 * @param updatedVars the updated variables in the state
	 */
	@Override
	public void trigger(DialogueState state, Collection<String> updatedVars) {
		if (updatedVars.contains("a_u") && state.hasChanceNode("a_u")) {
			String user_act = state.queryProb("a_u").toDiscrete().getBest().toString();
                        // string matching/slot filling code here
                        
                        //String ET = state.queryProb("ET");
                        //HashMap<String,String> hm = Filler.myfunc(user_act, ET, current_step);

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
