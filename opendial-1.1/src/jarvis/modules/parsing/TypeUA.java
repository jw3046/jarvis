

package jarvis.modules.parsing;

public class TypeUA implements UserAct
{
    private EventType type;

    public TypeUA(EventType x){
        type = x;
    }

    public String toString(){
        return "Type("+type+")";
    }
}
