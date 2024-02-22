import javazoom.jl.player.advanced.AdvancedPlayer;
import javazoom.jl.player.advanced.PlaybackEvent;
import javazoom.jl.player.advanced.PlaybackListener;

import java.io.*;
import java.util.ArrayList;

public class MusicPlayer extends PlaybackListener {
    private static final Object playSignal = new Object();
    private MusicPlayerGUI musicPlayerGUI;
    private Song currentSong;
    public Song getCurrentSong(){
        return currentSong;
    }
    private ArrayList<Song> playlist;
    private int currentPlaylistIndex;
    private AdvancedPlayer advancedPlayer;
    private boolean isPaused;
    private boolean songFinished;
    private boolean pressedNext, pressedPrev;
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
        playlist = null;

        if(!songFinished){
            stopSong();
        }

        if(currentSong != null){
            currentFrame = 0;
            currentTimeInMillisecond = 0;
            musicPlayerGUI.setPlaybackSliderValue(0);

            playCurrentSong();
        }
    }

    public void loadPlaylist(File playlistFile){
        playlist = new ArrayList<>();

        try{
            FileReader fileReader = new FileReader(playlistFile);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String songPath;
            while((songPath = bufferedReader.readLine()) != null){
                Song song = new Song(songPath);
                playlist.add(song);
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }

        if(playlist.size() > 0){
            musicPlayerGUI.setPlaybackSliderValue(0);
            currentTimeInMillisecond = 0;

            currentSong = playlist.get(0);
            currentFrame = 0;

            musicPlayerGUI.enablePauseButtonDisablePlayButton();
            musicPlayerGUI.updateSongTitleSongArtist(currentSong);
            musicPlayerGUI.updatePlaybackSlider(currentSong);
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

    public void nextSong(){
        if(playlist == null) return;

        if(currentPlaylistIndex + 1 > playlist.size() -1) return;

        pressedNext = true;

        if(!songFinished){
            stopSong();
        }

        currentPlaylistIndex++;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMillisecond = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleSongArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
    }

    public void prevSong(){
        if(playlist == null) return;

        if(currentPlaylistIndex - 1 < 0) return;

        pressedPrev = true;

        if(!songFinished){
            stopSong();
        }

        currentPlaylistIndex--;
        currentSong = playlist.get(currentPlaylistIndex);
        currentFrame = 0;
        currentTimeInMillisecond = 0;

        musicPlayerGUI.enablePauseButtonDisablePlayButton();
        musicPlayerGUI.updateSongTitleSongArtist(currentSong);
        musicPlayerGUI.updatePlaybackSlider(currentSong);

        playCurrentSong();
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

                while(!isPaused && !songFinished && !pressedNext && !pressedPrev){
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
        songFinished = false;
        pressedNext = false;
        pressedPrev = false;
    }

    @Override
    public void playbackFinished(PlaybackEvent evt) {
        System.out.println("Finish");

        if(isPaused){
            currentFrame += (int) ((double) evt.getFrame() * currentSong.getFrameRateMilliseconds());
        }
        else{
            if(pressedNext || pressedPrev) return;

            songFinished = true;

            if(playlist == null){
                musicPlayerGUI.enablePlayButtonDisablePauseButton();
            }
            else{
                if(currentPlaylistIndex == playlist.size() - 1){
                    musicPlayerGUI.enablePlayButtonDisablePauseButton();
                }
                else{
                    nextSong();
                }
            }
        }
    }
}
