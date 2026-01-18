import java.awt.*;
import java.awt.event.*;
import java.io.*;
import javax.swing.*;

@SuppressWarnings({"LeakingThisInConstructor", "ResultOfObjectAllocationIgnored"})
public final class GUI extends JFrame implements ActionListener{
    //scrolling/movement variables
    JLabel leftScroller;
    JLabel rightScroller;
    boolean scrollingLeft = false;
    boolean scrollingRight = false;

    
    //dialog variables
    private JLabel orcusSpecialDialog1;
    private final JTextField name = new JTextField("(Type your name and press [ENTER] to continue)");
    private JLabel orcusSpecialDialog2;
    private Timer orcusTimer;
    private int transparency = 0;
    private int dialogCounter = 0;

    //Swing componants and timers
    JLayeredPane layers;
    private final JLabel sky = new JLabel();
    private Timer menuMovementTimer;
    private final JButton newGameButton = new JButton("New Game");
    private final JButton continueButton = new JButton("Continue");
    private boolean doNotEnableContinue = false;
    private final JButton controlsButton = new JButton("Controls");
    private final JButton exitButton = new JButton("Exit");
    private final JButton creditsButton = new JButton("Credits");
    private final JLabel colosseum = new JLabel();
    private JLabel leftMargin;
    private JLabel rightMargin;
    final JLabel menu = new JLabel();
    private final JLabel statsInfo = new JLabel();
    final JLabel healthbar = new JLabel();
    private final JLabel timerBG = new JLabel();
    private final JLabel timerSurvive = new JLabel("Survive!");
    private final JLabel timerText = new JLabel();
    Timer roundTimer;
    private int secRemaining = 20;
    private final JLabel roundBG = new JLabel();
    private final JLabel roundIndicator = new JLabel("Round");
    private final JLabel roundText = new JLabel();
    private final JLabel bestText = new JLabel();


    //gameplay loop variables
    private int round = 1;
    private boolean gameOn = false;
    private Taberna taberna;
    private boolean inTaberna = false;

    //player acces variable
    private Player player;


    //Constructor
    public GUI() {
        
        //creates and sets up the game's window/Jframe
        setTitle("Orcus");
        setIconImage(new ImageIcon("img/GameIcon.png").getImage());
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setUndecorated(true);
        setBounds(0, 0, (int)(2560*Main.getMagnification()), (int)(1440*Main.getMagnification()));
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(new Color(0, 0, 0));
        setVisible(true);
        if (Main.getScreenWidth()/Main.getScreenHeight() == 2560/1440){
            setExtendedState(Frame.MAXIMIZED_BOTH);
        }

        //declares and sets up the layers
        layers = new JLayeredPane();
        layers.setBounds(0,0, getWidth(), getHeight());
        add(layers);    

        //used to play the first-time cutscene
        try (BufferedReader br = new BufferedReader(new FileReader("saveData.txt"));){
            if (br.readLine() == null){//checks if the player has played the game before(has save data)
                //disallows the continue button to be pressed, as there is no ave data to use;
                continueButton.setEnabled(false);
                doNotEnableContinue = true;

                //plays the sutcene by having orcus speak
                orcusSpeak(1, 3000, "My son...");
                orcusSpeak(1, 3000, "You must avenge me...");
                orcusSpeak(1, 3000, "Prove my might and we can take the underworld back from Pluto.");
                orcusSpeak(1, 3000, "You know what must be done.");      
                
                //special, three-lined version of the same strategy used in the orcus speak method, but only dissapearing when a name is entered instead of automatically
                //sets up the three lines and adds them
                orcusSpecialDialog1 = new JLabel("Let our names strike fear into their hearts...");
                orcusSpecialDialog1.setBounds(0,(int)(-30*Main.getMagnification()), getWidth(), getHeight());
                orcusSpecialDialog1.setFont(new Font("Algerian", Font.PLAIN, (int)(25*Main.getMagnification())));
                orcusSpecialDialog1.setForeground(new Color(255,255,255,0));
                orcusSpecialDialog1.setHorizontalAlignment(SwingConstants.CENTER);
                layers.add(orcusSpecialDialog1, JLayeredPane.DRAG_LAYER);
                name.setBounds((int)(getWidth()/2-500*Main.getMagnification()), (int)(getHeight()/2-(25*Main.getMagnification())/2), (int)(1000*Main.getMagnification()), (int)(25*Main.getMagnification()));
                name.setFont(new Font("Algerian", Font.PLAIN, (int)(25*Main.getMagnification())));
                name.setBorder(null);
                name.setOpaque(false);
                name.setForeground(new Color(255,255,255, 0));
                name.setEnabled(true);
                name.setHorizontalAlignment(SwingConstants.CENTER);
                name.addFocusListener(new FocusAdapter() {
                    //clears text and adds aesthetic quotation when the playr clicks on the text field
                    @Override
                    public void focusGained(FocusEvent e) {
                        if (name.getText().equals("(Type your name and press [ENTER] to continue)")) {
                            name.setText("\"");
                        }
                    }
        
                    @Override
                    public void focusLost(FocusEvent e) {
                        if (name.getText().isEmpty() || name.getText().equals("\"")) {
                            name.setText("(Type your name and press [ENTER] to continue)");
                        }
                    }
                });
                layers.add(name, JLayeredPane.DRAG_LAYER);
                orcusSpecialDialog2 = new JLabel("and Orcus\"");
                orcusSpecialDialog2.setBounds(0,0,getWidth(), (int)(getHeight()+60*Main.getMagnification()));
                orcusSpecialDialog2.setFont(new Font("Algerian", Font.PLAIN, (int)(25*Main.getMagnification())));
                orcusSpecialDialog2.setOpaque(true);
                orcusSpecialDialog2.setForeground(new Color(255,255,255,0));
                orcusSpecialDialog2.setBackground(new Color(0,0,0,255));
                orcusSpecialDialog2.setHorizontalAlignment(SwingConstants.CENTER);
                layers.add(orcusSpecialDialog2, JLayeredPane.DRAG_LAYER);

                //action listener used to fade in the text, further explained in the orcusSpeak method
                ActionListener fadeInTextListener = (ActionEvent g) -> {
                    if (transparency<=255){
                        orcusSpecialDialog1.setForeground(new Color(255,255,255, transparency));
                        name.setForeground(new Color(255,255,255, transparency));
                        orcusSpecialDialog2.setForeground(new Color(255,255,255, transparency));
                        update();
                        transparency+=3;
                        if (transparency > 255){
                            orcusTimer.stop();
                        }
                    }
                };
                orcusTimer = new Timer(15, fadeInTextListener);
                orcusTimer.start();
                name.addKeyListener(new KeyListener() {
                    @Override
                    public void keyPressed(KeyEvent e){
                        //causes the text lines to fade out when enter is pressed, then plays the bgm and gives the frame focus, fade-out is done the same way as seen in the orcusSpeak method
                        if (e.getKeyCode() == KeyEvent.VK_ENTER){
                            if (name.isVisible() && !name.getText().equals("(Type your name and press [ENTER] to continue)") && !name.getText().equals("\"")&& !name.getText().isEmpty()){
                                //name.setEnabled(false);
                                ActionListener fadeInTextListener = (ActionEvent g) -> {
                                    if (transparency>=0){
                                        transparency-=3;
                                        orcusSpecialDialog1.setForeground(new Color(255,255,255, transparency));
                                        name.setForeground(new Color(255,255,255, transparency));
                                        orcusSpecialDialog2.setForeground(new Color(255,255,255, transparency));
                                        update();
                                        if (transparency==0){
                                            orcusTimer.stop();
                                            orcusSpecialDialog1.setVisible(false);
                                            name.setVisible(false);
                                            orcusSpecialDialog2.setVisible(false);
                                            requestFocus();
                                            Main.playBgm();
                                        }
                                    }
                                };
                                orcusTimer = new Timer(15, fadeInTextListener);
                                orcusTimer.start();  
                            }
                        }
                    }

                    @Override
                    public void keyTyped (KeyEvent e){}
                    @Override
                    public void keyReleased(KeyEvent e){}
                });
            }
            //if there is no save data, the bgm is immidiatly played
            else{
                Main.playBgm();
            }
        } 
        catch (IOException e) {
            System.out.println("ERROR AT CATCH OF FILE READER: " + e);
        }

        //sets up the sky and main menu buttons
        getContentPane().setBackground(new Color(0, 182, 174));
        ImageIcon skyIcon = Main.scaleImageIcon(new ImageIcon("img/Sky.png"));
        sky.setIcon(skyIcon);
        sky.setBounds(0, 0, skyIcon.getIconWidth(), skyIcon.getIconHeight());
        add(sky);
        mainMenuButtonSetup(newGameButton, 525, 1185);
        mainMenuButtonSetup(continueButton, 925, 1185);         
        mainMenuButtonSetup(controlsButton, 1350, 1200);       
        mainMenuButtonSetup(exitButton, 1650, 1200);       
        mainMenuButtonSetup(creditsButton, 1950, 1200);       
        setFocusable(true);
        update();
        addKeyListener(new KeyListener() {
            //adds keyboard functionality and input
            @Override
            public void keyPressed(KeyEvent e) {
                if(gameOn){ //ensures the game is on
                    //sets accosiated variables when W, A, D, space, or their respective alternatives are pressed, to true
                    if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W' || e.getKeyCode() == KeyEvent.VK_UP) {
                        player.setWPressed(true);
                    }
                    else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A' || e.getKeyCode() == KeyEvent.VK_LEFT) {
                        player.setAPressed(true);
                    }
                    else if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D' || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        player.setDPressed(true);
                    }
                    else if (e.getKeyChar() == ' '){
                        player.setSpacePressed(true);
                    }

                    //opens the menu and displays stats as well as the exit button when escape is pressed
                    else if (e.getKeyCode() == KeyEvent.VK_ESCAPE){
                        if (!menu.isVisible()){
                            menu.setVisible(true);
                            statsInfo.setText("<html><body>Health: "+indent(8)+player.getHp() + "/" + player.getMaxHp()+"<br>Speed: "+indent(11)+player.getMoveSpd()+"<br>Jump Str: "+indent(6)+player.getJumpStrength()+"<br>Dodge Str: "+indent(3)+player.getDodgeStrength()+"<br>Damage: "+indent(7)+player.getDamage()+"<br>Dmg Mult: "+indent(4)+player.getDamageMult()+"<br>Block Rate: "+player.getBlockRate()+"<br>Immunity: "+indent(6)+player.getImmunityDuration());
                            Main.gameRunTimer.stop();
                            roundTimer.stop();
                        }
                        else{
                            menu.setVisible(false);
                            if (!inTaberna){
                                Main.gameRunTimer.start();
                                roundTimer.start();
                            }
                        }
                    }
                }
            }
        
            @Override
            public void keyReleased(KeyEvent e) {
                if(gameOn){ //ensures the game is on
                    //sets accosiated variables when W, A, D, space, or their respective alternatives are released, to false
                    if (e.getKeyChar() == 'w' || e.getKeyChar() == 'W' || e.getKeyCode() == KeyEvent.VK_UP) {
                        player.setWPressed(false);
                    }
                    else if (e.getKeyChar() == 'a' || e.getKeyChar() == 'A' || e.getKeyCode() == KeyEvent.VK_LEFT) {
                        player.setAPressed(false);
                    }
                    else if (e.getKeyChar() == 'd' || e.getKeyChar() == 'D' || e.getKeyCode() == KeyEvent.VK_RIGHT) {
                        player.setDPressed(false);
                    }
                }
            }
            @Override
            public void keyTyped(KeyEvent e) {}
        });
        addMouseListener(new MouseListener() {
            //adds mouse functionality and input
            @Override
            public void mousePressed(MouseEvent e) {
                if(gameOn){//ensures the game is on
                    //sets accosiated variables when the left/right mouse buttons pressed to true
                    if(e.getButton() == MouseEvent.BUTTON1){
                        player.setLeftClickPressed(true);

                    }
                    if(e.getButton() == MouseEvent.BUTTON3){
                        player.setRightClickPressed(true);
                    }
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                if(gameOn){//ensures the game is on
                    //sets accosiated variables when the left/right mouse buttons released to false
                    if(e.getButton() == MouseEvent.BUTTON1){
                        player.setLeftClickPressed(false);
                    }
                    if(e.getButton() == MouseEvent.BUTTON3){
                        player.setRightClickPressed(false);
                    }
                }
            }
            @Override
            public void mouseClicked(MouseEvent e) {}
            @Override
            public void mouseEntered(MouseEvent e) {}
            @Override
            public void mouseExited(MouseEvent e) {}
        });

        
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        //run when one of the menu buttons are pressed
        newGameButton.setEnabled(false);
        continueButton.setEnabled(false);
        controlsButton.setEnabled(false);
        exitButton.setEnabled(false);
        creditsButton.setEnabled(false);
        if (e.getSource() == newGameButton || e.getSource() == continueButton){ //starts the game when either the new game or continue buttons are pressed
            //if the new game button was pressed, the player's high score and name are saved in temporary variables while the rest of the file is deleted, they are then placed back into the file.
            if (e.getSource() == newGameButton){
                boolean resetFile = false;
                int highScoreTemp = 1;
                String nameTemp = "";
                try (BufferedReader br = new BufferedReader(new FileReader("saveData.txt"))){
                    if (br.readLine() != null){
                        resetFile = true;
                        highScoreTemp = Integer.parseInt(br.readLine());
                        nameTemp = br.readLine();
                    }
                } 
                catch (IOException f) {
                    System.out.println("ERROR AT CATCH OF NEW GAME BUTTON PRESS: "+ f);
                }
                try {
                    if (resetFile){
                        File file = new File("saveData.txt");
                        file.delete();
                        try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
                            pw.print("\n" + highScoreTemp + "\n" + nameTemp);
                        }
                    }
                } 
                catch (IOException f) {
                    System.out.println("ERROR AT CATCH OF NEW GAME BUTTON PRESS: "+ f);
                }
            }
            //starts game by sliding on the Colloseum and then running the gameScreenSetup method
            ImageIcon colosseumIcon = Main.scaleImageIcon(new ImageIcon("img/Colosseum.png"));
            colosseum.setIcon(colosseumIcon);
            colosseum.setBounds(0, getHeight(), colosseumIcon.getIconWidth(), colosseumIcon.getIconHeight());
            layers.add(colosseum, JLayeredPane.DEFAULT_LAYER);
            ActionListener newGameListener = (ActionEvent g) -> {
                //slides the colloseum up by 10 pixels every 15 ms, until it is at the top
                colosseum.setLocation(0, colosseum.getY()-10);
                update();
                if (colosseum.getY()<=0){
                    menuMovementTimer.stop();
                    colosseum.setLocation(0,0);
                    gameScreenSetup();
                }
            };
            menuMovementTimer = new Timer(15, newGameListener);
            menuMovementTimer.start();
        }
        //opens the controls menu when the controls button is pressed
        if (e.getSource() == controlsButton){
            JLabel controls = new JLabel("<html><body>Gameplay:<br>&nbsp;&nbsp;Left/Right:"+indent(37)+"A/← & D/→<br>&nbsp;&nbsp;Jump:"+indent(50)+"W/↑<br>&nbsp;&nbsp;Dash:"+indent(50)+"␣<br>&nbsp;&nbsp;Sword:"+indent(47)+"Left Click<br>&nbsp;&nbsp;Shield:"+indent(47)+"Right Click<br><br>Menus:<br>&nbsp;&nbsp; Open (in-game):"+indent(29)+"ESC<br>&nbsp;&nbsp; Interact:"+indent(40)+"Left Click<html>");
            controls.setBounds(0,-getHeight(), getWidth(), getHeight());
            controls.setFont(new Font("Algerian", Font.PLAIN, (int)(20*Main.getMagnification())));
            controls.setForeground(new Color(0, 0, 0));
            controls.setHorizontalAlignment(SwingConstants.CENTER);
            add(controls);
            openExtraMenu(controls);
        }
        //closes the frame and ends the program when the exit button is pressed
        if (e.getSource() == exitButton){
            dispose();
            System.exit(0);
        }
        //opens the credits menu when the credits button is pressed
        if (e.getSource() == creditsButton){
            JLabel credits = new JLabel("<html><body>Programming, Art, Design, Production, Quality Assurance:"+indent(20)+"Eli McComas<br><br>Scaling Functionality: "+indent(93)+"Kenneth McComas<br><br>Computer Science teacher:"+indent(85)+"Mr. Lauder<br><br>Java Reference and Library Research:"+indent(60)+"stackoverflow.com/questions<br><br>Art made using Piskel sprite editor:"+indent(66)+"www.piskelapp.com<br><br>Art edited using Ezgif.com:"+indent(85)+"ezgif.com/rotate/ezgif-3b193f48210c624e.gif.html<br><br>Music sourced from pixabay:"+indent(81)+"pixabay.com/music/main-title-fire-in-your-heart-cinematic-epic-276660/<br<br>Design Reference images:"+indent(87)+"emporiumromanum.com/cdn/shop/articles/gladiators-the-ultimate-fighters-of-ancient-rome-180844.jpg<br><br>"+indent(141)+"history-making.com/wp-content/uploads/2022/02/Roman-Sheilds-Title-image.jpg<html>");
            credits.setBounds(0,-getHeight(), getWidth(), getHeight());
            credits.setFont(new Font("Algerian", Font.PLAIN, (int)(20*Main.getMagnification())));
            credits.setForeground(new Color(0, 0, 0));
            credits.setHorizontalAlignment(SwingConstants.CENTER);
            add(credits);
            openExtraMenu(credits);
        }
    }
  
    //sets up the game screen
    private void gameScreenSetup(){
        //removes the aesthetic quotation in the name submission, if it is still there
        if(name.getText().charAt(0) == '\"'){
            name.setText(name.getText().substring(1));  
        }

        //sets up the timer in the top right corner of the screen and adds it
        ImageIcon timerIcon = Main.scaleImageIcon(new ImageIcon("img/Timer BG.png"));
        timerBG.setIcon(timerIcon);
        timerBG.setBounds((int)(getWidth()-(250*Main.getMagnification())),(int)(28*Main.getMagnification()), timerIcon.getIconWidth(), timerIcon.getIconHeight());
        layers.add(timerBG, JLayeredPane.POPUP_LAYER);
        timerSurvive.setBounds(0,(int)(-17*Main.getMagnification()), timerIcon.getIconWidth(), timerIcon.getIconHeight());
        timerSurvive.setFont(new Font("Algerian", Font.PLAIN, (int)(20*Main.getMagnification())));
        timerSurvive.setForeground(new Color(150, 0, 0));
        timerSurvive.setHorizontalAlignment(SwingConstants.CENTER);
        timerBG.add(timerSurvive);
        timerText.setText("" + secRemaining);
        timerText.setBounds(0,(int)(15*Main.getMagnification()), timerIcon.getIconWidth(), timerIcon.getIconHeight());
        timerText.setFont(new Font("Algerian", Font.PLAIN, (int)(60*Main.getMagnification())));
        timerText.setForeground(new Color(150, 0, 0));
        timerText.setHorizontalAlignment(SwingConstants.CENTER);
        timerBG.add(timerText);
        ActionListener roundTimerListener = (ActionEvent e) -> {
            //triggers every second, lowering the counter by one and running the round finished method if it reaches 0
            secRemaining--;
            timerText.setText("" + secRemaining);
            if (secRemaining == 0){
                roundFinished();
            }
        };
        roundTimer = new Timer(1000, roundTimerListener);
        roundTimer.start();

        //sets up the round indicator and highscore in the top left corner of the screen and adds it
        ImageIcon roundIcon = Main.scaleImageIcon(new ImageIcon("img/Round BG.png"));
        roundBG.setIcon(roundIcon);
        roundBG.setBounds((int)(50*Main.getMagnification()),(int)(28*Main.getMagnification()), roundIcon.getIconWidth(), roundIcon.getIconHeight());
        layers.add(roundBG, JLayeredPane.POPUP_LAYER);
        roundIndicator.setBounds(0,(int)(-65*Main.getMagnification()), roundIcon.getIconWidth(), roundIcon.getIconHeight());
        roundIndicator.setFont(new Font("Algerian", Font.PLAIN, (int)(20*Main.getMagnification())));
        roundIndicator.setForeground(new Color(150, 0, 0));
        roundIndicator.setHorizontalAlignment(SwingConstants.CENTER);
        roundBG.add(roundIndicator);
        roundText.setText("1");
        roundText.setBounds(0,(int)(-30*Main.getMagnification()), roundIcon.getIconWidth(), roundIcon.getIconHeight());
        roundText.setFont(new Font("Algerian", Font.PLAIN, (int)(60*Main.getMagnification())));
        roundText.setForeground(new Color(150, 0, 0));
        roundText.setHorizontalAlignment(SwingConstants.CENTER);
        roundBG.add(roundText);
        bestText.setText("Best: 1");
        bestText.setBounds(0,(int)(60*Main.getMagnification()), roundIcon.getIconWidth(), roundIcon.getIconHeight());
        bestText.setFont(new Font("Algerian", Font.PLAIN, (int)(30*Main.getMagnification())));
        bestText.setForeground(new Color(150, 0, 0));
        bestText.setHorizontalAlignment(SwingConstants.CENTER);
        roundBG.add(bestText);

        //sets up the healthbar across the top of the screen and adds it
        healthbar.setText(name.getText()+", Last Son of Orcus");
        healthbar.setBounds((int)(300*Main.getMagnification()),(int)(75*Main.getMagnification()), (int)(1960*Main.getMagnification()), (int)(20*Main.getMagnification()));
        healthbar.setFont(new Font("Algerian", Font.PLAIN, (int)(15*Main.getMagnification())));
        healthbar.setForeground(new Color(255,255,255));
        healthbar.setBackground(new Color(150,0,0));
        healthbar.setOpaque(true);
        healthbar.setHorizontalAlignment(SwingConstants.CENTER);
        layers.add(healthbar, JLayeredPane.POPUP_LAYER);
        
        //sets up the left and right scroller hitboxes
        leftScroller = new JLabel();
        leftScroller.setBounds((int)(getWidth()-(200*Main.getMagnification())), (int)(-1440*Main.getMagnification()), (int)(200*Main.getMagnification()), (int)(2880*Main.getMagnification()));
        layers.add(leftScroller);
        rightScroller = new JLabel();
        rightScroller.setBounds(0, (int)(-1440*Main.getMagnification()), (int)(200*Main.getMagnification()), (int)(2880*Main.getMagnification()));
        layers.add(rightScroller);

        //creates all the terrain hitboxes and positions them correctly in the colloseum, as well as left and right mergins that prevent the player from seeing off the map
        new Roof (2970, 920, 280, 1, this);

        new Ground(0, 1280, 5120, 150, this);
        new Ground(1420, 1150, 130, 1, this);
        new Ground(2970, 890, 280, 1, this);
        
        new LeftWall(0, -1440, 180, 2880, this);
        new LeftWall(1580, 1175, 1, 260, this);
        new LeftWall(3270, 915, 1, 5, this);
        leftMargin = new JLabel();
        leftMargin.setOpaque(true);
        leftMargin.setBackground(new Color(169, 143, 106));
        leftMargin.setBounds((int)(-50*Main.getMagnification()), 0, (int)(100*Main.getMagnification()), getHeight());
        layers.add(leftMargin, JLayeredPane.PALETTE_LAYER);

        new RightWall(4940, -1440, 180, 2880, this);
        new RightWall(1390, 1175, 1, 260, this);
        new RightWall(2950, 915, 1, 5, this);
        rightMargin = new JLabel();
        rightMargin.setOpaque(true);
        rightMargin.setBackground(new Color(169, 143, 106));
        rightMargin.setBounds((int)(colosseum.getWidth()-50*Main.getMagnification()), 0, (int)(100*Main.getMagnification()), getHeight());
        layers.add(rightMargin, JLayeredPane.PALETTE_LAYER);

        //sets up the menu that is displayed when escape is pressed
        menu.setBounds(0,0,(int)(1000*Main.getMagnification()), getHeight());
        menu.setBackground(new Color(0,0,0));
        menu.setOpaque(true);
        menu.setVisible(false);
        layers.add(menu, JLayeredPane.DRAG_LAYER);
        statsInfo.setBounds((int)(100*Main.getMagnification()), (int)(100*Main.getMagnification()),(int)(800*Main.getMagnification()), (int)(getHeight()-300*Main.getMagnification()));
        statsInfo.setForeground(new Color(255,255,255));
        statsInfo.setFont(new Font("Algerian", Font.PLAIN, (int)(50*Main.getMagnification())));
        statsInfo.setHorizontalAlignment(SwingConstants.CENTER);
        menu.add(statsInfo);
        exitButton.setLocation((int)(400*Main.getMagnification()), (int)(getHeight()-100*Main.getMagnification()));
        exitButton.setForeground(new Color(255,255,255));
        exitButton.setEnabled(true);
        menu.add(exitButton);


        //runs the game and loads any save data
        Main.runGame();
        taberna = new Taberna(this, player);    
        load();
        gameOn = true;
        requestFocus();
    }

    //creates a short cutscene in which orcus speaks the given dialog
    private void orcusSpeak(int mode, int duration, String dialog){
        //creates a JLabel with the dialog
        JLabel orcusDialog = new JLabel(dialog);
        orcusDialog.setBounds(0,0,getWidth(), getHeight());
        orcusDialog.setFont(new Font("Algerian", Font.PLAIN, (int)(25*Main.getMagnification())));
        orcusDialog.setOpaque(true);
        orcusDialog.setForeground(new Color(255,255,255,0));
        orcusDialog.setBackground(new Color(0,0,0,255));
        orcusDialog.setHorizontalAlignment(SwingConstants.CENTER);
        layers.add(orcusDialog, JLayeredPane.DRAG_LAYER);

        //sets up a timer that triggers every 15 ms, changing the transparency of the text each time
        ActionListener fadeInTextListener = (ActionEvent g) -> {
            if(dialogCounter<(int)(duration/15)){//converts the requested duration into the amount of trigger required, and runs if it is yet to be fufilled
                dialogCounter++; //increases dialog counter
                //if the text is not fully opaque, it fades in by 3a every trigger
                if (transparency<=255){     
                    if(transparency<0)
                        transparency=0; 
                    orcusDialog.setForeground(new Color(255,255,255, transparency));
                    update();
                    transparency+=3;
                }
            }
            //after the counter reaches the requested timeout, the text will fade out
            else{
                transparency-=3;
                orcusDialog.setForeground(new Color(255,255,255, transparency));
                update();
                //once the text is fully invisable, this timer is stopped
                if (transparency==0){
                    dialogCounter =0;
                    orcusTimer.stop();
                    orcusDialog.setVisible(false);
                    //if the second mode (for cutscenes in between rounds) was selected, the round and gameRun timers are restarted.
                    if (mode == 2){
                        roundTimer.start();
                        Main.gameRunTimer.start();
                    }
                    //if the third mode (for death cutscenes) was selected, the taberna is now opened.
                    else if (mode ==3){
                        taberna.open();
                    }
                }
            }
        };
        orcusTimer = new Timer(15, fadeInTextListener);
        orcusTimer.start();
        //if the first mode(used for the first-time opening cutscene) was selected, other threads are put to sleep, allowing dialog to chain
        if (mode == 1){
            try {
                Thread.sleep(duration+3000);
            } catch (InterruptedException e) {
                System.out.println("ERROR AT SLEEPING IN FIRST TIME CUTSCENE: " + e);
            }
        }
        //for any other mode, the round and gameRun timers are stopped
        else{
            roundTimer.stop();
            Main.gameRunTimer.stop();
        }
    }

    //used to move every arena componant, excluding the player, creating the illusion that the player is moving whenreally everything else is; if left/right is -1, everything will scroll left, if it is 1, everything will scroll right
    public void scroll(int leftRight){
        //creates a multiplier for if the player is dashing
        if (player.isDodging()){
            leftRight *= 4;
        }
        
        //The sky is moved to the requested side by a fifth of the player's move speed, giving a more realistic effect; the coloseum, left margin, and right margin are moved to the requested side by the player's move speed
        sky.setLocation(sky.getX()+((int)((player.getMoveSpd()/5)*Main.getMagnification())*leftRight), sky.getY());
        colosseum.setLocation(colosseum.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight), colosseum.getY());
        leftMargin.setLocation(leftMargin.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight), leftMargin.getY());
        rightMargin.setLocation(rightMargin.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight), rightMargin.getY());

        //each instance of Roof is moved to the requested side by the player's move speed
        for (int i = 1; i<=Roof.getNoOfRoofs(); i++) {
            Roof roof = Roof.getWithId(i);
            if (roof != null){
                roof.setLocation(roof.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight), roof.getY());
            }
        }

        //each instance of Ground is moved to the requested side by the player's move speed
        for (int i = 1; i<=Ground.getNoOfGrounds(); i++) {
            Ground ground = Ground.getWithId(i);
            if (ground != null){
                ground.setLocation(ground.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight), ground.getY());
            }
        }

        //each instance of LeftWall is moved to the requested side by the player's move speed
        for (int i = 1; i<=LeftWall.getNoOfLeftWalls(); i++) {
            LeftWall leftWall = LeftWall.getWithId(i);
            if (leftWall != null){
                leftWall.setLocation(leftWall.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight), leftWall.getY());
            }
        }

        //each instance of RightWall is moved to the requested side by the player's move speed
        for (int i = 1; i<=RightWall.getNoOfRightWalls(); i++) {
            RightWall rightWall = RightWall.getWithId(i);
            if (rightWall != null){
                rightWall.setLocation(rightWall.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight), rightWall.getY());
            }
        }

        //each enemy is moved to the requested side by the player's move speed
        for (int i = 1; i<=Enemy.getNoOfEnemies(); i++) {
            Enemy enemy = Enemy.getWithId(i);
            if (enemy != null){
                enemy.setX(enemy.getX()+((int)(player.getMoveSpd()*Main.getMagnification())*leftRight));
            }
        }
    }
    

    //sets up the main menu buttons
    private void mainMenuButtonSetup(JButton button, int x, int y){
        //sets the button to the required size, visibility, font, and action listener, at the desired location, then adds it to the sky
        button.setBounds((int)(x*Main.getMagnification()),(int)(y*Main.getMagnification()),(int)(200*Main.getMagnification()),(int)(100*Main.getMagnification()));
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setFont(new Font("Algerian", Font.PLAIN, (int)(25*Main.getMagnification())));
        button.addActionListener(this);
        sky.add(button);
    }

    //opens an extra menu(controls/credits)
    private void openExtraMenu(JLabel text){
        //creates and adds the back button
        JButton back = new JButton("Return");
        back.setBounds((int)(getWidth()/2-150*Main.getMagnification()),(int)(-150*Main.getMagnification()), (int)(300*Main.getMagnification()), (int)(100*Main.getMagnification()));
        back.setFont(new Font("Algerian", Font.PLAIN, (int)(40*Main.getMagnification())));
        back.setForeground(new Color(0, 0, 0));
        back.setBackground(new Color(225, 251, 250));
        back.setBorderPainted(false);
        back.setFocusPainted(false);
        back.setHorizontalAlignment(SwingConstants.CENTER);
        add(back);
        back.setEnabled(false);
        back.addActionListener((ActionEvent e) -> {
            //when the back button is pressed, it is disabled and the menues are shifted upwards until the main menu is fully on screen, the extra menu then becomes invisible and the timer is stopped
            back.setEnabled(false);
            ActionListener menuReturnListener = (ActionEvent g) -> {
                sky.setLocation(0, sky.getY()-20);
                text.setLocation(0, text.getY()-20);
                back.setLocation(back.getX(), back.getY()-20);
                update();
                if (sky.getY()<=0){
                    menuMovementTimer.stop();
                    sky.setLocation(0,0);
                    text.setVisible(false);
                    back.setVisible(false);
                    newGameButton.setEnabled(true);
                    continueButton.setEnabled(!doNotEnableContinue);
                    controlsButton.setEnabled(true);
                    exitButton.setEnabled(true);
                    creditsButton.setEnabled(true);
                }
            };
            menuMovementTimer = new Timer(15, menuReturnListener);
            menuMovementTimer.start();  
        });
        ActionListener skyDownListener = (ActionEvent g) -> {
            //the sky and newly created menu shift downwards until the sky is fully off screen, then the timer stops
            sky.setLocation(0, sky.getY()+20);
            text.setLocation(0, text.getY()+20);
            back.setLocation(back.getX(), back.getY()+20);
            update();
            if (sky.getY()>=getHeight()){
                back.setEnabled(true);
                menuMovementTimer.stop();
            }
        };
        menuMovementTimer = new Timer(15, skyDownListener);
        menuMovementTimer.start();
    }

    //runs when a round is finished; plays a cutscene sets up the next round
    private void roundFinished(){
        //selects a random quote for orcus to say, then run it through orcusSpeak
        String [] dialog = {"Well done, " + name.getText() + ".", "You've got their attention. Now keep it.", "They see your strength. ", "You won't let me down, right, " + name.getText() + "?", "Pluto will feel that one.", "You keep slaying and i'll keep summoning. The plan is working.", "Hear that? They fear you, as it should be.", "You really are a monster, " + name.getText() + ". Good.", "Show them that punishment is eternal.", name.getText() + ", you must remain powerful. Never cease to show them our wrath."};
        orcusSpeak(2, 3000, dialog[(int)(Math.random()*dialog.length)]);

        //resets the timer to the new time
        secRemaining = 15+round*5;
        timerText.setText("" + secRemaining);
        round++;
        roundText.setText("" + round);
        if(round>Integer.parseInt(bestText.getText().substring(6))){
            bestText.setText("Best: " + round);
        }

        //resets the sky, colosseum, left margin, right margin, and player
        sky.setLocation(0,0);
        colosseum.setLocation(0,0);
        leftMargin.setBounds((int)(-50*Main.getMagnification()), 0, (int)(100*Main.getMagnification()), getHeight());
        rightMargin.setBounds((int)(colosseum.getWidth()-50*Main.getMagnification()), 0, (int)(100*Main.getMagnification()), getHeight());
        player.reset();

        //resets each instance of Roof
        for (int i = 1; i<=Roof.getNoOfRoofs(); i++) {
            Roof roof = Roof.getWithId(i);
            if (roof != null){
                if (this.getBounds().intersects(roof.getBounds())){
                    roof.reset();
                }
            }
        }
        
        //resets each instance of Ground
        for (int i = 1; i<=Ground.getNoOfGrounds(); i++) {
            Ground ground = Ground.getWithId(i);
            if (ground != null){
                ground.reset();
            }
        }

        //resets each instance of LeftWall
        for (int i = 1; i<=LeftWall.getNoOfLeftWalls(); i++) {
            LeftWall leftWall = LeftWall.getWithId(i);
            if (leftWall != null){
                leftWall.reset();
            }
        }

        //resets each instance of RightWall
        for (int i = 1; i<=RightWall.getNoOfRightWalls(); i++) {
            RightWall rightWall = RightWall.getWithId(i);
            if (rightWall != null){
                rightWall.reset();
            }
        }

        //resets each enemy
        for (int i = 1; i<=Enemy.getNoOfEnemies(); i++) {
            Enemy enemy = Enemy.getWithId(i);
            if (enemy != null){
                enemy.reset();
            }
        }

        //summons random new monster, except for on rounds 2 and 3, where it is set to a Harpy on round 2 to eliminate remaining safezones and a Gorgon on round 3 to prevent camping
        switch (round) {
            case 2 -> new Harpy(4000, 700, (int)(Math.random()*8+10), (int)(Math.random()*2) == 0, this);
            case 3 -> new Gorgon(this, player);
            default -> {
                switch((int)(Math.random()*(3)+1)){
                    case 1 -> new Minotaur(this);
                    case 2 -> new Harpy(this);
                    case 3 -> new Gorgon(this, player);
                }
            }
        }

        taberna.setAureus(taberna.getAureus()+((round-1)*5));//Aureus are added for the round
        save(); //the game is saved
    }

    //triggers when the player dies, incidcating the end of the current run; sets up for the next run
    public void runFinished(){
        //plays a cutscene in which orcus says something that indicates you died
        String [] dialog = {"You must persevere. This one mishap shoulden't halt your momentum.", "You have disappointed me, "+ name.getText() + "...", "Your losing their grasp, "+ name.getText() + ".", "DONT FAIL ME AGAIN OR YOU WILL FACE THE CONSEQUENCES"};
        orcusSpeak(3, 3000, dialog[(int)(Math.random()*dialog.length)]);
    
        //resets the round to one
        round = 1;
        secRemaining = 20;
        timerText.setText(""+secRemaining);
        roundText.setText(""+1);

        //resets the sky, colosseum, left margin, right margin, and player
        sky.setLocation(0,0);
        colosseum.setLocation(0,0);
        leftMargin.setBounds((int)(-50*Main.getMagnification()), 0, (int)(100*Main.getMagnification()), getHeight());
        rightMargin.setBounds((int)(colosseum.getWidth()-50*Main.getMagnification()), 0, (int)(100*Main.getMagnification()), getHeight());
        player.reset();

        //resets each instance of Roof
        for (int i = 1; i<=Roof.getNoOfRoofs(); i++) {
            Roof roof = Roof.getWithId(i);
            if (roof != null){
                roof.reset();
            }
        }
        
        //resets each instance of Ground
        for (int i = 1; i<=Ground.getNoOfGrounds(); i++) {
            Ground ground = Ground.getWithId(i);
            if (ground != null){
                ground.reset();
            }
        }

        //resets each instance of LeftWall
        for (int i = 1; i<=LeftWall.getNoOfLeftWalls(); i++) {
            LeftWall leftWall = LeftWall.getWithId(i);
            if (leftWall != null){
                leftWall.reset();
            }
        }

        //resets each instance of RightWall
        for (int i = 1; i<=RightWall.getNoOfRightWalls(); i++) {
            RightWall rightWall = RightWall.getWithId(i);
            if (rightWall != null){
                rightWall.reset();
            }
        }
        
        new Minotaur (4000,1030, 20, true, this);//creates the specific round 1 minotaur
        save(); //saves the game
    }


    //used to load save data when the game is re-opened
    private void load(){
        try (BufferedReader br = new BufferedReader(new FileReader("saveData.txt"));){
            if(br.readLine()!=null){ //ensures the save data file is not empty
                
                //sets the highscore and name
                bestText.setText("Best: " + Integer.valueOf(br.readLine()));
                name.setText(br.readLine());
                healthbar.setText(name.getText() + ", Last Son of Orcus");

                if (br.readLine() != null){ //checks if there is more save data
                    //sets player's Aureus balnce, all stats, current round, and current hp
                    taberna.setAureus(Integer.parseInt(br.readLine()));
                    player.setMaxHP(Integer.parseInt(br.readLine()));
                    player.setMoveSpd(Integer.parseInt(br.readLine()));
                    player.setJumpStrength(Integer.parseInt(br.readLine()));
                    player.setDodgeStrength(Integer.parseInt(br.readLine()));
                    player.setDamageMult(Double.parseDouble(br.readLine()));
                    player.setImmunityDuration(Integer.parseInt(br.readLine()));
                    player.setBlockRate(Integer.parseInt(br.readLine()));
                    player.setDamage(Integer.parseInt(br.readLine()));
                    round = Integer.parseInt(br.readLine());
                    roundText.setText("" + round);
                    secRemaining = 15+round*5;
                    timerText.setText("" + secRemaining);
                    player.setHp(Integer.parseInt(br.readLine()));
                    healthbar.setSize(((int)(1960*Main.getMagnification()))* (player.getHp())/player.getMaxHp(), (int)(20*Main.getMagnification()));
                }
                br.readLine();//discards a line
                while (br.readLine() != null){//runs for every enemy written into the file
                    switch(br.readLine()){
                        //creates the decided enemy at the loccation given and with the required stats
                        case "Minotaur" -> new Minotaur(Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()) == 0, this);
                        case "Harpy" -> new Harpy(Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()) == 0, this);
                        case "Gorgon" -> new Gorgon(Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()), Integer.parseInt(br.readLine()), this, player);
                    }
                }
            }
        } 
        catch (IOException e) {
            System.out.println("ERROR AT CATCH WHEN LOADING: " + e);
        }
    }

    //used to save data for if the game is closed
    private void save(){
        try {
            //deletes the outdated information
            File file = new File("saveData.txt");
            file.delete();
            try (PrintWriter pw = new PrintWriter(new FileWriter(file, true))) {
                //writes the player's highscore, name, Aureus balance, all stats, current round, and current hp
                pw.println("\n"+bestText.getText().substring(6));
                pw.println(name.getText());
                pw.println("\n"+taberna.getAureus()+"\n"+player.getMaxHp()+"\n"+player.getMoveSpd()+"\n"+player.getJumpStrength()+"\n"+player.getDodgeStrength()+"\n"+player.getDamageMult()+"\n"+player.getImmunityDuration()+"\n"+player.getBlockRate()+"\n"+player.getDamage());
                pw.println(round);
                pw.println(player.getHp());

                //runs for each enemy, writing their information to the file
                for (int i = 2; i<=Enemy.getNoOfEnemies(); i++) {
                    Enemy enemy = Enemy.getWithId(i);
                    if (enemy != null){
                        //prints the enemy type
                        if (enemy instanceof Minotaur){
                            pw.println("\n\nMinotaur");
                        }
                        else if (enemy instanceof Harpy){
                            pw.println("\n\nHarpy");
                        }
                        else if (enemy instanceof Gorgon){
                            pw.println("\n\nGorgon");
                        }

                        pw.print(enemy.getOriginalX() + "\n" + enemy.getOriginalY() + "\n" + enemy.getMoveSpd()); //prints their spawn location and move spee
                        //for Harpies and Minotaurs, their beggining direction is also saved
                        if(!(enemy instanceof Gorgon)){
                            if (enemy.getOriginalyMovingLeft() == true){
                                pw.print("\n0");
                            }
                            else{
                                pw.print("\n1");
                            }
                        }
                    }
                }
            }
        } 
        catch (IOException e){
            System.out.println("ERROR AT CATCH WHEN SAVING: " + e);
        }
    }


    //uses recursion and polymorphism to return the desired amount of HTML-compatible spaces
    private String indent(int length){
        if(length <= 0){
            return "";
        }
        return indent("&nbsp;", length-1);
    }
    private String indent(String Blank, int length){
        if(length <= 0){
            return Blank;
        }
        return indent(Blank+"&nbsp;", length-1);
    }

    //grants access to the player
    public void passPlayer(Player player){
        this.player = player;
    }

    //repaints and revalidates the screen
    public final void update(){
        revalidate();
        repaint();
    }    

    //setter method
    public void setInTaberna(boolean inTaberna){
        this.inTaberna = inTaberna;
    }
}