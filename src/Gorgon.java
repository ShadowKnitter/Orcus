import javax.swing.ImageIcon;

public final class Gorgon extends Enemy{
    //Player var (used to get the player's location for tracking)
    private Player player;


    //Constructors
    //Constructor used for loading/creating with specific locations and stats
    public Gorgon (int x, int y, int moveSpd, GUI screen, Player player){
        super(x, y, new ImageIcon("img/Monsters/GorgonRight.png"), new ImageIcon("img/Monsters/GorgonLeft.png"), 80, moveSpd, 1, 0, false, screen);
        this.player = player;
    }
    //Constructor used for making a new Gorgon at a random location and with random stats
    public Gorgon (GUI screen, Player player){
        this((int)(Math.random()*(4490) + 1) + 300, (int)(Math.random()*(900)+50), (int)(Math.random()*5+3), screen, player);
    }


    //Special Gorgon movement code, moves towards the player
    @Override
    public void enemyFrame(){
        //Calculates which direction to move on the x-axis and does so; sets icons
        if (player.getX()<getX() && player.getX() < getX()-(int)(10*Main.getMagnification())){
            setX(getX()-(int)(getMoveSpd()*Main.getMagnification()));
            setIcon(getLeftIcon());
        }
        else if (player.getX()>getX() && player.getX() > getX()+(int)(10*Main.getMagnification())){
            setX(getX()+(int)(getMoveSpd()*Main.getMagnification()));
            setIcon(getRightIcon());
        }
        //Calculates which direction to move on the y-axis and does so
        if (player.getY()<getY() && player.getY() < getY()-(int)(10*Main.getMagnification())){
            setY(getY()-(int)(getMoveSpd()*Main.getMagnification()));
        }
        else if (player.getY()>getY() && player.getY() > getY()+(int)(10*Main.getMagnification())){
            setY(getY()+(int)(getMoveSpd()*Main.getMagnification()));
        }

        //Prevents 2 Gorgons of the same speed from overlapping and staying that way. Works very similarly to the player chasing code, but moves away rather than towards.
        for (int i = 1; i<=Enemy.getNoOfEnemies(); i++) {
            Enemy enemy = Enemy.getWithId(i);
            if (enemy != null){
                if (this.getBounds().intersects(enemy.getBounds()) && enemy instanceof Gorgon && enemy.getMoveSpd() == getMoveSpd()){
                    if (enemy.getX()<getX() && enemy.getX() < getX()-(int)(10*Main.getMagnification())){
                        setX(getX()+(int)(getMoveSpd()*Main.getMagnification()));
                    }
                    else if (enemy.getX()>getX() && enemy.getX() > getX()+(int)(10*Main.getMagnification())){
                        setX(getX()-(int)(getMoveSpd()*Main.getMagnification()));
                    }
            
                    if (enemy.getY()<getY() && enemy.getY() < getY()-(int)(10*Main.getMagnification())){
                        setY(getY()+(int)(getMoveSpd()*Main.getMagnification()));
                    }
                    else if (enemy.getY()>getY() && enemy.getY() > getY()+(int)(10*Main.getMagnification())){
                        setY(getY()-(int)(getMoveSpd()*Main.getMagnification()));
                    }
                }
            }
        }
    }
}
