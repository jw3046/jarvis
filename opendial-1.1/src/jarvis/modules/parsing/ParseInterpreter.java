
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
        // TODO: use parse results
        ArrayList<InformUA> extractedInfo = new ArrayList<InformUA>();
        for (String token: parseResult.getTokens()){
            String word = token.toLowerCase();
            for (SlotType t: SlotType.values()){
                if (word.equals(t.toString().toLowerCase())){
                    extractedInfo.add(new InformUA(t,"usersaidkeyword"));
                }
            }
        }
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
