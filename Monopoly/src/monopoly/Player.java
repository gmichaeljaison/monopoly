package monopoly;

import java.awt.Color;
import java.util.ArrayList;

public class Player {

    private String name_;
    private Color color_;
    private int placeIndex_ = 0;
    private int cash_;
    private ArrayList<Place> placePointers_ = new ArrayList<Place>();

    public Player (String name,Color color) {
        name_ = new String(name);
        color_ = new Color(color.getRGB());
        cash_ = Game.initCash;
    }

    public boolean setPlace (int plcIndex) {    // returns crossed GO or NOT
        placeIndex_=plcIndex;
        return true;
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
        for (int i=Game.getPlayers().indexOf(this)+1;i<Game.nPlayers;i++) {
            for (Place place : Game.getPlayer(i).placePointers_) {
                System.out.println(place.getName());
                place.setOwnerIndex(place.getOwnerIndex()-1);
            }
        }
        return true;
    }
    public String getName() {
        return name_;
    }
    public Color getColor() {
        return color_;
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
//    public boolean isGrouped(Place place) {
//        if (getNumOfGroupedPlaces(place) == Game.getGroup(place.getGroupIndex()).totalPlaces())
//            return true;
//        return false;
//    }
    public int getPlaceIndex() {
        return (placeIndex_);
    }
    public int getCash() {
        return cash_;
    }
    public int nPlacesOwned() {
        return placePointers_.size();
    }
    public void addPlace (Place place) {
        placePointers_.add(place);
    }
    public void removePlace (Place place) {
        placePointers_.remove(place);
    }
    public boolean setCash (int value) {
        cash_ = value;
        return true;
    }
}
