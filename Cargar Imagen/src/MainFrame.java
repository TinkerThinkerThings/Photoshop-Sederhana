import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainFrame extends JFrame {
  public MainFrame() {
    setTitle("Cargar Imagen");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(1280, 720);

    centerFrameOnScreen();
  }

  private void centerFrameOnScreen() {
    // Untuk mendapatkan ukuran layar
    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

    // Untuk mendapatkan ukuran frame
    Dimension frameSize = getSize();

    // Perhitungan posisi x dan y agar frame berada di tengah
    int x = (screenSize.width - frameSize.width) / 2;
    int y = (screenSize.height - frameSize.height) / 2;

    // Set posisi frame
    setLocation(x, y);
  }
}