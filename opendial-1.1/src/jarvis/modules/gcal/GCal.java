
package jarvis.modules.gcal;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.batch.BatchRequest;
import com.google.api.client.googleapis.batch.json.JsonBatchCallback;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.googleapis.json.GoogleJsonError;
import com.google.api.client.http.HttpHeaders;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.client.util.Lists;
import com.google.api.client.util.store.DataStoreFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import com.google.api.services.calendar.model.Calendar;
import com.google.api.services.calendar.model.CalendarList;
import com.google.api.services.calendar.model.CalendarListEntry;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.Events;

import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.Date;
import java.util.TimeZone;
import java.util.List;

public class GCal
{
  /**
   * Be sure to specify the name of your application. If the application name is {@code null} or
   * blank, the application will log a warning. Suggested format is "MyCompany-ProductName/1.0".
   */
  private static final String APPLICATION_NAME = "Jarvis-GCal/0.1";

  /** Directory to store user credentials. */
  private static final java.io.File DATA_STORE_DIR =
      //new java.io.File(System.getProperty("user.home"), ".store/calendar_sample");
      new java.io.File(".store/user_calendar_credentials");

  /**
   * Global instance of the {@link DataStoreFactory}. The best practice is to make it a single
   * globally shared instance across your application.
   */
  private static FileDataStoreFactory dataStoreFactory;
 
  /** Global instance of the HTTP transport. */
  private static HttpTransport httpTransport;

  /** Global instance of the JSON factory. */
  private static final JsonFactory JSON_FACTORY = JacksonFactory.getDefaultInstance();

  private static com.google.api.services.calendar.Calendar client;

  public GCal(){
      try {
          // initialize the transport
          httpTransport = GoogleNetHttpTransport.newTrustedTransport();

          // initialize the data store factory
          dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

          // authorization
          Credential credential = authorize();

          // set up global Calendar instance
          client = new com.google.api.services.calendar.Calendar.Builder(
              httpTransport, JSON_FACTORY,
              credential).setApplicationName(APPLICATION_NAME).build();
      } catch (Throwable t){
          t.printStackTrace();
          System.exit(1);
      }
  }

  /** Authorizes the installed application to access user's protected data. */
  private static Credential authorize() throws Exception {
    // load client secrets
    GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
        new InputStreamReader(GCal.class.getResourceAsStream("/client_secrets.json")));
    if (clientSecrets.getDetails().getClientId().startsWith("Enter")
        || clientSecrets.getDetails().getClientSecret().startsWith("Enter ")) {
      System.out.println(
          "Enter Client ID and Secret from https://code.google.com/apis/console/?api=calendar "
          + "into calendar-cmdline-sample/src/main/resources/client_secrets.json");
      System.exit(1);
    }
    // set up authorization code flow
    GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
        httpTransport, JSON_FACTORY, clientSecrets,
        Collections.singleton(CalendarScopes.CALENDAR)).setDataStoreFactory(dataStoreFactory)
        .build();
    // authorize
    return new AuthorizationCodeInstalledApp(flow, new LocalServerReceiver()).authorize("user");
  }

  public static void main(String[] args) {
    try {
      // initialize the transport
      httpTransport = GoogleNetHttpTransport.newTrustedTransport();

      // initialize the data store factory
      dataStoreFactory = new FileDataStoreFactory(DATA_STORE_DIR);

      // authorization
      Credential credential = authorize();

      // set up global Calendar instance
      client = new com.google.api.services.calendar.Calendar.Builder(
          httpTransport, JSON_FACTORY, credential).setApplicationName(APPLICATION_NAME).build();

      // run commands
      showCalendars();
      String calendarID = getPrimaryCalendarID();
      System.out.println(calendarID);
      addEvent(calendarID, "test event 1");
      showEvents(calendarID);


    } catch (IOException e) {
      System.err.println(e.getMessage());
    } catch (Throwable t) {
      t.printStackTrace();
    }
    System.exit(1);
  }

  private static String getPrimaryCalendarID() throws IOException {
      // returns the primary calendar id
      // returns null if no primary calendar found
      View.header("Primary Calendar");
      String primaryCalendarID = client.calendars().get("primary").execute().getId();
      return primaryCalendarID;
  }

  private static Calendar updateCalendar(String calendarID) throws IOException {
    View.header("Update Calendar");
    Calendar entry = new Calendar();
    entry.setSummary("Updated Calendar for Testing");
    Calendar result = client.calendars().patch(calendarID, entry).execute();
    View.display(result);
    return result;
  }

  private static void addEvent(String calendarID, String data) throws IOException {
    View.header("Add Event");
    Event event = newEvent(data);
    Event result = client.events().insert(calendarID, event).execute();
    View.display(result);
  }

  private static Event newEvent(String data) {
    //TODO: implement newEvent where u can set the fields
    Event event = new Event();
    event.setSummary(data);
    event.setDescription("This is a test event...");
    
    // check if time value has been filled
    String WHEN = "WHEN=";
    String NULL = WHEN+"null";
    int startIndex = data.indexOf(WHEN);
    if (data.substring(startIndex,startIndex+NULL.length()).equals(NULL)){
        
        Date startDate = new Date();
        Date endDate = new Date(startDate.getTime() + 3600000);
        DateTime start = new DateTime(startDate, TimeZone.getTimeZone("UTC"));
        event.setStart(new EventDateTime().setDateTime(start));
        DateTime end = new DateTime(endDate, TimeZone.getTimeZone("UTC"));
        event.setEnd(new EventDateTime().setDateTime(end));
    }
    else {
        // extract time info
        int endIndex = data.indexOf("}", startIndex);
        String time = data.substring(startIndex+WHEN.length(),endIndex);
        event.setStart(new EventDateTime().setDateTime(new DateTime(time+":00-04:00")));
        event.setEnd(new EventDateTime().setDateTime(new DateTime(time+":01-04:00")));
    }
    return event;
  }

  public void addNewEvent(String data) throws IOException {
      addEvent(getPrimaryCalendarID(), data);
  }

  private static void showEvents(String calendarID) throws IOException {
    View.header("Show Events");
    Events feed = client.events().list(calendarID).execute();
    View.display(feed);
  }

  private static void showCalendars() throws IOException {
    View.header("Show Calendars");
    CalendarList feed = client.calendarList().list().execute();
    View.display(feed);
  }
}

