package ModuleScrobbler;

/**
 * Created by Christoffer on 2015-01-13.

public class WeekByWeekCorrector{


    cout "1.correct track by track"
    cout "2.correct weekly Snapshott"


   public void correctTrackBytrack()
    {
        //should the class keep track of which 'number' of week or pointers to weekobjects
        class week
        {
            nextWeek = 	fetch tracks (1 recent week)-> hashmap(id,trackInfo) recentWeek
            currentWeek = fetch tracks (1 recent week)-> hashmap(id,trackInfo) recentWeek
            weekBefore = fetch tracks (1 recent week)-> hashmap(id,trackInfo) recentWeek

            next()
            {
                nextWeek = currentWeek;
                currentWeek = weekBefore;
                weekBefore = fetch tracks (1 recent week)-> hashmap(id,trackInfo) recentWeek
            }

            older()
            {
                weekBefore = currentWeek;
                currentWeek = nextWeek;
                nextWeek = fetch tracks (1 recent week)-> hashmap(id,trackInfo) recentWeek

            }

            recentWeek -> arraylist recentWeekNumbered
        }

//display recentWeek
        for each trackInfo{
        track = recentweekNumbered[i];
        cout i "." track.getTitle +  track.getArtist + track.getAlbum + track.getDate
    }
        cout "o/n older newer"

        if cin == number
        {
            trackToCorrect = recentWeekNumbered[number];

            //".."-".."-".."
            correctingString = cin
        }

        if cin == "o"
        getnext week
        week fetchWeek()




    }





    correctTrackBytrack(correctingString, trackToCorrect)
    {
        nrOfDash = correctingString.find -
                nrOfRabitEars = correctingString.find "

        if nrOfDash == 0
        title = correctingString
        artist = trackToCorrect.getArtist
        album = trackToCorrect.getAlbum
        date = trackToCorrect.getDate
        else if nrOfDash == 1
        title = correctingString -> everythingBefore(-) -> everythingSurroundedBy(")
            artist = correctingString -> everythingAfter(-) -> everythingSurroundedBy(")
            album = trackToCorrect.getAlbum
            date = trackToCorrect.getDate
        else if nrOfDash == 2
        title = correctingString -> everythingBefore(-) -> everythingSurroundedBy(")
            artist = correctingString -> everythingAfter(-) -> everythingBefore(-) -> everythingSurroundedBy(")
            album = correctingString.everythingAfter(-).everythingSurroundedBy(")
                    date = trackToCorrect.getDate
        else if nrOfDash == 3
        title = correctingString -> everythingBefore(-) -> everythingSurroundedBy(")
            artist = correctingString -> everythingAfter(-) -> everythingBefore(-) -> everythingSurroundedBy(")
            album = correctingString -> everythingAfter(-) -> everythingBefore(-) -> everythingSurroundedBy(")
            date = correctingString.everythingAfter(-).everythingSurroundedBy(")

        new scrobbleObject(title,artist,album,date)

        cout scrobbleObject
        cout "do you want to save changes"

        if Cin == y
        scrobble(title, artist, album, date)
        else
        scrobbleObject = trackToCorrect;

        cout <- "do you want to delete " + scrobbleObject + "?"

        if Cin == y
        track.delete(scrobbleObject)

    }

}
 */