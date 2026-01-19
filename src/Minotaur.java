import javax.swing.*;

public final class Minotaur extends Enemy{

    //Constructors
    //Constructor used for loading/creating with specific locations and stats
    public Minotaur(int x, int y, int moveSpd, boolean movingLeft, GUI screen) {
        super(x, y, new ImageIcon("img/Monsters/MinotaurRight.gif"), new ImageIcon("img/Monsters/MinotaurLeft.gif"), 300, moveSpd, 10, 25, movingLeft, screen);
    }
    //Constructor used for making a new Minotaur at a random location and with random stats
    public Minotaur(GUI screen) {
        this((int)(Math.random()*(4490) + 1) + 300, 1030, (int)(Math.random()*9)+17, (int)(Math.random()*2) == 0, screen);
    }
}