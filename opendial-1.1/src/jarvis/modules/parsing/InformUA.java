

package jarvis.modules.parsing;

public class InformUA implements UserAct
{
    private SlotType slot;
    private String value;

    public InformUA(SlotType y, String z){
        slot = y;
        value = z;
    }

    public String toString(){
        return "Inform("+slot+","+value+")";
    }
}
