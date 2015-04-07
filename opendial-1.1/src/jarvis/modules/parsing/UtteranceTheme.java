
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class UtteranceTheme
{
    private ArrayList<ConllEntry> entries;
    private UtteranceTheme bind;
    private String classification;

    public UtteranceTheme(){
        entries = new ArrayList<ConllEntry>();
        bind = null;
        classification = null;
    }
    public UtteranceTheme(ConllEntry entry){
        entries = new ArrayList<ConllEntry>();
        entries.add(entry);
        bind = null;
        classification = null;
    }

    public void add(ConllEntry entry){
        entries.add(entry);
    }

    public void remove(int id){
        for (int i=entries.size()-1; i>=0; i--){
            if (entries.get(i).getID()==id){
                entries.remove(i);
            }
        }
    }

    public void bindTo(UtteranceTheme otherTheme){
        bind = otherTheme;
    }

    public UtteranceTheme getBind(){
        return bind;
    }

    public void classifyAs(String classification){
        this.classification = classification;
    }

    public String getClassification(){
        return classification;
    }

    public ArrayList<ConllEntry> getEntries(){
        // sort ConllEntrys by id
        Collections.sort(entries, new Comparator<ConllEntry>(){
            @Override
            public int compare(ConllEntry entry1, ConllEntry entry2){
                if (entry1.getID()>entry2.getID()){
                    return 1;
                }
                else if (entry1.getID()<entry2.getID()){
                    return -1;
                }
                else {
                    return 0;
                }
            }
        });
        return entries;
    }
}
