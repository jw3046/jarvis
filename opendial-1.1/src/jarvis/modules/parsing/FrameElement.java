
package jarvis.modules.parsing;

import com.google.api.client.util.Key;
import java.util.ArrayList;

public class FrameElement
{
    @Key
    private int start;
    @Key
    private int end;
    @Key
    private String name;
    @Key
    private String text;

    public int getStart(){return start;}
    public int getEnd(){return end;}
    public String getName() {return name;}
    public String getText() {return text;}
    public ArrayList<Integer> getIDs() {
        ArrayList<Integer> IDs = new ArrayList<Integer>();
        for (int i=start; i<end; i++){
            IDs.add(i+1);
        }
        return IDs;
    }

    public String toString(){
      return name+": "+text+" ("+start+"-"+end+")";
    }
}

