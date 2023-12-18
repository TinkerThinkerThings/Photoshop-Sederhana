import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.color.ColorSpace;
import java.awt.image.ColorConvertOp;

public class LoadImageApp extends JFrame {
    private JLabel imageLabel;
    private JLabel rgbLabel;
    private JLabel resolutionLabel;
    private JButton zoomInButton;
    private JButton zoomOutButton;

    public LoadImageApp() {
        setTitle("Cargar Imagen");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);

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

        showResolutionButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String resolutionText = resolutionLabel.getText();
                JOptionPane.showMessageDialog(null, resolutionText, "Resolusi Gambar", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        loadButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);
                        ImageIcon icon = new ImageIcon(image);
                        imageLabel.setIcon(icon);
                        rgbLabel.setText("");
                        int width = image.getWidth();
                        int height = image.getHeight();
                        resolutionLabel.setText("Resolusi: " + width + "x" + height);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });

        imageLabel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                if (imageLabel.getIcon() != null) {
                    ImageIcon icon = (ImageIcon) imageLabel.getIcon();
                    BufferedImage image = (BufferedImage) icon.getImage();
                    int x = e.getX();
                    int y = e.getY();
                    if (x < image.getWidth() && y < image.getHeight()) {
                        Color pixelColor = new Color(image.getRGB(x, y));
                        rgbLabel.setText(
                                "RGB: " + pixelColor.getRed() + ", " + pixelColor.getGreen() + ", "
                                        + pixelColor.getBlue());
                    }
                }
            }
        });

        JPanel topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout());

        setLayout(new BorderLayout());
        add(topPanel, BorderLayout.NORTH);
        add(imageLabel, BorderLayout.CENTER);
        add(rgbLabel, BorderLayout.SOUTH);

        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("File");
        JMenuItem insertPictureMenuItem = new JMenuItem("Insert Picture");
        insertPictureMenuItem.addActionListener(loadButton.getActionListeners()[0]);
        fileMenu.add(insertPictureMenuItem);
        menuBar.add(fileMenu);
        setJMenuBar(menuBar);

        JMenu viewMenu = new JMenu("View");
        JMenuItem zoomInMenuItem = new JMenuItem("Zoom In");
        zoomInMenuItem.addActionListener(zoomInButton.getActionListeners()[0]);
        viewMenu.add(zoomInMenuItem);

        JMenuItem zoomOutMenuItem = new JMenuItem("Zoom Out");
        zoomOutMenuItem.addActionListener(zoomOutButton.getActionListeners()[0]);
        viewMenu.add(zoomOutMenuItem);

        JMenuItem resolutionItem = new JMenuItem("Detail");
        resolutionItem.addActionListener(showResolutionButton.getActionListeners()[0]);
        viewMenu.add(resolutionItem);
        menuBar.add(viewMenu);
        JMenu histogramMenu = new JMenu("Histogram");

        JMenuItem colorHistogramItem = new JMenuItem("Color Histogram");

        colorHistogramItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                showColorHistogram();
            }
        });
        histogramMenu.add(colorHistogramItem);
        menuBar.add(histogramMenu);

        JMenu editMenu = new JMenu("Edit");
        JMenuItem resizeMenuItem = new JMenuItem("Resize Image");
        resizeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (imageLabel.getIcon() != null) {
                    ImageIcon icon = (ImageIcon) imageLabel.getIcon();
                    BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                            BufferedImage.TYPE_INT_ARGB);
                    Graphics g = image.createGraphics();
                    icon.paintIcon(null, g, 0, 0);
                    g.dispose();
                    int newWidth = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter new width:"));
                    int newHeight = Integer.parseInt(JOptionPane.showInputDialog(null, "Enter new height:"));
                    Image newImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                    imageLabel.setIcon(new ImageIcon(newImage));
                    resolutionLabel.setText("Resolusi: " + newWidth + "x" + newHeight);
                }
            }

        });
        editMenu.add(resizeMenuItem);

        JMenuItem grayscaleMenuItem = new JMenuItem("Grayscale");
        grayscaleMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertToGrayscale();
            }
        });
        editMenu.add(grayscaleMenuItem);

        menuBar.add(editMenu);

        JMenuItem blackWhiteMenuItem = new JMenuItem("Black and White");
        blackWhiteMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertToBlackAndWhite();
            }
        });
        editMenu.add(blackWhiteMenuItem);

        menuBar.add(editMenu);
        JMenuItem negativeMenuItem = new JMenuItem("Negative");
        negativeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertToNegative();
            }
        });
        editMenu.add(negativeMenuItem);
        menuBar.add(editMenu);
        // Menambahkan item menu Histogram Warna
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

    private void convertToGrayscale() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            BufferedImage grayscaleImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_GRAY);

            ColorConvertOp op = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(image, grayscaleImage);

            ImageIcon grayscaleIcon = new ImageIcon(grayscaleImage);
            imageLabel.setIcon(grayscaleIcon);
        }
    }

    private void convertToBlackAndWhite() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            BufferedImage blackWhiteImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_BYTE_BINARY);

            ColorConvertOp op = new ColorConvertOp(
                    ColorSpace.getInstance(ColorSpace.CS_GRAY), null);
            op.filter(image, blackWhiteImage);

            ImageIcon blackWhiteIcon = new ImageIcon(blackWhiteImage);
            imageLabel.setIcon(blackWhiteIcon);
        }
    }

    private void convertToNegative() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    int rgba = image.getRGB(x, y);
                    Color color = new Color(rgba, true);

                    int red = 255 - color.getRed();
                    int green = 255 - color.getGreen();
                    int blue = 255 - color.getBlue();

                    Color negativeColor = new Color(red, green, blue, color.getAlpha());
                    image.setRGB(x, y, negativeColor.getRGB());
                }
            }

            ImageIcon negativeIcon = new ImageIcon(image);
            imageLabel.setIcon(negativeIcon);
        }
    }

    // Method untuk menampilkan histogram warna
    private void showColorHistogram() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                    BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            int[] redHistogram = new int[256];
            int[] greenHistogram = new int[256];
            int[] blueHistogram = new int[256];

            // Hitung histogram untuk setiap komponen warna
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    Color pixelColor = new Color(image.getRGB(x, y));
                    redHistogram[pixelColor.getRed()]++;
                    greenHistogram[pixelColor.getGreen()]++;
                    blueHistogram[pixelColor.getBlue()]++;
                }
            }

            // Buat BufferedImage baru untuk menampung histogram
            BufferedImage histogramImage = new BufferedImage(800, 300, BufferedImage.TYPE_INT_ARGB);
            Graphics2D histogramGraphics = histogramImage.createGraphics();
            drawHistogram(histogramGraphics, redHistogram, greenHistogram, blueHistogram);
            histogramGraphics.dispose();

            // Tampilkan histogram dalam jendela pop-up
            ImageIcon histogramIcon = new ImageIcon(histogramImage);
            JLabel histogramLabel = new JLabel(histogramIcon);
            JOptionPane.showMessageDialog(null, histogramLabel, "Color Histogram", JOptionPane.PLAIN_MESSAGE);
        }
    }

    private void drawHistogram(Graphics g, int[] redHistogram, int[] greenHistogram, int[] blueHistogram) {
        int maxFrequency = Math.max(
                Math.max(Arrays.stream(redHistogram).max().orElse(0), Arrays.stream(greenHistogram).max().orElse(0)),
                Arrays.stream(blueHistogram).max().orElse(0));

        int histogramHeight = 200;
        int barWidth = 1;
        int spacing = 2;
        int startX = 20;
        int startY = histogramHeight + 20;

        g.setColor(Color.RED);
        for (int i = 0; i < 256; i++) {
            int barHeight = (int) ((double) redHistogram[i] / maxFrequency * histogramHeight);
            g.fillRect(startX + i * (barWidth + spacing), startY - barHeight, barWidth, barHeight);
        }

        g.setColor(Color.GREEN);
        for (int i = 0; i < 256; i++) {
            int barHeight = (int) ((double) greenHistogram[i] / maxFrequency * histogramHeight);
            g.fillRect(startX + i * (barWidth + spacing), startY + histogramHeight + 10 - barHeight, barWidth,
                    barHeight);
        }

        g.setColor(Color.BLUE);
        for (int i = 0; i < 256; i++) {
            int barHeight = (int) ((double) blueHistogram[i] / maxFrequency * histogramHeight);
            g.fillRect(startX + i * (barWidth + spacing), startY + 2 * (histogramHeight + 10) - barHeight, barWidth,
                    barHeight);
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
