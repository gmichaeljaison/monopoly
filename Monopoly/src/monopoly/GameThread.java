package monopoly;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

public class GameThread extends Thread {

    private Socket socket_;
    private DataInputStream in_;
    private DataOutputStream out_;
    protected int index_;
    private boolean ready_  = false;

    public GameThread(int port) {
        try {
            socket_ = new Socket("192.168.7.18", port);
            in_ = new DataInputStream(socket_.getInputStream());
            out_ = new DataOutputStream(socket_.getOutputStream());
        } catch (IOException ex) {
            Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public void run() {
        String msg = new String();
        msg = receiveMessage();
        if (msg.charAt(0)=='i')
            index_ = Integer.parseInt(msg.substring(2));
        while(!ready_) {
            msg = receiveMessage();
            System.out.println(msg);
            switch (msg.charAt(0)) {
                case 'p':               // player info
                    setPlayerInfo(msg);
                    if (Game.nPlayers == Game.getPlayers().size())
                        ready_ = true;
                    break;
            }
        }
        Game.gameUI.repaint();
        System.out.println("out of while");
        //sendMessage("p|"+index_+"|"+Game.getPlayer(index_).getName()+"|"+Game.getPlayer(index_).getColor());
        String[] msgSplit;
        while (true) {
            Game.gameUI.repaint();
            msg = receiveMessage();
            switch (msg.charAt(0)) {
                case 'd':
                    setDice(msg);
                    Game.gameUI.repaint();
                    break;
                case 'f':
                    msgSplit=msg.split("[|]");
                    Game.currentPlayer=Integer.parseInt(msgSplit[1]);
                    if (Game.currentPlayer == index_) {
                        Game.gameUI.setTurn();
                    }
                    if(Integer.parseInt(msgSplit[1])==index_)
                    {
                        //Roll Dice/Initiate trade/Mortgage/Upgrade
                        
                    }
                    break;
                case 'r':
                    msgSplit=msg.split("[|]");
                    int affIndex=Integer.parseInt(msgSplit[1]);
                    Game.getPlayer(affIndex).setPlace(Integer.parseInt(msgSplit[4]));
                    if(msgSplit.length > 5)
                        if(msgSplit[5].equals("b"))
                            Game.getPlace(Integer.parseInt(msgSplit[4])).setOwnerIndex(affIndex);
                    break;
                case 'm':
                    msgSplit=msg.split("[|]");
                    Game.getPlace(Integer.parseInt(msgSplit[1])).mortgage();
                    break;
                case 'u':
                    msgSplit=msg.split("[|]");
                    Game.getPlace(Integer.parseInt(msgSplit[1])).upgrade();
                    break;
                case 'v':
                    msgSplit=msg.split("[|]");
                    Game.getPlace(Integer.parseInt(msgSplit[1])).degrade();
                    break;
                case '$':
                    msgSplit=msg.split("[|]");
                    Game.getPlayer(Integer.parseInt(msgSplit[1])).setCash(Integer.parseInt(msgSplit[2]));
                    break;
                case 't':
                    msgSplit=msg.split("[|]");
                    String[] frmPlaces=msgSplit[3].split("[,]");
                    String[] toPlaces=msgSplit[4].split("[,]");
                    for (int i=0;i<frmPlaces.length;i++)
                        Game.getPlace(Integer.parseInt(frmPlaces[i])).setOwnerIndex(Integer.parseInt(msgSplit[1]));
                    for (int i=0;i<toPlaces.length;i++)
                        Game.getPlace(Integer.parseInt(frmPlaces[i])).setOwnerIndex(index_);
                    break;
                case 'g':
                    msgSplit=msg.split("[|]");
                    Game.getPlayer(Integer.parseInt(msgSplit[1])).setCash(Integer.parseInt(msgSplit[2]));
                    Game.gameUI.dice1Value.setText(msgSplit[1]);
                    Game.gameUI.dice2Value.setText(msgSplit[2]);
                    Game.gameUI.repaint();
                    break;
                case 'q':
                    msgSplit=msg.split("[|]");
                    quitPlayer(Integer.parseInt(msgSplit[1]));
                    Game.gameUI.repaint();
                    break;
                case 'w':
                    JOptionPane.showMessageDialog(null, "You won the Game");
                    Game.gameUI.dispose();
                    break;
            }
        }
    }

    public void setDice(String msg) {
        String[] msgSplit = msg.split("[|]");
        if (!msgSplit[1].equals(msgSplit[2])) {
            Game.gameUI.finishTurnButton.setEnabled(true);
        }
        else {
           Game.gameUI.setTurn();
        }
        Game.gameUI.dice1Value.setText(msgSplit[1]);
        Game.gameUI.dice2Value.setText(msgSplit[2]);
        Game.getPlayer(this.index_).setPlace(Integer.parseInt(msgSplit[3]));
        Game.gameUI.repaint();
        int type = Game.getPlace(Game.getPlayer(this.index_).getPlaceIndex()).getType();
        if ((type==1) || (type==2)) {
            if (msgSplit.length > 4) {
                if (msgSplit[4].equals("b")) {              // ask to buy
                    if (JOptionPane.showConfirmDialog(Game.gameUI, "Do you want to buy this place ?")==JOptionPane.YES_OPTION) {
                        this.sendMessage("y");
                        Game.getPlace(Integer.parseInt(msgSplit[3])).setOwnerIndex(index_);
                    }
                    else
                        this.sendMessage("n");
                }
                else if (msgSplit[4].equals("r")) {        // pay rent
                    String tmp = Game.getPlayer(index_).getName()+" paid rent to "+Game.getPlayer(Integer.parseInt(msgSplit[5])).getName();
                    JOptionPane.showMessageDialog(Game.gameUI, tmp);
                }
            }
        }
        else if ((type==4)||(type==5)) {  //chance / commmunity chest
            JOptionPane.showMessageDialog(null, "chance : "+msgSplit[4]);
        }
    }

    public boolean setPlayerInfo(String msg) {
        String[] msgSplit = msg.split("[|]");
        int index = Integer.parseInt(msgSplit[1]);
        Color color = new Color(Integer.parseInt(msgSplit[3]));
        Game.addPlayer(new Player(msgSplit[2],color));
        return false;
    }

    public boolean quitPlayer(int index) {
        Game.getPlayer(index).freePlaces();
        Game.getPlayers().remove(index);
        Game.nPlayers--;
        return true;
    }

    public String receiveMessage () {
        try {
//            if (in_.available()>0)
//                System.out.println("avilable");
//            else
//                System.out.println("not avilable");
            String msg = in_.readUTF();
            System.out.println(msg);
            return (msg);
        } catch (IOException ex) {
            Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
            return "error";
        }
    }

    public void sendMessage(String msg) {
        try {
            out_.writeUTF(msg);
            out_.flush();
        } catch (IOException ex) {
            Logger.getLogger(GameThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}