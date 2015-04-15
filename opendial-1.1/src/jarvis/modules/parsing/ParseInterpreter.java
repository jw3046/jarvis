
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

public class ParseInterpreter
{

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
            // TODO: get time values
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
                UtteranceTheme timeEntry = leftOvers.remove((int)themePosition);
                timeEntry.setClassification("WHEN");
                subAction.put("WHEN", timeEntry);
            }

            // get WHERE
            // TODO: use NER, Place or Organization
            
            // get first occurrence of proper noun WHO
            // TODO: use NER, Person
            subAction.put("WHO",null);
            for (int i=0; i<leftOvers.size(); i++){
                boolean brk = false;
                for (ConllEntry entry: leftOvers.get(i).getEntries()){
                    if (entry.getPOSTAG().equals("NNP")){
                        UtteranceTheme properNounEntry = leftOvers.remove(i);
                        subAction.put("NNP",properNounEntry);
                        brk = true;
                        break;
                    }
                }
                if (brk) break;
            }

            // get first occurrence of nonproper noun WHAT
            // use NER, Organization
            subAction.put("WHAT",null);
            for (int i=0; i<leftOvers.size(); i++){
                boolean brk = false;
                for (ConllEntry entry: leftOvers.get(i).getEntries()){
                    if (entry.getPOSTAG().contains("NN")&&
                            !(entry.getPOSTAG().equals("NNP"))){
                        UtteranceTheme nounEntry = leftOvers.remove(i);
                        subAction.put("NN", nounEntry);
                        brk = true;
                        break;
                    }
                }
                if (brk) break;
            }
            
            // add this subaction to the actions list
            actions.add(subAction);
        }
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
                    //idea.bindTo(prev);
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


