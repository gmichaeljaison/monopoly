package monopoly;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Game {

    private static int port_ = 10000;    
    private static ArrayList<Player> players_ = new ArrayList<Player>();
    private static ArrayList<Place> places_ = new ArrayList<Place>();
    private static ArrayList<Group> groups_ = new ArrayList<Group>();

    protected static GameThread thread = new GameThread(port_);
    protected static GameUI gameUI;
    protected static final int nPlaces = 40;
    protected static final int nHorBoxes = 11;
    protected static final int nVerBoxes = 11;
    protected static int nPlayers = 2;
    protected static int maxStarValue = 5;
    protected static int salary = 1000;
    protected static int clubAmount = 100;
    protected static int restHouseAmount = 100;
    protected static int bailAmount = 100;
    protected static double rentRate = 12.5;
    protected static double taxRate = 10;
    protected static int taxAmount = 500;
    protected static int rateOfReturn = 55;
    protected static int initCash = 2000;

    protected static int currentPlayer;

    public Game() {
        File fil=new File("place.txt");
        try {
            FileInputStream fin=new FileInputStream(fil);
            DataInputStream din=new DataInputStream(fin);
            while(din.available()>0)
            {
                String[] plc=din.readLine().split("[|]");
                if(plc[1].matches("1")||plc[1].matches("2"))
                {
                    places_.add(new Place(plc[0], Integer.parseInt(plc[1]), Integer.parseInt(plc[2]), Integer.parseInt(plc[3])));
                } else
                {
                    places_.add(new Place(plc[0], Integer.parseInt(plc[1])));
                }
                if (plc[1].matches("1")||plc[1].matches("2")) {
                    if (isGroupAvailable(Integer.parseInt(plc[2]))) {
                        groups_.get(Integer.parseInt(plc[2])).addPlace(places_.get(places_.size()-1));
                    } else {
                        groups_.add(Integer.parseInt(plc[2]), new Group(Integer.parseInt(plc[2])));
                        groups_.get(Integer.parseInt(plc[2])).addPlace(places_.get(places_.size()-1));
                    }
                }
            }
            fin.close();
        } catch (IOException ex) {
            Logger.getLogger(Game.class.getName()).log(Level.SEVERE, null, ex);
        }
        //thread_.start();
    }
    public static void addPlayer(Player player) {
        players_.add(player);
    }
    public static Player getPlayer(int index) {
        return (players_.get(index));
    }
    public static ArrayList<Player> getPlayers() {
        return (players_);
    }
    public static Place getPlace(int index) {
        return (places_.get(index));
    }
    public static ArrayList<Place> getPlaces() {
        return (places_);
    }
    public static Group getGroup(int index) {
        return (groups_.get(index));
    }
    public static ArrayList<Group> getGroups() {
        return (groups_);
    }
    public boolean isGroupAvailable (int grpIndex) {
        for (Group grp : groups_) {
            if (grp.getIndex()==grpIndex)
                    return true;
        }
        return false;
    }
}
