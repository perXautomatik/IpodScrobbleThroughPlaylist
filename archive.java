import ModuleScrobbler.IpodScrobbler;
import ModuleScrobbler.commonMethods;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import commonMethods.CommonMethods;
import de.umass.lastfm.Track;
import de.umass.lastfm.User;
import org.farng.mp3.AbstractMP3Tag;
import org.farng.mp3.id3.AbstractID3v2;
import org.farng.mp3.id3.ID3v1;
import org.farng.mp3.id3.ID3v2_4;

import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.security.DigestException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static commonMethods.CommonMethods.ScrobbleObject.scrobbleObjectToHash;
import static org.apache.commons.io.FileUtils.copyFile;

/**
 * Created by user on 2014-07-24.
 */
public class archive {
    final static int NROFFILES = 15;
    final static int MAXIMUMSCROBBLEOBJECTS = 4000;
    private static final int MAX_NR_OF_DUPES = 10;

    /*
        private static void copyArrayToArray(IpodScrobbler.ScrobbleObject[] x, IpodScrobbler.ScrobbleObject[] o)
        {
        //foreach simplification,
        for (IpodScrobbler.ScrobbleObject scrobbleObject : x)
        {
        System.arraycopy(x, 0, o, 0, x.length);
        }

        }
        If an m3u file is edited with a text editor,
        it must be saved in the Windows-1252 format.
          "m3u" files properly use the Latin-1 character set.
          [citation needed] The Unicode version of "m3u" is "m3u8",
           which uses UTF-8 Unicode characters.

        List sortList = Arrays.asList(sortArray);
        Collections.sort(sortList, new archive.NaturalOrderComparator());
        sortList.toArray(sortArray);

        for (int i = 0; i < nrOfObjects; i++)
        {
        try
        {
        writerToTagTxt.println(sortArray[i]);
        }
        catch (Exception e){
        System.out.println(e + " @m3uPrint @sortArray print");
        }
        }
        writerToTagTxt.close();

                PrintWriter writerToTagTxt = new PrintWriter(playlistFolder + "\\playlist.txt");
                String[] sortArray = new String[nrOfObjects];

        artist = scrobbleObjects[i].getArtist();
        album = scrobbleObjects[i].getAlbum();
        title = scrobbleObjects[i].getTitle();
        sortArray[i] = (filename + " xqx " + artist + " xwx " + title + " xwx " + album);

        else
        {
        i = i;
        }
        */
    public static void m3uPrint( Map<String, IpodScrobbler.ScrobbleObject> itunesRaw, Map<String, IpodScrobbler.ScrobbleObject> fetchedScrobbles) throws FileNotFoundException {

        HashMap<String, IpodScrobbler.ScrobbleObject> scrobbleObjects = new HashMap<String, IpodScrobbler.ScrobbleObject>(itunesRaw);

        File playlistFolder = new File(ModuleScrobbler.IpodScrobbler.PROJECTFOLDER);

        int nrOfPrints = 0;
        String textLine = "#EXTM3U" + "\n";

        if (!playlistFolder.isDirectory()) playlistFolder.mkdirs();

        PrintWriter writerToM3u = new PrintWriter(playlistFolder + "\\playlist.m3u");

        try{writerToM3u.println(textLine);}catch (Exception e){System.out.println(e + " @m3uPrint @writerToM3u.println(textLine);");}
        {
            int i = 0;
            final int size = fetchedScrobbles.size();
            IpodScrobbler.Progressbar progressbar = new IpodScrobbler.Progressbar(size,"Analysing fetched scrobbles:");

            for (IpodScrobbler.ScrobbleObject value : fetchedScrobbles.values()) {
                int tempScrobbleCount;
                String fetchedKey = IpodScrobbler.nameGen(value);
                try{
                    tempScrobbleCount = scrobbleObjects.get(fetchedKey).getScrobbleCount();
                }
                catch (Exception e){
                    tempScrobbleCount = 0;
                }
                if (scrobbleObjects.put(fetchedKey, value) != null) {
                    scrobbleObjects.get(fetchedKey).setScrobbleCount((tempScrobbleCount - scrobbleObjects.get(fetchedKey).getScrobbleCount()));
                }
                else if (scrobbleObjects.put(fetchedKey, value) == null)
                {
                    scrobbleObjects.remove(fetchedKey);
                    System.out.println("unrecognized track encountered: " + value.present());
                }
                progressbar.printAndIncrement();
            }
        }

        final int size = scrobbleObjects.size();
        IpodScrobbler.Progressbar progressbar = new IpodScrobbler.Progressbar(size,"M3uing scrobbles:");

        for (IpodScrobbler.ScrobbleObject scrobbleObject : scrobbleObjects.values())
        {
            try
            {
                if(scrobbleObject.getScrobbleCount() > 0) {
                    for (int j = 0; j < scrobbleObject.getScrobbleCount(); j++) {
                        try {
                            writerToM3u.println(IpodScrobbler.nameGen(scrobbleObject));
                            nrOfPrints++;
                        } catch (Exception e) {
                            System.out.println(e + " @m3uPrint @writerToM3u.println(filename);");
                        }
                    }
                }
            }
            catch (Exception e){
                System.out.println(e + " @m3uPrint @writerToM3u.println(filename);");
            }
            progressbar.printAndIncrement();
        }

        writerToM3u.close();
        System.out.println(" closing \n nrOfPrints:" + nrOfPrints);
    }

    //  Writer writer = null;
    // ...
//            }
//            for(int i = 0; i < scrobbleObjects.size(); i++)
//            {
    //                        printArray[j++] = tempFile;
    public static void makeMp3(Map<String, IpodScrobbler.ScrobbleObject> scrobbleObjects, File destFolder, File srcFile) throws IOException, NoSuchAlgorithmException, DigestException
    {
        if(scrobbleObjects.size() != 1)
        {
            IpodScrobbler.Progressbar progressbar = new IpodScrobbler.Progressbar(scrobbleObjects.size(),"\nmakeMp3 progress:");

            for (IpodScrobbler.ScrobbleObject value : scrobbleObjects.values())
            {
                File tempFile = new File (destFolder, IpodScrobbler.nameGen(value));

                if (!tempFile.exists() && !tempFile.isDirectory())
                {
                    copyFile(srcFile, tempFile, false);

                    try
                    {
                        mp3TagWrite(tempFile,value,false);
                    }
                    catch(Exception U)
                    {
                        System.out.print(U + "@tagWrite");
                    }
                }
                else
                {
                    try
                    {
                        mp3TagWrite(tempFile,value, true);
                    }
                    catch(Exception U)
                    {
                        System.out.print(U + "@tagWrite");
                    }
                }
                progressbar.printAndIncrement();
            }
        }
    }

    /*

        if(!tempString.equals("") && j != 0)
        {
            for(int i = 0; i < nrOfObjects; i++)
            {
                try
                {
                    copyFile(srcFile, printArray[i], false);
                    mp3TagWrite(printArray[i],scrobbleObjects[i]);
                }
                catch(Exception U)
                {
                System.out.print(U + "@makeMp3");
                }
            }
        }
    */




/*
        mainMap<artistname, MapOfalbumsByArtist<AlbumName, ListOftracksByAlbum<track>>>

    map
        artis1
            album 1 - asga
            album 5 - xas
            album 3 - track1,track7
        artis2
            album 1 - asga
            album 5 - xas
            album 3 - track1,track7
*/
            HashMap <String,HashMap<String, HashSet<ScrobbleObject>>> newMap = new HashMap();

            for(final ScrobbleObject track : this.values())
            {
                final String trackArtist = track.getArtist();
                final String trackAlbum = track.getAlbum();

                if (!newMap.containsKey(trackArtist))
                {
                    newMap.put(trackArtist, new HashMap<String, HashSet<ScrobbleObject>>());
                    newMap.get(trackArtist).put(trackAlbum, new HashSet<ScrobbleObject>());
                }
                else
                {
                    if (!newMap.get(trackArtist).containsKey(trackAlbum))
                    {
                        newMap.get(trackArtist).put(trackAlbum, new HashSet<ScrobbleObject>());
                    }
                }

                newMap.get(trackArtist).get(trackAlbum).add(track);
            }

            /*            int nrOfArtists = arrangedMap.size();
            HashSet albumSet = new HashSet();

            for (HashMap a : arrangedMap.values())
            {
                albumSet.addAll(a.keySet());
            }

            int nrOfUniqeAlbums = albumSet.size();*/


/*            ArrangedView = new HashMap
            //aödjaäjdäa
            print the least possible nodes with the largest number
            of subnodes
            if album artists > 1, albumartist is various artist
            if artist nrOftracks == 1 && track.albumartist == various*/

    //first group tracks by albums


    //sort list by artist
    //albumView.sortByArtist();

    //then group albums by common artist
    public static void report(Map<String, IpodScrobbler.ScrobbleObject> scrOs, File destFile, int scroNr)
    {
        //  Writer writer = null;
        int skipped = 0;
        int notskipped = 0;
        int nrOfFiles;

        nrOfFiles = destFile.listFiles().length;

        if(!scrOs.isEmpty())
        {
            for (IpodScrobbler.ScrobbleObject value : scrOs.values())
            {
                File f = new File(destFile +"\\"+ IpodScrobbler.nameGen(value));

                if (!f.exists() && !f.isDirectory())
                {
                    notskipped++;
                }
                else{
                    skipped++;
                }
            }

        }
        System.out.println("nrOfFiles: " + nrOfFiles);
        System.out.println("nrOfskipped: " + skipped);
        System.out.println("nrOfDBscrobbleObjects: " + scroNr);
        System.out.println("nrOfNotSkipped: " + notskipped);
    }

    private static void mp3TagWrite(File destFile, IpodScrobbler.ScrobbleObject scrO, Boolean excists) throws IOException, org.jaudiotagger.tag.TagException {
        // String absolutepath = destFile.getAbsolutePath();
        boolean changes = false;
        org.farng.mp3.MP3File file;
        //AudioFileIO.read(File)
        ID3v2_4 tag = new ID3v2_4();
        //ID3v24Tag tag = new ID3v24Tag();
        String encoding = "UNICODE";
        try
        {

            file = new org.farng.mp3.MP3File(destFile,true);

            //  file = new org.jaudiotagger.audio.mp3.MP3File(destFile);

            if(excists)
            {
                if(file.hasID3v1Tag() && !file.hasID3v2Tag())
                {
                    ID3v1 tagfile = file.getID3v1Tag();
                    //ID3v1Tag tagfile = file.getID3v1Tag();

                    try{
                        tag = new ID3v2_4(tagfile);
                        //tag = new ID3v24Tag(tagfile);
                    }
                    catch (Exception e){
                        System.out.print(e+"@tagwrite:");
                    }
                }
                else if (file.hasID3v2Tag() && !file.hasID3v1Tag())
                {
                    AbstractID3v2 tagfile = file.getID3v2Tag();
                    //AbstractID3v2Tag tagfile = file.getID3v2Tag();

                    try{
                        tag = new ID3v2_4(tagfile);
                     //   tag = new ID3v24Tag(tagfile);
                    }
                    catch (Exception e){
                        System.out.print(e+"@tagwrite:");
                    }
                }

                //String encoding = "ISO-8859-1";

                final boolean equals = tag.getAlbumTitle().equals(scrO.getAlbum());
               // final boolean equals = (tag.getFirst(ID3v24Frames.FRAME_ID_ALBUM)).equals(scrO.getAlbum());
                if(!equals) {
                    tag.setAlbumTitle(new String(scrO.getAlbum().getBytes(), encoding));
                //    tag.setField(FieldKey.ALBUM, new String(scrO.getAlbum().getBytes(), encoding));
                    //tag.setAlbumTitle("\u0010" + scrO.getAlbum());
                    changes = true;
                }
                final boolean equals1 = tag.getLeadArtist().equals(scrO.getArtist());
                if(!equals1) {
                    tag.setLeadArtist(new String(scrO.getArtist().getBytes(), encoding));
//                    final boolean equals1 = (tag.getFirst(ID3v24Frames.FRAME_ID_TITLE)).equals(scrO.getAlbum());
//                    if(!equals) {
//                        tag.setField(FieldKey.ARTIST, new String(scrO.getArtist().getBytes(), encoding));
//                        //tag.setLeadArtist("\u0010" + scrO.getArtist());
                        changes = true;
                }
                final boolean equals2 = tag.getSongTitle().equals(scrO.getTitle());
                if(!equals2) {
                    tag.setSongTitle(new String(scrO.getTitle().getBytes(), encoding));
//                        final boolean equals2 = (tag.getFirst(ID3v24Frames.FRAME_ID_ARTIST)).equals(scrO.getAlbum());
//                        if(!equals) {
//                            tag.setField(FieldKey.ARTIST, new String(scrO.getTitle().getBytes(), encoding));
                        //            tag.setSongTitle("\u0010" + scrO.getTitle());
                        changes = true;
                    }
            }
            else
            {
                tag.setAlbumTitle(new String(scrO.getAlbum().getBytes(), encoding));
                tag.setLeadArtist(new String(scrO.getArtist().getBytes(), encoding));
                tag.setSongTitle(new String(scrO.getTitle().getBytes(), encoding));

//                        tag.setField(FieldKey.ALBUM, new String(scrO.getAlbum().getBytes(), encoding));
//                        tag.setField(FieldKey.ARTIST, new String(scrO.getArtist().getBytes(), encoding));
//                        tag.setField(FieldKey.ARTIST, new String(scrO.getTitle().getBytes(), encoding));
                changes = true;
            }

            if (changes) {
                file.setID3v2Tag((AbstractMP3Tag)tag);
                file.save(1);
//                        file.commit();
            }
        }
        catch (Exception e)
        {
        System.out.print(e+"@tagwrite:");
        }

    }

    private static String fetchPage(String s) {
        String content = null;
        URLConnection connection = null;
        try
        {
            connection =  new URL(s).openConnection();
            Scanner scanner = new Scanner(connection.getInputStream());
            scanner.useDelimiter("\\Z");
            content = scanner.next();

        }catch ( Exception ex ) {
            ex.printStackTrace();
        }
        return content;
    }

    public static int listFilesForFolder(final File folder, File[] fileList)
    {
        int i = 0;
        String progress = "";
    try{
            for (final File fileEntry : folder.listFiles())
            {
                if (fileEntry.isDirectory()) {
                    listFilesForFolder(fileEntry, fileList);
                    System.out.println("recoursing");
                }
                else {
                    fileList[i++] = fileEntry;
                    progress += "|";
                }
            }
        System.out.println(progress);
        //     fileList = folder.listFiles();
            return i;
    }catch(Exception e){System.out.println(e + " @listFilesForFolder");        return i;}
    }

    public static void databaseWrite(Map<String, IpodScrobbler.ScrobbleObject> scrobbleObjects, File path) throws FileNotFoundException {

        DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmmss");
        Date date = new Date();
        String rDate = dateFormat.format(date);

        int j=0;

        PrintWriter writerOLD = new PrintWriter(path + "\\" + rDate);

        for (IpodScrobbler.ScrobbleObject value : scrobbleObjects.values()) {

            if(value != null)
            {
                writerOLD.println(
                    value.getTitle()+ "\t" +
                            value.getArtist()+ "\t" +
                            value.getAlbum()+ "\t" +
                            value.getScrobbleCount());
            }
            j++;
        }

        System.out.println(j + "entries written, closing database");
        //cleansing array
//        try
//        {
//            IpodScrobbler.ScrobbleObject[] emptyArray = new IpodScrobbler.ScrobbleObject[objects];
//            //scrobbleObjects = emptyArray;
//            //you can't alter the reference of an array whom was passed as function argument
//        }

//        catch (Exception e){System.out.println(e + " @databaseWrite");}

        writerOLD.close();
    }

    public static int databaseRead(File path, Map<String, IpodScrobbler.ScrobbleObject> scrobbleObjects) throws FileNotFoundException
    {
        File[] fileArray = new File[NROFFILES];
        int nrOfFiles = listFilesForFolder(path,fileArray);
        String title;
        String artist;
        String album;
        int fileNr;
        int index = 0;

        for (int i = 0; i < nrOfFiles; i++)
        {
            Scanner in = new Scanner(fileArray[i]);

            while (in.hasNext())
            {
                String line = in.nextLine();
                if(line != null)
                    try {
                        Scanner lineIn = new Scanner(line);
                        lineIn.useDelimiter("\t+");

                        title = lineIn.next();
                        artist = lineIn.next();
                        album = lineIn.next();
                        fileNr = lineIn.nextInt();

                        IpodScrobbler.ScrobbleObject scrobbleObject = new IpodScrobbler.ScrobbleObject(title, artist, album, fileNr);
                        scrobbleObjects.put(IpodScrobbler.nameGen(scrobbleObject), scrobbleObject);
                        lineIn.close();
                        index++;
                    } catch (Exception e) {
                        System.out.print(e + "@database text readin");
                    }
                else
                    System.out.println("null encountered");
            }
            in.close();
        }
        return index;
    }

    static void deleteCurrentDatab(File path)
    {
        File[] fileArray = new File[NROFFILES];
        int nrOfFiles = listFilesForFolder(path,fileArray);
        for (int i = 0; i < nrOfFiles; i++)
        {
            try{
                if(fileArray[i].delete())
                {
                    System.out.println(fileArray[i].getName() + " is deleted!");
                }

                else
                {
                    System.out.println("Delete failed.");
                }

            }catch(Exception e){System.out.println(e + " @databaseRead(delete)");
            }
        }
    }

    public static int readIn(File folder, IpodScrobbler.ScrobbleObject[] scrobbleObjects)
    {
        String title;
        String artist;
        String album;
        int fileNr;
        int index = 0;
        String line = "";
        //String charSet = "UNICODE";
        String charSet = "UTF-8";


        try
        {
            for (File file : folder.listFiles())
            {
                Scanner in = new Scanner(file,charSet);
                //filename = fileArray[i].getName();
                fileNr = Integer.parseInt(IpodScrobbler.stripNonDigits(file.getName()));

                //removes the topline
                in.nextLine();

                while (in.hasNext())
                {
                    line = in.nextLine();
                    if(line != null)
                    {
                        Scanner lineIn = new Scanner(line);
                        lineIn.useDelimiter("\t+");

                        title = lineIn.next();
                        artist = lineIn.next();
                        album = lineIn.next();

                        scrobbleObjects[index++] = new IpodScrobbler.ScrobbleObject(title, artist, album, fileNr);
                        lineIn.close();
                    }
                    else
                    {
                        System.out.println("null encountered");
                    }
                    //                System.out.print("pause");
                }
                in.close();
            }
            return index;
        }
        catch (Exception e){System.out.println(e + " @readIn");
            return index;}
    }

    public static void compareScrobbles(Map<String, IpodScrobbler.ScrobbleObject> dbMap, Map<String, IpodScrobbler.ScrobbleObject> output, File itunesExportFolder)
    {
        IpodScrobbler.ScrobbleObject[] iTunesReadArray = new IpodScrobbler.ScrobbleObject[MAXIMUMSCROBBLEOBJECTS];

        int itunesFetches = readIn(itunesExportFolder, iTunesReadArray);

        if (!dbMap.isEmpty())
        {
            if((IpodScrobbler.doesLeftContainRightNmore(iTunesReadArray, dbMap) == 1) && (itunesFetches > dbMap.size()) && (itunesFetches < (dbMap.size() + MAX_NR_OF_DUPES)))
            {
                output.putAll(dbMap);
            }
            else
            {
                output.putAll(purge(iTunesReadArray, itunesFetches));
            }
        }
    }

    private static Map<String, IpodScrobbler.ScrobbleObject> purge(IpodScrobbler.ScrobbleObject p0[], int index) {

        HashMap<String, IpodScrobbler.ScrobbleObject> dedupedScrobbleObjects = new HashMap<String, IpodScrobbler.ScrobbleObject>();

        actOnEvaluate(p0,index,dedupedScrobbleObjects);

        return dedupedScrobbleObjects;

    }
    //Progressbar progressbar = new Progressbar(leftMap.size(), "Analysing fetched scrobbles:");
//                    if (!rightObject.equals(leftObject)) {
//                        System.out.println(leftObject.present() + " \n&: " + rightObject.present() +"\n");
//                    }
    //boolean justPrintedUnequality = true;

    //        if(maptToCount.containsKey("playcount"))
//        {
//            sum = -maptToCount.getSubmissionDates("playcount").getScrobbleCount();
//        }
//            else
//                System.out.println("scrobbles according to apiCall:" + value.getScrobbleCount() + "\n");


    //        while (nrDupes !=0)
//        {
//            System.out.println("to combine: pick c to combine,r to refresh, and a to close");
//            choice = in.next().charAt(0);
//
//            switch (choice)
//            {
//                case 'c':
//                {
//                    System.out.println("type the two generateHash string you want to correct");
//                    String nrA = in.next();
//                    String nrB = in.next();
//
//                    try{
//                        System.out.println("smallList: " + mapToCheck.get(nrA).present() + "\nlongList: " + referenceMap.get(nrB).present());
//                    }
//                    catch (Exception e){
//                        String temp = nrA;
//                        nrA = nrB;
//                        nrB = temp;
//
//                        try{
//                            System.out.println("smallList: " + mapToCheck.get(nrA).present() + "\nlongList: " + referenceMap.get(nrB).present());
//                        }
//                        catch (Exception f){
//                            System.out.print(f + "@");
//                        }
//                    }
//
//                    choice = in.next().charAt(0);
//
//                    if(choice == 'y')
//                    {
//                        nrDupes--;
//                        try{
//                            dupeRemember(mapToCheck.get(nrA), referenceMap.get(nrB));
//                        }
//                        catch (Exception e){
//                            System.out.print(e + "@");
//                            try{
//                                dupeRemember(referenceMap.get(nrB), mapToCheck.get(nrA));
//                            }
//                            catch (Exception f){
//                                System.out.print(f + "@");
//                            }
//                        }
//                        mapToCheck.remove(nrA);
//                    }
//                    break;
//                }
//                case 'r':
//                {
//                    nrDupes = 0;
//                    findAndActOnDupes(mapToCheck, referenceMap);
//                }
//                case 'a':
//                {
//                   nrDupes = 0;
//                }
//            }
//        }
    private static int actOnEvaluate(IpodScrobbler.ScrobbleObject[] inputArray, int scroNr, HashMap<String, IpodScrobbler.ScrobbleObject> scrobbleObjectHashMap)
    {
        Scanner in = new Scanner(System.in);
        char choice;
        int nrdupes = evaluateScrobbleobjects(inputArray, scroNr);

        while (nrdupes !=0)
        {
            System.out.println("to combine: pick c to combine and a to ad a . to title");
            choice = in.next().charAt(0);

            switch (choice)
            {
                case 'c':
                {
                    System.out.println("type number 1 and number to end each with enter");
                    int nrA = in.nextInt();
                    int nrB = in.nextInt();

                    System.out.println("combine: " + inputArray[nrA].present() + "\n " + inputArray[nrB].present());
                    choice = in.next().charAt(0);

                    if(choice == 'y')
                    {
                        nrdupes--;
                        inputArray[nrA].setScrobbleCount(inputArray[nrA].getScrobbleCount() + inputArray[nrB].getScrobbleCount());
                        inputArray[nrB] = null;
                    }
                    break;
                }

                case 'a':
                {
                    System.out.println("pick nr to extend");
                    int nrA = in.nextInt();
                    inputArray[nrA].setTitle(inputArray[nrA].getTitle()+" ");
                    nrdupes--;
                }
            }
            //         j++;
            //outputArray[j] = new IpodScrobbler.ScrobbleObject(scrobbleObject);
        }
        for (IpodScrobbler.ScrobbleObject scrobbleObject : inputArray)
        {
            if(scrobbleObject != null)
            {
                try
                {
                    scrobbleObjectHashMap.put(IpodScrobbler.nameGen(scrobbleObject), scrobbleObject);
                }
                catch (Exception e){
                    System.out.println(e + " @input to output");
                }
            }
        }

        return scrobbleObjectHashMap.size();
    }

    private static int doesLeftContainRightNmore(IpodScrobbler.ScrobbleObject[] itunesRaw, Map<String, IpodScrobbler.ScrobbleObject> db)
    {
        boolean itunesHasAllDb = false;

        boolean dbHasAllItune = false;

        for (IpodScrobbler.ScrobbleObject itunesItem : itunesRaw)
        {
            for (IpodScrobbler.ScrobbleObject dbItem : db.values())
            {
                if (itunesItem != null)
                //dbHasAllItune = (itunesItem.getArtist().equals(dbItem.getArtist()) && itunesItem.getTitle().trim().equals(dbItem.getTitle().trim()) && itunesItem.getAlbum().equals(dbItem.getAlbum()));
                 try{
                     dbHasAllItune = itunesItem.equals(dbItem);
                 }
                 catch(IpodScrobbler.PlayCountDifferentException e){
                     System.out.println(e);
                     dbHasAllItune= e.getValue();
                 }

                if(dbHasAllItune)
                    break;
            }
            if(!dbHasAllItune)
                break;
        }
        for (IpodScrobbler.ScrobbleObject dbItem : db.values()) {
            for (IpodScrobbler.ScrobbleObject itunesItem : itunesRaw)
            {
                if (itunesItem != null)
//                    itunesHasAllDb = (itunesItem.getArtist().equals(dbItem.getArtist()) && itunesItem.getTitle().trim().equals(dbItem.getTitle().trim()) && itunesItem.getAlbum().equals(dbItem.getAlbum()));
                    try{
                        itunesHasAllDb = itunesItem.equals(dbItem);
                    }
                    catch(IpodScrobbler.PlayCountDifferentException e){
                        System.out.println(e);
                        itunesHasAllDb = e.getValue();
                    }

                if(itunesHasAllDb)
                    break;
            }
            if(!itunesHasAllDb)
                break;

        }

        if(!dbHasAllItune && itunesHasAllDb)
        return 1;

        else if(dbHasAllItune && !itunesHasAllDb)
        return -1;

        else
        return 0;

    }

    static Map urlXmlToMap(String url) throws IOException
    {
        String jsonText = null;
        if (url.contains("&format=json")) {
            url = url.replaceAll("&format=json", "");
        }
        InputStream is = new URL(url).openStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
        jsonText = IpodScrobbler.readAll(rd);

// convert to XML
        XStream xStream = new XStream(new DomDriver());
        Map map2 = new HashMap<String, Object>();

        String[] trackEntries = jsonText.split("</track>");

        for(String string: trackEntries)
        {
            string = ("<?xml version=\"1.0\" encoding=\"utf-8\"?><track>".concat((string.split("<track>"))[1])).concat("</track>");
            try{
                map2 = (Map) xStream.fromXML(string);
            }
            catch (Exception e){
                e.fillInStackTrace();
            }
        }
        return map2;
    }

    static int evaluateScrobbleobjects(IpodScrobbler.ScrobbleObject[] objectArray, int nrOfObjects)
    {
        IpodScrobbler.Progressbar progressbar = new IpodScrobbler.Progressbar(nrOfObjects);
        int nrOfDupes =0;
        for (int j=0;j<nrOfObjects;j++)
        {
            if(objectArray[j] != null)
            {
                for (int k=j+1;k<nrOfObjects;k++)
                {
                    if (objectArray[k] != null)
                    {
                        if (k != j) {
                            try{
                            if (IpodScrobbler.nameGen(objectArray[k]).equals(IpodScrobbler.nameGen(objectArray[j])))
                            {
                                nrOfDupes++;
                                System.out.println(k + ": " + objectArray[k].present() + "\n " + j + ": " + objectArray[j].present());
                            }
                            }catch(Exception e){
                                System.out.println(e + " @evaluateTwoMaps");
                            }
                        }
                    }
                }
            }
            progressbar.printAndIncrement();
        }
        return nrOfDupes;
    }

    private static String weakNameGen(IpodScrobbler.ScrobbleObject scrobbleObject)
    {
        String title;
        String artist;

        try
        {
            artist = scrobbleObject.getArtist();
            artist.length();
        }
        catch (Exception e){
            artist = "";
        }
        try
        {
            title = scrobbleObject.getTitle();
            title.length();
        }
        catch (Exception e){
            title = "";
        }

        if(artist.toLowerCase().startsWith("the ", 0))
        {
            artist = artist.toLowerCase().split("the ")[1];
        }

        String returnString = "";
        switch(3 <= artist.length()? 3 : artist.length())
        {
            case (3):
            {
                returnString += (String.valueOf(artist.charAt(2)) + artist.charAt(artist.length()-3));
            }
            case 2:
            {
                returnString += (String.valueOf(artist.charAt(1)) + artist.charAt(artist.length()-2));
            }
            case 1:
            {
                returnString += (String.valueOf(artist.charAt(0)) + artist.charAt(artist.length()-1));
                break;
            }
            case 0:
        }

        switch(6 <= title.length()? 6 : title.length()){

            case (6):
            {
                returnString += (String.valueOf(title.charAt(5)));
            }
            case (5):
            {
                returnString += (String.valueOf(title.charAt(4)));
            }
            case (4):
            {
                returnString += (String.valueOf(title.charAt(3)));
            }
            case (3):
            {
                returnString += (String.valueOf(title.charAt(2)));
            }
            case 2:
            {
                returnString += (String.valueOf(title.charAt(1)));
            }
            case 1:
            {
                returnString += (String.valueOf(title.charAt(0)));
                break;
            }
            case 0:
        }

        if (title.toLowerCase().contains("(")) {
            if (title.toLowerCase().charAt(title.indexOf("(") + 1) == 'r') {
                returnString += title.length();
            } else {
                returnString += title.charAt(title.indexOf("(") + 1);
                returnString += title.charAt(title.indexOf("(") + 2);
            }
        }
        if (title.toLowerCase().contains("2k9")) {
            returnString += title.length();
        }

        returnString = returnString.toLowerCase();
        //.replaceAll("[^a-z0-9]","_")
       // returnString = URLEncoder.encode(returnString);

        return (returnString);
    }
   /*        fetchedScrobbles = sortMapByPlayCount(fetchedScrobbles);
            extractedFromItunes = sortMapByPlayCount(extractedFromItunes);
            for (IpodScrobbler.ScrobbleObject value : fetchedScrobbles.values()) {
                if(i < i1)
                {
                    System.out.println(value.getScrobbleCount() + " : " + value.present());
                    i++;
                }
                else {
                    break;
                }
            }*/

    private static ScrobbleObjectMap formerSubbmissions(
            ScrobbleObjectMap referenceMap, ScrobbleObjectMap itunesTracks, String dbPath, boolean dbExists
    ) throws IOException {
        ScrobbleObjectMap errorSubmissions;
        ScrobbleObjectMap alienEntries;
        ScrobbleObjectMap tracksToScrobble;
        boolean ignorePrefetch = false;
        final ScrobbleObjectMap referenceMap1 = referenceMap;
        final ScrobbleObjectMap itunesTracks1 = itunesTracks;

        do {

            ScrobbleObjectMap pastSuccessfulScrobbles =
                    getMapOfPastScrobbles(referenceMap1, dbPath, dbExists, ignorePrefetch, USERNAME);


            errorSubmissions = new ScrobbleObjectMap();
            alienEntries = new ScrobbleObjectMap();

            tracksToScrobble = subtractLeftFromRight(pastSuccessfulScrobbles, itunesTracks1, errorSubmissions, alienEntries);

            printStats(itunesTracks1, pastSuccessfulScrobbles, tracksToScrobble, errorSubmissions, alienEntries);

            System.out.println("\nrefetch?");

            ignorePrefetch = (new Scanner(System.in).next().charAt(0) == 'y');

        } while (ignorePrefetch);
        return tracksToScrobble;
    }


    private static void scrobbleUserContinually() throws IOException, NoSuchAlgorithmException {
        //command line input to autostart scrobbleing from desktop for certain user
        final Scanner scanner = new Scanner(System.in);
        System.out.println("specify host user to fetch scrobbles from;\n");
        String hostUser = scanner.next();

        LinkedList<String> guestUsers = new LinkedList<String>();
        System.out.println("list the users that wants to scrobble host, type n to continue\n");
        do {
            guestUsers.add(scanner.next());
        }while (!guestUsers.getLast().equals("n"));
            guestUsers.removeLast();
        boolean allUsersHasSessionKey = false;
        for(String user : guestUsers)
        {
            if(IpodScrobbler.getSessionKey(user).equals(""))
            {
                allUsersHasSessionKey = false;
                break;
            }
            else
            {
                allUsersHasSessionKey = true;
            }
        }

        if(allUsersHasSessionKey)
        {
            IpodScrobbler.ScrobbleObject lastScrobbledTrack = null;

            boolean runForever = true;

            do
            {
                final IpodScrobbler.ScrobbleObject currentlyPlaying;

                try {
                    currentlyPlaying = IpodScrobbler.fetchCurrentlyPlaying(hostUser);

                boolean equals;

                if (lastScrobbledTrack == null && currentlyPlaying.isNowPlaying())
                {
                    equals = false;
                }
                else if(currentlyPlaying.isNowPlaying())
                {
                    try
                    {
                        equals = lastScrobbledTrack.equals(currentlyPlaying);
                    }
                    catch (IpodScrobbler.PlayCountDifferentException e)
                    {
                        equals = true;
                    }
                }
                else
                    equals = true;

                if (!equals)
                {
                    lastScrobbledTrack = currentlyPlaying;
                    lastScrobbledTrack.setScrobbleCount(1);
                    for(String user : guestUsers)
                    {
                        IpodScrobbler.scrobble(IpodScrobbler.getSessionKey(user), lastScrobbledTrack);
                    }
                }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                try
                {
                    Thread.sleep(30000);
                }
                catch (InterruptedException ex)
                {
                    Thread.currentThread().interrupt();
                }
            }while(runForever);
        }
    }

    private static IpodScrobbler.ScrobbleObject fetchCurrentlyPlaying(String hostUser) throws Exception {
        Track track;

        track =
            new ArrayList<Track>(
                    User.getRecentTracks(hostUser, 1, 1, commonMethods.APIKEY).getPageResults()).get(0);
            return new IpodScrobbler.ScrobbleObject(track);
    }

    static class NaturalOrderComparator implements Comparator
    {
        /*
     archive.NaturalOrderComparator.java -- Perform 'natural order' comparisons of strings in Java.
     Copyright (C) 2003 by Pierre-Luc Paour <natorder@paour.com>

     Based on the C version by Martin Pool, of which this is more or less a straight conversion.
     Copyright (C) 2000 by Martin Pool <mbp@humbug.org.au>

     This software is provided 'as-is', without any express or implied
     warranty.  In no event will the authors be held liable for any damages
     arising from the use of this software.

     Permission is granted to anyone to use this software for any purpose,
     including commercial applications, and to alter it and redistribute it
     freely, subject to the following restrictions:

     1. The origin of this software must not be misrepresented; you must not
     claim that you wrote the original software. If you use this software
     in a product, an acknowledgment in the product documentation would be
     appreciated but is not required.
     2. Altered source versions must be plainly marked as such, and must not be
     misrepresented as being the original software.
     3. This notice may not be removed or altered from any source distribution.
     */



        public int compare(Object o1, Object o2)
        {
            if((o1 == null) ||(o2 == null))
            {
                int i = 0;
            }
            String stringA = (o1 == null)? "" : o1.toString();
            String stringB = (o2 == null)? "" : o2.toString();


            int intA = 0, intB = 0;
            int nrOfZeroA, nrOfZeroB;
            char charA, charB;
            int result;

            while (true)
            {
                // only count the number of zeroes leading the last number compared
                nrOfZeroA = nrOfZeroB = 0;

                charA = charAt(stringA, intA);
                charB = charAt(stringB, intB);

                // skip over leading spaces or zeros (unleas 0 precedes a nondigit)
                if(charA == '0' && ((intA == 0) || !Character.isDigit(charAt(stringA, (intA - 1)))) && !Character.isDigit(charAt(stringA, (intA+1))))
                {
                    nrOfZeroA++;
                }
                else
                {
                    while (Character.isSpaceChar(charA) || charA == '0')
                    {
                        if (charA == '0')
                        {
                            nrOfZeroA++;
                        }
                        else
                        {
                            // only count consecutive zeroes
                            nrOfZeroA = 0;
                        }

                        charA = charAt(stringA, ++intA);
                    }
                }

                if((charB == '0') && !Character.isDigit(charAt(stringB, (intB + 1))) && (intB == 0 || !Character.isDigit(charAt(stringB, (intB - 1)))))
                {
                    nrOfZeroB++;
                }
                else
                {
                    while (Character.isSpaceChar(charB) || charB == '0')
                    {
                        if (charB == '0')
                        {
                            nrOfZeroB++;
                        }
                        else
                        {
                            // only count consecutive zeroes
                            nrOfZeroB = 0;
                        }

                        charB = charAt(stringB, ++intB);
                    }
                }
                // process run of digits
                if (Character.isDigit(charA) && Character.isDigit(charB))
                {
                    if ((result = compareRight(stringA.substring(intA), stringB.substring(intB))) != 0)
                    {
                        return result;
                    }
                }

                if (charA == '0' && charB == '0' && (0 !=nrOfZeroA - nrOfZeroB))
                {
                    // The strings compare the same. Perhaps the caller
                    // will want to call strcmp to break the tie.
                    return nrOfZeroA - nrOfZeroB;
                }

                if (charA < charB)
                {
                    return -1;
                }
                else if (charA > charB)
                {
                    return +1;
                }

                ++intA;
                ++intB;
            }
        }

        int compareRight(String stringA, String stringB)
        {
            int bias = 0;
            int intA = 0;
            int intB = 0;

            // The longest run of digits wins. That aside, the greatest
            // value wins, but we can't know that it will until we've scanned
            // both numbers to know that they have the same magnitude, so we
            // remember it in BIAS.
            for (;; intA++, intB++)
            {
                char charA = charAt(stringA, intA);
                char charB = charAt(stringB, intB);

    /*            if((charA == '0' && intA == 0) || (charB == '0' && intB == 0))
                {
                    charA = charA;
                }*/

                if (!Character.isDigit(charA) && !Character.isDigit(charB))
                {
                    return bias;
                }
                else if (!Character.isDigit(charA))
                {
                    return -1;
                }
                else if (!Character.isDigit(charB))
                {
                    return +1;
                }
                else if (charA < charB)
                {
                    if (bias == 0)
                    {
                        bias = -1;
                    }
                }
                else if (charA > charB)
                {
                    if (bias == 0)
                        bias = +1;
                }
                else if (charA == '0' && charB == '0')
                {
                    return bias;
                }
            }
        }

        static char charAt(String s, int i)
        {
            if (i >= s.length())
            {
                return 0;
            }
            else
            {
                return s.charAt(i);
            }
        }
    }

    private static final Map<Integer, Integer> SavedFromTo;
    static {
        Map<Integer, Integer> aMap = new HashMap<Integer, Integer>();
        aMap.put(1404254850, 1404342284);
        aMap.put(1405877938, 1405881537);
        aMap.put(1407363970, 1407366104);
        SavedFromTo = Collections.unmodifiableMap(aMap);
    }
}

    /*
  Construct the Scanner and PrintWriter objects for reading and writing
  Read the input and write the output


int longestL = (newScrobbleObjects.size() >= oldScrobbleObjects.size() ? oldScrobbleObjects.size() : oldScrobbleObjects.size());

same equals true only if artist title album and placount is the same

for(int i = 0; i < longestL; i++)

{

same = newScrobbleObjects[i].getTitle().equals(oldScrobbleObjects[i].getTitle())

&& newScrobbleObjects[i].getArtist().equals(oldScrobbleObjects[i].getArtist())

&& newScrobbleObjects[i].getAlbum().equals(oldScrobbleObjects[i].getAlbum())

        IpodScrobbler.ScrobbleObject[] loadedFromItunes = new IpodScrobbler.ScrobbleObject[MAXIMUMSCROBBLEOBJECTS];
        IpodScrobbler.ScrobbleObject[] cleansedArray = new IpodScrobbler.ScrobbleObject[MAXIMUMSCROBBLEOBJECTS];
String homeFolder = "C:\\Users\\user\\Dropbox\\Cygwin\\Home\\";
http://ws.audioscrobbler.com/2.0/?method=user.getrecenttracks&user=rj&api_key=d165934486baa1453af66ca761cb180d&format=json
          final String method = "auth.getSession";
         String api_sig = encode("api_key" + APIKEY + "methodauth.getSessiontoken" + token + SECRET);
               String token= Authenticator.getToken(APIKEY);
            System.out.println("api_key = " + APIKEY);
        System.out.println("api_sig = " + hashedSig);
        String request = "http://ws.audioscrobbler.com/2.0/";

        String apiSig = "api_key"+ APIKEY +"methodauth.getSessiontoken"+ token + SECRET;
      MessageDigest md = MessageDigest.getInstance("MD5");
         FOR DEBUGGING
        md.update(apiSig.getBytes("UTF-8"));
        byte byteData[] = md.digest();
        convert the byte to hex format
        StringBuffer sb = new StringBuffer(byteData.length*2);
        for (int i = 0; i < byteData.length; i++) {
            sb.append(String.format("%02X", byteData[i]));
        }
        String hashedSig = sb.toString();
        "&description=" + URLEncoder.encode(description, "UTF-8") +  + "&title=" + URLEncoder.encode(title, "UTF-8")

        String sessionKey = fetchPage("http://ws.audioscrobbler.com/2.0/?method=auth.getSession&api_key=" + APIKEY + "&token=" + token + "&api_sig=" + hashedSig);
        String urlParameters = "method=" + method + "&api_key=" + APIKEY + "&api_sig=" + hashedSig + "&sk=" + sessionKey;
        System.out.println("session key = " + sessionKey);

    URL url = new URL(request);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setDoOutput(true);
    connection.setDoInput(true);
    connection.setInstanceFollowRedirects(false);
    connection.setRequestMethod("POST");
    connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
    connection.setRequestProperty("charset", "utf-8");
    connection.setRequestProperty("Content-Length", "" + Integer.toString(urlParameters.getBytes().length));
    connection.setUseCaches(false);

    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
    wr.writeBytes(urlParameters);
    wr.flush();
    wr.close();
        Session session = Session.createSession(APIKEY, SECRET, sessionKey);


itextLineScrobbler(scrobbleObjects, nrOfScrobbleObjects);

        Hashtable<Integer, IpodScrobbler.ScrobbleObject> table = new Hashtable<Integer, IpodScrobbler.ScrobbleObject>(nrOfScrobbleObjects);
        for (int i = 0; i < nrOfScrobbleObjects; i++) {
            table.hashCode();
        }

        hashtable = scrobbleObjects;

        evalueateExictingFiles(objectArray,ressultArray,tempFileArray);

        if(0 < evalueateDuplicates(objectArray,ressultArray))
        {
            for(File dupeObject : ressultArray)
            {
                System.out.println("double at " + i);
                try{
                System.out.println(dupeObject.getName());
                }catch(Exception e){System.out.println("no dupes to show");
                break;
                }
            }
        }
        else{System.out.println("No errors, no dupes");}

        Scanner in = new Scanner(System.in);
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }

   tempString = scrobbleObjects[i].getArtist().replaceAll("/", "_").replaceAll(":", "_").replaceAll(">", "_") + " xöx " +
                                    scrobbleObjects[i].getAlbum().replaceAll("/", "_").replaceAll(":", "_").replaceAll(">", "_") + " xöx " +
                                    scrobbleObjects[i].getTitle().replaceAll("/", "_").replaceAll(":", "_").replaceAll(">", "_") + ".mp3";
                            try
                            {
                                copyFile(srcFile, tempFile, false);
                            }
                            catch(Exception P)
                            {

                            System.out.println(" error printing " + tempString);
                            }


    private static void previewBeforePrint(Fil[] tempFidleArray)
    {
        reportNclean(printArray, skipped, new File[0]);

    }
    static int evalueateDuplicates(Object[] objectArray, Object[] ressultarray)
    {
        int i =0;
        try{
        for (int j=0;j<objectArray.length;j++)
        {
            for (int k=j+1;k<objectArray.length;k++)
            {
                if ((k != j) && (objectArray[k].equals(objectArray[j])) && (objectArray[j] != null))
                {
                    System.arraycopy(ressultarray, i++, objectArray, k, ressultarray.length); ;
                }
            }
        }
        }catch(Exception e){System.out.println(e + " @evalueateDuplicates");}
        return i;
    }

    static void evalueateExictingFiles(Object[] objectArray, Object[] ressultarray, File[] tempFileArray)
    {
        for(int i=0;(i<ressultarray.length && i<objectArray.length);i++)
        ressultarray[i] = objectArray[i];
try{
        for (int j=0;j<tempFileArray.length;j++)
        {
            for (int k=0;k<objectArray.length;k++)
            {
                if ( (tempFileArray[j].equals(objectArray[k])) && (tempFileArray[j] != null))
                {
                    j++;
                    ressultarray[k] = null;
                    k = 0;
                }
            }
        }
}catch(Exception e){System.out.print(e);}
        for(int i=0;i<ressultarray.length;i++)
        {
            if (ressultarray[i] != null)
            {
                System.out.println(ressultarray[i]);
            }
        }

    }


        String part1 ="";
        String part2 ="";
        String part3 ="";
        String part4 ="";
        String part5 ="";

        try
        {
            part2 = String.valueOf(artist.charAt(1%artist.length())) + title.charAt(1%title.length()) + album.charAt(1 % album.length());
        }
        catch (Exception e)
        {
            System.out.println(e + " @(part 2 charat(1):" + scrobbleObject.present());
        }
        try {
            part3 = String.valueOf(artist.charAt(2%artist.length()) + title.charAt(2%title.length()) + album.charAt(2%album.length()));
        }
        catch (Exception e)
        {
            System.out.println(e + " @(part 3 charat(2):" + scrobbleObject.present());
        }
        try
        {
            part4 = String.valueOf(artist.charAt(artist.length()/2)) + title.charAt(title.length()/2) + album.charAt(album.length()/2);
        }
        catch (Exception e)
        {
            System.out.println(e + " @(part 4 charat(1):" + scrobbleObject.present());
        }
        try
        {
            part5 = String.valueOf(artist.charAt(artist.length()-1)) + title.charAt(title.length()-1) + album.charAt(album.length()-1);
        }
        catch (Exception e)
        {
            System.out.println(e + " @(part 5 charat(1):" + scrobbleObject.present());
        }

        part1 = String.valueOf(album.charAt(0)) + artist.charAt(0) + title.charAt(0)
                + artist.length() + title.length() + album.length();

        return (part1 + part2 + part3 + part4 + part5

    function takes strings with textLine code, collect each in an array, once the array is full, the array is printed to a file
  the file is called by a number wich is the restult of deviding printNr with PRINTSPERFILE,
    *
 * Created by user on 2014-05-08.
 * how to make the ipodscrobbler consider old scrobbles
 * program reads folder 1 with new scrobbles
 * into array scrobble objects
 * program reads folder 3 with old scrobbles
 * into array oldScrobbleObjects
 *
 *operator overloading would probably make it easier to compare
 * maybe would be best to sort each array
 *
 *
 * as the program finishes, it should print a copie of all scrobbled obejcts
 * to old scrobbles folder
 * the structure of saved file is not necesarly readable by humans
 *
 a general function should be made to read specified folder
 functionname(scrobbleObject[] x,"folderpath")

        }

        catch (Exception e){
            System.out.println(e + " @m3uPrint");
        }

writerLB.close();
        }
        catch (FileNotFoundException ex)
        {

        }
    if (leftValue.getScrobbleCount() != rightPlayCount) {
                            final int leftPlayCount = leftValue.getScrobbleCount();

                            if (rightMap.put(leftName, leftValue) != null) {
                                final int summedPlayCount = rightPlayCount - leftPlayCount;

                                if (summedPlayCount == 0) {
                                    rightMap.remove(leftName);
                                } else if (summedPlayCount < 0) {
                                    System.out.println("negative playcount encountered" + leftValue.present() + " with:" + summedPlayCount + "\n");
                                    negativePlayCount += summedPlayCount;
                                } else {
                                    rightMap.getSubmissionDates(leftName).increasePlayCountWith(summedPlayCount);
                                }
                            } else {
                                System.out.println("unrecognized track encountered: " + leftValue.present());
                                rightMap.remove(leftName);
                            }
                        } else {
                            rightMap.remove(leftName);
                        }
                                System.out.println(json.toString());
        System.out.println(json.getSubmissionDates("id"));
        System.out.println(((Map)((Map)((ArrayList)((Map)map.getSubmissionDates("recenttracks")).getSubmissionDates("track")).getSubmissionDates(0)).getSubmissionDates("artist")).getSubmissionDates("#text"));
System.out.println(map.getSubmissionDates("recenttracks").getSubmissionDates("track").getSubmissionDates(0).getSubmissionDates("artist").getSubmissionDates("#text"));
Iterator<String> keySetIterator = map.keySet().iterator();

while(keySetIterator.hasNext()){
String key = keySetIterator.next();
System.out.println("key: " + key + " value: " + map.getSubmissionDates(key));
}


File destFolder = new File(PROJECTFOLDER + "IpodScrobblerPlaylist");

archive.report(extractedFromItunes, destFolder, nrCleansedObjects);

m3uPrint(extractedFromItunes, fetchScrobbles());
makeMp3(extractedFromItunes, destFolder, new File(PROJECTFOLDER + MP3LENGHT));
        HashMap<String, IpodScrobbler.ScrobbleObject> xloadedCleaned = new HashMap<String, IpodScrobbler.ScrobbleObject>();
        compareScrobbles(loadedFromDb, xloadedCleaned, new File(PROJECTFOLDER + "IpodScrobbleNotUtf8"));

*/