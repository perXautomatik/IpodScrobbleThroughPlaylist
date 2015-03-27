package ModuleScrobbler;

import de.umass.lastfm.Track;

import java.io.*;
import java.util.*;

import static commonMethods.CommonMethods.*;
import static de.umass.lastfm.User.getLovedTracks;

/**
 * Created by Christoffer on 2015-01-13.
 */
public class GetRecommendedTracks extends ModularScrobbler{
    public static int Main() {
        System.out.print("for wich user?");
        String userName = new Scanner(System.in).next();

        //subtrackt already known songs?
        //dowload loved tracks

        List<ScrobbleObject> userLoved = new ArrayList<ScrobbleObject>();

        UserLibrary threadA = new UserLibrary(userName,new SessionSaver(),new ScrobbleObjectMap());
        DownloadSimilar threadB = new DownloadSimilar(userLoved);
        Screenupdate threadC = new Screenupdate(threadA,threadB);

        Thread a = new Thread(threadA);
        Thread b = new Thread(threadB);
        Thread c = new Thread(threadC);

        threadA.run();

        a.start();
        b.start();
        c.start();

        while(a.isAlive() && b.isAlive())
        {
            c.start();
        }
        //in thread 2 dowload similar for each loved

        return 0;
    }

    //        update screen for each iteration
//        where similarresults - userlibrary;

        /*
simmilar result will be a increasing list whom for each iteration might or might not repeat values
                the library substraction have to substract for each insertion
                question is weather this will be awfully runtime heavy.

*/

       /* File file = new File("temp");
        FileOutputStream f = new FileOutputStream(file);
        ObjectOutputStream s = new ObjectOutputStream(f);
        final Object o = hm.keySet().toArray()[0];
        s.writeObject(new ScrobbleObject((Track) o) );
        s.close();


        FileInputStream f1 = new FileInputStream(file);
        ObjectInputStream s1 = new ObjectInputStream(f1);
        ScrobbleObject oa = (ScrobbleObject) s1.readObject();
        s1.close();

        Assert.assertEquals(fileObj.hashCode(), fileObj2.hashCode());
        Assert.assertEquals(fileObj.toString(), fileObj2.toString());
        Assert.assertTrue(fileObj.equals(fileObj2));
        return 0;*/
    
    private static class DownloadSimilar implements Runnable {
        private final List<ScrobbleObject> userLoved;
        HashMap<Track,List<Track>> hm = new HashMap<Track, List<Track>>();

        private DownloadSimilar(List<ScrobbleObject> userLoved) {
            this.userLoved = userLoved;
        }

        public HashMap<Track,List<Track>> getSimilar()
        {
            return hm;
        }

        public void run()
        {

            //should each track store it's own similar artists'

            //then saving the ressult will require xml or sql structure

            //    alternative would be save a new small file for each track
            //    scrobbloObject could overide getSimilar, calling trax method and dealing with the internal storeing

            for(ScrobbleObject t: userLoved)
            {
                    hm.put(t, new ArrayList<Track>(t.getSimilar()));
            }

        }

    }

    /**
     * Created by Christoffer on 2015-03-17.
     */
    public static class Screenupdate implements Runnable{

        private static UserLibrary userLib;
        private static DownloadSimilar similarMap;

        public Screenupdate(UserLibrary threadA, DownloadSimilar threadB)
        {
            userLib = threadA;
            similarMap = threadB;
        }

        public void run()
        {
            ScrobbleObjectMap library = userLib.getLibrary();
            ScrobbleObjectMap similarities = new ScrobbleObjectMap();
            final HashMap<Track,List<Track>> similar = similarMap.getSimilar();


            //should it reloop of each iterate of the thread or should it remember its current state
            // and oline act on changes of the similarMap
            //     should this thread run alongside the other or should
            //       be recreated on interval, clear screen, print current similarities


            for(Track track : similar.keySet())
            {
                for(Track track1 : similar.get(track))
                    similarities.put(track1);
            }

            similarities.subtract(library);


            //similarities.

            try {
                clearConsole();
            } catch (IOException e) {
                e.printStackTrace();
            }
            similarities.print(0,"");

 /*           similrities.print()

            wait(10sec)   OR on input
*/        }
    }
}
