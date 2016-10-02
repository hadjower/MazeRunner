package com.maze.runner.controller;

import com.maze.runner.modul.ImageCreator;
import com.maze.runner.modul.Maze;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import java.awt.*;
import java.util.Random;

public class Controller {

    public static Controller instance;
    private GraphicsContext gc;
    private Maze maze;
    private int scale;

    @FXML
    Canvas field;
    @FXML
    public Button findExitBtn;

    //todo make visual part prettier
    @FXML
    public void initialize() {
        instance = this;
        gc = field.getGraphicsContext2D();
    }

    private void drawMaze(int[][] f) {
        for (int i = 0; i < maze.getHeight(); i++) {
            for (int j = 0; j < maze.getWidth(); j++) {
                gc.setFill(f[i][j] == 1 ? Color.WHITE : Color.BLACK);
                gc.fillRect(j* scale, i* scale, scale, scale);
            }
        }
    }

    public void findExit(ActionEvent actionEvent) {
        if (findExitBtn.getText().equals("Find exit")){
            maze.findExit();
            findExitBtn.setText("Pause");
        } else if (findExitBtn.getText().equals("Pause")) {
            findExitBtn.setText("Countinue");
            maze.getThread().suspend();
        } else {
            findExitBtn.setText("Pause");
            maze.getThread().resume();
        }
    }

    public void drawRect(Point point, Color color) {
        gc.setFill(color);
        gc.fillRect(point.x * scale, point.y * scale, scale, scale);

    }


    public void showThreads(ActionEvent actionEvent) {
        Thread.getAllStackTraces().keySet().forEach((t) -> {
            System.out.println(t.getName() + "\nIs Daemon " + t.isDaemon() + "\nIs Alive " + t.isAlive());
            System.out.println();
            System.out.println("---------------------------------------------");
        });
    }

    public void generateMaze(ActionEvent actionEvent) {
        findExitBtn.setDisable(false);
        field.getGraphicsContext2D().clearRect(0, 0, field.getWidth(), field.getHeight());
        Random random = new Random();
        int height = random.nextInt(750) + 50;
        int width = random.nextInt(750) + 50;
        maze = new Maze(height%2 == 0 ? height - 1 : height, width%2 == 0 ? width - 1 : width);

        scale = 800 / (maze.getHeight() > maze.getWidth() ? maze.getHeight() : maze.getWidth());

        drawMaze(maze.getMaze());
        drawRect(maze.getEnter(), Color.GREEN);
        drawRect(maze.getExit(), Color.BLUEVIOLET);
    }

    public void clearPath(ActionEvent actionEvent) {
        if (maze.getThread() != null && maze.getThread().isAlive())
            maze.getThread().stop();
        findExitBtn.setText("Find exit");
        field.getGraphicsContext2D().clearRect(0, 0, field.getWidth(), field.getHeight());
        drawMaze(maze.getMaze());
        drawRect(maze.getEnter(), Color.GREEN);
        drawRect(maze.getExit(), Color.BLUEVIOLET);
    }

    public void clearMaze(ActionEvent actionEvent) {
        findExitBtn.setDisable(true);
        findExitBtn.setText("Find exit");
        if (maze.getThread() != null && maze.getThread().isAlive())
            maze.getThread().stop();
        field.getGraphicsContext2D().clearRect(0, 0, field.getWidth(), field.getHeight());
    }

    public void saveMaze(ActionEvent actionEvent) {
        ImageCreator.saveMaze(maze, "image.png");
        if (maze.getCopy() != null)
            ImageCreator.savePassedMaze(maze, "path.png");
    }
}
