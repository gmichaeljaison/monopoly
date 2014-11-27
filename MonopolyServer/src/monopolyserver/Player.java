package monopolyserver;

import java.awt.Color;
import java.util.ArrayList;

public class Player {

    private String name_;
    private Color color_;
    private int placeIndex_;
    private int cash_ = Server.initCash;
    private ArrayList<Place> placePointers_ = new ArrayList<Place>();

    public Player (String name,Color color) {
        name_ = new String(name);
        color_ = new Color(color.getRGB());
    }
    public int changePlace (int value) {    // returns crossed GO or NOT
        int temp = placeIndex_;
        placeIndex_ += value;
        placeIndex_ %= Server.nPlaces;
        if (temp > placeIndex_) // crossed GO
            return (Server.nPlaces - 1 - temp);
        return -1;              // doesn't crossed GO
     //   return (Server.getPlace(placeIndex_).getType());
     //   return (Server.getPlace(placeIndex_).isFree());
    }
    public boolean freePlaces() {
        int size = placePointers_.size();
        for (int i=0;i<size;i++) {
            Place place = placePointers_.get(0);
            place.setOwnerIndex(-1);
            if (place.isMortgaged())
                place.mortgage();
            System.out.println(place.getName());
        }
        System.out.println("removed");
        for (int i=Server.getPlayers().indexOf(this)+1;i<Server.nPlayers;i++) {
            for (Place place : Server.getPlayer(i).placePointers_) {
                place.setOwnerIndex(place.getOwnerIndex()-1);
            }
        }
        return true;
    }
    public int getNumOfGroupedPlaces(Place place) {
        int gIndex = place.getGroupIndex(),gcnt=0;
        for(Place place1 : placePointers_)
        {
            if(place1.getGroupIndex()==gIndex)
                gcnt++;
        }
        return gcnt;
    }
    public String getName() {
        return name_;
    }
    public Color getColor() {
        return color_;
    }
//    public boolean isGrouped(Place place) {
//        if (getNumOfGroupedPlaces(place) == Server.getGroup(place.getGroupIndex()).totalPlaces())
//            return true;
//        return false;
//    }
    public int getPlaceIndex() {
        return (placeIndex_);
    }
    public int getCash() {
        return cash_;
    }
    public void addPlace (Place place) {
        System.out.println("Before adding"+placePointers_.size());
        placePointers_.add(place);
        System.out.println("After adding"+placePointers_.size());
    }
    public void removePlace (Place place) {
        placePointers_.remove(place);
    }
    public boolean changeCash (int value) {
        cash_ += value;
        if(cash_<0) {
            Server.getThread(Server.getPlayers().indexOf(this)).sendMessage("x");
            return false;
        }
        return true;
    }
}
