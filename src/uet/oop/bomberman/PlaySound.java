package uet.oop.bomberman;

import sun.audio.*;
import java.io.*;

public class PlaySound {
    private AudioPlayer MGP;
    private InputStream test;
    private AudioStream BGM;
    private boolean isPlaying;

    public PlaySound() {
        isPlaying = false;
    }

    public void playSound(String file_name, boolean isLoop) {
        this.isPlaying = true;
        MGP = AudioPlayer.player;

        try
        {
            test = new FileInputStream(file_name);
            BGM = new AudioStream(test);
            AudioPlayer.player.start(BGM);
            MGP.start(null);

        }
        catch(FileNotFoundException e){
            System.out.print(e.toString());
        }
        catch (IOException error)
        {
            error.printStackTrace();
        }
    }

    public void stopSound() {
        this.isPlaying = false;
        AudioPlayer.player.stop(BGM);
    }

    public boolean isPlaying() {
        return this.isPlaying;
    }
}
