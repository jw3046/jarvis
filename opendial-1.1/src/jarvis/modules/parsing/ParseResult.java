
package jarvis.modules.parsing;

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
    public ConllParse getConll(){return new ConllParse(conll);}
    public String getText(){return text;}
    public List<String> getTokens(){return tokens;}
    public List<ParseFrame> getFrames(){return frames;}

    
}


