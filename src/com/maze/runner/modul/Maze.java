package com.maze.runner.modul;

import java.awt.*;

import com.maze.runner.controller.Controller;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Random;
import java.util.Stack;


public class Maze {

    private int height;
    private int width;

    //    private boolean[][] mAze;
    private Point enter;
    private Point exit;


    private int[][] maze;
    private int[][] copy;
    private Point currentPoint;
    private int lastDirection;
    private Thread thread;


    public Maze(int height, int width) {
        this.height = height;
        this.width = width;

        fill();
        generateMaze();

//        mAze = getBoolean();

        enter.translate(-1, 0);

    }

    public Point getEnter() {
        return enter;
    }

    public Point getExit() {
        return exit;
    }

//    public boolean[][] getmAze() {
//        return mAze;
//    }

    public void findExit() {
        Runnable runnable = this::move;
        thread = new Thread(runnable);
        thread.setDaemon(true);
        thread.start();
    }

    private void sleep(int time) {
        try {
            Thread.currentThread().sleep(time);
        } catch (InterruptedException e) {
            System.err.println("Sleep error!!!");
        }
    }

    private void move() {

        copy = getCopy(maze);

        Point cursor = enter;

        Stack<Point> cursors = new Stack<>();
        Stack<Point> forks = new Stack<>();
        ArrayList<Point> pathes = new ArrayList<>();
        Random random = new Random();


        do {

            int pathCounter = 0;
            try {
                if (copy[cursor.y + 1][cursor.x] == 1) {
                    pathCounter++;
                    pathes.add(new Point(cursor.x, cursor.y + 1));
                }
                if (copy[cursor.y - 1][cursor.x] == 1) {
                    pathCounter++;
                    pathes.add(new Point(cursor.x, cursor.y - 1));
                }
                if (copy[cursor.y][cursor.x + 1] == 1) {
                    pathCounter++;
                    pathes.add(new Point(cursor.x + 1, cursor.y));
                }
                if (copy[cursor.y][cursor.x - 1] == 1) {
                    pathCounter++;
                    pathes.add(new Point(cursor.x - 1, cursor.y));
                }
            } catch (ArrayIndexOutOfBoundsException e) {
//                if (!cursor.equals(enter))
//                    break;
            }

            if (pathCounter > 0) {
                if (pathCounter > 1)
                    forks.push(cursor);
                cursors.push(cursor);

                cursor = pathes.get(random.nextInt(pathes.size()));
                pathes.clear();

                copy[cursor.y][cursor.x] = 2;
                Controller.instance.drawRect(cursor, Color.RED);
            } else if (!cursors.isEmpty()) {
                Controller.instance.drawRect(cursor, Color.BLUE);
                copy[cursor.y][cursor.x] = 3;
                while (!forks.peek().equals(cursors.peek())) {
                    Point point = cursors.pop();
                    Controller.instance.drawRect(point, Color.BLUE);

                    copy[point.y][point.x] = 3;
//                    sleep(5);
                }
                cursor = forks.pop();
            } else {
                System.err.println("There is no exit!!!");
                break;
            }

//            sleep(5);

        } while (!cursor.equals(exit));
        System.out.println("Exit is found!!!");

    }

    private int[][] getCopy(int[][] mAze) {
        int[][] copy = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                copy[i][j] = maze[i][j];
            }
        }
        return copy;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    private void fill() {
        maze = new int[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if ((i % 2 != 0 && j % 2 != 0) && (i < height - 1 && j < width - 1)) {
                    maze[i][j] = 1;
                }
            }
        }
    }

    private void drawSide(Point point, int direction) {
        switch (direction) {
            case 1:
                maze[point.y][point.x - 1] = -1;
                break;
            case 2:
                maze[point.y][point.x + 1] = -1;
                break;
            case 3:
                maze[point.y + 1][point.x] = -1;
                break;
            case 4:
                maze[point.y - 1][point.x] = -1;
                break;
        }
        maze[point.y][point.x] = -1;
    }

    private boolean checkDirections(boolean[] tempCount) {
        for (int i = 0; i < 4; i++) {
            if (!tempCount[i])
                return false;
        }
        return true;
    }

    private Point randNeighbor() {
        Random random = new Random();
        Point tempPoint = new Point();
        int tempDirection;
        boolean[] count = new boolean[4];
        do {
            do {
                tempDirection = random.nextInt(4) + 1;
                if (lastDirection == 1 && (tempDirection == 3 || tempDirection == 4 || tempDirection == 1)) {
                    break;
                }
                if (lastDirection == 2 && (tempDirection == 3 || tempDirection == 4 || tempDirection == 2)) {
                    break;
                }
                if (lastDirection == 3 && (tempDirection == 1 || tempDirection == 2 || tempDirection == 3)) {
                    break;
                }
                if (lastDirection == 4 && (tempDirection == 1 || tempDirection == 2 || tempDirection == 4)) {
                    break;
                }
                if (lastDirection == 0) {
                    break;
                }
            } while (true);
            lastDirection = tempDirection;
            switch (lastDirection) {
                case 1:
                    tempPoint.x = currentPoint.x + 2;
                    tempPoint.y = currentPoint.y;
                    if (tempPoint.y < height - 1 && tempPoint.y > 0 && tempPoint.x < width - 1 && tempPoint.x > 0) {
                        if (maze[tempPoint.y][tempPoint.x] == 1) {
                            drawSide(tempPoint, 1);
                            return tempPoint;
                        }
                    }
                    count[0] = true;
                    break;
                case 2:
                    tempPoint.x = currentPoint.x - 2;
                    tempPoint.y = currentPoint.y;
                    if (tempPoint.y < height - 1 && tempPoint.y > 0 && tempPoint.x < width - 1 && tempPoint.x > 0) {
                        if (maze[tempPoint.y][tempPoint.x] == 1) {
                            drawSide(tempPoint, 2);
                            return tempPoint;
                        }
                    }
                    count[1] = true;
                    break;
                case 3:
                    tempPoint.x = currentPoint.x;
                    tempPoint.y = currentPoint.y - 2;
                    if (tempPoint.y < height - 1 && tempPoint.y > 0 && tempPoint.x < width - 1 && tempPoint.x > 0) {
                        if (maze[tempPoint.y][tempPoint.x] == 1) {
                            drawSide(tempPoint, 3);
                            return tempPoint;
                        }
                    }
                    count[2] = true;
                    break;
                case 4:
                    tempPoint.x = currentPoint.x;
                    tempPoint.y = currentPoint.y + 2;
                    if (tempPoint.y < height - 1 && tempPoint.y > 0 && tempPoint.x < width - 1 && tempPoint.x > 0) {
                        if (maze[tempPoint.y][tempPoint.x] == 1) {
                            drawSide(tempPoint, 4);
                            return tempPoint;
                        }
                    }
                    count[3] = true;
                    break;
            }
        } while (!checkDirections(count));
        return null;
    }

    private void generateMaze() {
        Stack<Point> points = new Stack<>();
        Point tempPoint;
        enter = randStartPoint();
        points.add(enter);
        do {
            tempPoint = randNeighbor();
            if (tempPoint == null) {
                currentPoint = points.pop();
            } else {
                points.add(tempPoint);
                currentPoint = tempPoint;
            }
        } while (points.size() != 0);
        randFinalPoint();
        binarMaze();
    }

    private void binarMaze() {
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                if (maze[i][j] == -1)
                    maze[i][j] = 1;
            }
        }
    }

    private void randFinalPoint() {
        Random random = new Random();
        Point point = new Point();
        do {
            point.setLocation(width - 2, random.nextInt(height));
            if (maze[point.y][point.x] == -1) {
                if (width % 2 == 0) {
                    maze[point.y][point.x + 2] = -1;
                }
                maze[point.y][point.x + 1] = -1;
                exit = new Point(point.x + 1, point.y);
                break;
            }
        } while (true);
    }

    private Point randStartPoint() {
        Random random = new Random();
        Point point = new Point();
        do {
            point.setLocation(1, (random.nextInt(height - 1) + 1));
            if (point.y % 2 != 0) {
                break;
            }
        } while (true);
        maze[point.y][point.x - 1] = -1;
        currentPoint = point;
        maze[point.y][point.x] = -1;
        return point;
    }

    private boolean[][] getBoolean() {
        boolean[][] tempMaze = new boolean[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                tempMaze[i][j] = maze[i][j] != 0;
            }
        }
        return tempMaze;
    }

    public Thread getThread() {
        return thread;
    }

    public int[] getRGBMatrix() {
        int white = java.awt.Color.WHITE.getRGB();
        int black = java.awt.Color.BLACK.getRGB();
        int greenRGB = java.awt.Color.GREEN.getRGB();
        int orangeRGB = java.awt.Color.ORANGE.getRGB();

        int[] RGBMatrix = new int[width * height * 4];
        int p = 0;

        for (int i = 0; i < height * 2; i++) {
            for (int j = 0; j < width * 2; j++) {

                if (enter.x == j / 2 && enter.y == i / 2) {
                    RGBMatrix[p++] = greenRGB;
                } else if (exit.x == j / 2 && exit.y == i / 2) {
                    RGBMatrix[p++] = orangeRGB;
                } else
                    RGBMatrix[p++] = maze[i / 2][j / 2] == 1 ? white : black;
            }
        }

        return RGBMatrix;
    }

    public int[][] getCopy() {
        return copy;
    }

    public int[] getPassedRGBMatrix() {
        int whiteRGB = java.awt.Color.WHITE.getRGB();
        int blackRGB = java.awt.Color.BLACK.getRGB();
        int greenRGB = java.awt.Color.GREEN.getRGB();
        int orangeRGB = java.awt.Color.ORANGE.getRGB();
        int redRGB = java.awt.Color.RED.getRGB();
        int blueRGB = java.awt.Color.BLUE.getRGB();

        int[] RGBMatrix = new int[width * height * 4];
        int p = 0;

        for (int i = 0; i < height * 2; i++) {
            for (int j = 0; j < width * 2; j++) {
//                RGBMatrix[p++] = mAze[i/2][j/2] ? white : black;
                if (enter.x == j / 2 && enter.y == i / 2) {
                    RGBMatrix[p++] = greenRGB;
                } else if (exit.x == j / 2 && exit.y == i / 2) {
                    RGBMatrix[p++] = orangeRGB;
                } else {
                    switch (copy[i / 2][j / 2]) {
                        case 0:
                            RGBMatrix[p++] = blackRGB;
                            break;
                        case 1:
                            RGBMatrix[p++] = whiteRGB;
                            break;
                        case 2:
                            RGBMatrix[p++] = redRGB;
                            break;
                        case 3:
                            RGBMatrix[p++] = blueRGB;
                            break;
                    }
                }
            }
        }

        return RGBMatrix;
    }

    public int[][] getMaze() {
        return maze;
    }
}
