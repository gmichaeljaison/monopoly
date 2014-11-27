package monopoly;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Font;

public class GameUI extends javax.swing.JFrame {
    private int startX_ = 10;
    private int startY_ = 30;
    /** Creates new form GameUI */
    public GameUI() {
        initComponents();
        setVisible(true);
        finishTurnButton.setEnabled(false);
        setTitle("Monopoly - ");
        setExtendedState(MAXIMIZED_BOTH);
    }

    @Override
    public void paint(Graphics g) {
        if (Game.nPlayers > Game.getPlayers().size())
            return;
        super.paint(g);
        byte playerWidth = 35,playerHeight = 30,soldIconWidth = 5;
        byte horBoxWidth = 85,verBoxWidth = 60,boxHeight = 100;
        int k = Game.nHorBoxes-1;
        int j = Game.nHorBoxes+Game.nVerBoxes-2;
        short scorePlayer = 250;                                      // player field in score board
        g.setFont(new Font("Arial",1,14));
        int scoreX = startX_+(Game.nHorBoxes-2)*horBoxWidth+2*boxHeight+25;
        for (int i=0;i<Game.nPlayers;i++) {                                                              // score board
            g.setColor(Color.BLACK);
            g.drawRect(scoreX, startY_+i*30, scorePlayer, 30);
            g.drawRect(scoreX+scorePlayer, startY_+i*30, 75, 30);
            g.drawString(Integer.toString(Game.getPlayer(i).getCash()), scoreX+20+scorePlayer+20, startY_+i*30+25);
            g.setColor(Game.getPlayer(i).getColor());
            g.drawString(Game.getPlayer(i).getName(), scoreX+20, startY_+i*30+25);
        }
        g.setColor(Color.BLACK);
        //g.drawString("Bank : "+Integer.toString(bank.cash), scoreX+25+20, startY+Game.nPlayers*30+25);
        g.setFont(new Font("Arial",1,10));
        int horBottomY = startY_+boxHeight+(Game.nVerBoxes-2)*verBoxWidth;
        for (int i=0;i<Game.nHorBoxes;i++,j++,k--) {                                        // horizontal boxes
            int horX = startX_+boxHeight+(i-1)*horBoxWidth;
            if (i==0)
                horX = startX_;

            if (Game.getPlace(j).getPrice()>0) {
                g.drawString("Rs "+Integer.toString(Game.getPlace(j).getPrice()), horX+20, startY_+75);
//                if (places[j].isPlace) {
//                   g.setColor(groupColors[places[j].group]);
//                    g.fillRect(horX, startY+9*boxHeight/10, horBoxWidth, boxHeight/10);         // group color
//                }
                    if (!Game.getPlace(j).isFree()) {                                                     // sold symobol for top boxes
                        g.setColor(Game.getPlayer(Game.getPlace(j).getOwnerIndex()).getColor());
                        g.fillRect(horX+25, startY_+boxHeight, horBoxWidth/2, soldIconWidth);
                    }
                    g.setColor(Color.BLACK);
            }
            if (Game.getPlace(k).getPrice()>0) {
                g.drawString("Rs. "+Integer.toString(Game.getPlace(k).getPrice()), horX+20, horBottomY+75);
//                if (places[k].isPlace) {
//                    g.setColor(groupColors[places[k].group]);
//                    g.fillRect(horX, horBottomY, horBoxWidth, boxHeight/10);
//                }
                      if (!Game.getPlace(k).isFree()) {                                                               // sold symobol for bottom boxes
                        g.setColor(Game.getPlayer(Game.getPlace(k).getOwnerIndex()).getColor());
                        g.fillRect(horX+25, horBottomY-soldIconWidth, horBoxWidth/2, soldIconWidth);
                    }
                    g.setColor(Color.BLACK);
            }

            if ((i==0) || (i==Game.nHorBoxes-1)) {                     // boxes
                g.drawRect(horX, startY_, boxHeight, boxHeight);
                g.drawRect(horX, horBottomY, boxHeight, boxHeight);
            }
            else {
                g.drawRect(horX, startY_, horBoxWidth, boxHeight);
                g.drawRect(horX, horBottomY, horBoxWidth, boxHeight);
            }
            if (Game.getPlace(j).getName().contains(" ")) {                           // place name
                String[] plac = Game.getPlace(j).getName().split("[ ]");             // to print place name in 2 lines
                int nameY = startY_+20;
                for (int m=0;m<plac.length;m++) {
                    g.drawString(plac[m], horX+15, nameY+m*10);
                }
            }
            else
                g.drawString(Game.getPlace(j).getName(), horX+15, startY_+20);

            if (Game.getPlace(k).getName().contains(" ")) {
                String[] plac = Game.getPlace(k).getName().split("[ ]");
                int nameY = horBottomY+20;
                for (int m=0;m<plac.length;m++) {
                    g.drawString(plac[m], horX+15, nameY+m*10);
                }
            }
            else
                g.drawString(Game.getPlace(k).getName(), horX+15, horBottomY+20);
            for (int x=0;x<Game.nPlayers;x++) {                                                // draw players in horizontal boxes
                if (j==Game.getPlayer(x).getPlaceIndex()) {
                    g.setColor(Game.getPlayer(x).getColor());
                    g.fillOval(horX+25+x*10, startY_+50, playerWidth, playerHeight);
                    g.setColor(Color.BLACK);
                }
                else if (k==Game.getPlayer(x).getPlaceIndex()) {
                    g.setColor(Game.getPlayer(x).getColor());
                    g.fillOval(horX+25+x*10, horBottomY+50, playerWidth, playerHeight);
                    g.setColor(Color.BLACK);
                }
            }
        }
        j = Game.nHorBoxes+Game.nVerBoxes-3;
        k = 2*Game.nHorBoxes+Game.nVerBoxes-2;
        int verRightX = startX_+boxHeight+(Game.nHorBoxes-2)*horBoxWidth;
        for (int i=1;i<Game.nVerBoxes-1;i++,j--,k++) {                                              // vertical boxes
            int verY = startY_+boxHeight+(i-1)*verBoxWidth;

            if (Game.getPlace(j).getName().contains(" ")) {                                             // name in 2 lines
                String[] plac = Game.getPlace(j).getName().split("[ ]");
                int nameY = verY+20;
                for (int m=0;m<plac.length;m++) {
                    g.drawString(plac[m], startX_+15, nameY+m*10);
                }
            }
            else
                g.drawString(Game.getPlace(j).getName(), startX_+20, verY+25);
            if (Game.getPlace(j).getPrice()>0) {
                g.drawString(Integer.toString(Game.getPlace(j).getPrice()), startX_+20, verY+50);
//                if (places[j].isPlace) {
//                    g.setColor(groupColors[places[j].group]);
//                    g.fillRect(startX+9*boxHeight/10, verY, boxHeight/10, verBoxWidth);
//                }
                    if (!Game.getPlace(j).isFree()) {                                                           // sold symbol for left boxes
                        g.setColor(Game.getPlayer(Game.getPlace(j).getOwnerIndex()).getColor());
                        g.fillRect(startX_+boxHeight, verY+verBoxWidth/4, soldIconWidth, verBoxWidth/2);
                    }
                    g.setColor(Color.BLACK);
            }

            if (Game.getPlace(k).getName().contains(" ")) {
                String[] plac = Game.getPlace(j).getName().split("[ ]");
                int nameY = verY+20;
                for (int m=0;m<plac.length;m++) {
                    g.drawString(plac[m], verRightX+15, nameY+m*10);
                }
            }
            else
                g.drawString(Game.getPlace(k).getName(), verRightX+25, verY+25);
            if (Game.getPlace(k).getPrice()>0) {
                g.drawString(Integer.toString(Game.getPlace(k).getPrice()), verRightX+20, verY+50);
//                if (places[k].isPlace) {
//                    g.setColor(groupColors[places[k].group]);
//                    g.fillRect(verRightX, verY, boxHeight/10, verBoxWidth);
//                }
                    if (!Game.getPlace(k).isFree()) {                                                             // sold symbol for right boxes
                        g.setColor(Game.getPlayer(Game.getPlace(k).getOwnerIndex()).getColor());
                        g.fillRect(verRightX-soldIconWidth, verY+verBoxWidth/4, soldIconWidth, verBoxWidth/2);
                    }
                    g.setColor(Color.BLACK);
            }
            g.drawRect(startX_, verY, boxHeight, verBoxWidth);           // boxes
            g.drawRect(verRightX, verY, boxHeight, verBoxWidth);
            for (int x=0;x<Game.nPlayers;x++) {                                                    // draw players in vertical boxes
                if (j==Game.getPlayer(x).getPlaceIndex()) {
                    g.setColor(Game.getPlayer(x).getColor());
                    g.fillOval(startX_+25+x*10, verY+verBoxWidth/4, playerWidth, playerHeight);
                    g.setColor(Color.BLACK);
                }
                else if (k==Game.getPlayer(x).getPlaceIndex()) {
                    g.setColor(Game.getPlayer(x).getColor());
                    g.fillOval(verRightX+25+x*10, verY+verBoxWidth/4, playerWidth, playerHeight);
                    g.setColor(Color.BLACK);
                }
            }
        }
    }

    public void setTurn() {
        finishTurnButton.setEnabled(false);
        rollDice.setEnabled(true);
        if (Game.getPlayer(Game.thread.index_).nPlacesOwned()>0) {
            mortgageButton.setEnabled(true);
            unmortgageButton.setEnabled(true);
            //finishTurnButton.setEnabled(true);
        }
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        rollDice = new javax.swing.JButton();
        dice1Value = new javax.swing.JLabel();
        dice2Value = new javax.swing.JLabel();
        quitButton = new javax.swing.JButton();
        upgradeButton = new javax.swing.JButton();
        degradeButton = new javax.swing.JButton();
        mortgageButton = new javax.swing.JButton();
        unmortgageButton = new javax.swing.JButton();
        finishTurnButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setFont(new java.awt.Font("Berlin Sans FB", 1, 14));

        rollDice.setText("Roll Dice");
        rollDice.setEnabled(false);
        rollDice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rollDice(evt);
            }
        });

        dice1Value.setFont(new java.awt.Font("Miriam Fixed", 1, 14));
        dice1Value.setText("0");

        dice2Value.setFont(new java.awt.Font("Meiryo", 1, 14));
        dice2Value.setText("0");

        quitButton.setText("Quit");
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitGame(evt);
            }
        });

        upgradeButton.setText("Upgrade");
        upgradeButton.setEnabled(false);

        degradeButton.setText("Degrade");
        degradeButton.setEnabled(false);

        mortgageButton.setText("Mortgage");
        mortgageButton.setEnabled(false);

        unmortgageButton.setText("Unmortgage");
        unmortgageButton.setEnabled(false);

        finishTurnButton.setText("Finish Turn");
        finishTurnButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                finishTurn(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(quitButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(upgradeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(degradeButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(mortgageButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(unmortgageButton))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(323, 323, 323)
                        .addComponent(dice1Value)
                        .addGap(27, 27, 27)
                        .addComponent(dice2Value))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(310, 310, 310)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(finishTurnButton)
                            .addComponent(rollDice))))
                .addContainerGap(324, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(221, 221, 221)
                .addComponent(rollDice)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(dice1Value)
                    .addComponent(dice2Value))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(finishTurnButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 198, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(quitButton)
                    .addComponent(upgradeButton)
                    .addComponent(degradeButton)
                    .addComponent(mortgageButton)
                    .addComponent(unmortgageButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void rollDice(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rollDice
        // TODO add your handling code here:
        rollDice.setEnabled(false);
        Game.thread.sendMessage("r");
        
//        String msg = new String();
//        System.out.println("waiting to recieve");
//        //System.out.println(Game.thread.receiveMessage());
//        msg = Game.thread.receiveMessage();
//        System.out.println("msg recieved");
//        System.out.println(msg);
//        String[] msgSplit = msg.split("[|]");
//        if (msg.charAt(0)=='d') {
//            dice1Value.setText(msgSplit[1]);
//            dice2Value.setText(msgSplit[2]);
//            Game.getPlayer(thread.index_).setPlace(Integer.parseInt(msgSplit[3]));
//            repaint();
//            int type = Game.getPlace(Game.getPlayer(thread.index_).getPlaceIndex()).getType();
//            if ((type==1) || (type==2)) {
//                if (msgSplit.length > 4) {
//                    if (msgSplit[4].equals("b")) {              // ask to buy
//                        if (JOptionPane.showConfirmDialog(this, "Do you want to buy this place ?")==JOptionPane.YES_OPTION)
//                            thread.sendMessage("y");
//                        else
//                            thread.sendMessage("n");
//                    }
//                    else if (msgSplit[4].equals("r")) {        // pay rent
//
//                    }
//                }
//            }
//            else if ((type==4)||(type==5)) {  //chance / commmunity chest
//
//            }
//        }
    }//GEN-LAST:event_rollDice

    private void finishTurn(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_finishTurn
        // TODO add your handling code here:
        mortgageButton.setEnabled(false);
        unmortgageButton.setEnabled(false);
        upgradeButton.setEnabled(false);
        degradeButton.setEnabled(false);
        finishTurnButton.setEnabled(false);
        Game.thread.sendMessage("f");
        repaint();
    }//GEN-LAST:event_finishTurn

    private void exitGame(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_exitGame
        // TODO add your handling code here:
        Game.thread.sendMessage("x");
        Game.thread.stop();
        this.dispose();
    }//GEN-LAST:event_exitGame

//    public static void main(String args[]) {
//        java.awt.EventQueue.invokeLater(new Runnable() {
//            public void run() {
//                new GameUI().setVisible(true);
//            }
//        });
//    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton degradeButton;
    protected javax.swing.JLabel dice1Value;
    protected javax.swing.JLabel dice2Value;
    protected javax.swing.JButton finishTurnButton;
    private javax.swing.JButton mortgageButton;
    private javax.swing.JButton quitButton;
    private javax.swing.JButton rollDice;
    private javax.swing.JButton unmortgageButton;
    private javax.swing.JButton upgradeButton;
    // End of variables declaration//GEN-END:variables

}
