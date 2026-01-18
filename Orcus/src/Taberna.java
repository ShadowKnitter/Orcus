import java.awt.*;
import java.awt.event.ActionEvent;
import javax.swing.*;

@SuppressWarnings("LeakingThisInConstructor")
public final class Taberna extends JLabel{
    //Final Variables/Swing Componants
    private final GUI screen;
    private final JLabel aureusBalance;
    private final ShopItem item1;
    private final ShopItem item2;
    private final ShopItem item3;

    //money variable
    private int aureus = 0;
    

    //Constructor
    public Taberna(GUI screen, Player player){
        //gives object access to the GUI given
        this.screen = screen;

        //initial setup
        setBounds(0,0,screen.getWidth(),screen.getHeight());
        setOpaque(true);
        setBackground(new Color(0,0,0));
        screen.layers.add(this, JLayeredPane.DRAG_LAYER);
        setVisible(false);

        //aurauce balance counter setup and addition
        aureusBalance = new JLabel("Aureus: " + aureus);
        aureusBalance.setBounds((int)(2035*Main.getMagnification()),(int)(25*Main.getMagnification()),(int)(500*Main.getMagnification()),(int)(100*Main.getMagnification()));
        aureusBalance.setForeground(new Color(255,255,255));
        aureusBalance.setFont(new Font("Algerian", Font.PLAIN, (int)(50*Main.getMagnification())));
        add(aureusBalance);

        //back/continue button setup and addition
        JButton back = new JButton("Again.");
        back.setBounds((int)(1080*Main.getMagnification()),(int)(1215*Main.getMagnification()),(int)(400*Main.getMagnification()),(int)(200*Main.getMagnification()));
        back.setForeground(new Color(255,255,255));
        back.setFont(new Font("Algerian", Font.PLAIN, (int)(25*Main.getMagnification())));
        back.setContentAreaFilled(false);
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        add(back);
        //action listener for the back/contunue button
        back.addActionListener((ActionEvent e) -> {
            if (e.getSource() == back && !screen.menu.isVisible()){ //ensures the menu isnt open, preventing glitches
                //restarts game timers and closes the taberna; resets player hp.
                Main.gameRunTimer.start();
                screen.roundTimer.start();
                screen.setInTaberna(false);
                player.setHp(player.getMaxHp());
                setVisible(false);

            }
            //ensures the screen has focus
            screen.requestFocus();
        });

        //creates the three items
        item1 = new ShopItem(this, player, 1);
        item2 = new ShopItem(this, player, 2);
        item3 = new ShopItem(this, player, 3);
    }

    //opens and refreshes the taberna and it's items
    public void open(){
        setVisible(true);
        screen.setInTaberna(true);
        item1.stockItem(1);
        item2.stockItem(2);
        item3.stockItem(3);
    }

    //setter method
    public void setAureus(int aureus){
        this.aureus = aureus;
        aureusBalance.setText("Aureus: " + aureus);
    }

    //getter method
    public int getAureus(){
        return aureus;
    }
}