package ModuleScrobbler;

import ScrobbleUserContinually.ScrobbleUserContinualy;

import java.io.File;
import java.io.IOException;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import static commonMethods.CommonMethods.*;

public class ModularScrobbler

{
    public static final String APIKEY = "d165934486baa1453af66ca761cb180d";
    private static final String LIBRARY_LOCATION = "C:\\Users\\christoffer\\" + "Music\\iTunes 2\\iTunes Library.xml";
    static final String SECRET = "09140722c7f851f00908d3fd4d14c692";
    public static final String PROJECTFOLDER = askSystemMyDocumentsPath() + "\\ModularScrobbler\\";
    private static final String DB_LOCATION = PROJECTFOLDER + "\\" + "itunes2";


    public static void main(String[] args) throws IOException, DigestException, NoSuchAlgorithmException, ClassNotFoundException {

        int choice = 0;
        while(choice != 9)
        {
            String dbLocation = loadFromFile("", new File(PROJECTFOLDER + "\\" + "dbLocation"));
            String libraryLocation = loadFromFile("", new File(PROJECTFOLDER + "\\" + "libraryLocation"));
            choice = menu();

            switch (choice)
            {
                case 1: {
                    new IpodScrobbler(dbLocation, libraryLocation);
                    IpodScrobbler.main(args);
                    break;
                }
                case 2: {
                    new ScrobbleUserRetroactivly();
                    break;
                }
                case 3: {
                   new ScrobbleUserContinualy();
                   break;
                }
                case 4: {
                    GetRecommendedTracks.Main();
                    break;
                }
                case 5: {
                    setLocations(dbLocation, libraryLocation);
                    break;
                }
                case 6: {
                    saveAsFile(APIKEY, PROJECTFOLDER + "apikey");
                    saveAsFile(SECRET, PROJECTFOLDER + "secret");
                    break;
                }
                case 7: {
                    mergeItunesDB(dbLocation);
                    break;
                }
                case 8: {
                    new FindAndActOnDupes(dbLocation, libraryLocation);
                    break;
                }

            }
        }
    }

    private static void mergeItunesDB(String libraryLocation)
    {

        if(libraryLocation == null) {
            saveAsFile(LIBRARY_LOCATION, PROJECTFOLDER + "\\" + "libraryLocation");

        }
        else {

            System.out.println("current locations: " + "\n" +

                    libraryLocation);

            System.out.println("which is the path you wanna merge this with?");
            final Scanner scanner = new Scanner(System.in);
            if ((scanner.next().charAt(0) == 'y'))
            {
                System.out.println("generalizing lib");

                ScrobbleObjectMap library1 = loadFromFile(new ScrobbleObjectMap(), new File(libraryLocation));

                library1.storeAtPath(libraryLocation + ".bkp");

                library1.generalize();
                //library1.generealizeByReference(onlineScrobbles);

                library1.storeAtPath(libraryLocation);

            }
            else
            {
                File file = new File(scanner.nextLine());

                while (!file.exists()) {
                    System.out.println("which is the path you wanna merge this with?");
                    file = new File(scanner.nextLine());
                }

                System.out.println("you sure you want to merge " + file.getPath() + " & " + "\n" + libraryLocation + "?");

                if (scanner.next().charAt(0) == 'y') {

                    ScrobbleObjectMap library1;
                    ScrobbleObjectMap library2;

                    library1 = loadFromFile(new ScrobbleObjectMap(), new File(libraryLocation));
                    library2 = loadFromFile(new ScrobbleObjectMap(), file);

                    library1.storeAtPath(libraryLocation + ".bkp");

                    library1.putAll(library2);
                    library1.generalize();
                    //library1.generealizeByReference(onlineScrobbles);

                    library1.storeAtPath(libraryLocation);

                }
            }
        }

    }

    private static void setLocations(String dbLocation, String libraryLocation)
    {
        if(libraryLocation == null || dbLocation == null) {
            saveAsFile(LIBRARY_LOCATION, PROJECTFOLDER + "\\" + "libraryLocation");
            saveAsFile(DB_LOCATION, PROJECTFOLDER + "\\" + "dbLocation");
        }
        else {

            System.out.println("current locations:" + "\n" +

                    "current Library location: " + libraryLocation + "\n" +
                    "current Library location: " + dbLocation);

            System.out.println("do you wanna change these?");
            final Scanner scanner = new Scanner(System.in);
            if (scanner.next().charAt(0) == 'y') {
                System.out.println("from where do you wanna load itunes from");

                //because the line is still open since last call for scanner
                scanner.nextLine();

                saveAsFile(scanner.nextLine(), PROJECTFOLDER + "\\" + "libraryLocation");

                System.out.print("from where do you want to store locale db");

                saveAsFile(scanner.next(), PROJECTFOLDER + "\\" + "dbLocation");
            }
        }
    }



    private static int menu()
    {
        System.out.print
        (
        "1. sync ipod\n" +
        "2. scrobble other user retroactively\n" +
        "3. scrobble other user continually\n" +
        "4. fetch recommended songs\n" +
        "5. switch userprofile\n"+
        "6. extract API key\n"+
        "7. merge ItunesLibEmtries\n"+
        "8. Find errors\n"
        );

        return (new Scanner(System.in).nextInt());
    }


}

