
package jarvis.modules.parsing;

import java.util.ArrayList;
import java.time.LocalDateTime;
import java.time.DayOfWeek;
import java.time.temporal.TemporalAdjusters;
import java.time.temporal.ChronoUnit;

public class CalendarTimeUtil
{
    public static String toAbsoluteDate(String relativeDate){
        // converts user utterance to absolute date
        //TODO: recognize time
        ArrayList<String> days = new ArrayList<String>();
        for (DayOfWeek d: DayOfWeek.values()){
            days.add(d.toString().toLowerCase());
        }
        LocalDateTime datetime = LocalDateTime.now()
            .truncatedTo(ChronoUnit.MINUTES);

        // parse the date
        relativeDate = relativeDate.toLowerCase();
        
        if (relativeDate.contains("tomorrow")){
            // increment by 1 day
            datetime = datetime.plusDays(1);
        }
        else {
            boolean brk = false;
            for (String day: days){
                if (relativeDate.contains(day)){
                    // TemporalAdjuster
                    if (relativeDate.contains("next")){
                        // skip a week
                        datetime = datetime.with(TemporalAdjusters.next(
                                    DayOfWeek.valueOf(day.toUpperCase())))
                            .with(TemporalAdjusters.next(
                                        DayOfWeek.valueOf(day.toUpperCase())));
                    }
                    else {
                        // the coming day of week
                        datetime = datetime.with(TemporalAdjusters.next(
                                    DayOfWeek.valueOf(
                                        day.toUpperCase())));
                    }
                    brk = true;
                    break;
                }
            }
            if (!brk){
                // doesn't contain any case listed, default to tomorrow
                datetime = datetime.plusDays(1);
            }
        }

        int NIGHT = 17;
        int NOON = 12;
        int MORNING = 8;
        // TODO: update temporary 'later today' modification
        if (relativeDate.contains("today")){
            // later today either means noon, night, or tomorrow
            if (datetime.isBefore(datetime.withHour(NOON))){
                relativeDate += ", noon";
            }
            else if (datetime.isBefore(datetime.withHour(NIGHT))){
                relativeDate += ", night";
            }
            else {
                datetime = datetime.plusDays(1);
            }
        }
        // time
        if (relativeDate.contains("night")){
            // set time to 5pm
            datetime = datetime.withHour(NIGHT).withMinute(0);
        }
        else if (relativeDate.contains("noon")){
            // set time to 12pm
            datetime = datetime.withHour(NOON).withMinute(0);
        }
        else {
            // set time to 8am
            datetime = datetime.withHour(MORNING).withMinute(0);
        }
        return datetime.toString();
    }
}
