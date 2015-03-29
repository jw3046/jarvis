
package semafor;

import com.google.api.client.util.Key;
import java.util.List;

public class ParseFrame
{
    @Key
    private FrameElement target; // frame to be filled
    @Key
    private List<AnnotationSet> annotationSets; // elements filling the frame

    public FrameElement getTarget() {return target;}
    private AnnotationSet getAnnotationSet() {return annotationSets.get(0);}
    public List<FrameElement> getElements() {
        return getAnnotationSet().getFrameElements();
    }
    public double getScore(){return getAnnotationSet().getScore();}
    public int getRank(){return getAnnotationSet().getRank();}

    public String toString(){
        String parseFrameString = "=====\n_TARGET_\n"+
            target+"\n_ELEMENTS_\n";
        for (FrameElement element: getElements()){
            parseFrameString += element+"\n";
        }
        return parseFrameString+"=======\n";
    }
}

