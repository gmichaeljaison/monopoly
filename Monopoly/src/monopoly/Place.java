package monopoly;


public class Place {

// type of place
//        0 - go
//        1 - normal place (22)
//        2 - ports (6)
//        3 - tax places (2)
//        4 - chance (3)
//        5 - community chest (3)
//        6 - rest house
//        7 - club
//        8 - jail

    private String name_;
    private int type_;
    private Object placePointer_;           // points to the corresponding place object

    protected static int nChances = 10;         // no. of chances in the file
    protected static int nCommunityChest = 10;

    public Place(String name,int type) {
        name_=name;
        type_=type;
        placePointer_=null;
    }
    public Place(String name,int type,int grpIndex,int price) {
        name_=name;
        type_=type;
        if(type_==1)
        {
            placePointer_=new NormalPlace(grpIndex, price);
        } else {
            placePointer_=new Port(grpIndex, price);
        }
    }
    public boolean isGrouped() {
        if (Game.getPlayer(getOwnerIndex()).getNumOfGroupedPlaces(this) == Game.getGroup(getGroupIndex()).totalPlaces())
            return true;
        return false;
    }
    public boolean isFree() {
        if(type_==1)
            return (((NormalPlace)placePointer_).ownerIndex==-1);
        else if(type_==2)
            return (((Port)placePointer_).ownerIndex==-1);
        return false;
    }
    public boolean isMortgaged() {
        if(type_==1)
            return (((NormalPlace)placePointer_).mortgaged);
        else if(type_==2)
            return (((Port)placePointer_).mortgaged);
        return false;
    }
    public int getOwnerIndex() {
        if (type_==1) {
            return (((NormalPlace)placePointer_).ownerIndex);
        }
        else if (type_==2) {
            return (((Port)placePointer_).ownerIndex);
        }
        return -1;      // error
    }
    public int getGroupIndex() {
        if (type_==1) {
            return (((NormalPlace)placePointer_).groupIndex);
        }
        else if (type_==2) {
            return (((Port)placePointer_).groupIndex);
        }
        return -1;      // error
    }
    public int getPrice() {
        if (type_==1) {
            return (((NormalPlace)placePointer_).price);
        }
        else if (type_==2) {
            return (((Port)placePointer_).price);
        }
        return -1;      // error
    }
    public int getRent(int dice) {
        if (type_==1) {
            return (int) ((((NormalPlace)placePointer_).price * Game.rentRate * (((NormalPlace)placePointer_).star + 1) / 100) / (((NormalPlace)placePointer_).mortgaged ? 2 : 1) * (isGrouped() && (((NormalPlace)placePointer_).star == 0) ? 2 : 1));
        }
        else if (type_==2) {
            return (Port.rent * dice * Game.getPlayer(((Port)placePointer_).ownerIndex).getNumOfGroupedPlaces(this));
        }
        return -1;
    }
    public int getStar() {
        if(type_==1)
            return ((NormalPlace)placePointer_).star;
        return -1;
    } 
    public int getType() {
        return (type_);
    }
    public String getName() {
        return name_;
    }
    public boolean setOwnerIndex(int ownerIndex) {
        if (type_==1) {
            if(((NormalPlace)placePointer_).ownerIndex!=-1)
                Game.getPlayer(((NormalPlace)placePointer_).ownerIndex).removePlace(this);
            ((NormalPlace)placePointer_).ownerIndex = ownerIndex;
            if(((NormalPlace)placePointer_).ownerIndex!=-1)
                Game.getPlayer(ownerIndex).addPlace(this);
            return true;
        }
        else if (type_==2) {
            if(((Port)placePointer_).ownerIndex!=-1)
                Game.getPlayer(((Port)placePointer_).ownerIndex).removePlace(this);
            ((Port)placePointer_).ownerIndex = ownerIndex;
            if(((Port)placePointer_).ownerIndex!=-1)
                Game.getPlayer(ownerIndex).addPlace(this);
            return true;
        }
        return false;
    }
    public boolean upgrade() {
        ((NormalPlace)placePointer_).star++;
        return true;
    }
    public boolean degrade() {
        ((NormalPlace)placePointer_).star--;
        return true;
    }
    public boolean mortgage() {
        if(type_==1) {
            ((NormalPlace)placePointer_).mortgaged = !((NormalPlace)placePointer_).mortgaged;
            return true;    // success
        }
        else if(type_==2) {
            ((Port)placePointer_).mortgaged = !((Port)placePointer_).mortgaged;
            return true;
        }
        return false;       // failure
    }
}
class Port {
    protected int price;
    protected int groupIndex;
    protected int ownerIndex = -1;
    protected boolean mortgaged = false;
    protected static int rent = 10;

    public Port(int grpIndex, int pric) {
        groupIndex=grpIndex;
        price=pric;
    }
}

class NormalPlace {
    protected int price;
    protected int groupIndex;
    protected int ownerIndex = -1;
    protected int star = 0;
    protected boolean mortgaged = false;

    public NormalPlace(int grpIndex, int pric) {
        groupIndex=grpIndex;
        price=pric;
    }
}
