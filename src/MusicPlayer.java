import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    private Song currentSong;
    public Song getCurrentSong(){
        return currentSong;
    }
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused;
    private int currentFrame; //for catch moment pause
    public void setCurrentFrame(int frame){
        currentFrame = frame;
    }
    private int currentTimeInMillisecond;
    public void setCurrentTimeInMillisecond(int timeInMillisecond){
        currentTimeInMillisecond = timeInMillisecond;
    }

    public MusicPlayer(MusicPlayerGUI musicPlayerGUI){
        this.musicPlayerGUI = musicPlayerGUI;
    }
    public void loadSong(Song song){
        currentSong = song;

        if(currentSong != null){
            playCurrentSong();
        }
    }

    public void pauseSong(){
        if(advancedPlayer != null){
            isPaused = true;

            stopSong();
        }
    }

    public void stopSong(){
        if(advancedPlayer != null){
            advancedPlayer.stop();
            advancedPlayer.close();
            advancedPlayer = null;
        }
    }

    public void playCurrentSong(){
        if(currentSong == null) return;

        try{
            FileInputStream fileInputStream = new FileInputStream(currentSong.getFilePath());
            BufferedInputStream bufferedInputStream = new BufferedInputStream(fileInputStream);

            advancedPlayer = new AdvancedPlayer(bufferedInputStream);
            advancedPlayer.setPlayBackListener(this);

            startMusicThread();

            startPlaybackSliderThread();
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void startMusicThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    if(isPaused){
                        synchronized(playSignal){
                            isPaused = false;

                            playSignal.notify();
                        }

                        advancedPlayer.play(currentFrame, Integer.MAX_VALUE);
                    }
                    else{
                        advancedPlayer.play();
                    }
                }
                catch(Exception e){
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void startPlaybackSliderThread(){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if(isPaused){
                    try{
                        synchronized(playSignal){
                            playSignal.wait();
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }

                while(!isPaused){
                    try{
                        currentTimeInMillisecond++;

                        int calculateFrame = (int) ((double) currentTimeInMillisecond * 2.08 * currentSong.getFrameRateMilliseconds());

                        musicPlayerGUI.setPlaybackSliderValue(calculateFrame);

                        Thread.sleep(1);
                    }
                    catch(Exception e){
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public void playbackStarted(PlaybackEvent evt) {
        System.out.println("Started");
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Finish");

        if(isPaused){
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRateMilliseconds());
        }
    }
}
