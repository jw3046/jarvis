
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

    // current list of user_acts
    HashMap<String,ArrayList<String>> user_acts;
    /**
     * Creates new instance of parsing module (makes API calls to semafor).
     *
     * @param system the dialogue system to which the module should be attached
     */
    public Semafor(DialogueSystem system) {
        this.system = system;
        parseInterpreter = new ParseInterpreter();
        user_acts = new HashMap<String,ArrayList<String>>();
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

            // clear previous
            String[] act_keys =
                {"Person","Place","Date","Object","Confirm","EventType","Type"};
            for (String actType: act_keys){
                this.user_acts.put(actType,new ArrayList<String>());
            }

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

                    // set frame_u for EventType model
                    String event_clues = "(";
                    for (ParseFrame frame: parseResult.getFrames()){
                        event_clues += frame.getTarget().getName() + ",";
                    }

                    if (actions.size()>0){
                        // join user acts into (key,value)
                        // where key={Person,Place,Date,Object,Confirm}
                        String user_act = "";
                        for (HashMap<String,UtteranceTheme> action: actions){
                            for (String key: action.keySet()){
                                UtteranceTheme idea = action.get(key);
                                if (idea!=null){
                                    user_act += "("+key+","+idea+") ";
                                    // add to internal list
                                    this.user_acts.get(key).add(idea.toString());
                                }
                            }
                        }
                        system.addContent(new Assignment("a0_u",user_act));

                        // for frame_u
                        for (String key: actions.get(0).keySet()){
                            if (key.equals("Object")){
                                event_clues += "#" + actions.get(0).get(key) + ")";
                            }
                        }
                    }
                    else {
                        // check for yes/no/quit
                        String confirm = parseInterpreter.extractConfirm(parseResult);
                        if (confirm.equals("")){
                            // utterance completely incomprehensible
                            confirm = "_?_";
                        }
                        system.addContent(new Assignment("a0_u",confirm));
                        if (!confirm.equals("_?_")){
                            this.user_acts.get("Confirm").add(confirm);
                        }
                        // for frame_u
                        event_clues += "#unknown)";
                    }

                    // set frame_u for EventType module
                    system.addContent(new Assignment("frame_u", event_clues.toLowerCase()));

                    // set string match keywords for EventType extractor
                    String eventTypes = 
                        "anniversary;birthday;chill;graduation;job;party;seminar";
                    system.addContent(new Assignment("_etcsvlist",eventTypes));
                    
                    // DEBUG
                    System.out.println("=======\nEntities Extracted");
                    System.out.println(ideas);
                    System.out.println(actions);
                    System.out.println(event_clues);
                    
                } catch (HttpResponseException e) {
                    System.err.println(e.getMessage());
                }
            } catch (Throwable t) {
                t.printStackTrace();
            }
        }
        else if (updatedVars.contains("a2_u") && state.hasChanceNode("a2_u")){
            String user_act = state.queryProb("a0_u").toDiscrete().getBest().toString();
            String eventTypeStr = state.queryProb("a2_u").toDiscrete().getBest().toString();
            user_act += " " + eventTypeStr;
            this.user_acts.get("EventType").add(eventTypeStr);
            // also add Type(); note that a2_u guaranteed to be defined if a1_u is
            if (updatedVars.contains("a1_u") && state.hasChanceNode("a1_u")){
                String typeStr = state.queryProb("a1_u").toDiscrete().getBest().toString();
                user_act += " "+ typeStr;
                this.user_acts.get("Type").add(typeStr);
            }
            system.addContent(new Assignment("a_u", user_act));

            System.out.println("\n\n==========");
            System.out.println("==========");
            System.out.println("==========");
                    for (String key: this.user_acts.keySet()){
                        System.out.print(key+":");
                        System.out.println(this.user_acts.get(key));
                    }
            System.out.println("==========");
            System.out.println("==========");
            System.out.println("==========");
/*
            for (String key: this.user_acts.keySet()){
                for (String val: this.user_acts.get(key)){
                    System.out.println(key+":"+val);
                }
            }
            */
            // fill slots
            String ET =
                state.queryProb("ET").toDiscrete().getBest().toString();
            String CurrStep =
                state.queryProb("current_step").toDiscrete().getBest().toString();
            String system_act = state.queryProb("a_m").toDiscrete().getBest().toString();
            HashMap<String,String> newVars =
                SlotMapper.map(this.user_acts,ET,CurrStep,system_act);

            
            for (String key: newVars.keySet()){
                System.out.println(key + ":" + newVars.get(key));
                system.addContent(new Assignment(key, newVars.get(key)));
            }
        


            //DEBUG
            System.out.println(user_act);
        }
        else{
            /*
            System.out.println(state);
            String user_act = state.queryProb("a_u").toDiscrete().getBest().toString();
            System.out.println("a_u");
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
