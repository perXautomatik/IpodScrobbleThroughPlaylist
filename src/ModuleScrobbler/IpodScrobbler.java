package ModuleScrobbler;

import commonMethods.CommonMethods.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static commonMethods.CommonMethods.*;
import static commonMethods.CommonMethods.ScrobbleObjectMap.*;

/**
 * Created by Christoffer on 2015-01-11.
 */
public class IpodScrobbler {
    public static final String PROJECTFOLDER = askSystemMyDocumentsPath() + "\\ModularScrobbler\\";
    //private static final String MP3LENGHT = "1min.mp3";
    private static final String USERNAME = "konstruktor_k";
    private static String dbLocation1;
    private static String libraryLocation;

    public IpodScrobbler(String dbLocation, String libraryLocation) {
        this.dbLocation1 = dbLocation;
        this.libraryLocation = libraryLocation;
    }


    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        new NameDupes();
        new SessionSaver();
        //SessionSaver fromTo = new SessionSaver(SavedFromTo);
        SessionSaver.read();

        final Scanner scanner = new Scanner(System.in);

        System.out.println("\nsynced Ipod since last program run?");
        final boolean recentResync = scanner.next().charAt(0) == 'y';

        ScrobbleObjectMap tracksToScrobble;

        final ScrobbleObjectMap iLibMap = itunesFetch(libraryLocation);

        ScrobbleObjectMap db;

        db = loadFromFile(new ScrobbleObjectMap(), new File(dbLocation1));
        tracksToScrobble = recentResync ? extractChanges(iLibMap, db) : db;

        if(db == null)
        {
            tracksToScrobble = iLibMap;
        }

        //        if (!recentResync) {
//
//            tracksToScrobble = formerSubbmissions(referenceMap, tracksToScrobble, dbPath, dbExists);
//        }
//        else
//        {
    /*    fixItunesIncuficiencyBug
        (
            tracksToScrobble,
            getMapOfPastScrobbles(iLibMap, dbPath, false, USERNAME)
        );
*/
        tracksToScrobble.print(0,"scrobbles left according to filter: " + tracksToScrobble.countScrobbles());

//        }

        System.out.print("remove something before scrobble?");
        if (scanner.next().charAt(0) == 'y') {

            //Yes, Blah.valueOf("A") will give you Blah.A.
            //ScrobbleObjectMap removed = tracksToScrobble.remove();
           // removed.storeAtPath(PROJECTFOLDER + "removed");
        }



        System.out.print("y to scrobble");
        if (scanner.next().charAt(0) == 'y') {
            scrobbleMap(tracksToScrobble, USERNAME);
            SessionSaver.store();
        }

        if (recentResync) {
            System.out.print("store changes to itunes lib?");

            if (scanner.next().charAt(0) == 'y') {

//                tracksToScrobble.putAll(loadScrobbleObjectHashMap(dbLocation1));
//
//                tracksToScrobble.storeAtPath(dbLocation1);
                mergeDBStore(tracksToScrobble, db,dbLocation1);
            }
        }
    }

    private static void mergeDBStore(ScrobbleObjectMap itunes, ScrobbleObjectMap db, String path) throws FileNotFoundException {
        HashSet<String> subSet = new HashSet<String>();

        subSet.addAll(itunes.keySet());
        try {
            subSet.addAll(db.keySet());
        }
        catch (NullPointerException ignore)
        {
            db = new ScrobbleObjectMap();
        }

        ScrobbleObjectMap resultMap = new ScrobbleObjectMap();
        for (String key : subSet) {

            final boolean dbContainsKey = db.containsKey(key);
            final boolean iContainsKey = itunes.containsKey(key);

            Boolean dbBiggerThanItunes = false;

            ScrobbleObject io = null;
            ScrobbleObject dbo = null;

            if (dbContainsKey)
                dbo = db.get(key);
            if (iContainsKey)
                io = itunes.get(key);

            try {
                if (dbContainsKey && iContainsKey)
                    dbo.equals(io);

            } catch (Exception ignore) {
                dbBiggerThanItunes = io.getPlaycount() < dbo.getPlaycount();
            }

            if (!dbBiggerThanItunes || !dbContainsKey && iContainsKey) {
                resultMap.put(io);
            } else {
                //if (dbHasButNotItunes || bothEqual) {
                resultMap.put(dbo);
                if (dbBiggerThanItunes) {
                    resultMap.get(dbo).setScrobbleCount(io.getPlaycount());
                }
            }

        }
        if (path != null) {
            resultMap.storeAtPath(path);
        }
        else
            throw new FileNotFoundException();
    }
    
    private static void fixItunesIncuficiencyBug(ScrobbleObjectMap tracksToScrobble, ScrobbleObjectMap pastSucessfulScrobbles) {
        System.out.println("before fix " + tracksToScrobble.size());

        for (ScrobbleObject pSS : pastSucessfulScrobbles.values())
        {
            for (Iterator<ScrobbleObject> trackToEvaluate = tracksToScrobble.values().iterator(); trackToEvaluate.hasNext(); )
            {
                ScrobbleObject tToSo = trackToEvaluate.next();
                if (tToSo != null && (pSS != null))
                {
                    final int tToSoSc = tToSo.getScrobbleCount();
                    final boolean trackToScrobbleMoreThanFour = tToSoSc > 4;

                    try
                    {
                        if (tToSo.equals(pSS) && trackToScrobbleMoreThanFour) {
                            trackToEvaluate.remove();
                        }
                    }
                    catch (PlayCountDifferentException e)
                    {
                        final boolean trackToScGreaterThanPastSucessfull = tToSoSc < pSS.getScrobbleCount();
                        if (trackToScrobbleMoreThanFour & trackToScGreaterThanPastSucessfull) {
                            trackToEvaluate.remove();
                        }
                    }
                }
            }
        }
        System.out.println("after fix " + tracksToScrobble.size());
    }


}