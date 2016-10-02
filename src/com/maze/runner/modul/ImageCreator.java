package com.maze.runner.modul;


import javax.imageio.ImageIO;
import java.applet.Applet;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.MemoryImageSource;
import java.io.File;
import java.io.IOException;


public class ImageCreator {

    public static void saveMaze(Maze maze, String path) {
        int[] matrix = maze.getRGBMatrix();
        MemoryImageSource mis = new MemoryImageSource(maze.getWidth() * 2, maze.getHeight() * 2, matrix, 0, maze.getWidth() * 2);
        Applet applet = new Applet();
        Image img = applet.createImage(mis);
        BufferedImage image = toBufferedImage(img);

        File file = new File(path);
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void savePassedMaze(Maze maze, String path) {
        int[] matrix = maze.getPassedRGBMatrix();
        MemoryImageSource mis = new MemoryImageSource(maze.getWidth() * 2, maze.getHeight() * 2, matrix, 0, maze.getWidth() * 2);
        Applet applet = new Applet();
        Image img = applet.createImage(mis);
        BufferedImage image = toBufferedImage(img);

        File file = new File(path);
        try {
            ImageIO.write(image, "png", file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static BufferedImage toBufferedImage(Image img) {
        if (img instanceof BufferedImage) {
            return (BufferedImage) img;
        }

        // Create a buffered image with transparency
        BufferedImage bimage = new BufferedImage(img.getWidth(null), img.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Draw the image on to the buffered image
        Graphics2D bGr = bimage.createGraphics();
        bGr.drawImage(img, 0, 0, null);
        bGr.dispose();

        // Return the buffered image
        return bimage;
    }
}
