
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.ie.crf.*;

public class ParseInterpreter
{
    // stanford NER tagger
    AbstractSequenceClassifier<CoreLabel> classifier;

    public ParseInterpreter(){
        // 4 class:
        // PERSON, LOCATION, ORGANIZATION, MISC
        String serializedClassifier = "classifiers/english.conll.4class.distsim.crf.ser.gz";
        try {
            classifier = CRFClassifier.getClassifier(serializedClassifier);
        }
        catch (Exception e){
            System.out.println(e);
            System.exit(1);
        }
    }

    /** Semantic type classification based on frames and dependencies
     * @param ideas list of relevant ideas in sentence in order of importance
     * @param frames frame-based parsing results
     */
    public ArrayList<HashMap<String,UtteranceTheme>>
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
                if (frame.getTarget().getName().equals("Calendric_unit")||
                        frame.getTarget().getName().equals("Measure_duration")){
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
            subAction.put("Date",null);
            if (firstFrame != null){
                // fill out the slot and remove that theme
                UtteranceTheme timeEntry = leftOvers.remove((int)themePosition);
                timeEntry.setClassification("Date");
                subAction.put("Date", timeEntry);
            }


            // get first occurrence of WHO
            subAction.put("Person",null);
            for (int i=0; i<leftOvers.size(); i++){
                if (leftOvers.get(i).getNER().equals("PERSON")){
                    // maybe also use POSTAG here (NNP)
                    UtteranceTheme properNounEntry = leftOvers.remove(i);
                    subAction.put("Person",properNounEntry);
                    break;
                }
            }

            // get WHERE
            subAction.put("Place",null);
            for (int i=0; i<leftOvers.size(); i++){
                if (leftOvers.get(i).getNER().equals("LOCATION")){
                    // maybe also use POSTAG here (NNP)
                    UtteranceTheme properNounEntry = leftOvers.remove(i);
                    subAction.put("Place",properNounEntry);
                    break;
                }
                if (leftOvers.get(i).getNER().equals("ORGANIZATION")){
                    UtteranceTheme nounEntry = leftOvers.remove(i);
                    subAction.put("Object", nounEntry);
                    break;
                }
            }

            // get WHAT
            subAction.put("Object",null);
            for (int i=0; i<leftOvers.size(); i++){
                
                if (leftOvers.get(i).getNER().equals("MISC")){
                    UtteranceTheme nounEntry = leftOvers.remove(i);
                    subAction.put("Object", nounEntry);
                    break;
                }
                boolean brk = false;
                for (ConllEntry entry: leftOvers.get(i).getEntries()){
                    //if (entry.getPOSTAG().contains("NN")&&
                            //!(entry.getPOSTAG().equals("NNP"))){
                    if (entry.getPOSTAG().contains("NN")){
                        UtteranceTheme nounEntry = leftOvers.remove(i);
                        subAction.put("Object", nounEntry);
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
    public ArrayList<UtteranceTheme> getUtteranceThemes(ConllParse conll){
        ArrayList<UtteranceTheme> ideas = new ArrayList<UtteranceTheme>();

        int rootID = conll.getChildren(0).get(1).getID();

        // construct sentence from tokens of conll
        String sentence = "";
        for (int i=1;i<conll.size();i++)
            sentence += conll.get(i).getFORM() + " ";

        // NER parse
        String output = classifier.classifyToString(sentence, "tsv", false);
        ArrayList<String> tags = new ArrayList<String>();
        for (String line: output.split("\n")){
            tags.add(line.split("\t")[1]);
        }

        dfsDependencyParse(conll, tags, rootID, ideas);

        return ideas;
    }

    public void dfsDependencyParse(ConllParse conll,
            ArrayList<String> nertags, int id, ArrayList<UtteranceTheme> ideas){
        ConllEntry current = conll.get(id);
        
        String currentPOS = current.getPOSTAG();
        String headPOS = conll.get(current.getHEAD()).getPOSTAG();
        String deprel = current.getDEPREL();
        
        if (currentPOS.contains("NN")){
            if (headPOS.contains("NN")&&
                    (deprel.contains("nn")||deprel.contains("conj"))&&
                    // special case for time
                    !(conll.get(current.getHEAD()).getDEPREL().contains("tmod"))){
                // append idea (extend the phrase)
                if (ideas.size()<1){
                    UtteranceTheme idea = new UtteranceTheme();
                    idea.setNER(nertags.get(id-1));
                    ideas.add(idea);
                }
                UtteranceTheme prev = ideas.get(ideas.size()-1);
                prev.add(current);
            }
            /*
            else if (headPOS.contains("NN")&&deprel.contains("poss")){
                // add new idea; this is likely a Person
                UtteranceTheme idea = new UtteranceTheme(current);
                idea.setNER(nertags.get(id-1));
                ideas.add(idea);
            }
            */
            else {
                // add new idea to the list (the noun itself)
                UtteranceTheme idea = new UtteranceTheme(current);
                idea.setNER(nertags.get(id-1));
                ideas.add(idea);
            }
        }
        else {
            if ((headPOS.contains("NN")&&deprel.contains("amod"))||
                // special extra cases for time
                (headPOS.contains("NN")&&deprel.contains("dep")&&
                conll.get(current.getHEAD()).getDEPREL().contains("tmod"))
                || (headPOS.contains("NNP")&&
                    (deprel.contains("amod")||deprel.contains("num"))&&
                    (currentPOS.contains("JJ")||currentPOS.contains("CD")))
                ){
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
            dfsDependencyParse(conll, nertags, child.getID(), ideas);
        }
    }



    public String extractConfirm(ParseResult parseResult){
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
        String quit = "quit";

        for (String word: parseResult.getTokens()){
            if (yes.contains(word.toLowerCase())){
                return "ConfirmY";
            }
            if (no.contains(word.toLowerCase())){
                return "ConfirmN";
            }
            if (quit.equals(word.toLowerCase())){
                return "Quit";
            }
        }
        return "";
    }
}


