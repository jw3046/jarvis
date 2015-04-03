
package jarvis.modules.parsing;

import com.google.api.client.util.Key;

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

    public String toString(){
      return name+": "+text+" ("+start+"-"+end+")";
    }
}
