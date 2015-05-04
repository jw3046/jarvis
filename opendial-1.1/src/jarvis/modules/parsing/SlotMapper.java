
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;



/**
 * Slot Mapper module used for domain management (jarvis).
 */
public class SlotMapper {

	public SlotMapper() {
         
	}

	private static HashMap<String,ArrayList<String>> removeNull(HashMap<String,ArrayList<String>> user_act){
            
            for (Iterator<Map.Entry<String,ArrayList<String>>> it =
                    user_act.entrySet().iterator();
                    it.hasNext();){
                Map.Entry<String,ArrayList<String>> pair = it.next();
                if (pair.getValue().isEmpty()){
                    it.remove();
                }
            }
		
		return user_act;
	}
	
	public static HashMap<String,String> map(HashMap<String,ArrayList<String>> user_act, String ET, String current_step, String system_act) {
		
		HashMap<String,String> returnMap = new HashMap<String,String>();
		ArrayList<String> valueList = new ArrayList<String>();
		
		String value = new String();
		
		HashMap<String, ArrayList<String>> a_u = removeNull(user_act);

		
		//Quit
		if (a_u.containsKey("Confirm")) {
			value = valueList.get(0);
			if (value.equals("Quit")){
				returnMap.put("a_m", "Exit");
				return returnMap;
			}
		}
		
		//Basic information NoEvent
		if (ET.equals("NoEvent") && a_u.containsKey("Date")){
			valueList = a_u.get("Date");
			value = valueList.get(0);
			returnMap.put("Date", value);
		}
		if (ET.equals("NoEvent") && a_u.containsKey("Person")){
			valueList = a_u.get("Person");
			value = valueList.get(0);
			returnMap.put("Person", value);
		}
		if (ET.equals("NoEvent") && a_u.containsKey("Place")){
			valueList = a_u.get("Place");
			value = valueList.get(0);
			returnMap.put("Place", value);
		}
		if (ET.equals("NoEvent") && a_u.containsKey("Type")){
			valueList = a_u.get("Type");
			value = valueList.get(0);
			returnMap.put("Type", value);
		}
		if (ET.equals("NoEvent") && a_u.containsKey("EventType")){
			valueList = a_u.get("EventType");
			value = valueList.get(0);
                        valueList.remove(0);
                        a_u = removeNull(a_u);
			returnMap.put("EventType", value);
                        ET = value;
		}
		
		//BIRTHDAY EVENT 
		if (ET.equals("Birthday")){
			if (a_u.containsKey("Object") && current_step.equals("Gift")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Gift", value);
				returnMap.put("a_m", "CheckGift");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}	
		
		//ANNIVERSARY EVENT 
		if (ET.equals("Anniversary")){
			if (a_u.containsKey("Object") && current_step.equals("Gift")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Gift", value);
				returnMap.put("a_m", "CheckGift");
				return returnMap;
			}
			else if (a_u.containsKey("Type") && current_step.equals("Subject")){
				valueList = a_u.get("Type");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Subject")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}		
		
		//PARTY EVENT 
		if (ET.equals("Party")){
			if (a_u.containsKey("Object") && current_step.equals("Gift")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Gift", value);
				returnMap.put("a_m", "CheckGift");
				return returnMap;
			}
			else if (a_u.containsKey("Type") && current_step.equals("Subject")){
				valueList = a_u.get("Type");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Subject")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}		

		//CHILL EVENT 
		if (ET.equals("Chill")){
			if (a_u.containsKey("Object") && current_step.equals("Gift")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Gift", value);
				returnMap.put("a_m", "CheckGift");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}
		//Exams EVENT 
		if (ET.equals("Exams")){
			if (a_u.containsKey("Type") && current_step.equals("Subject")){
					valueList = a_u.get("Type");
					value = valueList.get(0);
					returnMap.put("Subject", value);
					returnMap.put("a_m", "CheckType");
					return returnMap;
				}
			else if (a_u.containsKey("Object") && current_step.equals("Subject")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
	
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}		
		//SEMINAR EVENT 
		if (ET.equals("Seminar")){
			if (a_u.containsKey("Type") && current_step.equals("Subject")){
				valueList = a_u.get("Type");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Subject")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}
		
		//JOB EVENT 
		if (ET.equals("Job")){
			if (a_u.containsKey("Type") && current_step.equals("Subject")){
				valueList = a_u.get("Type");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Subject")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Subject", value);
				returnMap.put("a_m", "CheckType");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}
		
		//GRADUATION EVENT 
		if (ET.equals("Graduation")){
			if (a_u.containsKey("Object") && current_step.equals("Gift")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Gift", value);
				returnMap.put("a_m", "CheckGift");
				return returnMap;
			}
			
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}		
		
		//Other EVENT 
		if (ET.equals("Other")){
			if (a_u.containsKey("Object") && current_step.equals("Gift")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Gift", value);
				returnMap.put("a_m", "CheckGift");
				return returnMap;
			}
			
			else if (a_u.containsKey("Object") && current_step.equals("DressCode")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("DressCode", value);
				returnMap.put("a_m", "CheckDressCode");
				return returnMap;
			}
			else if (a_u.containsKey("Person")){
				valueList = a_u.get("Person");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Person")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Person", value);
				returnMap.put("a_m", "CheckPerson");
				return returnMap;
			}
			else if (a_u.containsKey("Date")){
				valueList = a_u.get("Date");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Date")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Date", value);
				returnMap.put("a_m", "CheckDate");
				return returnMap;
			}
			else if (a_u.containsKey("Place")){
				valueList = a_u.get("Place");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
			else if (a_u.containsKey("Object") && current_step.equals("Place")){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Place", value);
				returnMap.put("a_m", "CheckPlace");
				return returnMap;
			}
		}			
		
		
		//YES  
		if (a_u.containsKey("Confirm")) {
			if (current_step.equals("GuessforGift") && a_u.containsKey("Object") ){
				valueList = a_u.get("Object");
				value = valueList.get(0);
				returnMap.put("Gift", value);
				returnMap.put("a_m", "GuessedGift");
				return returnMap;
			}
			else if (current_step.equals("Gift_Request")){
				valueList = a_u.get("Confirm");
				value = valueList.get(0);
				if (value.equals("yes")){
					returnMap.put("current_step", "Gift");
					return returnMap;

				}
			}
			else if (current_step.equals("DressCode_Request")){
				valueList = a_u.get("Confirm");
				value = valueList.get(0);
				if (value.equals("yes")){
					returnMap.put("current_step", "DressCode");
					return returnMap;

				}
			}
			else {
				valueList = a_u.get("Confirm");
				value = valueList.get(0);
				if (value.equals("yes")){
					returnMap.put("Event", "ConfirmY");
					return returnMap;

				}
			}
	
		}		
		//NO 
		if (a_u.containsKey("Confirm")) {
			if (current_step.equals("Gift_Request")){
				valueList = a_u.get("Confirm");
				value = valueList.get(0);
				if (value.equals("no")){
					returnMap.put("current_step", "Gift");
					return returnMap;

				}
			}
			else if (current_step.equals("DressCode_Request")){
				valueList = a_u.get("Confirm");
				value = valueList.get(0);
				ArrayList<String> EventTypeList = new ArrayList<String> ();
				String EventType = EventTypeList.get(0);
				if (value.equals("no")){
					returnMap.put("DressCode", "NoDressCode");
					returnMap.put("Event", EventType);
					returnMap.put("a_m", "CheckNoDressCode");
					return returnMap;

				}
			}
			else if (current_step.equals("Gift_Request")){
				valueList = a_u.get("Confirm");
				value = valueList.get(0);
				if (value.equals("no")){
					returnMap.put("a_m", "CheckGift");
					returnMap.put("Gift", "NoGift");
					return returnMap;

				}
			}
			else {
				valueList = a_u.get("Confirm");
				value = valueList.get(0);
				if (value.equals("no")){
					returnMap.put("Event", "ConfirmN");
					return returnMap;

				}
			}
	
		}	
		
		//Catch-all rule that simply keeps the slots to their current value in the absence of any other new information
		
		
		
		
		return returnMap;
	}
	/* test code 
	public static void main(String[] args) {
		HashMap<String,ArrayList<String>> user_act = new HashMap<String,ArrayList<String>> ();
		String ET = "Birthday";
		String current_step = "Gift";
		String system_act = "test";
		ArrayList<String> dateList = new ArrayList<String>();
		dateList.add("March");
		dateList.add("May");
		user_act.put("Object",dateList);
		System.out.println(dateList);
		HashMap<String, String> testReturn = new HashMap<String,String>();
		testReturn = map(user_act, ET, current_step, system_act);
		System.out.println(testReturn);
	}
	*/
		

}
