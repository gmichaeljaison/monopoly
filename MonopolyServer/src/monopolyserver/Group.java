package monopolyserver;

import java.util.ArrayList;

public class Group {
    private int groupIndex_;
    private ArrayList<Place> placePointers_ = new ArrayList<Place>();

    public Group(int grpIndex) {
        groupIndex_=grpIndex;
    }
    public int minStar() {
        int star=Server.maxStarValue;
        for(Place place : placePointers_)
        {
            star = star>place.getStar()?place.getStar():star;
        }
        return star;
    }
    public int maxStar() {
        int star=-1;
        for(Place place : placePointers_)
        {
            star = star<place.getStar()?place.getStar():star;
        }
        return star;
    }
    public boolean isUpgradable(Place place) {
        if (place.getType()==1) {
            if(place.isGrouped()&&(place.getStar()==minStar()))
                return true;
        }
        return false;
    }
    public boolean isDegradable(Place place) {
        if (place.getType()==1) {
            if(place.isGrouped()&&(place.getStar()==maxStar()))
                return true;
        }
        return false;
    }
    public void addPlace(Place place) {
        placePointers_.add(place);
    }
    public void removePlace(Place place) {
        placePointers_.remove(place);
    }
    public int totalPlaces() {
        return placePointers_.size();
    }
    public int getIndex() {
        return groupIndex_;
    }
}