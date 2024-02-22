import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Hashtable;

public class MusicPlayerGUI extends JFrame {
    public static final Color FRAME_COLOR = Color.DARK_GRAY;
    public static final Color TEXT_COLOR = Color.WHITE;
    private MusicPlayer musicPlayer;
    private JFileChooser jFileChooser;
    private JLabel songTitle, songArtist;
    private JPanel playbackButtons;
    private JSlider playbackSlider;


    public MusicPlayerGUI(){
        super("Luke Player");

        setSize(400, 600);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        setLayout(null);

        getContentPane().setBackground(FRAME_COLOR);

        musicPlayer = new MusicPlayer(this);

        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/songs"));
        jFileChooser.setFileFilter(new FileNameExtensionFilter("MP3", "mp3"));

        addGuiComponents();
    }

    private void addGuiComponents(){
        addToolBar();

        JLabel songImage = new JLabel(loadImage("src/images/record.jpg"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Arial", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Arial", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
        playbackSlider.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                musicPlayer.pauseSong();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                JSlider source = (JSlider) e.getSource();
                int frame = source.getValue();
                musicPlayer.setCurrentFrame(frame);
                musicPlayer.setCurrentTimeInMillisecond((int) (frame / (2.08 * musicPlayer.getCurrentSong().getFrameRateMilliseconds())));
                musicPlayer.playCurrentSong();

                enablePauseButtonDisablePlayButton();
            }
        });
        add(playbackSlider);

        addPlaybackButtons();
    }

    private void addToolBar(){
        JToolBar toolBar = new JToolBar();
        toolBar.setBounds(0 , 0, getWidth(), 20);
        toolBar.setFloatable(false);

        //MenuBar
        JMenuBar menuBar = new JMenuBar();
        toolBar.add(menuBar);

        //SongBar
        JMenu songMenu = new JMenu("Song");
        menuBar.add(songMenu);

        //Load Song in SongBar
        JMenuItem loadSong = new JMenuItem("Load Song");
        loadSong.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int result = jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectFile = jFileChooser.getSelectedFile();

                if(result == JFileChooser.APPROVE_OPTION && selectFile != null){
                    Song song = new Song(selectFile.getPath());

                    musicPlayer.loadSong(song);

                    updateSongTitleSongArtist(song);

                    updatePlaybackSlider(song);

                    enablePauseButtonDisablePlayButton();
                }
            }
        });
        songMenu.add(loadSong);

        //Playlist
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        createPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new MusicPlaylistDialog(MusicPlayerGUI.this).setVisible(true);
            }
        });
        playlistMenu.add(createPlaylist);

        JMenuItem loadPlaylist = new JMenuItem("Load Playlist");
        loadPlaylist.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser jFileChooser1 = new JFileChooser();
                jFileChooser1.setFileFilter(new FileNameExtensionFilter("Playlist", ".txt"));
                jFileChooser1.setCurrentDirectory(new File("src/songs"));

                int result = jFileChooser1.showOpenDialog(MusicPlayerGUI.this);
                File selectedFile = jFileChooser1.getSelectedFile();

                if(result == JFileChooser.APPROVE_OPTION && selectedFile != null){
                    musicPlayer.stopSong();

                    musicPlayer.loadPlaylist(selectedFile);
                }
            }
        });
        playlistMenu.add(loadPlaylist);

        add(toolBar);
    }

    private void addPlaybackButtons(){
        playbackButtons = new JPanel();
        playbackButtons.setBounds(0, 435, getWidth() - 10, 80);
        playbackButtons.setBackground(null);

        JButton prevButton = new JButton(loadImage("src/images/prev.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        prevButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.prevSong();
            }
        });
        playbackButtons.add(prevButton);

        JButton playButton = new JButton(loadImage("src/images/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePauseButtonDisablePlayButton();

                musicPlayer.playCurrentSong();
            }
        });
        playbackButtons.add(playButton);

        JButton pauseButton = new JButton(loadImage("src/images/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        pauseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enablePlayButtonDisablePauseButton();

                musicPlayer.pauseSong();
            }
        });
        playbackButtons.add(pauseButton);

        JButton nextButton = new JButton(loadImage("src/images/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        nextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                musicPlayer.nextSong();
            }
        });
        playbackButtons.add(nextButton);

        add(playbackButtons);
    }

    public void setPlaybackSliderValue(int frame){
        playbackSlider.setValue(frame);
    }

    public void updateSongTitleSongArtist(Song song){
        songTitle.setText(song.getSongTitle());
        songArtist.setText(song.getArtist());
    }

    public void updatePlaybackSlider(Song song){
        playbackSlider.setMaximum(song.getMp3File().getFrameCount());

        Hashtable<Integer, JLabel> labelTable = new Hashtable<>();

        JLabel labelBegin = new JLabel("00:00");
        labelBegin.setFont(new Font("Arial", Font.BOLD, 18));
        labelBegin.setForeground(TEXT_COLOR);

        JLabel labelEnd = new JLabel(song.getSongLength());
        labelEnd.setFont(new Font("Arial", Font.BOLD, 18));
        labelEnd.setForeground(TEXT_COLOR);

        labelTable.put(0, labelBegin);
        labelTable.put(song.getMp3File().getFrameCount(), labelEnd);

        playbackSlider.setLabelTable(labelTable);
        playbackSlider.setPaintLabels(true);
    }

    public void enablePauseButtonDisablePlayButton(){
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        playButton.setVisible(false);
        playButton.setEnabled(false);
        pauseButton.setVisible(true);
        pauseButton.setEnabled(true);
    }

    public void enablePlayButtonDisablePauseButton(){
        JButton playButton = (JButton) playbackButtons.getComponent(1);
        JButton pauseButton = (JButton) playbackButtons.getComponent(2);

        playButton.setVisible(true);
        playButton.setEnabled(true);
        pauseButton.setVisible(false);
        pauseButton.setEnabled(false);
    }

    private ImageIcon loadImage(String imagePath){
        try{
            BufferedImage image = ImageIO.read(new File(imagePath));
            return new ImageIcon(image);
        }
        catch(Exception e){
            e.printStackTrace();
        }

        return null;
    }
}
