
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

public class UtteranceTheme
{
    private ArrayList<ConllEntry> entries;
    private String classification;
    private String ner;

    public UtteranceTheme(){
        entries = new ArrayList<ConllEntry>();
        classification = "";
        ner = "";
    }
    public UtteranceTheme(ConllEntry entry){
        entries = new ArrayList<ConllEntry>();
        entries.add(entry);
        classification = "";
        ner = "";
    }

    public void add(ConllEntry entry){
        entries.add(entry);
    }

    public boolean remove(int id){
        for (int i=entries.size()-1; i>=0; i--){
            if (entries.get(i).getID()==id){
                entries.remove(i);
            }
        }
        if (entries.size()>0) return true;
        else return false;
    }

    public void setClassification(String classification){
        this.classification = classification;
    }

    public String getClassification(){
        return classification;
    }

    public void setNER(String ner){
        this.ner = ner;
    }

    public String getNER(){
        return ner;
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
    
    public String toString(){
        String phrase = "";
        for (ConllEntry entry: getEntries()){
            phrase += entry.getFORM() + " ";
        }
        if (entries.size()>0 && getClassification().equals("Date")){
            phrase = CalendarTimeUtil.toAbsoluteDate(phrase);
        }
        return phrase.trim();
    }
}


