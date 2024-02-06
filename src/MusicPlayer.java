import javazoom.jl.player.advanced.AdvancedPlayer;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer {
    private Song currentSong;
    private AdvancedPlayer advancedPlayer;

    public MusicPlayer(){

    }
    public void loadSong(Song song){
        currentSong = song;

        if(currentSong != null){
            playCurrentSong();
        }
    }

    public void playCurrentSong(){
        try{
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}