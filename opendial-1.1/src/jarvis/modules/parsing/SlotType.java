
package jarvis.modules.parsing;

public enum SlotType
{
    NAME("name"),
    DATE("date"),
    TIME("time"),
    GIFT("gift"),
    DRESS("dress"),
    VENUE("venue");

    private final String x;
    private SlotType(String x){
        this.x = x;
    }

    public String toString(){
        return x;
    }
}
