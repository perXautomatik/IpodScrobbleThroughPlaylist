package ModuleScrobbler;

import commonMethods.CommonMethods;

import java.io.IOException;
import java.util.Map;
import java.util.Scanner;

import static commonMethods.CommonMethods.*;

/**
 * Created by Christoffer on 2015-01-13.
 */
public class ScrobbleUserRetroactivly
{
    public static int main(String[] args) throws IOException
    {
        String currentUser = "konstruktor_k";
        final Scanner scanner = new Scanner(System.in);
        System.out.println("specify user to fetch scrobbles from;\n");
        String user = scanner.next();

        final ScrobbleObjectMap tempMap = onlineScrobbleFetch(0, 0, user);

        System.out.println("which account do you wanna scrobble to;\n");
        currentUser = scanner.next();


        System.out.println("specify from when to when in UNIX time");
        int from = 0;
        int to = 0;

        // gather a number of pages scrobbles, bunch them in dates and present this dates, rather than asking for dates
        // select date, remove tracks if wanted, push scrobble

        try
        {
            //from = (int) inputDateToUnixTime(scanner.next());
            from = scanner.nextInt();
        }
        catch (Exception e)
        {
            from = 0;
            e.printStackTrace();
        }
        try
        {
            //to = (int) inputDateToUnixTime(scanner.next());
            to = scanner.nextInt();

        } catch (Exception e)
        {
            // todays date inputDateToUnixTime(scanner.next())
            e.printStackTrace();
        }
        if(from > to)
        {
            int temp = from;
            from = to;
            to = temp;
        }

        scrobbleMap(tempMap, currentUser);
        return 0;
    }
}
