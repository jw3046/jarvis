// =================================================================                                                                   
// Copyright (C) 2011-2015 Pierre Lison (plison@ifi.uio.no)

// Permission is hereby granted, free of charge, to any person 
// obtaining a copy of this software and associated documentation 
// files (the "Software"), to deal in the Software without restriction, 
// including without limitation the rights to use, copy, modify, merge, 
// publish, distribute, sublicense, and/or sell copies of the Software, 
// and to permit persons to whom the Software is furnished to do so, 
// subject to the following conditions:

// The above copyright notice and this permission notice shall be 
// included in all copies or substantial portions of the Software.

// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, 
// EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
// MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. 
// IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY 
// CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, 
// TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE 
// SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
// =================================================================                                                                   

package jarvis.modules.gcal;

import opendial.DialogueSystem;
import opendial.arch.DialException;
import opendial.arch.Logger;
import opendial.datastructs.Assignment;
import opendial.modules.Module;
import opendial.state.DialogueState;

import java.util.Collection;

/**
 * Example of simple external module used for the flight-booking dialogue domain.
 * The module monitors for two particular values for the system action:<ol>
 * <li>"FindOffer" checks the (faked) price of the user order and returns MakeOffer(price)
 * <li>"Book" simulates the booking of the user order.
 * </ol>
 * 
 * @author  Pierre Lison (plison@ifi.uio.no)
 * @version $Date:: 2014-12-05 20:40:21 #$ *
 */
public class GCalAddEvent implements Module {

	// logger
	public static Logger log = new Logger("GCalAddEvent", Logger.Level.DEBUG);

	// the dialogue system
	DialogueSystem system;

	// whether the module is paused or active
	boolean paused = true;
    CalendarManager eventManager = new CalendarManager();
	/**
	 * Creates a new instance of the flight-booking module
	 *
	 * @param system the dialogue system to which the module should be attached
	 */
	public GCalAddEvent(DialogueSystem system) {
		System.out.println("system initializaed!");
        this.system = system;
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
		if (updatedVars.contains("a_m") && state.hasChanceNode("a_m")) {
			String action = state.queryProb("a_m").toDiscrete().getBest().toString();

            //String current_event = state.queryProb("current_step").toDiscrete().getBest().toString();

			if (action.equals("Confirm_Adding")) {
                String[] eventData=new String[7];
                //structure for eventData
                //0:EventType
                //1:Date
                //2:Place
                //3:Person
                //4:Gift
                //5:DressCode
                //6:Subject


                //filling in mandatory slots for all event types
                String EventType= state.queryProb("EventType").toDiscrete().getBest().toString();
                eventData[0]=EventType;
                String Date=state.queryProb("Date").toDiscrete().getBest().toString();
                EventType=EventType.toLowerCase();
                eventData[1] =Date;
                String Place=state.queryProb("Place").toDiscrete().getBest().toString();
                eventData[2]=Place;
                //mandatory information filled

                //getting all possible slots value
                String Person=state.queryProb("Person").toDiscrete().getBest().toString();
                String Gift=state.queryProb("Gift").toDiscrete().getBest().toString();
                String DressCode=state.queryProb("DressCode").toDiscrete().getBest().toString();
                String Subject=state.queryProb("Subject").toDiscrete().getBest().toString();

                System.out.println("info: type, data, place, person, gift, dresscode, subject");
                System.out.println(EventType+" "+Date+" "+Place+" "+Person+" "+Gift+" "+ DressCode+" "+Subject);

                //adding additional slots
                if(EventType.contains("birthday")||EventType.contains("party")){
                    eventData[3]=Person;
                    if(!Gift.equals("NoGift"))   eventData[4]="Gift: " + Gift;
                    if(!DressCode.equals("NoDressCode")) eventData[5]="Dress Code: " + DressCode;
                }

                if(EventType.contains("anniversary")){
                    eventData[3]=Person;
                    if(!Gift.equals("NoGift"))   eventData[4]="Gift: " + Gift;
                    if(!DressCode.equals("NoDressCode")) eventData[5]="Dress Code: " + DressCode;
                    eventData[6]="type: "+Subject;
                }

                if(EventType.contains("chill")){
                    eventData[3]=Person;
                    if(!DressCode.equals("NoDressCode")) eventData[5]="Dress Code: " + DressCode;

                }

                if(EventType.contains("exam")){
                    eventData[6]="Subject: "+Subject;
                }

                if(EventType.contains("seminar")||EventType.contains("job")){
                    eventData[3]=Person;
                    if(!DressCode.equals("NoDressCode")) eventData[5]="Dress Code: " + DressCode;
                    eventData[6]="Subject: "+Subject;
                }

                if(EventType.contains("graduation")){
                    eventData[3]=Person;
                    if(!Gift.equals("NoGift"))   eventData[4]="Gift: " + Gift;
                }


                System.out.println("Creating Event!");
                try{

                    eventManager.addNewEvent(eventData);
                    System.out.println("Adding event as "+EventType);
                }catch(Exception e){
                    System.out.println(e.toString());
                }
                system.addContent(new Assignment("current_step","Confirm"));
                System.out.println("Event Added successfully!");

                /*
				String returndate = state.queryProb("ReturnDate").toDiscrete().getBest().toString();
				
				// here, we fake the price estimation by making up numbers.  Obviously,
				// the prices should be derived from a database in a real system.
				int price = (returndate.equals("NoReturn"))? 179 : 299;
				String newAction="MakeOffer(" + price + ")";
				system.addContent(new Assignment("a_m", newAction));
				*/
			}

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
