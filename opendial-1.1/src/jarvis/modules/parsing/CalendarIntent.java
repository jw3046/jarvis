
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;

public class CalendarIntent
{
    /** Maps general inform intent into calendar-domain
     * (dialogue specific) slots.
     * @param actions list of dictionary of ideas keyed by its classification
     */
    public static ArrayList<InformUA>
        convertInform(List<HashMap<String,UtteranceTheme>> actions){
        //
        ArrayList<InformUA> userActs = new ArrayList<InformUA>();

        return userActs;
    } 

    /** Main activity type classification
     * @param slots dictionary of ideas keyed by its classification
     * @param leftOvers list of ideas that are secondary to the utterance
     * @param frames list of frames from parse results
     */
    public static TypeUA mainTypeClassification(HashMap<String,UtteranceTheme> slots,
            List<UtteranceTheme> leftOvers, List<ParseFrame> frames){
        // use DM to know
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

}

