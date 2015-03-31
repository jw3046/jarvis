

package jarvis.modules.parsing;

public enum EventType
{
    BIRTHDAY("birthday"),
    ANNIVERSARY("anniversary"),
    GRADUATION("graduation"),
    PARTY("party"),
    EVENT("event");

    private final String x;
    private EventType(String x){
        this.x = x;
    }

    public String toString(){
        return x;
    }
}
