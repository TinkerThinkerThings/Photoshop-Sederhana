import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

public class LoadImageApp extends JFrame {
    private JLabel imageLabel;
    private JLabel rgbLabel;
    private JLabel resolutionLabel;
    private JButton zoomInButton;
    private JButton zoomOutButton;

    // Konstruktor

    public LoadImageApp() {
        setTitle("Cargar Imagen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

        // Membuat Tombol "Insert Picture"
        JButton loadButton = new JButton("Insert Picture");
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        rgbLabel = new JLabel();
        resolutionLabel = new JLabel();
        zoomInButton = new JButton("Zoom In");
        zoomOutButton = new JButton("Zoom Out");
        JButton showResolutionButton = new JButton("Detail");
        zoomInButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomIn();
            }
        });

        zoomOutButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                zoomOut();
            }
        });

        // Menambahkan Aksi ke Tombol "Tampilkan Resolusi"
        showResolutionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                // Mengambil Teks Resolusi dari label "resolutionLabel"
                String resolutionText = resolutionLabel.getText();

                // Menampilkan resolusi dalam pesan pop-up
                JOptionPane.showMessageDialog(null, resolutionText, "Resolusi Gambar", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // Menambahkan aksi ke tombol "Muat Gambar"
        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        // Memuat gambar yang dipilih
                        BufferedImage image = ImageIO.read(selectedFile);
                        ImageIcon icon = new ImageIcon(image);
                        imageLabel.setIcon(icon);
                        rgbLabel.setText(""); // Menghapus teks RGB saat gambar diubah
                        int width = image.getWidth();
                        int height = image.getHeight();
                        resolutionLabel.setText("Resolusi: " + width + "x" + height);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        // Menambahkan MouseListener untuk label gambar
        imageLabel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (imageLabel.getIcon() != null) {
                    ImageIcon icon = (ImageIcon) imageLabel.getIcon();
                    BufferedImage image = (BufferedImage) icon.getImage();
                    int x = e.getX();
                    int y = e.getY();
                    Color pixelColor = new Color(image.getRGB(x, y));
                    rgbLabel.setText(
                            "RGB: " + pixelColor.getRed() + ", " + pixelColor.getGreen() + ", " + pixelColor.getBlue());
                }
            }
        });

        // Menambahkan komponen ke frame
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);
        add(rgbLabel, BorderLayout.SOUTH);

        // Menambahkan tombol ke status bar file
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem insertPictureMenuItem = new JMenuItem("Insert Picture");
        insertPictureMenuItem.addActionListener(loadButton.getActionListeners()[0]);
        fileMenu.add(insertPictureMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        // Menambahkan  status bar view
        JMenu viewMenu = new JMenu("View");
        JMenuItem zoomInMenuItem = new JMenuItem("Zoom In");
        zoomInMenuItem.addActionListener(zoomInButton.getActionListeners()[0]);
        viewMenu.add(zoomInMenuItem);

        JMenuItem zoomOutMenuItem = new JMenuItem("Zoom Out");
        zoomOutMenuItem.addActionListener(zoomOutButton.getActionListeners()[0]);
        viewMenu.add(zoomOutMenuItem);
        //resolusi
        JMenuItem resolutionItem = new JMenuItem("Detail");
        resolutionItem.addActionListener(showResolutionButton.getActionListeners()[0]);
        viewMenu.add(resolutionItem);
        menuBar.add(viewMenu);

        // menambahkan status bar edit
        JMenu editMenu = new JMenu("Edit");
        menuBar.add(editMenu);
    }

    private void zoomIn() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage();
            int width = image.getWidth(imageLabel);
            int height = image.getHeight(imageLabel);
            int newWidth = (int) (width * 1.1);
            int newHeight = (int) (height * 1.1);
            Image newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(newImage));
        }
    }

    private void zoomOut() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage();
            int width = image.getWidth(imageLabel);
            int height = image.getHeight(imageLabel);
            int newWidth = (int) (width / 1.1);
            int newHeight = (int) (height / 1.1);
            Image newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
            imageLabel.setIcon(new ImageIcon(newImage));
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                LoadImageApp app = new LoadImageApp();
                app.setVisible(true);
            }
        });
    }
}
