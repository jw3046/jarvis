
package jarvis.modules.parsing;

import java.util.ArrayList;

public class ParseInterpreter
{
    private static ParseResult parseResult;

    public static ArrayList<UserAct> run(ParseResult pr){
        parseResult = pr;
        /* Valid user acts:
         * Type(EventType)
         * Inform(SlotType,String)
         * Acknowledge(boolean)
         */

        // TODO: use DialogueState to determine which UserActs
        // should/could be extracted from the utterance
        ArrayList<UserAct> userActs =
            new ArrayList<UserAct>(extractInformUA());
        TypeUA type = extractTypeUA();
        AcknowledgeUA ack = extractAcknowledgeUA();

        if (type != null){
            userActs.add(type);
        }
        if (ack != null){
            userActs.add(ack);
        }

        return userActs;
    }

    public static ArrayList<InformUA> extractInformUA(){
        // extracts the slot values from the utterance
        // returns empty list if no information found
        // TODO: after DialogueState is integrated, return null
        // instead of empty List.
        
        // Uses simple string matching for now
        ArrayList<InformUA> extractedInfo = new ArrayList<InformUA>();
        
        // TODO: use parse results, implement
        //
        /* =============================
         * GENERAL SEMANTIC EXTRACTION
         * =============================
         * Initialize list of ideas
         * BFS starting from root. TODO: break ties
         * For each word:
         *  If POS=NN
         *      If ((HEAD.POS==NN)&&(DEPREL==nn||conj))
         *          append idea (extend the phrase, maintain original ordering)
         *      Else If ((HEAD.POS==NN)&&(DEPREL==poss))
         *          PossBind idea (weakly extend the phrase)
         *      Else
         *          add new idea (the noun itself) to list
         *  Else
         *      If ((HEAD.POS==NN)&&(DEPREL==amod))
         *          append idea (extend the phrase, maintain original ordering)
         *      Else
         *          do nothing
         * 
         * =============================
         * SEMANTIC TYPE CLASSIFICATION
         * =============================
         * For each idea:
         *  Get set of frames related to this idea.
         *      If 'Calendric_unit' in this set (and not encountered yet),
         *      If multiple exist, use the largest one (most coverage of utterance),
         *          Assign (CLASSIFICATION: WHEN) to this idea.
         *          TimeBind this idea to the closest,
         *              earliest (in the idea list), nontemporal idea.
         *          Remove all words in this frame from all ideas
         *          TODO: convert to absolute date(time).
         *      Else If (idea.POS contains NNP),
         *          Assign (CLASSIFICATION: WHO/WHERE) to this idea
         *      Else If (CLASSIFICATION: WHAT not assigned yet),
         *          Assign (CLASSIFICATION: WHAT) to this idea
         *              ie, assign leftmost noncalendar/nnp idea to 'what'
         *      Else,
         *          Assign (CLASSIFICATION: WHY) to the idea
         *
         * TODO: Note that we can recursively analyze ideas classified as WHY
         *          by extracting all WHYs from the list of ideas and reclassifying
         *          them as their own list of ideas.
         *
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
         */
        // Get all Nouns in utterance
        // Select all frames with those nouns (this will filter out bad frames)
        // Select all nouns not captured in those frames
        //
        for (String token: parseResult.getTokens()){
            String word = token.toLowerCase();
            for (SlotType t: SlotType.values()){
                if (word.equals(t.toString().toLowerCase())){
                    extractedInfo.add(new InformUA(t,"usersaidkeyword"));
                }
            }
        }

        // Use parse results
        //ArrayList<ConllEntry> dependencies = parseResult.getConll();
        //List<ParseFrame> frames = parseResult.getFrames();


        return extractedInfo;
    }

    public static TypeUA extractTypeUA(){
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

    public static AcknowledgeUA extractAcknowledgeUA(){
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


