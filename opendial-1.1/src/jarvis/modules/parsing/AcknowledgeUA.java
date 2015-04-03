
package jarvis.modules.parsing;

public class AcknowledgeUA implements UserAct
{
    private boolean yesno;
    
    public AcknowledgeUA(boolean w){
        yesno = w;
    }

    public String toString(){
        String w = "no";
        if (yesno){
            w = "yes";
        }
        return "Acknowledge("+w+")";
    }
}
