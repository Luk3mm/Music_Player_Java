import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;

public class MusicPlayerGUI extends JFrame {
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;
    private MusicPlayer musicPlayer;
    private JFileChooser jFileChooser;

    public MusicPlayerGUI(){
        super("Luke Player");

        setSize(400, 600);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        setLayout(null);

        getContentPane().setBackground(FRAME_COLOR);

        jFileChooser = new JFileChooser();
        jFileChooser.setCurrentDirectory(new File("src/songs"));

        addGuiComponents();
    }

    private void addGuiComponents(){
        addToolBar();

        JLabel songImage = new JLabel(loadImage("src/images/record.jpg"));
        songImage.setBounds(0, 50, getWidth() - 20, 225);
        add(songImage);

        JLabel songTitle = new JLabel("Song Title");
        songTitle.setBounds(0, 285, getWidth() - 10, 30);
        songTitle.setFont(new Font("Arial", Font.BOLD, 24));
        songTitle.setForeground(TEXT_COLOR);
        songTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(songTitle);

        JLabel songArtist = new JLabel("Artist");
        songArtist.setBounds(0, 315, getWidth() - 10, 30);
        songArtist.setFont(new Font("Arial", Font.PLAIN, 24));
        songArtist.setForeground(TEXT_COLOR);
        songArtist.setHorizontalAlignment(SwingConstants.CENTER);
        add(songArtist);

        JSlider playbackSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 0);
        playbackSlider.setBounds(getWidth()/2 - 300/2, 365, 300, 40);
        playbackSlider.setBackground(null);
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
                jFileChooser.showOpenDialog(MusicPlayerGUI.this);
                File selectFile = jFileChooser.getSelectedFile();

                if(selectFile != null){
                    Song song = new Song(selectFile.getPath());

                    musicPlayer.loadSong(song);
                }
            }
        });
        songMenu.add(loadSong);

        //Playlist
        JMenu playlistMenu = new JMenu("Playlist");
        menuBar.add(playlistMenu);

        JMenuItem createPlaylist = new JMenuItem("Create Playlist");
        playlistMenu.add(createPlaylist);

        JMenuItem loadPLaylist = new JMenuItem("Load Playlist");
        playlistMenu.add(loadPLaylist);

        add(toolBar);
    }

    private void addPlaybackButtons(){
        JPanel playbackButtons = new JPanel();
        playbackButtons.setBounds(0, 435, getWidth() - 10, 80);
        playbackButtons.setBackground(null);

        JButton prevButton = new JButton(loadImage("src/images/prev.png"));
        prevButton.setBorderPainted(false);
        prevButton.setBackground(null);
        playbackButtons.add(prevButton);

        JButton playButton = new JButton(loadImage("src/images/play.png"));
        playButton.setBorderPainted(false);
        playButton.setBackground(null);
        playbackButtons.add(playButton);

        JButton pauseButton = new JButton(loadImage("src/images/pause.png"));
        pauseButton.setBorderPainted(false);
        pauseButton.setBackground(null);
        pauseButton.setVisible(false);
        playbackButtons.add(pauseButton);

        JButton nextButton = new JButton(loadImage("src/images/next.png"));
        nextButton.setBorderPainted(false);
        nextButton.setBackground(null);
        playbackButtons.add(nextButton);

        add(playbackButtons);
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
