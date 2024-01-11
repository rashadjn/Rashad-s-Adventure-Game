import java.io.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.geom.RoundRectangle2D;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
 
/****************************************************************
 * class with the main method and "instantiates" the 
 *       JFrame with all components.
 ****************************************************************/   
public class RashadsAdventureGame9
{ 
   static final long serialVersionUID = 1;
   
   public static void main(String args[]) throws Exception 
   { 
      AdventureGame frame = new AdventureGame();
      frame.setLocationRelativeTo(null);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.beginGame();
   } //end main method
} //end class 
  
 
  
  
/****************************************************************
 * Graphics Adventure Game Class
 ****************************************************************/    
class AdventureGame extends JFrame implements ActionListener
{
   ////////////////////////// FIELDS ////////////////////////////
   static final long serialVersionUID = 1;
   
   // Picture area details
   BufferedImage buffer;   // instance variable for double buffering
   final int WIDTH = 1250, HEIGHT = 700;   //to fit with resolution of monitor
   int picWidth;
   Clip clip;
   
   // Set up fonts for the picture area
   Font chillerFont = new Font("Chiller", Font.PLAIN, 48); 
   Font smallArialFont = new Font("Arial", Font.BOLD, 20); 
   Font largeArialFont = new Font("Arial", Font.BOLD, 40); 
   
   // GUI components that will need to be accessed from many methods
   JPanel interactionPanel;
   String[] choices;
   JComboBox<String> choiceComboBox;
   JTextArea storyTextArea, inventoryTextArea;
   JPanel picturePanel;
   
   String[] collectables = {"sword", "berries", "key"};
   boolean[] haveCollected= {false, false, false,};
   
   
   
   ///////////////////// END FIELDS ////////////////////////////
   
      
     
   /*************************************************************************
   *     Constructor                                                        *
   *     Builds the main window and all of the components that we see.      *
   *************************************************************************/ 
   public AdventureGame()
   {
      super ("Rashads Adventure");
      int interactionPanelWidth = 400;
      picWidth = WIDTH-interactionPanelWidth;
      buffer = new BufferedImage (picWidth-5,HEIGHT +15, BufferedImage.TYPE_INT_RGB);
    ///////////////////////////// picturePanel /////////////////////////////////////////
    // picutre for stuff on the left pannel
      picturePanel = new JPanel();
      picturePanel.setDoubleBuffered(true);
    //////////////////////// picture panel end /////////////////////////////////////////////
    
    /////////////////////////Interraction Panel ///////////////////////////////////////////
    // interactive components for the right side
      interactionPanel = new JPanel();
      interactionPanel.setLayout(new BorderLayout(5,10));
      interactionPanel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
      
      
    // choicePanel: combo box
      JPanel choicePanel = new JPanel();
    
      //Titled border for combo box
      Border blackline = BorderFactory.createLineBorder(Color.black);
      TitledBorder choiceTitle = BorderFactory.createTitledBorder(blackline, "    What do you want to do?    ");
      choicePanel.setBorder(choiceTitle);
      String[] tempStrArray = {"  ", "Start the adventure.", "Quit. "};
      choices = tempStrArray;
      choiceComboBox = new JComboBox<>(choices);
      choiceComboBox.setFont(new Font("Serif", Font.PLAIN, 20));
      choiceComboBox.setEditable(false);
      choiceComboBox.setPreferredSize(new Dimension(interactionPanelWidth-40, 30));
      choiceComboBox.addActionListener(this);
      choicePanel.add(choiceComboBox);
      
      
      // Green area with story
      storyTextArea = new JTextArea();
      storyTextArea.setBackground(new Color(119,247,200));
      storyTextArea.setMargin(new Insets(10,10,10,10) );
      storyTextArea.setFont(new Font("Serif" , Font.PLAIN, 20));
      storyTextArea.setLineWrap(true);
      storyTextArea.setWrapStyleWord(true);
      storyTextArea.setEditable(false);
      storyTextArea.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(5,5,5,5),BorderFactory.createRaisedBevelBorder()));
      
           
      
      
      // inventory list
      inventoryTextArea = new JTextArea();
      inventoryTextArea.setMargin( new Insets(10,10,10,10) );
      inventoryTextArea.setFont(new Font("Serif", Font.PLAIN, 20));
      inventoryTextArea.setLineWrap(true);
      inventoryTextArea.setWrapStyleWord(true);
      inventoryTextArea.setEditable(false);
      TitledBorder inventoryTitle = BorderFactory.createTitledBorder(blackline, "inventory");
      inventoryTextArea.setBorder(inventoryTitle);
      inventoryTextArea.setMinimumSize(new Dimension(interactionPanelWidth-40, 80));
      setInventory();
      
      interactionPanel.add(choicePanel, BorderLayout.PAGE_START);
      interactionPanel.add(storyTextArea, BorderLayout.CENTER);
      interactionPanel.add(inventoryTextArea, BorderLayout.PAGE_END);
      //interactionPanel.add(nextBtn, BorderLayout.PAGE_END);
      
    //////////////////////////interaction panel end////////////////////////////////////
    
    
    ////////////////////////// contentPane /////////////////////////////////////
    // set sizes and borders of both panels 
      picturePanel.setPreferredSize(new Dimension(WIDTH-interactionPanelWidth,HEIGHT));
      picturePanel.setMinimumSize(new Dimension(WIDTH-interactionPanelWidth,HEIGHT));
      picturePanel.setBorder(BorderFactory.createCompoundBorder(
                              BorderFactory.createEmptyBorder(10,10,10,10),
                              BorderFactory.createLoweredBevelBorder()));
                              
      interactionPanel.setPreferredSize(new Dimension(interactionPanelWidth,HEIGHT));
      interactionPanel.setMinimumSize(new Dimension(interactionPanelWidth,HEIGHT));
      interactionPanel.setBorder(BorderFactory.createCompoundBorder(
                                    BorderFactory.createEmptyBorder(10,10,10,10),
                                    BorderFactory.createLoweredBevelBorder()));
   
                              
    //set and identifier for the screen area and place the panels on it
      JPanel pane= (JPanel) getContentPane();
      pane.setLayout(new BoxLayout(pane,BoxLayout.LINE_AXIS));  //left to right
      pane.add(picturePanel);
      pane.add(interactionPanel);
    //////////////////////////end contentPane ////////////////////////////////////                   
   
    //A Make the jFrame visible on the screen
      setSize(WIDTH, HEIGHT);
      pack();
      setLocationRelativeTo(null);
      setVisible(true);
   
   }
   /****************************************************************************************
   * Method : drawScreen
   *
   * Purpose: Place picture from buffer (memory) onto the screen.
   ****************************************************************************************/
   public void drawScreen()
   {
      Graphics2D g = (Graphics2D)this.getGraphics();
      g.drawImage(buffer,10,10,this);
      Toolkit.getDefaultToolkit().sync();
      g.dispose();
   }
   
    /***********************************************************************************
   * Method: paint()
   *
   * Purpose: To display the picture when needed
   ***********************************************************************************/
   public void paint(Graphics g)
   {
      try
      { 
         Thread.sleep(1000);
         drawScreen();
         interactionPanel.repaint();
      }
      catch(Exception e)
      {
         System.out.println("Problem painting the screen: \n");
         e.printStackTrace();
      }
   }
      
   /**************************************************************************************
   * Method: drawintroPicture
   *
   * Purpose: Use my wrapper method to drwa the first picture that my game will display
   
   *************************************************************************************/
   public void drawIntroPicture(Graphics2D b)
   {
      
      rectangle(b, new Color(87, 72, 2),25,200,50,500,0);
      rectangle(b, new Color(87, 72, 2),775,200,50,500,0);
      rectangle(b, new Color(87, 72, 2),275,200,50,500,0);
      rectangle(b, new Color(87, 72, 2),545,200,50,500,0);

            
      oval(b,new Color(16, 160, 45), -50, 150, 225, 225, 0);
      oval(b,new Color(16, 160, 45), 700, 150, 225, 225, 0);
      oval(b,new Color(16, 160, 45), 460, 150, 225, 225, 0);
      oval(b,new Color(16, 160, 45), 200, 150, 225, 225, 0);
      
      triangle(b, new Color(16, 160, 45), 0, 425, 20, 30, 140, 800, 140);


      
      write(b, new Color(16, 160, 45), "Arial", Font.ITALIC | Font.BOLD, 80, "Jungle Adventure!", 85, 135);
   
   }
   
   public void oval(Graphics2D b,Color c, int x, int y, int w, int h, int strokeSize)
   {
      b.setColor(c);
   
      if (strokeSize > 0)
      {
         b.setStroke(new BasicStroke(strokeSize));
         b.drawOval(x,y,w,h); //outline
      }
      else
         b.fillOval(x,y,w,h); //filled in rectangle
   
   }
   
   
   
   public void write(Graphics2D b, Color c, String font, int fontStyle, int fontSize, String msg, int x, int y)
   {
      b.setColor(c);
      Font newFont = new Font(font, fontStyle, fontSize);
      b.setFont(newFont);
      b.drawString(msg, x, y);
   }

   
   public void triangle(Graphics2D b, Color c, int strokeSize, int x1, int y1, int x2, int y2, int x3, int y3)
                           
   {
      b.setColor(c);
      int[] xValues = {x1, x2, x3};
      int[] yValues = {y1, y2, y3};
   
      if(strokeSize > 0)
         b.fillPolygon(xValues, yValues, 3);          
         
      else
      {
         b.setStroke(new BasicStroke(strokeSize));
         b.drawPolygon(xValues, yValues, 3);
      }
   }
     // rectangle(b,Color.blue,2,22,picWidth-10,HEIGHT-10,5);
   //      rectangle(b,Color.yellow,100,100,200,100,0);
   //      rectangle(b,Color.cyan,500,100,200,100,40);
   
   // b.setColor(Color.blue);
   //    b.setStroke(new BasicStroke(5));
   //    b.drawRect(2,22,picWidth-10,HEIGHT-10);
   //    
   //    b.setColor(Color.yellow);
   //    b.fillRect(100,100, 200, 100);
   //    
   //    b.setColor(Color,cyan);
   //    b.setStroke(new BasicStroke(40));
   //    b.drawRect(500,100 , 200, 100);   
   
  
   public void rectangle(Graphics2D b,Color c, int x, int y, int w, int h, int strokeSize)
   {
      b.setColor(c);
   
      if (strokeSize > 0)
      {
         b.setStroke(new BasicStroke(strokeSize));
         b.drawRect(x,y,w,h); //outline
      }
      else
         b.fillRect(x,y,w,h); //filled in rectangle
   
   }
   
        
   

   /************************************************************************************
   *  The adventure begins here
   ************************************************************************************/
   public void beginGame()
   {
      Graphics2D b = buffer.createGraphics();
      drawIntroPicture(b);
      b.dispose();
   }
   
/**************************************************************************************
*   Method: add background picture
*
*   Purpose: Scale an image to the size of the picture area then place it in th buffer.
****************************************************************************************/
   public void  addBackgroundPicture(Graphics2D b, String picFileName)
   {
      addPicture(b, picFileName, picWidth, HEIGHT, 0, 22);
   }  
   
/********************************************************************************************
*  Method: addPicture
*
*  Purpose: Place an image in a specific x, y position. Scale picture if width 
*     add height values are non-zero.
********************************************************************************************/
   public void addPicture(Graphics2D b, String picFileName, int width, int height, int x, int y)
   {
      BufferedImage img = null;
      Image newImage = null;
      play(clip, "JungSound.wav");
      try
      {
         img = ImageIO.read(getClass().getResource("res/"+picFileName ));
         if (width != 0 && height != 0)
         {
            newImage = img.getScaledInstance(width, height, Image.SCALE_DEFAULT);
            b.drawImage(newImage, x,y,  this);
         }
         else
            b.drawImage(img, x, y, this);
      }
      catch(Exception e)
      {
         e.printStackTrace();
         String msg1 = "Runtime error caught in addpicture: \""+picFileName+"\" not found or read properly. ";
         String msg2 = "Make sure that your picture file is in the same folder as your .java file.";
         System.out.println(msg1+"\n"+msg2);
         write(b, Color.ORANGE, "Arial", Font.BOLD, 20,msg1, 20, 50 );
         write(b, Color.ORANGE, "Arial", Font.BOLD, 20,msg2, 20, 75);
      
      }
   }






/*******************************************************************************************
*  method: actionPerformed
*  purpose: This method MUST be added when the ActionListener interface
*  is implemented".
*******************************************************************************************/

   public void actionPerformed(ActionEvent e)
   {
      if (e.getSource() == choiceComboBox)
      {
         String selected = choiceComboBox.getSelectedItem().toString();
         if (selected.equals("Start the adventure."))
            System.out.println("Start the adventure!"); 
            outside();
       
         if (selected.equals("Quit. "))
         {
            System.out.println("Quit:(");
            System.exit(0); 
            }
            
         else if(selected.equals("Go into the jungle."))
            Jungle();
            
         if(selected.equals("Go home."))
          {
            System.out.println("Quit:(");
            System.exit(0); 
            }
            
         else if(selected.equals("Pick up sword and fight!"))
         {
            take("sword");
            TigerDead();
        }
         else if(selected.equals("Look for berries"))
         {
            take("berries");           
            Berries();
         }
       else if(selected.equals("Run away :("))        
            TigerChase();
       else if(selected.equals("Pick up sword and fight!"))
          {
            take("sword");
            TigerDead();
        }
       else if(selected.equals("Get eaten and die"))
         {
            System.out.println("You were eaten :(");
            System.exit(0); 
            }
       else if(selected.equals("Keep exploring!"))
         DeadEnd();
       else if(selected.equals("Turn around"))
         {
            take("berries");           
            Berries();
         }
  
       else if(selected.equals("Fall into ditch and die"))
         {
            System.out.println("You fell and died :(");
            System.exit(0); 
            }
       else if(selected.equals("Look for shelter"))
         HiddenCave();
       else if(selected.equals("Pick up key and enter cave."))
         {
            take("key");           
            GoldRoom();
         }
       else if(selected.equals("Forever live on the outside of the cave."))
         HiddenCave();
       else if(selected.equals("Sleep on the floor"))
         Snake();
       else if(selected.equals("Get bit by the snake!"))
         {
            System.out.println("The snake was poisonous and ended up killing you :(");
            System.exit(0); 
            }      
             
             
   }
}     
/**********************************************************************************************************
*method: outside
**********************************************************************************************************/
   public void outside()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "junglePic.jpg", picWidth, HEIGHT, 0,22);
      
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("You are right outside of a jungle. Do you choose to enter the adventure of a lifetime, or go home and end the game :( ");
         String[] newItems={" ", "Go into the jungle.", "Go home."};
         setChoices(newItems);
      }
   
      drawScreen();
      b.dispose();
   
   
   }
   
   /**********************************************************************************************************
*method: Jungle
**********************************************************************************************************/
   public void Jungle()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "tiger2.JPG", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("You are now in the jungle and see a ferocious tiger. However, there is a sword on the ground! Would you like to fight the tiger, or run as far as you can.  ");
         String[] newItems={" ", "Pick up sword and fight!", "Run away :("};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }
   
  /**********************************************************************************************************
*method: TigerDead
**********************************************************************************************************/
   public void TigerDead()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "TigerDead.JPG", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("Congratulations you have killed the tiger! Would you like to keep exploring in the jungle, or stop and look for berries to eat");
         String[] newItems={" ", "Keep exploring!", "Look for berries"};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }

   
   
   
   
  
  /**********************************************************************************************************
*method: Berries
**********************************************************************************************************/
   public void Berries()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "Berry.jpg", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("Yay you have found a berry tree and collected some berries for when you get hungry. However, it is getting dark and you should look for some shelter. What would you like to do");
         String[] newItems={" ", "Look for shelter", "Sleep on the floor"};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }

  
   
   
/**********************************************************************************************************
*method: TigerChase
**********************************************************************************************************/
   public void TigerChase()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "chase.jpg", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("Oh no the tiger is chasing you! You must get the sword quickly before it eats you.");
         String[] newItems={" ", "Pick up sword and fight!", "Get eaten and die"};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }

  
   
/**********************************************************************************************************
*method: DeadEnd
**********************************************************************************************************/
   public void DeadEnd()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "dead.jpg", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("You have run into a dead end and must turn around before you fall into the ditch");
         String[] newItems={" ", "Turn around", "Fall into ditch and die"};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }   
   
/**********************************************************************************************************
*method: HiddenCave
**********************************************************************************************************/
   public void HiddenCave()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "cave.jpg", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("While looking for shelter, you stumble apon a hidden cave and notice a key near the entrance. What would you like to do.");
         String[] newItems={" ", "Pick up key and enter cave.", "Forever live on the outside of the cave."};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }   
   
   
/**********************************************************************************************************
*method: GoldRoom
**********************************************************************************************************/
   public void GoldRoom()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "gold.jpg", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("Congratulations you have found the rarest cave in the entire jungle! This is a cave completely made of gold and you can now live here for as long as you want. This is the end of your adventure!!! ");
         String[] newItems={" "};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }   
 
/**********************************************************************************************************
*method: Snake
**********************************************************************************************************/
   public void Snake()
   {
      Graphics2D b = buffer.createGraphics();
   //input:  Graphics2D b, String picFileName, int width, int height, int x, int y
      addPicture(b, "snake.jpg", picWidth, HEIGHT, 0,22);
      
      
      {   
      
      //add stroy line and choices for player
         storyTextArea.setText("While attempting to sleep you hear a snake hissing near you. You now have to decide between finding shelter or being bit by the poisonous snake.");
         String[] newItems={" ", "Look for shelter", "Get bit by the snake!"};
         setChoices(newItems);
      }
      
      
   
      drawScreen();
      b.dispose();
   
   
   }   
   
 
   

/**********************************************************************************************************
* Method: setChoices
*
* Purpose: Create new set of choices in combo box
***********************************************************************************************************/
   public void setChoices(String[] choices)
   {
      choiceComboBox.removeAllItems();
      for (int i = 0 ; i<choices.length; i++)
         choiceComboBox.addItem(choices[i]);
   }



     
/**********************************************************************************************************
*method: inventory
*
*Purpose: Show all collected and needed items on the screen.
**********************************************************************************************************/
   public void setInventory()
   {
      String collected = "Collected: ";
      String missing = "Need: ";
   
   
   // sort items into appropriate list: collected or missing 
      for(int i = 0; i<collectables.length ; i++)
         if (haveCollected[i])
            if (collected.equals("Collected: "))
               collected = collected+ collectables[i];
            else
               collected = collected+", "+ collectables[i];  
         else
            if (missing.equals("Need: "))
               missing = missing+ collectables[i];
            else
               missing = missing+", "+ collectables[i];
         
      inventoryTextArea.setText(collected+"\n"+missing);
      interactionPanel.repaint();
   
   }


/***********************************************************************************************************
* Method: haveItem
*
* Purpose: Returns true if we have collected this itme.
*************************************************************************************************************/ 
    public boolean haveItem(String theItem)
   {
      for (int i = 0 ; i<collectables.length ; i++)
         if (theItem.equals(collectables[i]))
            return haveCollected[i];
      return false;
   }
  
   public void take(String theItem)
   {
      for(int i = 0; i<collectables.length ; i++)
         if (theItem.equals(collectables[i]))
            haveCollected[i] = true;
         setInventory();
   }
   
   

/***********************************************************************************************************
* Method: Play a wave file*
* Purpose: Sound
*************************************************************************************************************/

   public void play(Clip myClip, String song)
   {
      try{
         AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(this.getClass().getResource("/res/"+song));
         myClip = AudioSystem.getClip();
         myClip.open(audioInputStream);
         myClip.start();
      }
      catch(Exception ex)  {
         System.out.println("Caught Error: "+ex);
         
         

      }
   
   }  




long startTime = System.currentTimeMillis();
long elapsedTime;
    long elapsedSeconds;
elapsedTime = System.currentTimeMillis() - startTime;
        elapsedSeconds = elapsedTime / 1000;
String timeAlive = String.valueOf(elapsedSeconds);
b.drawString(timeAlive, 210, 560);





}//end of class AdventureGame




