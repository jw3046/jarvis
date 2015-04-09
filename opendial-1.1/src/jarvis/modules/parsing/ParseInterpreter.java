
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class ParseInterpreter
{
        // user act formats:
         // Type(EventType)
         // Inform(SlotType,String)
         // Acknowledge(boolean)
    /*
    public static ArrayList<UserAct> run(ParseResult parseResult){
        //TODO:change after refactoring
        
        // extracts the slot values from the utterance
        // returns empty list if no information found
        // TODO: after DialogueState is integrated, return null
        // instead of empty List.
        
        // general semantic extraction
        ArrayList<UtteranceTheme> ideas = getUtteranceThemes(parseResult.getConll());
        System.out.println(ideas);

        // semantic type classification
        classify(ideas, parseResult.getFrames());

        // load most important (classified) values into slots
        ArrayList<UtteranceTheme> leftOvers = new ArrayList<UtteranceTheme>();
        HashMap<String,UtteranceTheme> slots = new HashMap<String,UtteranceTheme>();
        for (UtteranceTheme idea: ideas){
            if (idea.getClassification().equals("WHY")){
                leftOvers.add(idea);
            }
            else {
                slots.put(idea.getClassification(),idea);
            }
        }

        //TODO: refactor the rest up one level to have access to DM variables // just return the slots and save the leftOvers in a private static variable
        // in this class

        // convert general slots into dialogue specific slots (using DM variables)
        ArrayList<UserAct> extractedInfo = new ArrayList<UserAct>(convert(slots));;
        // type classification
        extractedInfo.add(mainTypeClassification(slots,leftOvers,parseResult.getFrames()));
        // acknowledgement classification
        extractedInfo.add(extractAcknowledgeUA(parseResult));

        return extractedInfo;
    }
    */


    /** Main activity type classification
     * @param slots dictionary of ideas keyed by its classification
     * @param leftOvers list of ideas that are secondary to the utterance
     * @param frames list of frames from parse results
     */
    public static TypeUA mainTypeClassification(HashMap<String,UtteranceTheme> slots,
            List<UtteranceTheme> leftOvers, List<ParseFrame> frames){
        // TODO: refactor up one level so it has access to DM to know
        //  whether this classifier is necessary at the current DM state
        return new TypeUA(EventType.EVENT);
        /*
         * =================================
         * MAIN ACTIVITY TYPE CLASSIFICATION
         * =================================
         * String match the WHAT idea, if nothing, string match frame names,
         *  if nothing, string match the WHY ideas.
         * If still nothing, (CLASSIFICATION: GENERAL_EVENT)
         *
         * Possible things to match
         * statement, speak_on_topic, talk, lecture
         * aggregate, amassing, social_event
         * placing, locative_relation (ie, hanging out)
         * locale_by_use, restaurant, ingestion, eat
         * getting
         * wear, clothing
         *
         *
         * PREVIOUS VERSION:
         public static TypeUA extractTypeUA(ParseResult parseResult){
             // extracts which EventType user mentions
             // returns null if none found
             
             // Uses simple string matching for now
             for (String token: parseResult.getTokens()){
                 String word = token.toLowerCase();
                 for (EventType t: EventType.values()){
                     if (word.equals(t.toString().toLowerCase())){
                         return new TypeUA(t);
                     }
                 }
             }
             return null;
         }
         */
    }

    /** Convert general WHO/WHERE; WHAT; WHEN slots into dialogue-specific slots
     * @param slots dictionary of ideas keyed by its classification
     */
    public static ArrayList<InformUA> convert(HashMap<String,UtteranceTheme> slots){
        // TODO: refactor up one level so it has access to DM
        ArrayList<InformUA> extractedInfo = new ArrayList<InformUA>();
        return extractedInfo;
    }

    /** Semantic type classification based on frames and dependencies
     * @param ideas list of relevant ideas in sentence in order of importance
     * @param frames frame-based parsing results
     */
    public static ArrayList<HashMap<String,UtteranceTheme>>
        classify(ArrayList<UtteranceTheme> ideas, List<ParseFrame> frames){

        ArrayList<HashMap<String,UtteranceTheme>> actions = 
            new ArrayList<HashMap<String,UtteranceTheme>>();

        ArrayList<UtteranceTheme> leftOvers =
            new ArrayList<UtteranceTheme>(ideas);
        while (leftOvers.size()>0){
            HashMap<String,UtteranceTheme> subAction =
                new HashMap<String,UtteranceTheme>();

            // get first occurence of Calendric_unit frame
            ParseFrame firstFrame = null;
            Integer themePosition = Integer.MAX_VALUE;
            for (ParseFrame frame: frames){
                if (frame.getTarget().getName().equals("Calendric_unit")){
                    // collect ids involved in the frame
                    ArrayList<Integer> ids = frame.getTarget().getIDs();
                    // find corresponding theme in ideas
                    for (int i=0; i<leftOvers.size(); i++){
                        UtteranceTheme theme = leftOvers.get(i);
                        for (ConllEntry entry: theme.getEntries()){
                            if (ids.contains(entry.getID())&&themePosition>=i){
                                // found frame corresponding to earlier theme
                                firstFrame = frame;
                                themePosition = i;
                            }
                        }
                    }
                }
            }
            subAction.put("WHEN",null);
            if (firstFrame != null){
                // fill out the slot and remove that theme
                subAction.put("WHEN",leftOvers.remove((int)themePosition));
            }

            // get first occurrence of proper noun (NNP)
            subAction.put("NNP",null);
            for (int i=0; i<leftOvers.size(); i++){
                boolean brk = false;
                for (ConllEntry entry: leftOvers.get(i).getEntries()){
                    if (entry.getPOSTAG().equals("NNP")){
                        subAction.put("NNP",leftOvers.remove(i));
                        brk = true;
                        break;
                    }
                }
                if (brk) break;
            }

            // get first occurrence of nonproper noun (NN)
            subAction.put("NN",null);
            for (int i=0; i<leftOvers.size(); i++){
                boolean brk = false;
                for (ConllEntry entry: leftOvers.get(i).getEntries()){
                    if (entry.getPOSTAG().contains("NN")&&
                            !(entry.getPOSTAG().equals("NNP"))){
                        subAction.put("NN", leftOvers.remove(i));
                        brk = true;
                        break;
                    }
                }
                if (brk) break;
            }
            
            // add this subaction to the actions list
            actions.add(subAction);
        }
        /* =============================
         * SEMANTIC TYPE CLASSIFICATION
         * =============================
         * For each idea:
         *  Get set of frames related to this idea.
         *      If 'Calendric_unit' in this set (and WHEN not assigned yet),
         *          If multiple exist, use the largest one
         *              (most coverage of utterance),
         *          Assign (CLASSIFICATION: WHEN) to this idea.
         *          TimeBind this idea to the closest,
         *              earliest (in the idea list), nontemporal idea.
         *          Remove all words in this frame from all ideas
         *          TODO: convert to absolute date(time).
         *      Else If (idea.POS contains NNP) and (WHO/WHERE not assigned yet),
         *          Assign (CLASSIFICATION: WHO/WHERE) to this idea
         *      Else If (CLASSIFICATION: WHAT not assigned yet),
         *          Assign (CLASSIFICATION: WHAT) to this idea
         *              ie, assign leftmost noncalendar/nnp idea to 'what'
         *      Else
         *          Assign (CLASSIFICATION: WHY) to the idea if nothing else
         *
         * TODO: Note that we can recursively analyze ideas classified as WHY
         *          by extracting all WHYs from the list of ideas and reclassifying
         *          them as their own list of ideas.
         */
        return actions;
    }

    /** General semantic extraction from POS and dependencies.
     * @param conll is a ConllParse object containing the POS and DEP
     */
    public static ArrayList<UtteranceTheme> getUtteranceThemes(ConllParse conll){
        ArrayList<UtteranceTheme> ideas = new ArrayList<UtteranceTheme>();

        int rootID = conll.getChildren(0).get(1).getID();

        dfsDependencyParse(conll, rootID, ideas);

        return ideas;
    }

    public static void dfsDependencyParse(ConllParse conll,
            int id, ArrayList<UtteranceTheme> ideas){
        ConllEntry current = conll.get(id);
        
        String currentPOS = current.getPOSTAG();
        String headPOS = conll.get(current.getHEAD()).getPOSTAG();
        String deprel = current.getDEPREL();
        
        if (currentPOS.contains("NN")){
            if (headPOS.contains("NN")&&
                    (deprel.contains("nn")||deprel.contains("conj"))){
                // append idea (extend the phrase)
                if (ideas.size()<1) ideas.add(new UtteranceTheme());
                UtteranceTheme prev = ideas.get(ideas.size()-1);
                prev.add(current);
            }
            else if (headPOS.contains("NN")&&deprel.contains("poss")){
                // add new idea, bind to previous idea (weakly extend)
                UtteranceTheme idea = new UtteranceTheme(current);
                if (ideas.size()>0){
                    UtteranceTheme prev = ideas.get(ideas.size()-1);
                    idea.bindTo(prev);
                }
                ideas.add(idea);
            }
            else {
                // add new idea to the list (the noun itself)
                ideas.add(new UtteranceTheme(current));
            }
        }
        else {
            if (headPOS.contains("NN")&&deprel.contains("amod")){
                // append idea (extend the phrase)
                if (ideas.size()<1) ideas.add(new UtteranceTheme());
                UtteranceTheme prev = ideas.get(ideas.size()-1);
                prev.add(current);
            }
            else {
                // do nothing; ignore this word
            }
        }
        // dfs on children, sorted
        ArrayList<ConllEntry> children = conll.getChildren(id);
        Collections.sort(children, new Comparator<ConllEntry>(){
            @Override
            public int compare(ConllEntry entry1, ConllEntry entry2){
                // closer to parent prioritized higher
                // left side of parent prioritized higher
                int e1_dist = Math.abs(entry1.getID()-id);
                int e2_dist = Math.abs(entry2.getID()-id);
                if (e1_dist>e2_dist){return 1;}
                else if (e1_dist<e2_dist){return -1;}
                else {
                    if (entry1.getID()<entry2.getID()) return -1;
                    else return 1;
                }
            }
        });
        for (ConllEntry child: children){
            dfsDependencyParse(conll, child.getID(), ideas);
        }
    }


    public static AcknowledgeUA extractAcknowledgeUA(ParseResult parseResult){
        // extracts whether the user acknowledges yes/no
        // returns null if neither
        
        // Uses simple string matching
        ArrayList<String> yes = new ArrayList<String>();
        yes.add("yes");
        yes.add("correct");
        yes.add("exactly");
        yes.add("sure");
        yes.add("right");
        ArrayList<String> no = new ArrayList<String>();
        no.add("no");
        no.add("nope");
        no.add("wrong");

        for (String word: parseResult.getTokens()){
            if (yes.contains(word.toLowerCase())){
                return new AcknowledgeUA(true);
            }
            if (no.contains(word.toLowerCase())){
                return new AcknowledgeUA(false);
            }
        }
        return null;
    }
}


