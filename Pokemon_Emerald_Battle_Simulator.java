/*
 Bingran Li, Nicholas Vadivelu, David Chen-Li
 19 January 2015
 ICS 203-02
 Summative Assignment - Pokemon Emerald Battle Simulator
 */

//REMEMBER TO GO TO PREFERENCES AND ADDITIONAL CLASS PATHS TO ADD
//HSA AND JLAYER.JAR

/*
 Current Bugs
 - HP bar changes colour randomly sometime
 - Something happens with opponent Pokemon's hp bar when it attacks (Occurs Sometimes)
 - Glitch with Glacia's Glalie (sprite?) (not sure needmore testing)
 
 Version 1.5.2 Changes (Nick, Bing, and David)
 - Fixed Grammar Error in message
 - Nerfed Gyarados
 
 Version 1.5.1 Changes (Bing)
 - Removed double messages displaying when inflicted with status effects
 - Removed unneccary actuons after pokemon faints
 - Changed Overheat to Tail Glow on Ninetails
 - Added element to statMessages[] to avoid ArrayOutOfBoundsException
 - Removed flickering on recieving pokemon if damage is 0
 
 Version 1.5 Changes (Nick)
 - Fixed moves hit messages, changed 0 effectiveness moves to: "It had no effect!"
 - Fixed ccol and ccol overload methods (maybe)
 - Fixed the glitch where pokemon is in battle messages shows instead of fainted message
 - Added ending screen
 - Added a frame dispose and console close
 - Added move list to preview hover
 - Buffed Kingdra's speed
 - Buffed Altaria's Defenses
 - Bugged Flygon somehow forgot
 - Added losing animation
 
 Version 1.4 Changes (Nick)
 - Changed all doubles to floats
 - Changed all ints to either short or byte
 - Nerfed Salamence's attack from 164 to -  140
 - Changed backgrounds of buttons to white for consistency
 - Fixed bug where sometimes you have the wrong buttons for situation
 
 
 Version 1.3.5 Changes (Nick)
 - Fixed red button that stays for moves
 
 Version 1.3.4 Changes (Bing)
 - Fixed reestore function, now restores atats to original state and removes status effects
 - Moved hsa folder and JLayer.jar out of Dependencies folder (Dr. Java issue)
 
 Version 1.3.3 Changes (Nick)
 - Removed null for the third and second classes to prevent null pointer exception
 
 Version 1.3.2 Changes (Nick)
 - Made extra audio null
 - Made variables null when not in use to save RAM
 
 Version 1.3.1 Changes (Nick)
 - Made FPS on beginning animation slower, reducing file size
 - Converted all low colour depth images to gif
 
 Version 1.3 Changes (Nick)
 - Re organized all dependencies and added read me
 - Created Resources Folder for all resources
 - Renamed Frames to Visual Resources
 - Renamed Audio to Audio Resources
 - Put all additional classes/ libraries (JLayer.jar and hsa) into Dependencies Folder
 - Created new folder for backgrounds, called Backgrounds
 - Renamed .java file
 - Changed all thread.sleep statements to sleeper
 - Fixed small discrepencies between 1.2 and 1.1
 - Chaned all c.print() to text()
 - Changed all c.println() to textr()
 - Removed redudant variable int choice and replaced with existing choice2
 - Added additional normal damage import just in case we add 4x effectiveness in the future
 
 Version 1.2 Changes (Bing):
 - Fixed small effectiveness display problem
 - Merged/patched discrepancies between Version 1 and Version 1.1
 - Moved all audio files inside the Audio folder
 
 Version 1.1 Changes (Nick):
 - Fixed static pokemon display (minor problem sometimes)
 - Nerfed Swampert (made level 55)
 - Buffed Milotic (replaced water pulse with body slam)
 - Buffed Walrein level; nerfed speed
 - Buffed Drake's Salamence; now faster than player's salamence
 - Buffed Wallace's Gyarados attack and speed; changed moveset to quick attack, dragon tail, aqua tail, earthquake
 - Eliminated unused files in folder
 - Eliminated unused code
 - Implemented Changelog into program itself
 - Implemented version number in game
 
 Version 1 Changes (Bing):
 - Pokemon fainting animation now faster
 - Pokemon switching animation now faster
 - During switches hp bar no longer goes full black
 - Status effect notice (BRN,FRZ,etc) placement correction
 - Sound effect changes (previously too loud)
 - Now status applies right before your turn
 */

import java.awt.*;
import hsa.Console;
// Image support
import javax.imageio.*;
import java.io.*;
import javazoom.jl.player.Player; //Need for Audio playing
import java.awt.image.BufferedImage; //need to animate image
import javax.swing.*;
import java.awt.event.*;

public class Pokemon_Emerald_Battle_Simulator implements ActionListener //Version 1.3.5
{
  static Console c;           // The output console
  static BufferedImage image, tex;
  static Graphics2D g, t;
  static Image background, upback;
  static Poke first;
  // Global variables so methods have easy access
  public static AudioPlayer Music[] = {new AudioPlayer ("Resources\\Audio Resources\\Elite Four.mp3"), new AudioPlayer ("Resources\\Audio Resources\\Champion.mp3") };
  public static Pokemon Opponent[];
  public static String oppName[] = {"Sidney", "Phoebe", "Glacia", "Drake", "Wallace"};
  public static Pokemon Player[] = new Pokemon [6];
  public static byte p; // Player's current pokemon
  //public static Image Sid,Pho,Gla,Dra,Wal,Pla; // Gifs for images of the league and player
  public static JFrame f;
  private static JButton[] movess = new JButton [4]; //holds moves
  private static JButton[] pokemonn = new JButton [6]; //holds pokemon
  private static JButton attack, swich; //options//quit, cancel
  static ImageIcon userPokes[] = new ImageIcon [6];
  static ImageIcon userPokes2[] = new ImageIcon [6];
  private static int textdelay = 30, usehp, opphp, defendhp;
  static Color opp, use, bx = new Color (231, 231, 231);
  static AudioPlayer attacksounds[] = {new AudioPlayer ("Resources\\Audio Resources\\nuller.mp3"), new AudioPlayer ("Resources\\Audio Resources\\not_effective.mp3"), new AudioPlayer ("Resources\\Audio Resources\\normaldamage.mp3"), null, new AudioPlayer ("Resources\\Audio Resources\\super_effective.mp3") }; //, new AudioPlayer ("Resources\\Audio Resources\\nuller.mp3"), new AudioPlayer ("Resources\\Audio Resources\\nuller.mp3"), new AudioPlayer ("Resources\\Audio Resources\\normaldamage.mp3"), new AudioPlayer ("Resources\\Audio Resources\\normaldamage.mp3")};
  static AudioPlayer statuser = new AudioPlayer ("Resources\\Audio Resources\\effect_recover2.mp3");
  static AudioPlayer turnsound = attacksounds [2];
  
  // Strength + weakness table - Bing Li
  // to use: weakness[defending_type][attackinging_type]
  // Type effectiveness chart
  
  public static boolean check = true, checkback = false, continuer = false;
  static char choice1 = 'z';
  static byte choice2 = -1, currpoke = 0, oldpoke = 0;
  
  //2D array is what determines effectiveness of attacks
  public static float weakness[] [] =
  {
    {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0},
    {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, (float) 0.5, 1, 1, 1, 0, 1, (float) 0.5},
    {0, 1, (float) 0.5, 1, (float) 0.5, 1, 2, 1, 1, 1, 1, (float) 0.5, 2, 2, (float) 0.5, 1, 1, 2},
    {0, 2, 1, 1, 1, (float) 0.5, 1, (float) 0.5, 1, 1, (float) 0.5, 2, 2, (float) 0.5, 1, 0, 2, 2},
    {0, 1, 2, 1, (float) 0.5, 1, (float) 0.5, 1, 1, 2, 1, 2, 1, 1, (float) 0.5, 1, 1, 1},
    {0, 1, 1, 2, 1, 1, 2, 1, (float) 0.5, 1, 1, (float) 0.5, 1, 2, 1, 1, 1, (float) 0.5},
    {0, 1, (float) 0.5, 1, 2, (float) 0.5, (float) 0.5, (float) 0.5, 1, 2, 1, 2, 1, (float) 0.5, (float) 0.5, 1, 1, (float) 0.5},
    {0, 1, 1, 1, 1, 1, 2, (float) 0.5, 1, (float) 0.5, 1, (float) 0.5, 1, 1, 1, (float) 0.5, 1, 0},
    {0, 1, 1, 1, 2, 2, (float) 0.5, 1, (float) 0.5, 0, 1, 1, 1, 1, (float) 0.5, 1, 1, 1},
    {0, 1, 2, 1, 1, 0, (float) 0.5, 2, 2, 1, 1, 2, 1, (float) 0.5, 1, 1, 1, 2},
    {0, 1, 1, 2, 1, 1, 1, 2, 1, 1, (float) 0.5, 1, 1, 1, 1, 1, 0, (float) 0.5},
    {0, 1, 2, (float) 0.5, 1, 2, 1, 1, 1, (float) 0.5, 1, 1, 2, 2, 1, 1, 1, (float) 0.5},
    {0, 1, (float) 0.5, 1, (float) 0.5, 2, 2, 1, 1, 2, 1, 1, (float) 0.5, 1, 2, 1, 1, (float) 0.5},
    {0, 1, (float) 0.5, (float) 0.5, 1, (float) 0.5, 2, (float) 0.5, 1, 1, 2, 1, 1, 1, 1, (float) 0.5, 2, (float) 0.5},
    {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, (float) 0.5},
    {0, 0, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, (float) 0.5, 1},
    {0, 1, 1, (float) 0.5, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 1, 2, (float) 0.5, 1},
    {0, 1, (float) 0.5, 1, (float) 0.5, 1, 1, 1, (float) 0.5, 1, 1, 2, 2, 1, 1, 1, 1, (float) 0.5}
  };
  // stat modifiers, depends on stages
  public static short mod[] = {33, 36, 43, 50, 60, 75, 100, 133, 166, 200, 250, 266, 300};
  
  // status "messages"
  public static String pokemonStatus[] = {"", "BRN", "FRZ", "PAR", "PSN", "SLP"};
  public static String statusMessages[] = {"", "burned", "frozen", "paralyzed", "poisoned", "asleep"};
  
  // stat "messages"
  public static String pokemonStat[] = {"", "attack", "defense", "special sttack", "special defense", "speed", "accuracy", "evade"};
  public static String statMessages[] = {" harshly fell", " fell", "", " rose", " greatly rose", " greatly rose"};
  
  // Bing Begin
  
  // Used to load images
  public static Image loadImage (String name)  //Loads image from file
  {
    Image img = null;
    try
    {
      img = ImageIO.read (new File (name));
    }
    catch (IOException e)
    {
    }
    return img;
  }
  
  
  //This method makes it easier to call upon thread.sleep
  static void sleeper (int i)
  {
    try
    {
      Thread.sleep (i);
    }
    catch (InterruptedException e)
    {
      e.printStackTrace ();
      // handle the exception...
      // For example consider calling Thread.currentThread().interrupt(); here.
    }
  }
  
  
  // Displays Pokemon Information
  public static void udisplay (Pokemon poke)
  {
    g.drawImage (poke.img, 40, 100, 235, 235, null);
    float hp = (float) poke.curhp / (float) poke.maxhp;
    short x = 330, y = 186;
    //Main information
    String message = "Lvl " + poke.level + "\t";
    g.drawString (message, 30 + x, 40 + y);
    g.drawString (poke.name, 90 + x, 40 + y);
    g.drawString (pokemonStatus [poke.status], 235 + x, 40 + y);
    g.drawString ("HP: " + poke.curhp + "/" + poke.maxhp, 165 + x, 60 + y);
    //Colour of HP bar
    if (hp > 0.5)
      use = Color.GREEN;
    else if (hp <= 0.25)
      use = Color.RED;
    else if (hp <= 0.6)
      use = Color.YELLOW;
    g.setColor (use);
    short len = (short) (219 * poke.curhp / poke.maxhp);
    if (len < 1)
      len = 1;
    //actual HP bar
    g.fillRect (58 + x, 66 + y, len, 11);
    g.setColor (Color.BLACK);
    usehp = len;
  }
  
  
  public static void edisplay (Pokemon poke)
  {
    g.drawImage (poke.img, 365, -26, 235, 235, null);
    float hp = (float) poke.curhp / (float) poke.maxhp;
    String message = "Lvl " + poke.level + "\t";
    //Information
    g.drawString (message, 30, 40);
    g.drawString (poke.name, 90, 40);
    g.drawString (pokemonStatus [poke.status], 235, 40);
    g.drawString ("HP: " + poke.curhp + "/" + poke.maxhp, 165, 60);
    //Colour of HP Bar
    if (hp > 0.5)
      opp = Color.GREEN;
    else if (hp <= 0.25)
      opp = Color.RED;
    else if (hp <= 0.6)
      opp = Color.YELLOW;
    g.setColor (opp);
    short len = (short) (219 * poke.curhp / poke.maxhp);
    if (len < 1)
      len = 1;
    //HP Bar
    g.fillRect (58, 66, len, 11);
    g.setColor (Color.BLACK);
    opphp = len;
  }
  
  
  // Display used when switching in a pokemon
  public static void edisplay1 (Pokemon poke)
  {
    float hp = (float) poke.curhp / (float) poke.maxhp;
    String message = "Lvl " + poke.level + "\t";
    //Information
    g.drawString (message, 30, 40);
    g.drawString (poke.name, 90, 40);
    g.drawString (pokemonStatus [poke.status], 235, 40);
    g.drawString ("HP: " + poke.curhp + "/" + poke.maxhp, 165, 60);
    //Colour of HP Bar
    if (hp > 0.5)
      opp = Color.GREEN;
    else if (hp <= 0.25)
      opp = Color.RED;
    else if (hp <= 0.6)
      opp = Color.YELLOW;
    g.setColor (opp);
    short len = (short) (219 * poke.curhp / poke.maxhp);
    if (len < 1)
      len = 1;
    //HP Bar
    g.fillRect (58, 66, len, 11);
    g.setColor (Color.BLACK);
    opphp = len;
  }
  
  
  public static void udisplay1 (Pokemon poke)
  {
    float hp = (float) poke.curhp / (float) poke.maxhp;
    short x = 330, y = 186;
    //Main information
    String message = "Lvl " + poke.level + "\t";
    g.drawString (message, 30 + x, 40 + y);
    g.drawString (poke.name, 90 + x, 40 + y);
    g.drawString (pokemonStatus [poke.status], 235 + x, 40 + y);
    g.drawString ("HP: " + poke.curhp + "/" + poke.maxhp, 165 + x, 60 + y);
    //Colour of HP bar
    if (hp > 0.5)
      use = Color.GREEN;
    else if (hp <= 0.25)
      use = Color.RED;
    else if (hp <= 0.6)
      use = Color.YELLOW;
    g.setColor (use);
    short len = (short) (219 * poke.curhp / poke.maxhp);
    if (len < 1)
      len = 1;
    //actual HP bar
    g.fillRect (58 + x, 66 + y, len, 11);
    g.setColor (Color.BLACK);
    usehp = len;
  }
  
  
  // Restores team to full pp and hp, removes status effects and stat buff/debuffs
  public static Pokemon[] restore (Pokemon team[])
  {
    for (byte i = 0 ; i < 6 ; i++)
    {
      team [i].curhp = team [i].maxhp;
      team [i].status = 0;
      for (byte j = 0 ; j < 4 ; j++)
        team [i].move [j].curPP = team [i].move [j].maxPP;
      for (byte j = 1 ; j < 8 ; j++)
        team [i].stage [j] = 6;
    }
    
    // De-referencing to free up memory
    Opponent = null;
    sleeper (2000);
    c.clear ();
    
    return team;
  }
  
  
  //this method allows the listening event to act like a waiting event
  public static boolean waiter ()
  {
    checkback = true;
    while (check)
    {
      try
      {
        Thread.sleep (100);
      }
      catch (InterruptedException e)
      {
        e.printStackTrace ();
        // handle the exception...
        // For example consider calling Thread.currentThread().interrupt(); here.
      }
    }
    checkback = false;
    return true;
  }
  
  
  //This method replaces print
  public static void text (String yes)
  {
    //yes =yes + " ";
    t.drawImage (upback, 0, 0, null);
    for (short i = 0 ; i < yes.length () ; i++)
    {
      c.print (yes.charAt (i));
      sleeper (textdelay);
    }
  }
  
  
  //this method replaces println
  public static void textr (String yes)
  {
    //yes += "\n";
    t.drawImage (upback, 0, 0, null);
    for (byte i = 0 ; i < yes.length () ; i++)
    {
      c.print (yes.charAt (i));
      sleeper (textdelay);
    }
    c.println ();
  }
  
  
  // Get the command from player
  public static char getCommand ()
  {
    text ("Would you like to switch out or attack? ");
    check = waiter ();
    return choice1;
  }
  
  
  // Get the choice from user
  public static byte getChoice (char cmd)
  {
    if (cmd == 's')
    { // If switch out, prompt for replacement pokemon
      // Display list of available pokemons
      
      textr ("Choose a pokemon.");
      check = waiter ();
      currpoke = choice2;
    }
    else if (cmd == 'a')
    { // if attack, prompt for move to be used
      // Display the list of moves and their pp
      
      textr ("Choose a move.");
      check = waiter ();
    }
    
    return choice2;
  }
  
  
  //calculates colour of HP bar
  public static Color ccol (Pokemon a)
  {
    Color col = Color.GREEN;
    float hp = (float) a.curhp / (float) a.maxhp;
    if (hp > 0.5)
      col = Color.GREEN;
    else if (hp <= 0.5 && hp > 0.25)
      col = Color.YELLOW;
    else if (hp <= 0.25)
      col = Color.RED;
    return col;
  }
  
  
  //overloaded version for chainging HP
  public static Color ccol (Pokemon a, float hper)
  {
    Color col = Color.GREEN;
    float hp = hper / (float) 219;
    if (hp > 0.5)
      col = Color.GREEN;
    else if (hp <= 0.5 && hp > 0.25)
      col = Color.YELLOW;
    else if (hp <= 0.25 && hp > 0.01)
      col = Color.RED;
    else
      col = Color.BLACK;
    return col;
  }
  
  
  //This displays the health bar information when in attack or other animations
  public static void displayer (Pokemon user, Pokemon opp2)
  {
    //displays the user pokemon's information
    
    //for the text
    g.setColor (Color.BLACK);
    short x = 330, y = 186;
    String message = "Lvl " + user.level + "\t";
    g.drawString (message, 30 + x, 40 + y);
    g.drawString (user.name, 90 + x, 40 + y);
    g.drawString (pokemonStatus [user.status], 235 + x, 40 + y);
    g.drawString ("HP: " + user.curhp + "/" + user.maxhp, 165 + x, 60 + y);
    //actual HP bar
    g.setColor (ccol (user, usehp));
    short len = (short) (219 * usehp / user.maxhp);
    g.fillRect (58 + x, 66 + y, len, 11);
    
    g.setColor (Color.BLACK);
    
    //Displays the opponent's information
    String message2 = "Lvl " + opp2.level + "\t";
    g.drawString (message2, 30, 40);
    g.drawString (opp2.name, 90, 40);
    g.drawString (pokemonStatus [opp2.status], 235, 40);
    g.drawString ("HP: " + opp2.curhp + "/" + opp2.maxhp, 165, 60);
    g.setColor (ccol (opp2, opphp));
    short len2 = (short) (219 * opphp / opp2.maxhp);
    g.fillRect (58, 66, len2, 11);
    g.setColor (Color.BLACK);
  }
  
  
  // Basis of attcking in battle (Self -> Enemy attack)
  public static Pokemon attack (Pokemon attacker, Move move, Pokemon defender)
  {
    // Determining type of move: status or damage
    if (move.category == 3)
    { // Status move
      // Test if the move hits first
      int prob = move.accuracy * (attacker.acc * mod [attacker.stage [6]] / 100) / (defender.evade * mod [defender.stage [7]] / 100);
      if ((byte) (Math.random () * 100) <= prob)
      {
        statuser.play ();
        defender = attack (defender, move); // Status effects
      }
      else
        textr (move.name + " missed");
      statuser.stop ();
    }
    else
    { // Damage move
      // Type effectivenes multiplyer
      float multiplier = 1;
      for (byte i = 0 ; i < defender.type.length ; i++)
      {
        multiplier *= weakness [move.type] [defender.type [i]];
        turnsound = attacksounds [(byte) (weakness [move.type] [defender.type [i]] * 2)];
      }
      
      // Effectiveness message
      String message;
      if (multiplier == 0)
        message = "It had no effect";
      else if (multiplier <= 0.5 && multiplier > 0)
        message = "It was not very effective";
      else if (multiplier >= 2)
        message = "It was super effective";
      else
        message = "The move hit";
      
      // STAB (Same type attack bonus)
      for (byte i = 0 ; i < attacker.type.length ; i++)
        if (attacker.type [i] == move.type)
        multiplier *= 1.5;
      
      // random variable between .85 and 1
      multiplier = multiplier * ((byte) (Math.random () * 15) + 85) / 100;
      
      // Test if the move hits first
      int prob = move.accuracy * (attacker.acc * mod [attacker.stage [6]] / 100) / (defender.evade * mod [defender.stage [7]] / 100);
      if ((byte) (Math.random () * 100) <= prob)
      {
        // Damage calculation depends on physical or special attack
        short damage;
        if (move.category == 1)
          damage = (short) ((((2 * (float) attacker.level + 10) / 250) * (((float) attacker.atk * mod [attacker.stage [1]] / 100) / ((float) defender.def * mod [defender.stage [2]] / 100)) * move.power + 2) * multiplier);
        else
          damage = (short) ((((2 * (float) attacker.level + 10) / 250) * (((float) attacker.spatk * mod [attacker.stage [3]] / 100) / ((float) defender.spdef * mod [defender.stage [4]] / 100)) * move.power + 2) * multiplier);
        
        // Dealing damage to pokemon and displaying message
        
        defendhp = defender.curhp;
        if (defender == Player [currpoke])
        {
          usehp = defender.curhp;
          opphp = attacker.curhp;
        }
        else if (attacker == Player [currpoke])
        {
          usehp = attacker.curhp;
          opphp = defender.curhp;
        }
        defender.damage (damage);
        textr (message + "! It dealt " + damage + " damage!");
        //player is the defender
        turnsound.play ();
        if (defender == Player [currpoke])
        {
          short y = -26;
          //Attacking animation approach
          for (short x = 365 ; x > 40 ; x -= 4)
          {
            g.drawImage (background, 0, 0, null);
            displayer (defender, attacker);
            g.drawImage (defender.img, 40, 100, 235, 235, null);
            g.drawImage (attacker.img, x, y, 235, 235, null);
            c.drawImage (image, 0, 172, null);
            sleeper (1);
            if (x % 3 == 0)
              y += 4;
          }
          //attachking animation retreat
          for (short x = 40 ; x < 365 ; x += 4)
          {
            g.drawImage (background, 0, 0, null);
            displayer (defender, attacker);
            g.drawImage (defender.img, 40, 100, 235, 235, null);
            g.drawImage (attacker.img, x, y, 235, 235, null);
            c.drawImage (image, 0, 172, null);
            sleeper (1);
            if (x % 3 == 0)
              y -= 4;
          }
          // If no effect ignore effects on defending pokemon
          if (damage != 0)
          {
            //attacking animation flicker
            for (short x = 0 ; x < 18 ; x++)
            {
              g.drawImage (background, 0, 0, null);
              displayer (defender, attacker);
              if (x % 2 == 0)
                g.drawImage (defender.img, 40, 100, 235, 235, null);
              g.drawImage (attacker.img, 365, -26, 235, 235, null);
              c.drawImage (image, 0, 172, null);
              sleeper (100);
            }
            //attacking animation hp bar change
            for (short x = (short) (219 * (float) defendhp / (float) defender.maxhp) ; x > (short) (219 * (float) defender.curhp / (float) defender.maxhp) ; x--)
            {
              g.drawImage (defender.img, 40, 100, 235, 235, null);
              g.setColor (ccol (defender, x));
              g.fillRect (58 + 330, 66 + 186, x, 11);
              g.setColor (ccol (attacker));
              g.fillRect (58, 66, opphp, 11);
              c.drawImage (image, 0, 172, null);
              sleeper (10);
              displayer (defender, attacker);
              g.setColor (Color.BLACK);
              g.fillRect (58 + 330, 64 + 186, 220, 13);
            }
          }
        }
        //opponent is the defender
        else if (attacker == Player [currpoke])
        {
          short y = 100;
          //attacking animation approach
          for (short x = 40 ; x < 365 ; x += 4)
          {
            g.drawImage (background, 0, 0, null);
            displayer (attacker, defender);
            g.drawImage (defender.img, 365, -26, 235, 235, null);
            g.drawImage (attacker.img, x, y, 235, 235, null);
            c.drawImage (image, 0, 172, null);
            sleeper (1);
            if (x % 3 == 0)
              y -= 4;
          }
          //attacking animation retreat
          for (short x = 365 ; x > 40 ; x -= 4)
          {
            g.drawImage (background, 0, 0, null);
            displayer (attacker, defender);
            g.drawImage (defender.img, 365, -26, 235, 235, null);
            g.drawImage (attacker.img, x, y, 235, 235, null);
            c.drawImage (image, 0, 172, null);
            sleeper (1);
            if (x % 3 == 0)
              y += 4;
          }
          // If no effect ignore effects on defending pokemon
          if (damage == 0)
          {
            //attaccking animation flicker
            for (short x = 0 ; x < 18 ; x++)
            {
              g.drawImage (background, 0, 0, null);
              displayer (attacker, defender);
              if (x % 2 == 0)
                g.drawImage (defender.img, 365, -26, 235, 235, null);
              g.drawImage (attacker.img, 40, 100, 235, 235, null);
              c.drawImage (image, 0, 172, null);
              sleeper (100);
            }
            //attacking animation hp bar change
            for (short x = (short) (219 * (float) defendhp / (float) defender.maxhp) ; x > (short) (219 * (float) defender.curhp / (float) defender.maxhp) ; x--)
            {
              g.drawImage (defender.img, 365, -26, 235, 235, null);
              g.setColor (ccol (defender, x));
              g.fillRect (58, 66, x, 11);
              g.setColor (ccol (attacker));
              g.fillRect (58 + 330, 66 + 186, usehp, 11);
              c.drawImage (image, 0, 172, null);
              sleeper (10);
              displayer (attacker, defender);
              g.setColor (Color.BLACK);
              g.fillRect (58, 64, 220, 13);
            }
          }
        }
        // Extra effects
        defender = attack (defender, move);
      }
      else
        textr (move.name + " missed");
      turnsound.stop ();
    }
    
    return defender;
  }
  
  
  // Basis of attcking in battle (Self -> Self status move)
  public static Pokemon attack (Pokemon attacker, Move move)
  {
    // Self-status move will always hit
    if (move.effect != 0 && attacker.curhp > 0)
    { // status effect
      if ((short) (Math.random () * 100) <= move.chance)
      {
        attacker.status = move.effect;
        if (attacker.status == 5)
          attacker.sleepCounter = (int) (Math.random () * 5) + 1; // Sleep is random from 1-6 turns
        // textr (attacker.name + " is " + statusMessages [move.effect]);
      }
    }
    if (move.stat != 0 && attacker.curhp > 0)
    { // stat changes
      if ((short) (Math.random () * 100) <= move.chance)
      {
        attacker.stage [move.stat] += move.stage;
        // Stat changes have limits, displays the stat changes too
        if (attacker.stage [move.stat] < 0)
        {
          attacker.stage [move.stat] = 0;
          textr (attacker.name + "'s " + pokemonStat [move.stat] + " can't go any lower.");
        }
        else if (attacker.stage [move.stat] > 12)
        {
          attacker.stage [move.stat] = 12;
          textr (attacker.name + "'s " + pokemonStat [move.stat] + " can't go any higher");
        }
        else
        {
          textr (attacker.name + "'s " + pokemonStat [move.stat] + statMessages [move.stage + 2]);
        }
      }
    }
    
    return attacker;
  }
  
  
  // Bing End
  public void actionPerformed (ActionEvent e)
  {
    //Attack button
    if (checkback)
    {
      if (e.getSource () == attack) //displays move options
      {
        
        f.remove (attack);
        f.remove (swich);
        for (byte y = 0 ; y < 4 ; y++)
          f.remove (movess [y]);
        for (byte y = 0 ; y < 6 ; y++)
          f.remove (pokemonn [y]);
        for (byte i = 0 ; i < 4 ; i++)
        {
          //formats each move using htmk
          movess [i].setText ("<html>" + "<p>" + Player [currpoke].move [i].name + "</p>" + "<p>" + "PP" + "  " + Player [currpoke].move [i].curPP + " / " + Player [currpoke].move [i].maxPP + "</p>" + "</html>");
          movess [i].setToolTipText ("<html>" +
                                     "<p>" + "Name: " + Player [currpoke].move [i].name + "</p>" +
                                     "<p>" + "Type: " + Player [currpoke].move [i].typenames [Player [currpoke].move [i].type] + "</p>" +
                                     "<p>" + "Category: " + Player [currpoke].move [i].cat [Player [currpoke].move [i].category] + "</p>" +
                                     "<p>" + "Power: " + Player [currpoke].move [i].power + "</p>" +
                                     "<p>" + "Accuracy: " + Player [currpoke].move [i].accuracy + "</p>" +
                                     "</html>");
          f.getContentPane ().add (movess [i]);
          if (Player [currpoke].move [i].curPP <= 0)
            movess [i].setBackground (Color.RED);
          else if (Player [currpoke].move [i].curPP > 0)
            movess [i].setBackground (Color.WHITE);
        }
        f.validate ();
        f.repaint ();
        check = false; //ends waiter loop
        choice1 = 'a'; //returns the choice
      }
      
      //Swich Button
      else if (e.getSource () == swich) //displays pokemon
      {
        f.remove (attack);
        f.remove (swich);
        for (byte i = 0 ; i < 4 ; i++)
          f.remove (movess [i]);
        for (byte i = 0 ; i < 6 ; i++)
          f.remove (pokemonn [i]);
        for (byte i = 0 ; i < 6 ; i++)
        {
          //formats buttons using html
          pokemonn [i].setIcon (userPokes2 [i]);
          pokemonn [i].setText ("<html>" + "<p>" + "</p>" + "<p>" + Player [i].name + "</p>" + "</html>");
          pokemonn [i].setToolTipText ("<html>" +
                                       "<p>" + "Level: " + Player [i].level + "</p>" +
                                       "<p>" + "HP: " + Player [i].curhp + " / " + Player [i].maxhp + "</p>" +
                                       "<p>" + "Attack: " + Player [i].atk + "</p>" +
                                       "<p>" + "Defense: " + Player [i].def + "</p>" +
                                       "<p>" + "Special Attack: " + Player [i].spatk + "</p>" +
                                       "<p>" + "Special Defense: " + Player [i].spdef + "</p>" +
                                       "<p>" + "Speed: " + Player [i].spd + "</p>" +
                                       "<p>" + "Status: " + Player [i].statuss [Player [i].status] + "</p>" +
                                       "<p>" + "Moves: " + "</p>" +
                                       "<p>" + " - " + Player [i].move [0].name + "</p>" +
                                       "<p>" + " - " + Player [i].move [1].name + "</p>" +
                                       "<p>" + " - " + Player [i].move [2].name + "</p>" +
                                       "<p>" + " - " + Player [i].move [3].name + "</p>" +
                                       "</html>");
          oldpoke = currpoke;
          if (Player [i].curhp <= 0)
            pokemonn [i].setBackground (Color.RED);
          else if (Player [i].curhp > 0)
            pokemonn [i].setBackground (Color.WHITE);
          f.getContentPane ().add (pokemonn [i]);
        }
        f.validate ();
        f.repaint ();
        check = false; //ends waiter looop
        choice1 = 's'; //returns s
      }
      
      //Moves Buttons
      for (byte i = 0 ; i < 4 ; i++)
      {
        if (e.getSource () == movess [i] && Player [currpoke].move [i].curPP > 0)
        {
          f.remove (attack);
          f.remove (swich);
          for (byte y = 0 ; y < 4 ; y++)
            f.remove (movess [y]);
          for (byte y = 0 ; y < 6 ; y++)
            f.remove (pokemonn [y]);
          f.getContentPane ().add (attack);
          f.getContentPane ().add (swich);
          //for (byte y = 0 ; y < 4 ; y++)
          //    f.remove (movess [y]);
          f.validate ();
          f.repaint ();
          check = false;
          choice2 = i;
        }
        //out of pp
        else if (e.getSource () == movess [i] && Player [currpoke].move [i].curPP <= 0)
        {
          JOptionPane.showMessageDialog (null, Player [currpoke].move [i].name + " is out of PP! Choose a different move.");
        }
      }
      
      //Pokemon Buttons
      
      for (byte i = 0 ; i < 6 ; i++)
      {
        if (e.getSource () == pokemonn [i])
        {
          currpoke = i;
          //fainted case
          if (Player [i].curhp < 0)
          {
            JOptionPane.showMessageDialog (null, Player [i].name + " is unconscious, and is unable to battle!");
          }
          //when the pokemon slected is already out
          else if (Player [oldpoke] == Player [i])
          {
            JOptionPane.showMessageDialog (null, "This pokemon is already in battle!! Choose a different pokemon.");
          }
          //good scenario
          else if (Player [i].curhp > 0) //&& Player [currpoke] != Player [i]
          {
            f.remove (attack);
            f.remove (swich);
            for (byte y = 0 ; y < 4 ; y++)
              f.remove (movess [y]);
            for (byte y = 0 ; y < 6 ; y++)
              f.remove (pokemonn [y]);
            f.getContentPane ().add (attack);
            f.getContentPane ().add (swich);
            //  for (byte y = 0 ; y < 6 ; y++)
            //    f.remove (pokemonn [y]);
            f.validate ();
            f.repaint ();
            check = false;
            choice2 = i;
          }
        }
      }
    }
  }
  
  
  // Main method - David and Nicholas
  public Pokemon_Emerald_Battle_Simulator ()
  {
    first = new Poke (); //COMMENT THIS OUT IF YOU WANT TO SKIP THE BEGINNING ANIMATION, BUT REMEMBER TO LEAVE THE Poke.n = true; UN COMMENTED
    //Nicholas Vadivelu (graphics)
    f = new JFrame ("Pokemon Emerald"); //creates new jframe for controls
    f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE); //allows u to exit the JFrame-
    f.setSize (496, 190); //sets the size of the things
    f.getContentPane ().setLayout (null); //  allows me to use absolute positioning for buttons
    
    //Poke.n = true; //COMMENT THIS OUT IF THE first = new Poke (); IS ACTIVE
    while (!Poke.n) //while the frame is open
      sleeper (1000);
    
    //Sets upo the battle
    f.setVisible (true); //make our frame true
    c = new Console ("Pokemon Emerald Battle Simulator"); //start frame
    Font font = new Font ("Georgia", Font.PLAIN, 16); //Helvetica
    Font bold = new Font ("Arial", Font.BOLD, 20); //Helvetica
    image = new BufferedImage (640, 328, BufferedImage.TYPE_INT_ARGB);
    tex = new BufferedImage (640, 172, BufferedImage.TYPE_INT_ARGB);
    t = tex.createGraphics ();
    g = image.createGraphics ();
    g.setColor (Color.BLACK);
    g.setFont (bold);
    t.setColor (Color.BLACK);
    t.setFont (font);
    background = loadImage ("Resources\\Visual Resources\\Backgrounds\\back0.jpg");
    upback = loadImage ("Resources\\Visual Resources\\Backgrounds\\upback0.jpg");
    t.drawImage (upback, 0, 0, null);
    g.drawImage (background, 0, 0, null);
    c.drawImage (image, 0, 172, null);
    
    first = null; // to conserve ram
    
    //Import all the front views of the user's pokemon
    userPokes2 [0] = new ImageIcon ("Resources\\Visual Resources\\Player\\swampert2.gif");
    userPokes2 [1] = new ImageIcon ("Resources\\Visual Resources\\Player\\aggron2.gif");
    userPokes2 [2] = new ImageIcon ("Resources\\Visual Resources\\Player\\milotic2.gif");
    userPokes2 [3] = new ImageIcon ("Resources\\Visual Resources\\Player\\absol2.gif");
    userPokes2 [4] = new ImageIcon ("Resources\\Visual Resources\\Player\\ninetales2.gif");
    userPokes2 [5] = new ImageIcon ("Resources\\Visual Resources\\Player\\salamence2.gif");
    
    //Pokemon buttons
    for (byte i = 0 ; i < 6 ; i++)
    {
      pokemonn [i] = new JButton ();
      pokemonn [i].addActionListener (this);
    }
    pokemonn [0].setBounds (0, 0, 160, 75);
    pokemonn [1].setBounds (160, 0, 160, 75);
    pokemonn [2].setBounds (320, 0, 160, 75);
    pokemonn [3].setBounds (0, 75, 160, 75);
    pokemonn [4].setBounds (160, 75, 160, 75);
    pokemonn [5].setBounds (320, 75, 160, 75);
    
    //Move buttons
    for (byte i = 0 ; i < 4 ; i++)
    {
      movess [i] = new JButton ("hi");
      movess [i].addActionListener (this);
    }
    movess [0].setBounds (0, 0, 240, 75);
    movess [1].setBounds (240, 0, 240, 75);
    movess [2].setBounds (0, 75, 240, 75);
    movess [3].setBounds (240, 75, 240, 75);
    
    //Attack Button
    attack = new JButton ("Attack");
    attack.addActionListener (this);
    attack.setBounds (0, 0, 240, 150);
    attack.setBackground (Color.WHITE);
    
    //Switch Button
    swich = new JButton ("Switch");
    swich.addActionListener (this);
    swich.setBounds (240, 0, 240, 150);
    swich.setBackground (Color.WHITE);
    
    //Adding Buttons
    f.getContentPane ().add (attack);
    f.getContentPane ().add (swich);
    f.validate ();
    f.repaint ();
    /*
     *
     HARDCODING ALL THE POKEMONS AND MOVES (hardest and most tedious part)
     All stats based on level, 0 EVs and IVs and a neutral nature
     Stats generated from: www.marriland.com/tools/stat-calculator
     
     NOTE: Creating arrays of formal classes is the same thing as with primitive data types
     
     An accuracy of 2048 means it will always hit
     
     Moves: Physical (1) or Special (2) move: Name, type, category, power, accuracy, pp
     Status move: Name, power, accuracy, pp
     */
    
    // Player - David
    Player [0] = new Pokemon ("Swampert", 55, 175, 126, 104, 98, 104, 71, 4, 9, "Resources\\Visual Resources\\Player\\swampert.gif"); //atk = 126, sp atk = 98, speed = 71
    Player [0].move [0] = new Move ("Earthquake", 9, 1, 100, 100, 10); //No effect
    Player [0].move [1] = new Move ("Hyper Beam", 1, 2, 120, 90, 5); //No effect
    Player [0].move [2] = new Move ("Hammer Arm", 3, 1, 90, 90, 10); //No effect
    Player [0].move [3] = new Move ("Surf", 4, 2, 90, 100, 15); //No effect
    
    Player [1] = new Pokemon ("Aggron", 57, 146, 130, 205, 73, 73, 62, 17, 11, "Resources\\Visual Resources\\Player\\aggron.gif");
    Player [1].move [0] = new Move ("Iron Tail", 17, 1, 100, 75, 15, 2, -1, 30); //Has a 30% chance to lower the target's Defense by 1 stage
    Player [1].move [1] = new Move ("Flash Cannon", 17, 2, 80, 100, 10, 4, -1, 10); //Has a 10% chance to lower the target's Sp. Def by 1 stage
    Player [1].move [2] = new Move ("Take Down", 1, 1, 75, 85, 20); //No effect
    Player [1].move [3] = new Move ("Double-Edge", 1, 1, 90, 100, 15); //No effect
    
    Player [2] = new Pokemon ("Milotic", 58, 178, 74, 96, 121, 150, 98, 4, 0, "Resources\\Visual Resources\\Player\\milotic.gif");
    Player [2].move [0] = new Move ("Hydro Pump", 4, 2, 120, 80, 5); //No effect
    Player [2].move [1] = new Move ("Aqua Tail", 4, 1, 90, 90, 10); //No effect
    Player [2].move [2] = new Move ("Dragon Tail", 14, 1, 75, 90, 10); //No effect
    Player [2].move [3] = new Move ("Body Slam", 1, 1, 85, 100, 15, 3, 30); //30% Chance of Paralysis
    
    Player [3] = new Pokemon ("Absol", 60, 148, 161, 77, 95, 77, 95, 16, 0, "Resources\\Visual Resources\\Player\\absol.gif");
    Player [3].move [0] = new Move ("Night Slash", 16, 1, 70, 100, 15); //No effect
    Player [3].move [1] = new Move ("Shadow Ball", 15, 2, 80, 100, 15, 4, -1, 20); //Has a 20% chance to lower the target's Sp. Def by 1 stage
    Player [3].move [2] = new Move ("Psycho Cut", 10, 1, 70, 100, 20); //No effect
    Player [3].move [3] = new Move ("Dark Pulse", 16, 2, 80, 100, 15); //No effect
    
    Player [4] = new Pokemon ("Ninetales", 60, 157, 96, 95, 102, 125, 125, 2, 0, "Resources\\Visual Resources\\Player\\ninetales.gif");
    Player [4].move [0] = new Move ("Flamethrower", 2, 2, 95, 100, 15, 1, 10); //Has a 10% chance to burn target
    Player [4].move [1] = new Move (1.0, "Tail Glow", 1, 90, 15, 1, 3, 100, 3); // Increase sp. attack by 3 stages
    Player [4].move [2] = new Move ("Fire Blast", 2, 2, 120, 85, 5, 1, 10); //Has a 10% chance to burn the target
    Player [4].move [3] = new Move ("Giga Impact", 1, 1, 120, 90, 5); //No effect
    
    Player [5] = new Pokemon ("Salamence", 59, 181, 140, 99, 134, 99, 123, 14, 5, "Resources\\Visual Resources\\Player\\salamence.gif");
    Player [5].move [0] = new Move ("Double-Edge", 1, 1, 90, 100, 15); //No effect
    Player [5].move [1] = new Move ("Dragon Claw", 14, 1, 80, 100, 15); //No effect
    Player [5].move [2] = new Move ("Giga Impact", 1, 1, 120, 90, 5); //No effect
    Player [5].move [3] = new Move ("Dragon Breath", 14, 2, 60, 100, 20, 3, 30); //Has a 30% chance to paralyze the target
    
    // Sidney - Bing
    Opponent = new Pokemon [5];
    Opponent [0] = new Pokemon ("Mightyena", 46, 120, 87, 69, 60, 60, 72, 16, 0, "Resources\\Visual Resources\\Opponents\\mightyena.gif");
    Opponent [0].move [0] = new Move ("Crunch", 16, 1, 80, 100, 15, 2, -1, 20); // Add 20% chance to lower def by 1 stage
    Opponent [0].move [1] = new Move ("Double-Edge", 1, 1, 90, 100, 15); // No pokemon
    Opponent [0].move [2] = new Move ("Tackle", 1, 1, 35, 35, 95); // No effects
    Opponent [0].move [3] = new Move ("Cut", 1, 1, 50, 95, 30); //No effect
    
    Opponent [1] = new Pokemon ("Shiftry", 48, 144, 101, 62, 91, 62, 81, 16, 6, "Resources\\Visual Resources\\Opponents\\shiftry.gif");
    Opponent [1].move [0] = new Move ("Tackle", 1, 1, 35, 35, 95); // No effects
    Opponent [1].move [1] = new Move (1.0, "Double Team", 1, 2048, 15, 7, 1, 100, 2); // Increase evade by 1 stage
    Opponent [1].move [2] = new Move (1.0, "Swagger", 1, 90, 15, 1, 1, 100, 2); // Increase attack by 1 stage
    Opponent [1].move [3] = new Move ("Extrasensory", 10, 2, 80, 100, 30); // No effect
    
    Opponent [2] = new Pokemon ("Cacturne", 46, 120, 110, 60, 110, 60, 55, 16, 6, "Resources\\Visual Resources\\Opponents\\cacturne.gif");
    Opponent [2].move [0] = new Move ("Needle Arm", 6, 1, 60, 100, 15); // No effect
    Opponent [2].move [1] = new Move ("Tackle", 1, 1, 35, 35, 95); // No effects
    Opponent [2].move [2] = new Move ("Faint Attack", 16, 1, 60, 2048, 20); // No effects
    Opponent [2].move [3] = new Move (1.0, "Cotton Spore", 6, 85, 40, 5, -2, 100, 1); // Decrease target speed by 2 stages
    
    Opponent [3] = new Pokemon ("Crawdaunt", 48, 118, 120, 86, 91, 57, 57, 16, 4, "Resources\\Visual Resources\\Opponents\\crawdaunt.gif");
    Opponent [3].move [0] = new Move (1.0, "Swords Dance", 1, 2048, 30, 1, 2, 100, 2); // Increase attack by 2 stages
    Opponent [3].move [1] = new Move ("Surf", 4, 2, 90, 100, 15); // No effect
    Opponent [3].move [2] = new Move ("Facade", 1, 1, 70, 100, 20); // No effect
    Opponent [3].move [3] = new Move ("Strength", 1, 1, 80, 100, 15); // No effect
    
    Opponent [4] = new Pokemon ("Absol", 49, 120, 87, 69, 60, 60, 69, 16, 0, "Resources\\Visual Resources\\Opponents\\absol2.gif");
    Opponent [4].move [0] = new Move ("Aerial Ace", 5, 1, 60, 2048, 20); // No effect
    Opponent [4].move [1] = new Move ("Rock Slide", 11, 1, 75, 90, 10); // No effect
    Opponent [4].move [2] = new Move (1.0, "Swords Dance", 1, 2048, 30, 1, 2, 100, 2); //Increase attack by 2 stages
    Opponent [4].move [3] = new Move ("Slash", 1, 1, 70, 100, 20); //No effect
    
    battle ((byte) 0, (byte) 0);
    Player = restore (Player);
    Opponent = null;
    AudioPlayer prebat = new AudioPlayer ("Resources\\Audio Resources\\PhoebeStartA.mp3");
    prebat.play ();
    for (short x = 1 ; x < 204 ; x++)
    {
      c.drawImage (loadImage ("Resources\\Visual Resources\\PhoebeStart\\phobs (" + (x) + ").gif"), 0, 20, null);
      sleeper (50);
    }
    prebat.stop ();
    prebat = null;
    background = loadImage ("Resources\\Visual Resources\\Backgrounds\\back2.jpg");
    upback = loadImage ("Resources\\Visual Resources\\Backgrounds\\upback2.jpg");
    t.drawImage (upback, 0, 0, null);
    g.drawImage (background, 0, 0, null);
    c.drawImage (image, 0, 172, null);
    
    //Phoebe - David
    Opponent = new Pokemon [5];
    Opponent [0] = new Pokemon ("Dusclops", 48, 96, 72, 129, 62, 129, 29, 15, 0, "Resources\\Visual Resources\\Opponents\\dusclops.gif");
    Opponent [0].move [0] = new Move ("Shadow Punch", 15, 1, 60, 2048, 20); //No effect
    Opponent [0].move [1] = new Move ("Faint Attack", 16, 1, 60, 2048, 20); // No effects
    Opponent [0].move [2] = new Move ("Body Slam", 1, 1, 85, 100, 15, 3, 20); //30% chance to paralyze target
    Opponent [0].move [3] = new Move ("Tackle", 1, 1, 35, 35, 95); // No effects
    
    Opponent [1] = new Pokemon ("Banette", 49, 121, 117, 68, 86, 66, 68, 15, 0, "Resources\\Visual Resources\\Opponents\\banette.gif");
    Opponent [1].move [0] = new Move ("Shadow Ball", 15, 2, 80, 100, 15, 4, -1, 20); //Has a 20% chance to lower the target's Special Defence by 1 stage
    Opponent [1].move [1] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    Opponent [1].move [2] = new Move ("Cut", 1, 1, 50, 95, 30); //No effect
    Opponent [1].move [3] = new Move ("Faint Attack", 16, 1, 60, 2048, 20); // No effects
    
    Opponent [2] = new Pokemon ("Sableye", 50, 110, 80, 80, 70, 70, 55, 15, 16, "Resources\\Visual Resources\\Opponents\\sableye.gif");
    Opponent [2].move [0] = new Move ("Shadow Ball", 15, 2, 80, 100, 15, 4, -1, 20); //Has a 20% chance to lower the target's Special Defence by 1 stage
    Opponent [2].move [1] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    Opponent [2].move [2] = new Move ("Night Shade", 15, 2, 50, 100, 15); //No effect
    Opponent [2].move [3] = new Move ("Faint Attack", 16, 1, 60, 2048, 20); // No effects
    
    Opponent [3] = new Pokemon ("Banette", 49, 121, 117, 68, 86, 66, 68, 15, 0, "Resources\\Visual Resources\\Opponents\\banette.gif");
    Opponent [3].move [0] = new Move ("Psychic", 10, 2, 90, 100, 10, 4, -1, 10); //10% chance to lower target's Sp. Def by 1
    Opponent [3].move [1] = new Move ("Shadow Ball", 15, 2, 80, 100, 15, 4, -1, 20); //20% chance to lower target's Sp. Def by 1
    Opponent [3].move [2] = new Move ("Thunderbolt", 8, 2, 95, 100, 15, 3, 10); //10% chance to paralyze target
    Opponent [3].move [3] = new Move ("Facade", 1, 1, 70, 100, 20); //No effect
    
    Opponent [4] = new Pokemon ("Dusclops", 51, 101, 76, 137, 66, 137, 30, 15, 0, "Resources\\Visual Resources\\Opponents\\dusclops.gif");
    Opponent [4].move [0] = new Move ("Shadow Ball", 15, 2, 80, 100, 15, 4, -1, 20); //Has a 20% chance to lower the target's Special Defence by 1 stage
    Opponent [4].move [1] = new Move ("Ice Ball", 12, 2, 95, 100, 10, 2, 10); //10% chance to freeze the target
    Opponent [4].move [2] = new Move ("Earthquake", 9, 1, 100, 100, 10); //No effect
    Opponent [4].move [3] = new Move ("Rock Slide", 11, 1, 75, 90, 10); //No effect
    
    
    battle ((byte) 1, (byte) 0);
    Player = restore (Player);
    Opponent = null;
    prebat = new AudioPlayer ("Resources\\Audio Resources\\GlaciaStartA.mp3");
    prebat.play ();
    for (short x = 1 ; x < 229 ; x++)
    {
      c.drawImage (loadImage ("Resources\\Visual Resources\\GlaciaStart\\glac (" + (x) + ").gif"), 0, 20, null);
      sleeper (50);
    }
    prebat.stop ();
    prebat = null;
    background = loadImage ("Resources\\Visual Resources\\Backgrounds\\back1.jpg");
    upback = loadImage ("Resources\\Visual Resources\\Backgrounds\\upback.jpg");
    t.drawImage (upback, 0, 0, null);
    g.drawImage (background, 0, 0, null);
    c.drawImage (image, 0, 172, null);
    
    //Glacia - David
    Opponent = new Pokemon [5];
    Opponent [0] = new Pokemon ("Sealeo", 50, 150, 65, 75, 80, 75, 50, 12, 4, "Resources\\Visual Resources\\Opponents\\sealeo.gif");
    Opponent [0].move [0] = new Move ("Cut", 1, 1, 50, 95, 30); //No effect
    Opponent [0].move [1] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    Opponent [0].move [2] = new Move ("Body Slam", 1, 1, 85, 100, 15, 3, 30); //30% chance to paralyze target
    Opponent [0].move [3] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    
    Opponent [1] = new Pokemon ("Glalie", 50, 140, 85, 85, 85, 85, 85, 12, 0, "Resources\\Visual Resources\\Opponents\\walrein.gif");
    Opponent [1].move [0] = new Move ("Crunch", 16, 1, 80, 100, 15, 2, -1, 20); //20% chance to lower the target's Def by 1
    Opponent [1].move [1] = new Move ("Icy Wind", 12, 2, 55, 95, 15, 5, -1, 100); //Lowers target's Speed by 1
    Opponent [1].move [2] = new Move ("Ice Beam", 12, 2, 95, 100, 10, 2, 10); //has a 10% chance to freeze the target
    Opponent [1].move [3] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    
    Opponent [2] = new Pokemon ("Sealeo", 52, 155, 67, 77, 83, 77, 51, 12, 4, "Resources\\Visual Resources\\Opponents\\sealeo.gif");
    Opponent [2].move [0] = new Move ("Blizzard", 12, 2, 120, 90, 5, 2, 10); //Has 10% chance to freeze the target
    Opponent [2].move [1] = new Move ("Double-Edge", 1, 1, 90, 100, 15); // No effect
    Opponent [2].move [2] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    Opponent [2].move [3] = new Move ("Body Slam", 1, 1, 85, 100, 15); //No effect
    
    Opponent [3] = new Pokemon ("Glalie", 52, 145, 88, 88, 88, 88, 88, 12, 0, "Resources\\Visual Resources\\Opponents\\glalie.gif");
    Opponent [3].move [0] = new Move ("Blizzard", 12, 2, 120, 90, 5, 2, 10); //Has 10% chance to freeze the target
    Opponent [3].move [1] = new Move ("Ice Beam", 12, 2, 95, 100, 10, 2, 10); //Has a 10% chance to freeze the target
    Opponent [3].move [2] = new Move ("Shadow Ball", 15, 2, 80, 100, 15, 4, -1, 20); //Has a 20% chance to lower the target's Sp. Def by 1
    Opponent [3].move [3] = new Move ("Cut", 1, 1, 50, 95, 30); //No effect
    
    Opponent [4] = new Pokemon ("Walrein", 59, 235, 136, 148, 154, 148, 70, 12, 4, "Resources\\Visual Resources\\Opponents\\walrein.gif");
    Opponent [4].move [0] = new Move ("Surf", 4, 2, 95, 100, 15); //No effect
    Opponent [4].move [1] = new Move ("Ice Beam", 12, 2, 95, 100, 10, 2, 10); //10% chance to freeze the target
    Opponent [4].move [2] = new Move ("Body Slam", 1, 1, 85, 100, 15, 3, 30); //Has a 30% chance to paralyze the target
    Opponent [4].move [3] = new Move ("Blizzard", 12, 2, 120, 90, 5, 2, 10); //10% chance to freeze
    
    battle ((byte) 2, (byte) 0);
    Player = restore (Player);
    Opponent = null;
    prebat = new AudioPlayer ("Resources\\Audio Resources\\DrakeStartA.mp3");
    prebat.play ();
    for (short x = 1 ; x < 217 ; x++)
    {
      c.drawImage (loadImage ("Resources\\Visual Resources\\DrakeStart\\drake (" + (x) + ").gif"), 0, 20, null);
      sleeper (50);
    }
    prebat.stop ();
    prebat = null;
    background = loadImage ("Resources\\Visual Resources\\Backgrounds\\back3.jpg");
    upback = loadImage ("Resources\\Visual Resources\\Backgrounds\\upback3.jpg");
    t.drawImage (upback, 0, 0, null);
    g.drawImage (background, 0, 0, null);
    c.drawImage (image, 0, 172, null);
    
    //Drake - David
    Opponent = new Pokemon [5];
    Opponent [0] = new Pokemon ("Shelgon", 52, 129, 103, 109, 67, 57, 57, 14, 0, "Resources\\Visual Resources\\Opponents\\shelgon.gif");
    Opponent [0].move [0] = new Move ("Rock Tomb", 11, 1, 50, 80, 10, 5, -1, 100); //Lowers target's Speed by 1
    Opponent [0].move [1] = new Move ("Dragon Claw", 14, 1, 80, 100, 15); //No effect
    Opponent [0].move [2] = new Move ("Double-Edge", 1, 1, 90, 100, 15); //No effect
    Opponent [0].move [3] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    
    Opponent [1] = new Pokemon ("Altaria", 54, 145, 80, 120, 80, 130, 91, 14, 5, "Resources\\Visual Resources\\Opponents\\altaria.gif");
    Opponent [1].move [0] = new Move ("Double-Edge", 1, 1, 90, 100, 15); //No effect
    Opponent [1].move [1] = new Move ("Dragonbreath", 14, 2, 60, 100, 20, 3, 30); //Has a 30% chance to paralyze target
    Opponent [1].move [2] = new Move ("Aerial Ace", 5, 1, 60, 2048, 20); //No effect
    Opponent [1].move [3] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    
    Opponent [2] = new Pokemon ("Kingdra", 53, 142, 105, 105, 105, 105, 125, 4, 14, "Resources\\Visual Resources\\Opponents\\kingdra.gif");
    Opponent [2].move [0] = new Move ("Cut", 1, 1, 50, 95, 30); //No effect
    Opponent [2].move [1] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    Opponent [2].move [2] = new Move ("Surf", 4, 2, 95, 100, 15); //No effect
    Opponent [2].move [3] = new Move ("Body Slam", 1, 1, 85, 100, 15, 3, 30); //Has a 30% chance to paralyze target
    
    Opponent [3] = new Pokemon ("Flygon", 53, 147, 111, 89, 89, 89, 111, 9, 14, "Resources\\Visual Resources\\Opponents\\flygon.gif");
    Opponent [3].move [0] = new Move ("Dragonbreath", 14, 2, 60, 100, 20, 3, 30); //Has a 30% chance to paralyze the target
    Opponent [3].move [1] = new Move ("Flamethrower", 2, 2, 95, 100, 15, 1, 10); //Has a 10% chance to burn the target
    Opponent [3].move [2] = new Move ("Crunch", 16, 1, 80, 100, 15, 2, -1, 20); //Has a 20% chance to lower the target's Def by 1
    Opponent [3].move [3] = new Move ("Earthquake", 9, 1, 100, 100, 10); //No effect
    
    Opponent [4] = new Pokemon ("Salamence", 55, 169, 153, 93, 126, 93, 124, 14, 5, "Resources\\Visual Resources\\Opponents\\salamence2.gif");
    Opponent [4].move [0] = new Move ("Dragon Claw", 14, 1, 80, 100, 15); //No effect
    Opponent [4].move [1] = new Move ("Flamethrower", 2, 2, 95, 100, 15, 1, 10); //Has a 10% chance to burn the target
    Opponent [4].move [2] = new Move ("Crunch", 16, 1, 80, 100, 15, 2, -1, 20); //Has a 20% chance to lower the target's Def by 1
    Opponent [4].move [3] = new Move ("Rock Slide", 11, 1, 75, 90, 10); //No effect
    
    battle ((byte) 3, (byte) 0);
    Player = restore (Player);
    Opponent = null;
    prebat = new AudioPlayer ("Resources\\Audio Resources\\WallaceStartA.mp3");
    prebat.play ();
    for (short x = 1 ; x < 438 ; x++)
    {
      c.drawImage (loadImage ("Resources\\Visual Resources\\WallaceStart\\wall (" + (x) + ").gif"), 0, 20, null);
      sleeper (50);
    }
    prebat.stop ();
    prebat = null;
    background = loadImage ("Resources\\Visual Resources\\Backgrounds\\backf.jpg");
    upback = loadImage ("Resources\\Visual Resources\\Backgrounds\\upbackf.jpg");
    t.drawImage (upback, 0, 0, null);
    g.drawImage (background, 0, 0, null);
    c.drawImage (image, 0, 172, null);
    
    // Wallace - David
    Opponent = new Pokemon [6];
    Opponent [0] = new Pokemon ("Tentacruel", 55, 153, 82, 76, 93, 137, 115, 4, 7, "Resources\\Visual Resources\\Opponents\\tentacruel.gif");
    Opponent [0].move [0] = new Move ("Hydro Pump", 4, 2, 120, 80, 5); //No effect
    Opponent [0].move [1] = new Move ("Ice Beam", 12, 2, 95, 100, 10, 2, 10); //Has a 10% chance to freeze the target
    Opponent [0].move [2] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    Opponent [0].move [3] = new Move ("Sludge Bomb", 7, 2, 90, 100, 10, 4, 30); //Has a 30% chance to poison the target
    
    Opponent [1] = new Pokemon ("Gyarados", 57, 172, 150, 93, 72, 117, 90, 4, 5, "Resources\\Visual Resources\\Opponents\\gyarados.gif");
    Opponent [1].move [0] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    Opponent [1].move [1] = new Move ("Dragon Tail", 14, 1, 75, 90, 10); // No effect
    Opponent [1].move [2] = new Move ("Aqua Tail", 4, 1, 90, 90, 10); //No effect
    Opponent [1].move [3] = new Move ("Earthquake", 9, 1, 100, 100, 10); //No effect
    
    Opponent [2] = new Pokemon ("Ludicolo", 56, 155, 83, 83, 105, 117, 83, 4, 6, "Resources\\Visual Resources\\Opponents\\ludicolo.gif");
    Opponent [2].move [0] = new Move ("Surf", 4, 2, 95, 100, 15); //No effect
    Opponent [2].move [1] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    Opponent [2].move [2] = new Move ("Giga Drain", 6, 2, 60, 125, 5); //No effect
    Opponent [2].move [3] = new Move ("Cut", 1, 1, 50, 95, 30); //No effect
    
    Opponent [3] = new Pokemon ("Whiscash", 56, 189, 92, 86, 90, 84, 72, 4, 9, "Resources\\Visual Resources\\Opponents\\whiscash.gif");
    Opponent [3].move [0] = new Move ("Surf", 4, 2, 95, 100, 15); //No effect
    Opponent [3].move [1] = new Move ("Earthquake", 9, 1, 100, 100, 10); //No effect
    Opponent [3].move [2] = new Move ("Hyper Beam", 1, 2, 120, 90, 5); // No effect
    Opponent [3].move [3] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    
    Opponent [4] = new Pokemon ("Wailord", 57, 260, 107, 56, 107, 56, 73, 4, 0, "Resources\\Visual Resources\\Opponents\\wailord.gif");
    Opponent [4].move [0] = new Move ("Quick Attack", 1, 1, 50, 100, 30); //No effect
    Opponent [4].move [1] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    Opponent [4].move [2] = new Move ("Blizzard", 12, 2, 120, 70, 5, 2, 10); //Has a 10% chance to freeze the target
    Opponent [4].move [3] = new Move ("Double-Edge", 1, 1, 90, 100, 15); //No effect
    
    Opponent [5] = new Pokemon ("Milotic", 58, 178, 74, 96, 121, 150, 98, 4, 0, "Resources\\Visual Resources\\Opponents\\milotic2.gif");
    Opponent [5].move [0] = new Move ("Surf", 4, 2, 95, 100, 15); //No effect
    Opponent [5].move [1] = new Move ("Ice Beam", 12, 2, 95, 100, 10, 2, 10); //Has a 10% chance to freeze the target
    Opponent [5].move [2] = new Move ("Cut", 1, 1, 50, 95, 30); //No effect
    Opponent [5].move [3] = new Move ("Tackle", 1, 1, 50, 100, 35); //No effect
    
    battle ((byte) 4, (byte) 1);
    Opponent = null;
    prebat = new AudioPlayer ("Resources\\Audio Resources\\WallaceEndA.mp3");
    prebat.play ();
    for (byte x = 1 ; x < 93 ; x++)
    {
      c.drawImage (loadImage ("Resources\\Visual Resources\\WallaceEnd\\wale (" + (x) + ").gif"), 0, 20, null);
      sleeper (50);
    }
    prebat.stop ();
    prebat = null;
    c.clear ();
    f.dispose ();
    g.drawImage (loadImage ("Resources\\Visual Resources\\yay.jpg"), 0, 0, 640, 500, null);
    c.drawImage (image, 0, 0, null);
    text ("YAY You win!");
    c.getChar ();
    c.close ();
    // Place your program here.  'c' is the output console
  } // main method
  
  
  public static void main (String[] args)
  {
    new Pokemon_Emerald_Battle_Simulator ();
  }
  
  
  // The Battle Function - Bing
  public static void battle (byte opponent, byte music)
  {
    char command; // Whether to switch the pokemon or to attack
    byte choice; // Pokemon to switch to or attack to use
    byte m; // Move used
    
    // Play music
    Music [music].loop ();
    p = 0; // Player pokemon is set to the first in party
    currpoke = 0;
    // Opponent will cycle through his pokemons from 1 to 6
    for (byte i = 0 ; i < Opponent.length ; i++)
    {
      c.drawImage (tex, 0, 0, null);
      text (oppName [opponent] + " sent out " + Opponent [i].name);
      //animates in opponent
      for (short x = 641 ; x > 365 ; x -= 2)
      {
        g.drawImage (background, 0, 0, null);
        edisplay1 (Opponent [i]);
        udisplay1 (Player [p]);
        g.drawImage (Opponent [i].img, x, -26, 235, 235, null);
        if (i != 0)
          g.drawImage (Player [p].img, 40, 100, 235, 235, null);
        c.drawImage (image, 0, 172, null);
      }
      if (i == 0) //as long as first battle
      {
        //amimated in user
        for (short x = -237 ; x < 40 ; x += 2)
        {
          g.drawImage (background, 0, 0, null);
          edisplay1 (Opponent [i]);
          udisplay1 (Player [p]);
          g.drawImage (Player [p].img, x, 100, 235, 235, null);
          g.drawImage (Opponent [i].img, 365, -26, 235, 235, null);
          c.drawImage (image, 0, 172, null);
        }
      }
      sleeper (1000);
      
      // Battle until Opponent's pokemon faints
      while (Opponent [i].curhp > 0)
      {
        // Display info
        c.clear ();
        g.drawImage (background, 0, 0, null);
        t.drawImage (upback, 0, 0, null);
        edisplay (Opponent [i]);
        udisplay (Player [p]);
        c.drawImage (tex, 0, 0, null);
        c.drawImage (image, 0, 172, null);
        // Reset the turn variable to true (they both have turns)
        Player [p].turn = true;
        Opponent [i].turn = true;
        m = 0;
        
        
        // Allowing the Player to plan his moves
        command = getCommand (); // Get the command
        choice = getChoice (command); // Get the choice depending on command
        if (command == 's')
        { // s = switch pokemon
          for (short x = 40 ; x > -237 ; x -= 2)
          {
            g.drawImage (background, 0, 0, null);
            edisplay (Opponent [i]);
            udisplay1 (Player [p]);
            g.drawImage (Player [p].img, x, 100, 235, 235, null);
            c.drawImage (image, 0, 172, null);
          }
          p = choice;
          for (short x = -237 ; x < 40 ; x += 2)
          {
            g.drawImage (background, 0, 0, null);
            edisplay (Opponent [i]);
            udisplay1 (Player [p]);
            g.drawImage (Player [p].img, x, 100, 235, 235, null);
            c.drawImage (image, 0, 172, null);
          }
          Player [p].turn = false; // Loses turn after switch in
        }
        else if (command == 'a')
        { // a = attack
          m = choice;
        }
        
        // Attack phase
        // Fastest Pokemon goes first, if same Player goes first
        // Opponent will use a random move
        byte w = (byte) (Math.random () * 4);
        if ((Opponent [i].spd * mod [Opponent [i].stage [5]] / 100) > (Player [p].spd * mod [Player [i].stage [5]] / 100))
        {
          // Apply status
          Opponent [i].status (c);
          // Split off into sttack and status moves, if status split into affecting enemy or self
          if (Opponent [i].turn)
          {
            textr (oppName [opponent] + "'s " + Opponent [i].name + " used " + Opponent [i].move [w].name);
            // If affecting opponent
            if (Opponent [i].move [w].target == 1)
              Player [p] = attack (Opponent [i], Opponent [i].move [w], Player [p]);
            // If affecting oneself
            else
              Opponent [i] = attack (Opponent [i], Opponent [i].move [w]);
            
            Opponent [i].move [w].use ();
          }
          
          // Apply Status
          if (Player [p].curhp > 0)
            Player [p].status (c);
          if (Player [p].turn)
          {
            textr ("Your " + Player [p].name + " used " + Player [p].move [m].name);
            // If affecting opponent
            if (Player [p].move [m].target == 1)
              Opponent [i] = attack (Player [p], Player [p].move [m], Opponent [i]);
            // If affecting oneself
            else
              Player [p] = attack (Player [p], Player [p].move [m]);
            
            Player [p].move [m].use ();
          }
        }
        else
        { // Same thing as before, except player goes first
          // Apply Status
          Player [p].status (c);
          // Split off into sttack and status moves, if status split into affecting enemy or self
          if (Player [p].turn)
          {
            textr ("Your " + Player [p].name + " used " + Player [p].move [m].name);
            // If affecting opponent
            if (Player [p].move [m].target == 1)
              Opponent [i] = attack (Player [p], Player [p].move [m], Opponent [i]);
            // If affecting oneself
            else
              Player [p] = attack (Player [p], Player [p].move [m]);
            
            Player [p].move [m].use ();
          }
          
          // Apply status
          if (Opponent [i].curhp > 0)
            Opponent [i].status (c);
          if (Opponent [i].turn)
          {
            textr (oppName [opponent] + "'s " + Opponent [i].name + " used " + Opponent [i].move [w].name);
            // If affecting opponent
            if (Opponent [i].move [w].target == 1)
              Player [p] = attack (Opponent [i], Opponent [i].move [w], Player [p]);
            // If affecting oneself
            else
              Opponent [i] = attack (Opponent [i], Opponent [i].move [w]);
            
            Opponent [i].move [w].use ();
          }
        }
        
        usehp = Player [p].curhp;
        opphp = Opponent [i].curhp;
        
        // If the pokemon faints, switch in another one
        if (Player [p].curhp == 0)
        {
          textr ("Your " + Player [p].name + " fainted");
          //if forced to switch display stuff
          for (short y = 100 ; y <= 327 ; y += 2)
          {
            //fainting animation
            g.drawImage (background, 0, 0, null);
            displayer (Player [p], Opponent [i]);
            g.drawImage (Player [p].img, 40, y, 235, 235, null);
            g.drawImage (Opponent [i].img, 365, -26, 235, 235, null);
            c.drawImage (image, 0, 172, null);
          }
          f.remove (attack);
          f.remove (swich);
          for (byte y = 0 ; y < 4 ; y++)
            f.remove (movess [y]);
          for (byte y = 0 ; y < 6 ; y++)
            f.remove (pokemonn [y]);
          for (byte x = 0 ; x < 6 ; x++)
          {
            //formatting buttons with html
            pokemonn [x].setIcon (userPokes2 [x]);
            pokemonn [x].setText (Player [x].name);
            pokemonn [x].setToolTipText ("<html>" +
                                         "<p>" + "Level: " + Player [x].level + "</p>" +
                                         "<p>" + "HP: " + Player [x].curhp + " / " + Player [x].maxhp + "</p>" +
                                         "<p>" + "Attack: " + Player [x].atk + "</p>" +
                                         "<p>" + "Defense: " + Player [x].def + "</p>" +
                                         "<p>" + "Special Attack: " + Player [x].spatk + "</p>" +
                                         "<p>" + "Special Defense: " + Player [x].spdef + "</p>" +
                                         "<p>" + "Speed: " + Player [x].spd + "</p>" +
                                         "<p>" + "Status: " + Player [x].statuss [Player [x].status] + "</p>" +
                                         "</html>");
            oldpoke = currpoke;
            if (Player [x].curhp <= 0)
              pokemonn [x].setBackground (Color.RED);
            f.getContentPane ().add (pokemonn [x]);
          }
          f.validate ();
          f.repaint ();
          p = getChoice ('s');
        }
        
        if (Opponent [i].curhp == 0)
        {
          textr (oppName [opponent] + "'s " + Opponent [i].name + " fainted");
          //opponent fainting animation
          for (short y = -26 ; y <= 327 ; y += 2)
          {
            g.drawImage (background, 0, 0, null);
            displayer (Player [p], Opponent [i]);
            g.drawImage (Player [p].img, 40, 100, 235, 235, null);
            g.drawImage (Opponent [i].img, 365, y, 235, 235, null);
            c.drawImage (image, 0, 172, null);
          }
        }
        sleeper (2000);
        
        
        boolean lose = true;
        for (int x = 0; x<6; x++)
          if(Player[x].curhp > 0) lose = false;
        
        if(lose) {
          c.clear();
          f.dispose();
          textr("You lose :(");
          c.getChar();
          c.close();
          break;
        }
      }
      
    }
    
    // Stopping music after battle
    Music [music].stop ();
  }
  
} // Test class


class Poke implements ActionListener
{
  private JButton skip, start, nameB, skip2, fastforward, normalspeed, fastforward0; //creates all the buttons
  public static String playerName = ""; //stores the Player's name
  private second s; //animated class
  public third t; //second animation
  public static JFrame f, f0;
  private JPanel p; //panel for name input
  private JLabel nameLabel;
  private JTextField nameField;
  private AudioPlayer startau;
  public static boolean n = false;
  public Poke ()  //constructor for animation
  {
    f = new JFrame ("Pokemon Emerald Simulator"); //creates frame to put everything on
    f0 = new JFrame ("Pokemon Emerald Simulator"); //creates a frame for the second thing
    s = new second (); //new instance of the animation
    s.setPreferredSize (new Dimension (853, 640));
    p = new JPanel (); //new panel for use with name input
    skip = new JButton ("Skip"); //creates button to skip animation intro
    start = new JButton ("Start"); //creates button to input name
    skip2 = new JButton ("Skip");
    normalspeed = new JButton ("Normal Speed");
    fastforward = new JButton ("Fast Forward");
    fastforward0 = new JButton ("Fast Forward");
    startau = new AudioPlayer ("Resources\\Audio Resources\\StartA.mp3"); //beginning audio
    startau.play (); //starts audo
    nameLabel = new JLabel ("What is your name?"); //prompts for name
    nameField = new JTextField (); //name input field
    nameB = new JButton ("Enter"); //enters name
    nameField.setPreferredSize (new Dimension (100, 25)); //sets size of input field
    skip.addActionListener (this);
    start.addActionListener (this);
    normalspeed.addActionListener (this);
    skip2.addActionListener (this);
    fastforward.addActionListener (this);
    fastforward0.addActionListener (this);
    nameB.addActionListener (this); //adds action listeners to all three buttons
    skip.setPreferredSize (new Dimension (90, 35));
    start.setPreferredSize (new Dimension (90, 35));
    skip2.setPreferredSize (new Dimension (90, 35));
    fastforward.setPreferredSize (new Dimension (130, 35));
    normalspeed.setPreferredSize (new Dimension (130, 35));
    fastforward0.setPreferredSize (new Dimension (130, 35));
    //nameB.setPreferredSize(new Dimension(90, 35)); //changes the size of the buttons
    s.add (skip); //adds skip button to animation
    s.add (fastforward0);
    f.getContentPane ().add (s); //adds the animation to frame
    f.validate (); //makes sure window is the right size
    f.pack ();
    f.repaint ();
    f.setVisible (true); //makes frame visible
    f.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE); //allows u to exit the JFrame
    f.setSize (853, 640); //sets size
    f0.setVisible (false); //makes frame invisible
    f0.setDefaultCloseOperation (JFrame.EXIT_ON_CLOSE); //allows u to exit the JFrame
    f0.setSize (853, 640); //sets size
    while (!n)
    {
      Pokemon_Emerald_Battle_Simulator.sleeper (1000);
    }
    f0.removeAll ();
    s = null;
    f0.dispose ();
  }
  
  
  public void actionPerformed (ActionEvent e)
  {
    if (e.getSource () == skip)
    { //basically this most of the beginning animation
      second.currentFrame = 902;
      s.remove (skip);
      s.revalidate ();
      s.add (start, BorderLayout.SOUTH);
    }
    else if (e.getSource () == start)
    { //this stops the audio, removes the animation and displays a text field with prompt
      startau.stop ();
      p.add (nameLabel);
      p.add (nameField);
      p.add (nameB);
      s.remove (start);
      f.remove (s);
      f.repaint ();
      f.getContentPane ().add (p);
      f.validate ();
    }
    else if (e.getSource () == nameB)
    { //stores name
      playerName = nameField.getText ();
      JOptionPane.showMessageDialog (null, "Welcome " + playerName + "! Good luck with the Elite Four!");
      t = new third (); //new second anmiation
      t.setPreferredSize (new Dimension (853, 640));
      f0.getContentPane ().add (t);
      t.add (skip2);
      t.add (fastforward);
      f0.validate ();
      f0.repaint ();
      f0.pack ();
      f.dispose ();
      f0.setVisible (true);
    }
    else if (e.getSource () == skip2)
    {
      t.currentFrame = 372;
      t.inc = 1;
      t.remove (skip2);
      t.remove (fastforward);
      t.repaint ();
    }
    else if (e.getSource () == fastforward)
    {
      t.inc = 4;
      t.remove (fastforward);
      t.add (normalspeed);
      t.repaint ();
    }
    else if (e.getSource () == normalspeed)
    {
      t.inc = 1;
      t.remove (normalspeed);
      t.add (fastforward);
      t.validate ();
      t.repaint ();
    }
    else if (e.getSource () == fastforward0)
    {
      second.inc = 5;
      s.remove (fastforward0);
      s.validate ();
      s.repaint ();
    }
  }
}


class second extends JPanel implements ActionListener //oof fancy
{
  private static final long serialVersionUID = 2L; //static final serialVersionUID for java to identify class
  private Timer animator; //you give this a delay and then an action listener
  private ImageIcon imageArray; //going to store all of our frames
  private short delay = 59, totalFrames = 978;
  public static short currentFrame = 0;
  public static byte inc = 1;
  public second ()  //creates constructor for this class
  {
    animator = new Timer (delay, this); //this is what does all the animating work
    animator.start (); //starts it
  }
  
  
  public void paintComponent (Graphics g)
  { //this overwrites the existing JFrame paint method (why the class extends JFrame)
    imageArray = new ImageIcon ("Resources\\Visual Resources\\Start\\Start (" + (currentFrame + 1) + ").gif"); //imports current image
    super.paintComponent (g); //refers to parent class
    if (currentFrame >= totalFrames) //loops a specific portion
      currentFrame = 902;
    currentFrame += inc; //goes to next frame
    imageArray.paintIcon (this, g, 0, 0); //paints icon onto JFrame
    imageArray = null; //ram pls
  }
  
  
  public void actionPerformed (ActionEvent e)
  { //repaints the frame every frame, so you have an animation
    repaint ();
  }
}


class third extends JPanel implements ActionListener //oof fancy
{
  private static final long serialVersionUID = 3L; //static final serialVersionUID for java to identify class
  private Timer animator; //you give this a delay and then an action listener
  private ImageIcon imageArray; //going to store all of our frames
  private short delay = 59, totalFrames = 447;
  private AudioPlayer sound = new AudioPlayer ("Resources\\Audio Resources\\SidneyStartB.mp3");
  public short currentFrame = 0;
  public byte inc = 1;
  
  public third ()  //creates constructor for this class
  {
    animator = new Timer (delay, this); //this is what does all the animating work
    sound.play ();
    animator.start (); //starts it
  }
  
  
  public void paintComponent (Graphics g)
  { //this overwrites the existing JFrame paint method (why the class extends JFrame)
    imageArray = new ImageIcon ("Resources\\Visual Resources\\SidneyStart\\SidneyStart (" + (currentFrame + 1) + ").gif"); //imports current image
    super.paintComponent (g); //refers to parent class
    if (currentFrame < totalFrames)
      currentFrame += inc; //goes to next frame
    else if (currentFrame == totalFrames)
    {
      animator.stop ();
      sound.stop ();
      Poke.n = true;
    }
    
    
    imageArray.paintIcon (this, g, 0, 0); //paints icon onto JFrame
    if (currentFrame > 748)
      inc = 1;
  }
  
  
  public void actionPerformed (ActionEvent e)
  { //repaints the frame every frame, so you have an animation
    repaint ();
  }
}


class Move
{
  public int type, category, power, accuracy, curPP, maxPP, target, effect, chance, stat, stage;
  public String name;
  public String[] typenames = {"None", "Normal", "Fire", "Fighting", "Water", "Flying", "Grass", "Poison", "Electric", "Ground", "Psychic", "Rock", "Ice", "Bug", "Dragon", "Ghost", "Dark", "Steel"};
  public String[] cat = {"None", "Physical", "Special", "Status"};
  /*
   Category:
   1 - Physical
   2 - Special
   3 - Status
   
   Target:
   1 - Enemy
   2 - Self
   
   Effects:
   0 - None
   1 - Burn
   2 - Freeze
   3 - Paralysis
   4 - Poison
   5 - Sleep
   
   Stats:
   0 - None
   1 - atk
   2 - def
   3 - spatk
   4 - spdef
   5 - spd
   6 - acc
   7 - evade
   
   0 - None            9 - Ground
   1 - Normal          10 - Psychic
   2 - Fire            11 - Rock
   3 - Fighting        12 - Ice
   4 - Water           13 - Bug
   5 - Flying          14 - Dragon
   6 - Grass           15 - Ghost
   7 - Poison          16 - Dark
   8 - Electric        17 - Steel
   */
  
  // Constructor for normal damage moves
  public Move (String n, int t, int cat, int pow, int acc, int pp)
  {
    name = n;
    type = t;
    category = cat;
    power = pow;
    accuracy = acc;
    curPP = maxPP = pp;
    effect = stat = 0;
    target = 1;
  }
  
  
  // Constructor for damage moves with effects (% to cause status)
  public Move (String n, int t, int cat, int pow, int acc, int pp, int e, int perc)
  {
    name = n;
    type = t;
    category = cat;
    power = pow;
    accuracy = acc;
    curPP = maxPP = pp;
    effect = e;
    chance = perc;
    stat = 0;
    target = 1;
  }
  
  
  // Constructor for damage moves with effects (% to affect stats)
  public Move (String n, int t, int cat, int pow, int acc, int pp, int s, int st, int perc)
  {
    name = n;
    type = t;
    category = cat;
    power = pow;
    accuracy = acc;
    curPP = maxPP = pp;
    stat = s;
    stage = st;
    chance = perc;
    effect = 0;
    target = 1;
    chance = 100;
  }
  
  
  // Constructor for status moves (% to cause status)
  // what i is doesn't matter, just makes it not get mixed up with other constructors
  public Move (double i, String n, int t, int acc, int pp, int e, int perc, int tar)
  {
    name = n;
    type = t;
    accuracy = acc;
    curPP = maxPP = pp;
    category = 3;
    effect = e;
    chance = perc;
    stat = 0;
    target = tar;
    chance = 100;
  }
  
  
  // Constructor for status moves (% to affect stats)
  public Move (double i, String n, int t, int acc, int pp, int s, int st, int perc, int tar)
  {
    name = n;
    type = t;
    accuracy = acc;
    curPP = maxPP = pp;
    category = 3;
    stat = s;
    stage = st;
    chance = perc;
    effect = 0;
    target = tar;
  }
  
  
  // Using the move decreases the PP
  public void use ()
  {
    curPP--;
  }
} // Move class


class Pokemon
{
  // Convienent, but a bit dangerous
  // Dynamic so each instance will have a different value
  public int level, curhp, maxhp, atk, def, spatk, spdef, spd, evade, acc, status;
  public byte stage[] = {0, 6, 6, 6, 6, 6, 6, 6};
  public int type[];
  public String name;
  public Move move[] = new Move [4];
  public boolean turn; // Rememeber to set to true at the beginning of the turn
  public int sleepCounter;
  public Image img;
  public String[] statuss = {"None", "Burn", "Freeze", "Paralysis", "Poison", "SLeep"};
  
  /*
   Types are represented by integers, the legend is as shown:
   0 - None            9 - Ground
   1 - Normal          10 - Psychic
   2 - Fire            11 - Rock
   3 - Fighting        12 - Ice
   4 - Water           13 - Bug
   5 - Flying          14 - Dragon
   6 - Grass           15 - Ghost
   7 - Poison          16 - Dark
   8 - Electric        17 - Steel
   
   Status effects are also represented by integers, legend as shown:
   0 - None
   1 - Burn (BRN)
   2 - Freeze (FRZ)
   3 - Paralysis (PAR)
   4 - Poison (PSN)
   5 - Sleep (SLP)
   */
  
  // Constructor - Creating a new pokemon, if creating single type, always input 0 as t2
  public Pokemon (String n, int l, int h, int a, int d, int sa, int sd, int s, int t1, int t2, String file)
  {
    // Seeing if it's a single or double type pokemon and assigning appropriately
    if (t2 == 0)
    {
      type = new int [1];
      type [0] = t1;
    }
    else
    {
      type = new int [2];
      type [0] = t1;
      type [1] = t2;
    }
    // Assigning the rest
    name = n;
    level = l;
    maxhp = curhp = h;
    atk = a;
    def = d;
    spatk = sa;
    spdef = sd;
    spd = s;
    evade = 100;
    acc = 100;
    status = 0;
    img = Pokemon_Emerald_Battle_Simulator.loadImage (file);
  }
  
  
  // Dealing damage to the pokemon
  public void damage (int dmg)
  {
    curhp -= dmg;
    if (curhp <= 0)
    {
      turn = false;
      curhp = 0;
    }
  }
  
  
  // Call this at the beginning of the turn, so turn can be set to false if it's skipped
  public void status (Console c)
  {
    if (status == 1)
    {
      damage (maxhp / 8); // Loses 1/8 of max hp if burned
      // Display message to say pokemon is burned
      Pokemon_Emerald_Battle_Simulator.textr (name + " is burned");
    }
    else if (status == 2)
    {
      if ((byte) (Math.random () * 100) <= 20)
        status = 0;                                    // 20% chance to thaw out
      else
        turn = false;
      // Display message to say pokemon is frozen or thawed out
      if (status == 0)
        Pokemon_Emerald_Battle_Simulator.textr (name + " is thawed");
      else
        Pokemon_Emerald_Battle_Simulator.textr (name + " is frozen");
    }
    else if (status == 3)
    {
      if ((byte) (Math.random () * 100) <= 25)
      {
        turn = false; // 25% chance to skip turn
        // Display message pokemon is paralyzed
        Pokemon_Emerald_Battle_Simulator.textr (name + " is paralyzed");
      }
    }
    else if (status == 4)
    {
      damage (maxhp / 8); // Loses 1/8 of max hp if poisoned
      // Display message to say pokemon is poisoned
      Pokemon_Emerald_Battle_Simulator.textr (name + " is poisoned");
    }
    else if (status == 5)
    {
      if (sleepCounter-- == 0)
        status = 0;
      // Display a message if pokemon is still asleep
      if (status == 0)
        Pokemon_Emerald_Battle_Simulator.textr (name + " woke up");
      else
        Pokemon_Emerald_Battle_Simulator.textr (name + " is fast asleep");
    }
    if (curhp <= 0)
      turn = false;
  }
} // Pokemon class


// Creds to Michael Kim
class AudioPlayer implements Runnable
{
  private Player player;
  private Thread thread;
  private boolean loop = false;
  private String fileName;
  public AudioPlayer (String s)
  {
    load (s);
  }
  
  
  public void load (String s)
  {
    fileName = s;
    try
    {
      player = new Player (new FileInputStream (s));
      thread = new Thread (this);
    }
    catch (Exception e)
    {
      e.printStackTrace ();
    }
  }
  
  
  public void play ()
  {
    if (thread != null)
      thread = null;
    if (thread == null)
      load (fileName);
    thread.start ();
  }
  
  
  public void loop ()
  {
    loop = true;
    play ();
  }
  
  
  public void stop ()
  {
    loop = false;
    thread = null;
    player.close ();
  }
  
  
  public void run ()
  {
    do
    {
      try
      {
        player.play ();
      }
      catch (Exception e)
      {
        e.printStackTrace ();
      }
      if (loop)
        load (fileName);
    }
    while (loop);
  }
}


