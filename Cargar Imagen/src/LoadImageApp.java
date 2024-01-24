import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.color.ColorSpace;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.RescaleOp;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.text.View;

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
        JLabel secondLabel = new JLabel();
        secondLabel.setHorizontalAlignment(JLabel.CENTER);
        JButton loadButton = new JButton("Insert Picture");
        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        rgbLabel = new JLabel();
        resolutionLabel = new JLabel();
        zoomInButton = new JButton("Zoom In");
        zoomOutButton = new JButton("Zoom Out");
        JButton showResolutionButton = new JButton("Detail");
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);

        // Panel untuk canvas
        JPanel canvasPanel = new JPanel();
        canvasPanel.setLayout(new BorderLayout());
        canvasPanel.add(imageLabel, BorderLayout.CENTER);

        // Panel untuk elemen-elemen lainnya (histogram, dll.)
        JPanel otherElementsPanel = new JPanel();
        otherElementsPanel.setLayout(new BorderLayout());
        otherElementsPanel.add(rgbLabel, BorderLayout.NORTH); // Contoh: Tambahkan elemen lain di sini

        splitPane.setLeftComponent(canvasPanel);
        splitPane.setRightComponent(otherElementsPanel);

        add(splitPane, BorderLayout.CENTER);
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

                        int canvasWidth = 1000;
                        int canvasHeight = 1000;

                        if (image.getWidth() > canvasWidth || image.getHeight() > canvasHeight) {
                            double widthRatio = (double) canvasWidth / image.getWidth();
                            double heightRatio = (double) canvasHeight / image.getHeight();
                            double scaleFactor = Math.min(widthRatio, heightRatio);

                            int newWidth = (int) (image.getWidth() * scaleFactor);
                            int newHeight = (int) (image.getHeight() * scaleFactor);

                            Image scaledImage = image.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
                            image = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
                            Graphics2D g = image.createGraphics();
                            g.drawImage(scaledImage, 0, 0, null);
                            g.dispose();
                        }

                        ImageIcon icon = new ImageIcon(image);

                        imageLabel.setIcon(icon);
                        secondLabel.setIcon(icon);

                        // ... rest of your code ...
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

        JMenuItem rotateRightMenuItem = new JMenuItem("Rotate Right");
        rotateRightMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateImage(90); // Rotasi 90 derajat ke kanan
            }
        });
        viewMenu.add(rotateRightMenuItem);

        JMenuItem rotateLeftMenuItem = new JMenuItem("Rotate Left");
        rotateLeftMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                rotateImage(-90); // Rotasi 90 derajat ke kiri
            }
        });

        JMenuItem saveMenuItem = new JMenuItem("Save");
        saveMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImage();
            }
        });
        fileMenu.add(saveMenuItem);

        viewMenu.add(rotateLeftMenuItem);
        // menu save as
        JMenuItem saveAsMenuItem = new JMenuItem("Save As");
        saveAsMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveImageAs();
            }
        });
        fileMenu.add(saveAsMenuItem);

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

        JMenuItem brightenMenuItem = new JMenuItem("Brighten");
        brightenMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                brightenImage();
            }
        });
        editMenu.add(brightenMenuItem);
        // menambhkan menu brighten
        JMenuItem edgeDetectionMenuItem = new JMenuItem("Edge Detection");
        edgeDetectionMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                detectEdges();
            }
        });
        editMenu.add(edgeDetectionMenuItem);

        JMenuItem normalizeEdgesMenuItem = new JMenuItem("Normalize Edges");
        normalizeEdgesMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                normalizeEdges();
            }
        });
        editMenu.add(normalizeEdgesMenuItem);
        JMenuItem chaincodeMenuItem = new JMenuItem("Convert to Chaincode");
        chaincodeMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                convertToChaincode();
            }
        });
        editMenu.add(chaincodeMenuItem);

        JMenuItem mirrorMenuItem = new JMenuItem("Mirror");
        mirrorMenuItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                mirrorImage();
            }
        });
        viewMenu.add(mirrorMenuItem);

    }

    private void mirrorImage() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            // Proses cermin gambar
            AffineTransform tx = AffineTransform.getScaleInstance(-1, 1);
            tx.translate(-image.getWidth(null), 0);
            AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
            image = op.filter(image, null);

            // Tampilkan gambar yang sudah di-mirror di imageLabel
            ImageIcon mirroredIcon = new ImageIcon(image);
            imageLabel.setIcon(mirroredIcon);
        }
    }

    private void normalizeEdges() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            int[][] sobelVertical = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
            int[][] sobelHorizontal = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

            BufferedImage edgeDetectedImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

            for (int y = 1; y < image.getHeight() - 1; y++) {
                for (int x = 1; x < image.getWidth() - 1; x++) {
                    int sumX = 0, sumY = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int pixel = image.getRGB(x + i, y + j);
                            int gray = (int) (0.21 * ((pixel >> 16) & 0xff) + 0.72 * ((pixel >> 8) & 0xff) +
                                    0.07 * (pixel & 0xff));

                            sumX += sobelHorizontal[i + 1][j + 1] * gray;
                            sumY += sobelVertical[i + 1][j + 1] * gray;
                        }
                    }

                    int magnitude = (int) Math.sqrt(sumX * sumX + sumY * sumY);
                    magnitude = Math.min(255, Math.max(0, magnitude));
                    int edgePixel = 0xff000000 | (magnitude << 16) | (magnitude << 8) | magnitude;
                    edgeDetectedImage.setRGB(x, y, edgePixel);
                }
            }

            // Normalize edges for better visibility
            float[] scales = { 1f, 1f, 1f };
            float[] offsets = { 0, 0, 0 };
            RescaleOp rescaleOp = new RescaleOp(scales, offsets, null);
            BufferedImage normalizedImage = rescaleOp.filter(edgeDetectedImage, null);

            ImageIcon edgeIcon = new ImageIcon(normalizedImage);
            imageLabel.setIcon(edgeIcon);
        }
    }

    private void convertToChaincode() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            // Konversi tepi menjadi chaincode
            StringBuilder chaincode = new StringBuilder();
            boolean[][] visited = new boolean[image.getWidth()][image.getHeight()];

            // Temukan titik awal (pixel pertama yang ditemukan)
            int startX = -1, startY = -1;
            outerloop: for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    if ((image.getRGB(x, y) & 0x00FFFFFF) == 0xFFFFFF) {
                        startX = x;
                        startY = y;
                        break outerloop;
                    }
                }
            }

            if (startX != -1 && startY != -1) {
                int currentX = startX, currentY = startY;
                int nextX, nextY;
                int[] dx = { -1, -1, 0, 1, 1, 1, 0, -1 };
                int[] dy = { 0, -1, -1, -1, 0, 1, 1, 1 };
                int direction = 7; // Mulai dari arah barat laut

                do {
                    chaincode.append(direction);
                    visited[currentX][currentY] = true;

                    boolean foundNext = false;
                    for (int i = 0; i < 8; i++) {
                        nextX = currentX + dx[(direction + i) % 8];
                        nextY = currentY + dy[(direction + i) % 8];

                        if (nextX >= 0 && nextX < image.getWidth() &&
                                nextY >= 0 && nextY < image.getHeight() &&
                                (image.getRGB(nextX, nextY) & 0x00FFFFFF) == 0xFFFFFF &&
                                !visited[nextX][nextY]) {

                            currentX = nextX;
                            currentY = nextY;
                            direction = (direction + i + 5) % 8; // Update arah berikutnya
                            foundNext = true;
                            break;
                        }
                    }

                    if (!foundNext)
                        break;

                } while (!(currentX == startX && currentY == startY));

                JOptionPane.showMessageDialog(null, "Chaincode: " + chaincode.toString(), "Chaincode Result",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "No edge found to convert to chaincode!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void detectEdges() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            int[][] sobelVertical = { { -1, 0, 1 }, { -2, 0, 2 }, { -1, 0, 1 } };
            int[][] sobelHorizontal = { { -1, -2, -1 }, { 0, 0, 0 }, { 1, 2, 1 } };

            BufferedImage edgeDetectedImage = new BufferedImage(
                    image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);

            for (int y = 1; y < image.getHeight() - 1; y++) {
                for (int x = 1; x < image.getWidth() - 1; x++) {
                    int sumX = 0, sumY = 0;
                    for (int i = -1; i <= 1; i++) {
                        for (int j = -1; j <= 1; j++) {
                            int pixel = image.getRGB(x + i, y + j);
                            int gray = (int) (0.21 * ((pixel >> 16) & 0xff) + 0.72 * ((pixel >> 8) & 0xff) +
                                    0.07 * (pixel & 0xff));

                            sumX += sobelHorizontal[i + 1][j + 1] * gray;
                            sumY += sobelVertical[i + 1][j + 1] * gray;
                        }
                    }

                    int magnitude = (int) Math.sqrt(sumX * sumX + sumY * sumY);
                    magnitude = Math.min(255, Math.max(0, magnitude));
                    int edgePixel = 0xff000000 | (magnitude << 16) | (magnitude << 8) | magnitude;
                    edgeDetectedImage.setRGB(x, y, edgePixel);
                }
            }

            ImageIcon edgeIcon = new ImageIcon(edgeDetectedImage);
            imageLabel.setIcon(edgeIcon);
        }

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

    private void brightenImage() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            float scaleFactor = 1.2f; // Faktor peningkatan kecerahan
            RescaleOp op = new RescaleOp(scaleFactor, 0, null);
            BufferedImage brightenedImage = op.filter(image, null);

            ImageIcon brightenedIcon = new ImageIcon(brightenedImage);
            imageLabel.setIcon(brightenedIcon);
        }
    }

    // Fungsi untuk rotasi gambar
    private void rotateImage(int degrees) {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            Image image = icon.getImage();

            // Mendapatkan lebar dan tinggi gambar asli
            int width = image.getWidth(null);
            int height = image.getHeight(null);

            // Membuat BufferedImage baru dengan mode RGBA (agar bisa menambahkan latar
            // belakang)
            BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);

            // Membuat latar belakang yang sesuai dengan gambar asli
            Graphics2D g2d = bufferedImage.createGraphics();
            g2d.setColor(Color.WHITE); // Atur latar belakang sesuai kebutuhan
            g2d.fillRect(0, 0, width, height);

            // Menghitung transformasi rotasi dan menerapkannya ke gambar asli
            g2d.rotate(Math.toRadians(degrees), width / 2.0, height / 2.0);
            g2d.drawImage(image, 0, 0, null);
            g2d.dispose();

            // Menampilkan gambar yang diputar kembali di imageLabel
            ImageIcon rotatedIcon = new ImageIcon(bufferedImage);
            imageLabel.setIcon(rotatedIcon);
        }
    }

    private void saveImage() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            JFileChooser fileChooser = new JFileChooser();
            int saveValue = fileChooser.showSaveDialog(null);
            if (saveValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    ImageIO.write(image, "png", file); // Menggunakan format PNG untuk contoh ini
                    JOptionPane.showMessageDialog(null, "Image saved successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    private void saveImageAs() {
        if (imageLabel.getIcon() != null) {
            ImageIcon icon = (ImageIcon) imageLabel.getIcon();
            BufferedImage image = new BufferedImage(
                    icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_RGB);

            Graphics g = image.createGraphics();
            icon.paintIcon(null, g, 0, 0);
            g.dispose();

            JFileChooser fileChooser = new JFileChooser();
            int saveValue = fileChooser.showSaveDialog(null);
            if (saveValue == JFileChooser.APPROVE_OPTION) {
                File file = fileChooser.getSelectedFile();
                try {
                    // Mendapatkan ekstensi file yang dipilih
                    String fileName = file.getName();
                    String formatName = fileName.substring(fileName.lastIndexOf(".") + 1);

                    // Menulis gambar dengan format yang dipilih oleh pengguna
                    ImageIO.write(image, formatName, file);
                    JOptionPane.showMessageDialog(null, "Image saved successfully.", "Success",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
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
