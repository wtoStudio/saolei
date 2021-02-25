package personal.wt.cleaner;

import javax.swing.*;
import java.awt.*;

public class GameFrame extends JFrame {
    private GamePan gamePan = new GamePan();

    public GameFrame(){
        this.gamePan.requestFocus(true);
        this.setTitle("扫雷-DEMO");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.add(gamePan, BorderLayout.CENTER);
        this.pack();
        this.setVisible(true);
        this.setResizable(false);
    }
}
