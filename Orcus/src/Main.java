import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import javax.sound.sampled.*;
import javax.swing.*;

@SuppressWarnings("ResultOfObjectAllocationIgnored")
public final class Main {
    //Vars used to get the screen's width and height, as well as a magnification value for scaling
    private static final Toolkit toolkit = Toolkit.getDefaultToolkit();
    private static final Dimension screenSize = toolkit.getScreenSize();
    private static final double SCREEN_WIDTH = screenSize.getWidth();
    private static final double SCREEN_HEIGHT = screenSize.getHeight();
    private static double MAGNIFICATION;

    //other variables
    private static Clip bgm;
    static Timer gameRunTimer;
    static GUI screen; 

    //Main method
    public static void main(String[] args) {
        //sets the magnification to a value that allows the game to fit the screen
        if (SCREEN_HEIGHT/SCREEN_WIDTH < 2560/1440) {
            MAGNIFICATION = SCREEN_WIDTH / 2560;
        }
        else{
            MAGNIFICATION = SCREEN_HEIGHT / 1440;
        }

        screen = new GUI();//creates GUI, booting up the game
    }


    //Public Methods
    //Retrieves music file and plays background music
    public static void playBgm(){
        try {
            File audioFile = new File("FireInYourHeart.wav");
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            bgm = AudioSystem.getClip();
            bgm.open(audioStream);
            bgm.loop(-1);
        }  
        catch (UnsupportedAudioFileException e) {
            System.out.println("Unsupported Audio File Exception AT TRY CATCH OF MUSIC SETUP IN MAIN: " + e);
        }
        catch (IOException e) {
            System.out.println("IO Exception AT TRY CATCH OF MUSIC SETUP IN MAIN: " + e);
        }
        catch (LineUnavailableException e) {
            System.out.println("Line Unavailable Exception AT TRY CATCH OF MUSIC SETUP IN MAIN: " + e);
        }
    }

    //first time initialization for the game
    public static void runGame(){
        //Creates player and gives the GUI access to it
        Player player = new Player(screen);
        screen.passPlayer(player);
        
        new Minotaur (4000,1030, 20, true, screen); //Creates first monster, which is always a Minotaur to keep the game from being impossible to lose

        //Runs every frame (15ms), allowing everything to move on-time with each other
        ActionListener gameRunTimerListener = (ActionEvent e) -> {
            player.playerFrame(); //runs player's frame code, including collision checks and movement
            //Runs for each enemy instance
            for (int i = 1; i<=Enemy.getNoOfEnemies(); i++) {
                Enemy enemy = Enemy.getWithId(i);
                if (enemy != null){
                    enemy.enemyFrame(); //runs enemy's frame code, including collision checks and movement
                }
            }
            //Checks if the screen is supposed to scroll and does so if needed, in the direction required
            if (screen.scrollingLeft){
                screen.scroll(-1);
            }
            else if (screen.scrollingRight){
                screen.scroll(1);
            }
            screen.update(); //Updates GUI
        };
        gameRunTimer = new Timer(15, gameRunTimerListener);
        gameRunTimer.start();
    }

    //scales given image icon based on the screen's magnification
    public static ImageIcon scaleImageIcon(ImageIcon imageIcon){
        //Scales the given imageIcon in accordance with the magnification
        int height = imageIcon.getIconHeight();
        int width = imageIcon.getIconWidth();        
        Image scaledImage = imageIcon.getImage().getScaledInstance((int)(width*MAGNIFICATION), (int)(height*MAGNIFICATION), Image.SCALE_DEFAULT);
        imageIcon.setImage(scaledImage);
        return imageIcon;
    }

    //Getter Methods
    public static double getScreenWidth() {
        return SCREEN_WIDTH;
    }
    public static double getScreenHeight() {
        return SCREEN_HEIGHT;
    }
    public static double getMagnification() {
        return MAGNIFICATION;
    }
}