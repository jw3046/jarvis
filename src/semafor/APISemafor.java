

/*
 * Copyright (c) 2011 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */

//package jarvis.modules.parsing;
package semafor;

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

  private static void run() throws Exception {
      // build http request
    HttpRequestFactory requestFactory =
        HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
          public void initialize(HttpRequest request) {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
          }
        });
    GenericUrl url = new GenericUrl("http://demo.ark.cs.cmu.edu/parse/api/v1/parse?sentence="+
            "Sam and I are seeing a movie this weekend.");
    HttpRequest request = requestFactory.buildGetRequest(url);

    // send request and parse response (JSON)
    HttpResponse response = request.execute();
    SemaforParse jsonResponse = response.parseAs(SemaforParse.class);

    ParseResult parseResult = jsonResponse.getParse();
    List<ParseFrame> frames = parseResult.getFrames();
    
    String conll = parseResult.getRawConll();
    System.out.println(conll);
    ArrayList<ConllEntry> conllTable = parseResult.getConll();
    for (ConllEntry row: conllTable){
        System.out.println(row);
    }

    // Extract relevant information from parse results
    
    // integrate with opendial and set "a_u" value to extracted information

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

  public static class ParseResult
  {
      @Key
      private String conll;
      @Key
      private String text;
      //@Key
      //private List<ParseRelation> relations;
      @Key
      private List<String> tokens;
      //@Key
      //private List<ParseEntity> entities;
      @Key
      private List<ParseFrame> frames;

      public String getRawConll(){return conll;}
      public ArrayList<ConllEntry> getConll(){return parseConll(conll);}
      public String getText(){return text;}
      //public List<ParseRelation> getRelations(){return relations;}
      public List<String> getTokens(){return tokens;}
      //public List<ParseEntity> getEntities(){return entities;}
      public List<ParseFrame> getFrames(){return frames;}

      public static ArrayList<ConllEntry> parseConll(String conll){
          // split by new line
          String lines[] = conll.split("\\n");
          ArrayList<ConllEntry> conllTable = new ArrayList<ConllEntry>();
          conllTable.add(
                  new ConllEntry(0,"_ROOT_","_ROOT_","_ROOT_",0,"_ROOT_"));
          for (String line: lines){
              // split each line by tab
              String item[] = line.split("\\t");
              conllTable.add(
                      new ConllEntry(Integer.parseInt(item[0]),item[1],
                          item[3],item[4],Integer.parseInt(item[6]),item[7]));
          }
          return conllTable;
      }
  }

  /** A row entry of conll */
  public static class ConllEntry
  {
      public ConllEntry(int id, String form, String cpostag,
              String postag, int head, String deprel){
          this.id = id;
          this.form = form;
          this.cpostag = cpostag;
          this.postag = postag;
          this.head = head;
          this.deprel = deprel;
      }

      private int id; // position in sentence
      private String form; // word
      // lemma ommitted
      private String cpostag; // course pos
      private String postag; // fine pos
      // feats ommitted
      private int head; // parent of dependency
      private String deprel; // relation to parent
      // phead ommitted
      // pdeprel ommitted
      
      public int getID(){return id;}
      public String getFORM(){return form;}
      public String getCPOSTAG(){return cpostag;}
      public String getPOSTTAG(){return postag;}
      public int getHEAD(){return head;}
      public String getDEPREL(){return deprel;}

      public String toString(){
          return id+"\t"+form+"\t"+cpostag+"\t"+
              postag+"\t"+head+"\t"+deprel;
      }
  }

  public static class ParseRelation
  {
      // we currently don't use this result, so we leave it
      /* the syntax of the json is:
       * [string, string, [[string,string],[string,string]]]
       */
  }

  public static class ParseEntity
  {
      // we currently don't use this result, so we leave it
      /* the syntax of the json is:
       * [string, string, [[int, int]]]
       */
  }

  public static class ParseFrame
  {
      @Key
      private FrameElement target;
      @Key
      private List<AnnotationSet> annotationSet;

      public FrameElement getTarget() {return target;}
      public AnnotationSet getAnnotationSet() {return annotationSet.get(0);}
  }

  public static class FrameElement
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
  }

  public static class AnnotationSet
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

  public static void main(String[] args) {
    try {
      try {
        run();
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

