package monopolyserver;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Server {
    /**
     * Server handles the current game session
     * This module organizes the players info and handles the threads corresponding to each player(client)
    */

    private static int port_ = 10000;
    private static ArrayList<ServerThread> threads_ = new ArrayList<ServerThread>();
    private static ArrayList<Player> players_ = new ArrayList<Player>();
    private static ArrayList<Place> places_ = new ArrayList<Place>();
    private static ArrayList<Group> groups_ = new ArrayList<Group>();
    protected static int currentPlayer_ = 0;

    protected static int nPlaces = 40;
    protected static int nPlayers = 3;
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
    
    protected static boolean ready = false;
    /**
     * Initializes the port number and all place informations.
     * listens for the client connection untill number of clients connected will meet number of players.
     * @param port - port number which we have to listen for client connection.
     * @param numOfPlayers - Number of players connecting to this server.
     */
    public Server(int port,int numOfPlayers) {
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
               // if(groups_.get(Integer.parseInt(plc[2]))==null) {
                if (plc[1].matches("1")||plc[1].matches("2")) {
                    if (isGroupAvailable(Integer.parseInt(plc[2]))) {
                        groups_.get(Integer.parseInt(plc[2])).addPlace(places_.get(places_.size()-1));
                    } else {
                        groups_.add(Integer.parseInt(plc[2]),new Group(Integer.parseInt(plc[2])));
                        groups_.get(Integer.parseInt(plc[2])).addPlace(places_.get(places_.size()-1));
                    }
                }
            }
            fin.close();
            port_ = port;
            Server.nPlayers = numOfPlayers;
            ServerSocket serverSock = new ServerSocket(port_);
            while (threads_.size() < Server.nPlayers) {
                ServerThread st = new ServerThread(serverSock.accept(),threads_.size());
                threads_.add(st);
                st.start();
            }
        } catch (IOException ex) {
            Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    /**
     * switch the turn to next player
     */
    public static void nextPlayer() {
        currentPlayer_++;
        currentPlayer_ %= nPlayers;
        sendToAll("f|"+Integer.toString(currentPlayer_));
    }

    /**
     * switch the turn when a player quits the game.
     */
    public static void nextPlayerq() {
        currentPlayer_ %= nPlayers;
        sendToAll("f|"+Integer.toString(currentPlayer_));
    }

    /**
     * Add Player details to the array list maintained in the server.
     * @param player - player details
     */
    public static void addPlayer(Player player) {
        players_.add(player);
    }

    /**
     * Get the player from the array list.
     * @param index - Index of the player in the arraylist
     * @return - Player details.
     */
    public static Player getPlayer(int index) {
        return (players_.get(index));
    }

    /**
     * Get the place from the array list.
     * @param index - Index of the place in the arraylist
     * @return - Place details.
     */
    public static Place getPlace(int index) {
        return (places_.get(index));
    }

    /**
     * Get the group from the array list.
     * @param index - Index of the group in the arraylist
     * @return - Group details.
     */
    public static Group getGroup(int index) {
        return (groups_.get(index));
    }

    /**
     * Get the thread from the array list.
     * @param index - Index of the thread in the arraylist
     * @return - Thread details.
     */
    public static ServerThread getThread(int index) {
        return (threads_.get(index));
    }

    /**
     * Get the array of players
     * @return -  Array List of players
     */
    public static ArrayList<Player> getPlayers() {
        return (players_);
    }

    /**
     * Get the array of places
     * @return - Array list of places
     */
    public static ArrayList<Place> getPlaces() {
        return (places_);
    }

    /**
     * Get the array of groups
     */
    public static ArrayList<Group> getGroups() {
        return (groups_);
    }
    public static ArrayList<ServerThread> getThreads() {
        return (threads_);
    }

    /**
     * Sends the messge to all players connected to this server
     * @param message - message to send to al players
     */
    public static void sendToAll(String message) {
        for (ServerThread st : threads_) {
            st.sendMessage(message);
        }
    }

    /**
     * Sends the message to all players except one player
     * @param message - message to send
     * @param thread - thread which it has to skip
     */
    public static void sendToAllExcept(String message, ServerThread thread) {
        for (ServerThread st : threads_) {
            if (st.equals(thread))
                continue;
            st.sendMessage(message);
        }
    }
    public boolean isGroupAvailable (int grpIndex) {
        for (Group grp : groups_) {
            if (grp.getIndex()==grpIndex)
                    return true;
        }
        return false;
    }
}
;