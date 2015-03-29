
package semafor;

import com.google.api.client.util.Key;

import java.util.List;
import java.util.ArrayList;

public class ParseResult
{
    @Key
    private String conll;
    @Key
    private String text;
    @Key
    private List<String> tokens;
    @Key
    private List<ParseFrame> frames;

    public String getRawConll(){return conll;}
    public ArrayList<ConllEntry> getConll(){return parseConll(conll);}
    public String getText(){return text;}
    public List<String> getTokens(){return tokens;}
    public List<ParseFrame> getFrames(){return frames;}

    public static ArrayList<ConllEntry> parseConll(String conll){
        // split by new line
        String lines[] = conll.split("\\n");
        ArrayList<ConllEntry> conllTable = new ArrayList<ConllEntry>();
        conllTable.add(
                new ConllEntry(0,"_ROOT_","_ROOT_","_ROOT_",0,"_ROOT_"));
        for (String line: lines){
            // split each line by tab
            String item[] = line.split("\\t");
            conllTable.add(
                    new ConllEntry(Integer.parseInt(item[0]),item[1],
                        item[3],item[4],Integer.parseInt(item[6]),item[7]));
        }
        return conllTable;
    }
}


