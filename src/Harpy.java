import javax.swing.ImageIcon;

public final class Harpy extends Enemy{

    //Constructors
    //Constructor used for loading/creating with specific locations and stats
    public Harpy (int x, int y, int moveSpd, boolean movingLeft, GUI screen){ 
        super(x, y, new ImageIcon("img/Monsters/HarpyRight.gif"), new ImageIcon("img/Monsters/HarpyLeft.gif"), 100, moveSpd, 20, 0, movingLeft, screen);
    }
    //Constructor used for making a new Harpy at a random location and with random stats
    public Harpy (GUI screen){
        this((int)(Math.random()*(4490) + 1) + 300, (int)(Math.random()*(900)+50), (int)(Math.random()*8+10), (int)(Math.random()*2) == 0, screen);
    }
}