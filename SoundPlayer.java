/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Temple;

/**
 *This class was reused from another finished project provided for this assignment
 *and was NOT written by Grant Kennedy
 */
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class SoundPlayer {
   private AudioInputStream soundStream; 
   private Clip clip;
   private int type;//1 for sounds that needs to be played all the time
                    // 2 for sounds that only need to be played once
   
   
   public SoundPlayer(int type, String soundFile){
       this.type = type;
       try{
           soundStream = AudioSystem.getAudioInputStream(SoundPlayer.class.getResource(soundFile));
           clip = AudioSystem.getClip();
           clip.open(soundStream);
       }
       catch(Exception e){
           System.out.println(e.getMessage() + "No sound documents are found");
       }
       if(this.type == 1){
    	   FloatControl gainControl = 
    			    (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
    			gainControl.setValue(-5.0f); // Reduce volume by 10 decibels.
           Runnable myRunnable = new Runnable(){
               public void run(){
                   while(true){
                       clip.start();
                       clip.loop(Clip.LOOP_CONTINUOUSLY);
                       try {
                           Thread.sleep(10000);
                       } catch (InterruptedException ex) {
                           Logger.getLogger(SoundPlayer.class.getName()).log(Level.SEVERE, null, ex);
                       }
                    }
               }
           };
           Thread thread = new Thread(myRunnable);
           thread.start();
       }
   }
   
   public void play(){
       clip.start();
   }
   public void stop(){
       clip.stop();
   }
}
