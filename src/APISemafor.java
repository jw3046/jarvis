

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

package jarvis.modules.parsing;

import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.Key;

import java.util.List;

/**
 * Simple example for the <a href="http://www.dailymotion.com/doc/api/graph-api.html">Dailymotion
 * Graph API</a>.
 */
public class APISemafor {

  static final HttpTransport HTTP_TRANSPORT = new NetHttpTransport();
  static final JsonFactory JSON_FACTORY = new JacksonFactory();

  private static void run() throws Exception {
    HttpRequestFactory requestFactory =
        HTTP_TRANSPORT.createRequestFactory(new HttpRequestInitializer() {
            @Override
          public void initialize(HttpRequest request) {
            request.setParser(new JsonObjectParser(JSON_FACTORY));
          }
        });
    GenericUrl url = new GenericUrl("http://demo.ark.cs.cmu.edu/parse/api/v1/parse?sentence="+
            "Sam and I are seeing a movie this weekend.");
    //DailyMotionUrl url = new DailyMotionUrl("https://api.dailymotion.com/videos/");
    HttpRequest request = requestFactory.buildGetRequest(url);

    //VideoFeed videoFeed = request.execute().parseAs(VideoFeed.class);
    HttpResponse response = request.execute();
    JSONObject parse_response = new JSONObject(response.getContent());
    System.out.println(response.getContent());
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

