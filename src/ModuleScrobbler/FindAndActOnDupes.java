package ModuleScrobbler;

import commonMethods.CommonMethods;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;



import static commonMethods.CommonMethods.*;
import static commonMethods.CommonMethods.ScrobbleObjectMap.*;

/**
 * Created by Christoffer on 2015-02-10.
 */
class FindAndActOnDupes
{

    private String USERNAME = "konstruktor_k";

    public FindAndActOnDupes(String dbLocation, String libraryLocation) throws FileNotFoundException
    {
        invoke(dbLocation,libraryLocation);
    }

    private static void findAndActOnDupes(ScrobbleObjectMap mapToCheck, ScrobbleObjectMap referenceMap) {
        Scanner in = new Scanner(System.in);
        char choice;
        List<CommonMethods.ScrobbleObject> thisObjectSet = new LinkedList<CommonMethods.ScrobbleObject>();
        List<CommonMethods.ScrobbleObject> isActuallyThatObjectSet = new LinkedList<CommonMethods.ScrobbleObject>();

        findDupes(mapToCheck, referenceMap, thisObjectSet, isActuallyThatObjectSet);

        int index = 0;
        Iterator<CommonMethods.ScrobbleObject> iterator2 = isActuallyThatObjectSet.iterator();
        for (ScrobbleObject thisObject : thisObjectSet)
        {
            ScrobbleObject isActuallyThatObject = iterator2.next();


                System.out.println(thisObject.present() + ":" + thisObject.generateHash() + "\n " + isActuallyThatObject.present() + ":" + isActuallyThatObject.generateHash());

                System.out.println(index + " of " + thisObjectSet.size() + " is same?");

                choice = in.next().charAt(0);
                if (choice == 'y') {
                    NameDupes.add(thisObject, isActuallyThatObject);
                } else if (choice == 'n') {
                    NameDupes.ignore(thisObject, isActuallyThatObject);
                } else if (choice == 'q')
                    break;

            index++;
            //
            // iterator2.hasNext();
        }
    }

    public static int findDupes(ScrobbleObjectMap innerMapSmall, ScrobbleObjectMap outerMapBig, List<ScrobbleObject> thisObjectMap, List<ScrobbleObject> isActuallyThatObjectMap) {
        Progressbar progressbar = new Progressbar(outerMapBig.size(), "finding dupes:");
        int nrOfDupes = 0;

        for (ScrobbleObject bigMapEntry : outerMapBig.values()) {

            for (ScrobbleObject smallMapEntry : innerMapSmall.values()) {
                final boolean sameAlbum = stripNonAlphaNumerical(bigMapEntry.getAlbum()).trim().toLowerCase().equals(stripNonAlphaNumerical(smallMapEntry.getAlbum()).trim().toLowerCase());
                final boolean sameArtist = stripNonAlphaNumerical(bigMapEntry.getArtist()).trim().toLowerCase().equals(stripNonAlphaNumerical(smallMapEntry.getArtist()).trim().toLowerCase());
                final boolean sameTitle = stripNonAlphaNumerical(bigMapEntry.getTitle()).trim().toLowerCase().equals(stripNonAlphaNumerical(smallMapEntry.getTitle()).trim().toLowerCase());

                final String sMEH = smallMapEntry.generateHash();
                final String bMEH = bigMapEntry.generateHash();
                final boolean notIgnored = !NameDupes.isToBeIgnored(bMEH, sMEH);
                boolean notActuallySame = !bMEH.equals(sMEH);
                boolean alreadyKnown = !NameDupes.isLeftKnownByRightName(bMEH,sMEH);

                final boolean similar = sameAlbum && (!sameArtist && sameTitle || sameArtist) || sameTitle || sameAlbum;

//                if(smallMapEntry.getArtist() == null)


                if (notIgnored && similar && notActuallySame && alreadyKnown)
                {
                    thisObjectMap.add(smallMapEntry);
                    isActuallyThatObjectMap.add(bigMapEntry);
                    nrOfDupes++;
                    NameDupes.isToBeIgnored(bMEH, sMEH);
                }
            }
            progressbar.printAndIncrement();
        }
        return nrOfDupes;
    }

    public static String stripNonAlphaNumerical(String string)
    {
        final CharSequence input
                = string != null ? string : "null";

         /* inspired by seh's comment */
        final StringBuilder sb = new StringBuilder(
                input.length() /* also inspired by seh's comment */);
        for (int i = 0; i < input.length(); i++) {
            final char c = input.charAt(i);
            if ((c > 47 && c < 58) || (c > 64 && c < 123) || c == 32) {
                sb.append(c);
            }
        }
        return String.valueOf(sb.length() > 0 ? sb.toString() : 0);
    }

    public void invoke(String dbLocation1, String libraryLocation) throws FileNotFoundException
    {
        new NameDupes();
        SessionSaver session = new SessionSaver();
        session.read();
        final String dbPath = PROJECTFOLDER + "\\" + "onlineFetch";

        ScrobbleObjectMap alienEntries;
        ScrobbleObjectMap errorSubmissions;
        ScrobbleObjectMap tracksToScrobble = null;
        final ScrobbleObjectMap itunesTracks = itunesFetch(libraryLocation);

        //SessionSaver fromTo = new SessionSaver(SavedFromTo);

        boolean ignorePrefetch = false;
        final Scanner scanner = new Scanner(System.in);
        do {
            ScrobbleObjectMap pastSuccessfulScrobbles =  null;
            errorSubmissions = new ScrobbleObjectMap();
            alienEntries = new ScrobbleObjectMap();

            try {
                pastSuccessfulScrobbles = getMapOfPastScrobbles(itunesTracks, dbPath, ignorePrefetch, USERNAME, session);
            } catch (IOException e) {
                e.printStackTrace();
            }

            tracksToScrobble = subtractLeftFromRight(itunesTracks, loadFromFile(new ScrobbleObjectMap(), new File(dbLocation1)), errorSubmissions, alienEntries);


            printStats(itunesTracks, pastSuccessfulScrobbles, tracksToScrobble, errorSubmissions, alienEntries);

//        findAndActOnDupes(tracksToScrobble, loadScrobbleObjectHashMap(dbLocation1));
//        NameDupes.store();
//        System.out.println("\n" + NameDupes.getNumberOfRegistered());
//        tracksToScrobble = subtractLeftFromRight(tracksToScrobble,alienEntries,new ScrobbleObjectMap(),new ScrobbleObjectMap());

        findAndActOnDupes(mergeMaps(alienEntries, errorSubmissions),tracksToScrobble);
        NameDupes.store();
        System.out.println("\n" + NameDupes.getNumberOfRegistered());
        System.out.println("\nAgain?");

        } while ((scanner.next().charAt(0) == 'y'));
    }
}
