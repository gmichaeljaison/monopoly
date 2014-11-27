package monopolyserver;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerThread extends Thread{
    private Socket socket_;
    private DataInputStream in_;
    private DataOutputStream out_;
    private int index_;                         // index of player using this thread
    //boolean ready_=false;

    public ServerThread(Socket socket,int index) {
        try {
            socket_ = socket;
            index_ = index;
            in_ = new DataInputStream(socket_.getInputStream());
            out_ = new DataOutputStream(socket_.getOutputStream());
            
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    @Override
    @SuppressWarnings("empty-statement")
    public void run() {
        System.out.println("Thread started for "+String.valueOf(index_));
        sendMessage("i|"+String.valueOf(index_));
        String msg = new String();
        String[] msgSplit;
        msg = "";
        //while(!Server.ready) {
//            for(int i=0;i<index_;i++) {
//                Color col = Server.getPlayer(i).getColor();
//                //String color = String.valueOf(col.getRed()) + "," + String.valueOf(col.getGreen()) + "," + String.valueOf(col.getBlue());
//                msg="p|" + String.valueOf(i) + "|" + Server.getPlayer(i).getName() + "|" + String.valueOf(col.getRGB());
//                sendMessage(msg);
//            }
            
            msg = receiveMessage();
            switch (msg.charAt(0)) {
                case 'p':
                    msgSplit = msg.split("[|]");
                    Server.addPlayer(new Player(msgSplit[1],new Color(Integer.parseInt(msgSplit[2]))));
                    System.out.println(String.valueOf(Server.ready)+"dddddd"+String.valueOf(Server.nPlayers)+"fffff"+String.valueOf(index_));
                    if (index_==Server.nPlayers-1)
                        Server.ready = true;
                    while(!Server.ready)
                        for(int i=0;i<1000;i++);
                    break;
            }
        //}
        System.out.println("bfore sendin all");
        for (int i=0;i<Server.nPlayers;i++) {
            Color col = Server.getPlayer(i).getColor();
            //String color = String.valueOf(col.getRed()) + "," + String.valueOf(col.getGreen()) + "," + String.valueOf(col.getBlue());
            msg="p|" + String.valueOf(i) + "|" + Server.getPlayer(i).getName() + "|" + String.valueOf(col.getRGB());
            //Server.sendToAll(msg);
            sendMessage(msg);
        }
        sendMessage("f|"+Integer.toString(0));

        Random rand = new Random();
        //rand.setSeed(index_ * socket_.getPort() * Server.nPlayers * Server.maxStarValue * 12345);
        System.out.println("Game loop");
//        for(int i=0;i<10000;i++)
//            for(int j=0;j<10000;j++);
        while (true) {                          // game msg loop
            msg = receiveMessage();
            switch (msg.charAt(0)) {
                case 'r':                               // roll dice
                    String tmp = rollDice(rand);
                    System.out.println(tmp);
                    Server.sendToAllExcept(tmp, this);
                    break;
                case 'm':                               // mortgage/unmortgage a place
                    if (mortgage(msg)) {
                        msgSplit = msg.split("[|]");
                        //msg = "m|" + String.valueOf(index_) + "|" + msgSplit[1];
                        Server.sendToAll(msg);
                    }
                    break;
                case 'u':                               // upgrade or degrade a place
                    if(Server.getPlace(Integer.parseInt((msg.split("[|]"))[1])).upgrade())
                    {
                        if (Server.getPlayer(index_).changeCash(-Server.getPlace(Integer.parseInt((msg.split("[|]"))[1])).getPrice()/2)==false)
                            sendMessage("n");
                        else
                            sendMessage("y");
                        //String[] msgSplit = msg.split("[|]");
                        //msg = "u|" + msgSplit[1];
                        Server.sendToAll(msg);
                    }
                    break;
                case 'v':
                    if(Server.getPlace(Integer.parseInt((msg.split("[|]"))[1])).degrade())
                    {
                        Server.getPlayer(index_).changeCash(Server.getPlace(Integer.parseInt((msg.split("[|]"))[1])).getPrice()*(100-Server.rateOfReturn)/100);
                        //String[] msgSplit = msg.split("[|]");
                        //msg = "v|" + String.valueOf(index_) + "|" + msgSplit[1];
                        Server.sendToAll(msg);
                    }
                    break;
                case 't':                               // finish turn
                    if (trade(msg)) {
                        msgSplit = msg.split("[|]");
                        msg = "t|" + String.valueOf(index_) + "|" + msgSplit[1] + "|" + msgSplit[2] + "|" + msgSplit[3] + "|" + msgSplit[4];
                        Server.sendToAll(msg);
                    }
                    break;
                case 'f':
                    Server.nextPlayer();
                    break;
                case 'x':                               // exit
                    quitPlayer();
                    if (Server.currentPlayer_==index_)
                        Server.nextPlayerq();
                    if (Server.nPlayers==1)
                        Server.sendToAll("w");
                    break;
                default:                                // wrong msg
                    break;
            }
        }
    }

    public String rollDice(Random rand) {
        String dice1 = String.valueOf(rand.nextInt(6) + 1);
        String dice2 = String.valueOf(rand.nextInt(6) + 1);        
        String playerIndex = Integer.toString(index_);
        String msg = new String();
        String cash = new String();

        int diff = Server.getPlayer(index_).changePlace(Integer.parseInt(dice1)+Integer.parseInt(dice2));
        String placeIndex = Integer.toString(Server.getPlayer(index_).getPlaceIndex());
        
        if (diff != -1) {    // crossed GO
            Server.getPlayer(index_).changeCash(Server.salary);
            Server.sendToAll("g|" + playerIndex + "|" + String.valueOf(Server.getPlayer(index_).getCash()) + "|" + dice1 +"|" + dice2);
        }
        int type = Server.getPlace(Server.getPlayer(index_).getPlaceIndex()).getType();
        if (type==8) {                              // jail
            
        }
        else if (type==6) {                          // rest house
            for (Player player : Server.getPlayers()) {
                player.changeCash(-Server.restHouseAmount);
            }
            Server.getPlayer(index_).changeCash(Server.nPlayers*Server.restHouseAmount);
            for (int i=0;i<Server.getPlayers().size();i++) {
                Server.sendToAll("$|"+i+"|"+Server.getPlayer(i).getCash());
            }
        }
        else if (type==7) {                     // club
            for (Player player : Server.getPlayers()) {
                player.changeCash(Server.clubAmount);
            }
            Server.getPlayer(index_).changeCash(-Server.nPlayers*Server.clubAmount);
            for (int i=0;i<Server.getPlayers().size();i++) {
                Server.sendToAll("$|"+i+"|"+Server.getPlayer(i).getCash());
            }
        }
        else if(type==3) {                           // tax places
            Server.getPlayer(index_).changeCash((int) (-Server.getPlayer(index_).getCash() * Server.taxRate / 100));
            sendMessage("d|"+ dice1 + "|" + dice2 + "|" + placeIndex);
            Server.sendToAll("$|"+index_+"|"+Server.getPlayer(index_).getCash());
        }
        else if (type==4) {                      // chance
            String chanceIndex = String.valueOf(rand.nextInt(Place.nChances));
            sendMessage("d|" + dice1 + "|" + dice2 + "|" + placeIndex + "|" + chanceIndex);
            //msg = receiveMessage();
            //if (msg.equals("d"))
                return("r|" + playerIndex + "|" + dice1 + "|" + dice2 + "|" + placeIndex + "|" + chanceIndex);
        }
        else if (type==5) {                 // community chest
            String communityIndex = String.valueOf(rand.nextInt(Place.nCommunityChest));
            sendMessage("d" + "|" + dice1 + "|" + dice2 + "|" + placeIndex + "|" + communityIndex);
//            msg = receiveMessage();
//            if (msg.equals("d"))     // confirmation
                return("r|"+ playerIndex + "|" + dice1 + "|" + dice2 + "|" + placeIndex + "|" + communityIndex);
        }
        else if ((type==1) || (type==2)) {
            if (Server.getPlace(Server.getPlayer(index_).getPlaceIndex()).isFree()) {       // ask to buy
                int playerCash = Server.getPlayer(index_).getCash();
                int placePrice = Server.getPlace(Server.getPlayer(index_).getPlaceIndex()).getPrice();
                System.out.println(playerCash);
                System.out.println(placePrice);
                if (placePrice <= playerCash) {
                    System.out.println("can buy");
                    sendMessage("d"  + "|"+ dice1 + "|" + dice2 + "|" + placeIndex + "|" + "b");
                    msg = receiveMessage();
                    if (msg.equals("y")) {
                        Server.getPlayer(index_).changeCash(-Server.getPlace(Server.getPlayer(index_).getPlaceIndex()).getPrice());
                        Server.sendToAll("$|"+String.valueOf(index_)+"|"+Integer.toString(Server.getPlayer(index_).getCash()));
                        Server.getPlace(Integer.parseInt(placeIndex)).setOwnerIndex(index_);
                        return("r" + "|" + playerIndex + "|" + dice1  + "|"+ dice2 + "|" + placeIndex + "|b");
                    }
                }
                else {
                    System.out.println("Cannot buy");
                    String tmp = "d" + "|"+ dice1 + "|" + dice2 + "|" + placeIndex;
                    System.out.println(tmp);
                    sendMessage(tmp);
                }
                return("r" + "|" + playerIndex + "|" + dice1  + "|"+ dice2 + "|" + placeIndex);
            }
            else {      // not free .. have to pay rent
                String toPlayerIndex = String.valueOf(Server.getPlace(Server.getPlayer(index_).getPlaceIndex()).getOwnerIndex());
                if (Integer.parseInt(toPlayerIndex) == index_) {              // same person owned the place
                    sendMessage("d" + "|" + dice1 + "|" + dice2 + "|" + placeIndex);
                    return ("r" + "|" + playerIndex + "|" + dice1 + "|" + dice2 + "|" + placeIndex);
                }                
                if (type==1)
                    cash = String.valueOf(Server.getPlace(Server.getPlayer(index_).getPlaceIndex()).getRent(Integer.parseInt(dice1)+Integer.parseInt(dice2)));
                else if (type==2)
                    cash = String.valueOf((Integer.parseInt(dice1)+Integer.parseInt(dice2)) * Port.rent);
                Server.getPlayer(index_).changeCash(-Integer.parseInt(cash));
                Server.getPlayer(Integer.parseInt(toPlayerIndex)).changeCash(Integer.parseInt(cash));
                sendMessage("d" + "|" + dice1 + "|" + dice2 + "|" + placeIndex + "|" + "r"  + "|"+ toPlayerIndex + "|" + cash);
                Server.sendToAll("$|"+playerIndex+"|"+String.valueOf(Server.getPlayer(index_).getCash()));
                Server.sendToAll("$|"+toPlayerIndex+"|"+String.valueOf(Server.getPlayer(Integer.parseInt(toPlayerIndex)).getCash()));
                return("r" + "|" + playerIndex + "|" + dice1  + "|"+ dice2 + "|" + placeIndex  + "|"+ "r" + "|" + toPlayerIndex + "|" + cash);
            }
        }
        sendMessage("d|" + dice1 + "|" + dice2 + "|" + placeIndex);
        return("r" + "|" + playerIndex + "|" + dice1  + "|"+ dice2 + "|" + placeIndex);             // tax, club, restHouse, GO
    }

    public boolean trade (String msg) {
        String buffer = new String();
        String[] msgSplit = msg.split("[|]");
        msg = "t|" + String.valueOf(index_) + "|" + msgSplit[2] + "|" + msgSplit[3] + "|" + msgSplit[4];
        Server.getThread(Integer.parseInt(msgSplit[1])).sendMessage(msg);
        msg = receiveMessage();
        if (buffer.equals("y")) {     // confirmation
            Server.getPlayer(index_).changeCash(-Integer.parseInt(msgSplit[4]));
            Server.getPlayer(Integer.parseInt(msgSplit[1])).changeCash(Integer.parseInt(msgSplit[4]));
            String[] frmPlaces = msgSplit[2].split("[,]");
            String[] toPlaces = msgSplit[3].split("[,]");
            for (int i=0;i<frmPlaces.length;i++)
                Server.getPlace(Integer.parseInt(frmPlaces[i])).setOwnerIndex(Integer.parseInt(msgSplit[1]));
            for (int i=0;i<toPlaces.length;i++)
                Server.getPlace(Integer.parseInt(frmPlaces[i])).setOwnerIndex(index_);
            return true;
        }
        return false;
    }

    public boolean mortgage(String msg) {
        Place place = Server.getPlace(Integer.parseInt(msg.substring(2)));
        Player player = Server.getPlayer(index_);
        if (place.isMortgaged()) {
            if (player.changeCash(-place.getPrice()*55/100)==false)
                sendMessage("n");
            else
                sendMessage("y");
        }
        else
            player.changeCash(place.getPrice()/2);
        return (place.mortgage());
    }

    public boolean quitPlayer() {
        Server.getPlayer(index_).freePlaces();
        Server.getPlayers().remove(index_);
        Server.getThreads().remove(index_);
        Server.sendToAll("q|"+index_);
        Server.nPlayers--;
        this.stop();
        return true;
    }

//    public String receiveMessage() {
//        try {
//            return in_.readUTF();
//        } catch (IOException ex) {
//            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
//            return ("error");
//        }
//    }

    public String receiveMessage () {
        try {
            String msg = in_.readUTF();
            System.out.println(msg);
            return (msg);
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    public void sendMessage(String msg) {
        try {
            out_.writeUTF(msg);
            out_.flush();
        } catch (IOException ex) {
            Logger.getLogger(ServerThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}