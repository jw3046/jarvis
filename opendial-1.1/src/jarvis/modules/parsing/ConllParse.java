
package jarvis.modules.parsing;

import java.util.ArrayList;

/** A full dependency parse in conll format */
public class ConllParse
{
    private ArrayList<ConllEntry> conllEntries;
    
    /** Parses a tab-separate table of conll */
    public ConllParse(String conllTableString){
        // split by new line
        String lines[] = conllTableString.split("\\n");
        conllEntries = new ArrayList<ConllEntry>();
        conllEntries.add(
                new ConllEntry(0,"_ROOT_","_ROOT_","_ROOT_",0,"_ROOT_"));
        for (String line: lines){
            // split each line by tab
            String item[] = line.split("\\t");
            conllEntries.add(
                    new ConllEntry(Integer.parseInt(item[0]),item[1],
                        item[3],item[4],Integer.parseInt(item[6]),item[7]));
        }
    }

    public ConllEntry get(int id){
        // note that the ConllEntrys are already ordered by id internally
        return conllEntries.get(id);
    }

    public ArrayList<ConllEntry> getPOS(String pos){
        ArrayList<ConllEntry> posList = new ArrayList<ConllEntry>();
        for (ConllEntry entry: conllEntries){
            if (entry.getPOSTAG().contains(pos)){
                posList.add(entry);
            }
        }
        return posList;
    }

    public ArrayList<ConllEntry> getChildren(int id){
        ArrayList<ConllEntry> childList = new ArrayList<ConllEntry>();
        for (ConllEntry entry: conllEntries){
            if (entry.getHEAD() == id){
                childList.add(entry);
            }
        }
        return childList;
    }
}
