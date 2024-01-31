import javax.swing.*;
import java.awt.*;

public class MusicPlayerGUI extends JFrame {
    public static final Color FRAME_COLOR = Color.BLACK;
    public static final Color TEXT_COLOR = Color.WHITE;

    public MusicPlayerGUI(){
        super("Luke Player");

        setSize(400, 600);

        setDefaultCloseOperation(EXIT_ON_CLOSE);

        setLocationRelativeTo(null);

        setResizable(false);

        setLayout(null);

        getContentPane().setBackground(FRAME_COLOR);

        addGuiComponents();
    }

    private void addGuiComponents(){
        addToolBar();
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
}
