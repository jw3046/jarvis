package jarvis.modules.parsing;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.json.GenericJson;
import com.google.api.client.util.Key;

import java.util.List;
import java.util.ArrayList;

/**
 * an api call to Semafor, JSON response parsed
 * The JSON response will include both the frame-based 
 * parsing by SEMAFOR and the dependency parse by TurboParser
 */
public class APISemafor {

  static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  static final JsonFactory JSON_FACTORY = new JacksonFactory();

  public static ParseResult run(String sentence) throws Exception {
      // build http request
    HttpRequestFactory requestFactory =
        HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
          public void initialize(HttpRequest request) {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
          }
        });
    GenericUrl url = new GenericUrl(
            "http://demo.ark.cs.cmu.edu/parse/api/v1/parse?sentence="+
            sentence);
    HttpRequest request = requestFactory.buildGetRequest(url);

    // send request and parse response (JSON)
    HttpResponse response = request.execute();
    SemaforParse jsonResponse = response.parseAs(SemaforParse.class);

    ParseResult parseResult = jsonResponse.getParse();
    List<ParseFrame> frames = parseResult.getFrames();
    
    String conll = parseResult.getRawConll();
    System.out.println(conll);
    
    System.out.println(frames.size()+" frames");
    for (ParseFrame frame: frames){
        System.out.println(frame);
    }
    return parseResult;

  }

  public static class SemaforParse// extends GenericJson
  {
      // we currently don't use all the json items,
      // so we don't extend GenericJson
      
      @Key("debug_info")
      private ProcessingTime time;
      @Key("sentences")
      private List<ParseResult> parseResult;

      public ProcessingTime getProcessingTime(){
          return time;
      }
      public ParseResult getParse(){
          return parseResult.get(0);
      }
  }
  
  public static class ProcessingTime
  {
      @Key
      private double frame_parser_elapsed_seconds;
      @Key
      private double dependency_parser_elapsed_seconds;

      public double getFrameParserSeconds(){
          return frame_parser_elapsed_seconds;
      }
      public double getDependencyParserSeconds(){
          return dependency_parser_elapsed_seconds;
      }
  }

  public static void main(String[] args) {
    try {
      try {
        run("Me and Kevin are going to see a movie tomorrow.");
        return;
      } catch (HttpResponseException e) {
        System.err.println(e.getMessage());
      }
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }
}

