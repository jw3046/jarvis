
package semafor;

import com.google.api.client.util.Key;
import java.util.List;

public class AnnotationSet
{
    @Key
    private List<FrameElement> frameElements;
    @Key
    private double score;
    @Key
    private int rank;

    public List<FrameElement> getFrameElements(){return frameElements;}
    public double getScore(){return score;}
    public int getRank(){return rank;}
}


