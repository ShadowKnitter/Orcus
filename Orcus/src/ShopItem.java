import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

@SuppressWarnings("LeakingThisInConstructor")
public class ShopItem extends JButton{
    //Final Veariables
    private final Player player;
    private final Taberna taberna;

    //Swing Componants
    private JLabel itemDesc;
    private JLabel icon;
    
    //Item Verification Variables
    private int item;
    private int price;

    //Constructor
    public ShopItem(Taberna taberna, Player player, int slot){
        //sets variables to those given in the parameters
        int x = 0;
        this.player = player;
        this.taberna = taberna;
        //sets item location based on slot number
        switch(slot){
            case 1 -> x=(int)(480*Main.getMagnification());
            case 2 -> x=(int)(1080*Main.getMagnification());
            case 3 -> x=(int)(1680*Main.getMagnification());
        }
        setBounds(x,(int)(420*Main.getMagnification()), (int)(400*Main.getMagnification()), (int)(600*Main.getMagnification()));
        setBackground(new Color(0,0,0));
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setEnabled(true);
        setLayout(null);
        taberna.add(this);

        //adds action listener used when the player clikcs the item to buy it
        addActionListener((ActionEvent e) -> {
            if(taberna.getAureus()>=price && e.getSource() == this){ //ensures the player has enough money
                setEnabled(false);
                //grants the items effect
                switch(item){
                    case 1 -> player.setDamage(25);
                    case 2 -> player.setBlockRate(50);
                    case 3 -> player.setDamage(75);
                    case 4 -> {
                        if ((int)(Math.random()*12+1) == 1 )
                            player.setBlockRate(90);
                        else
                            player.setBlockRate(10);
                    }
                    case 5 -> player.setMaxHP(player.getMaxHp()+20);
                    case 6 -> player.setMaxHP((int)(player.getMaxHp()*1.5));
                    case 7 -> player.setMoveSpd((player.getMoveSpd()+5));
                    case 8 -> player.setDodgeStrength((player.getDodgeStrength()+2));
                    case 9 -> player.setJumpStrength((player.getJumpStrength()+3));
                    case 10 -> player.setDamageMult(player.getDamageMult()+1.25);
                    case 11 -> player.setImmunityDuration((int)(player.getImmunityDuration()*1.5));
                }
                //removes the item from the shop
                itemDesc.setVisible(false);
                icon.setIcon(Main.scaleImageIcon(new ImageIcon("img/TabernaItemBought.gif")));
                taberna.setAureus(taberna.getAureus()-price);
            }
        });

        //item description and icon setup
        itemDesc = new JLabel();
        itemDesc.setBounds((int)(50*Main.getMagnification()),(int)(400*Main.getMagnification()),(int)(300*Main.getMagnification()),(int)(200*Main.getMagnification()));
        itemDesc.setForeground(new Color(255,255,255));
        itemDesc.setFont(new Font("Algerian", Font.PLAIN, (int)(25*Main.getMagnification())));
        itemDesc.setVerticalAlignment(SwingConstants.TOP);
        itemDesc.setHorizontalAlignment(SwingConstants.CENTER);
        setOpaque(true);
        add(itemDesc);
        icon = new JLabel(Main.scaleImageIcon(new ImageIcon("img/TabernaItem.png")));
        icon.setBounds((int)(100*Main.getMagnification()),(int)(50*Main.getMagnification()),icon.getIcon().getIconWidth(),icon.getIcon().getIconHeight());
        add(icon);
    }

    //replaces the item in this slot and allows the player to purchase it if they have enough money
    public void stockItem(int slot){
        //makes the item availiable
        icon.setIcon(Main.scaleImageIcon(new ImageIcon("img/TabernaItem.png")));
        itemDesc.setVisible(true);
        setEnabled(true);

        //detects if the slot is one and changes item pool if it is
        if(slot == 1){
            if(player.getBlockRate() == 0 && player.getDamage() == 0){
                item = (int)(Math.random()*2+1);
            }
            else{
                item = (int)(Math.random()*4+1);
            }
        }
        else{
            item = (int)(Math.random()*7+5);
        }

        //sets up price and description depending on the random item
        switch(item){
            case 1 -> { 
                itemDesc.setText("<html><body>&nbsp;&nbsp;&nbsp;Gladius<br>25 Damage<br><br>&nbsp;40 Aureus");
                price = 40;
            }
            case 2 -> { 
                itemDesc.setText("<html><body>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Scutum<br>50% Block Rate<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;55 Aureus");
                price = 55;
            }
            case 3 -> { 
                itemDesc.setText("<html><body>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Harpe<br>75 Damage<br><br>350 Aureus");
                price = 350;
            }
            case 4 -> { 
                itemDesc.setText("<html><body>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Ancile<br>Real: 90% Block Rate<br>Fake: 10% Block Rate<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;150 Aureus");
                price = 150;
            }
            case 5 -> { 
                itemDesc.setText("<html><body>Lorica Armor Set<br>&nbsp;&nbsp;&nbsp;&nbsp;+20 Max HP<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;50 Aureus");
                price = 50;
            }
            case 6 -> { 
                itemDesc.setText("<html><body>Achilles' Armor<br>&nbsp;&nbsp;&nbsp;&nbsp;+50% Max HP<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;135 Aureus");
                price = 135;
            }
            case 7 -> { 
                itemDesc.setText("<html><body>Mercury's Shoe<br>+5 Move Speed<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;90 Aureus");
                price = 90;
            }
            case 8 -> { 
                itemDesc.setText("<html><body>Minerva's Reaction<br>&nbsp;+2 Dodge Strength<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;110 Aureus");
                price = 110;
            }
            case 9 -> { 
                itemDesc.setText("<html><body>&nbsp;&nbsp;&nbsp;&nbsp;Jupiter's Lift<br>+3 Jump Strength<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;70 Aureus");
                price = 70;
            }
            case 10 -> { 
                itemDesc.setText("<html><body>Mars' Strength<br>&nbsp;&nbsp;&nbsp;+25% Damage<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;130 Aureus");
                price = 130;
            }
            case 11 -> { 
                itemDesc.setText("<html><body>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Orcus' Immunity<br>&nbsp;&nbsp;+50% Inivincilibilty after taking damage<br><br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;200 Aureus");
                price = 200;
            }
        }

        //disables button if the player doesnt have enough aureus
        if (taberna.getAureus()<price){
            setEnabled(false);
        }
    }
}