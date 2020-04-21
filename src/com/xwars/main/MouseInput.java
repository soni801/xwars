package com.xwars.main;

/*
 * Author: soni801
 */

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class MouseInput extends MouseAdapter
{
    private Handler handler;
    private HUD hud;
    private Game game;
    private Customise customise;
    private Settings settings;

    public int panX, panY;
    public int dragX, dragY;

    int startX, startY;

    public MouseInput(Handler handler, HUD hud, Game game, Customise customise, Settings settings)
    {
        this.handler = handler;
        this.hud = hud;
        this.game = game;
        this.customise = customise;
        this.settings = settings;
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        int mx = e.getX();
        int my = e.getY();

        startX = mx;
        startY = my;
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        int mx = e.getX();
        int my = e.getY();

        switch (game.gameState)
        {
            case Menu :
                if (mouseOver(mx, my, Game.WIDTH / 2 - 2 - 100, Game.HEIGHT - 220 - 30, 200, 40))
                {
                    game.gameState = STATE.Customise;
                }
                else if (mouseOver(mx, my, Game.WIDTH / 2 - 2 - 100, Game.HEIGHT - 220 + 60 - 30, 200, 40))
                {
                    game.gameState = STATE.Settings;
                }
                else if (mouseOver(mx, my, Game.WIDTH / 2 - 2 - 100, Game.HEIGHT - 220  + 120 - 30, 200, 40))
                {
                    System.exit(1);
                }
                break;
            case Customise :
                if (mouseOver(mx, my, Game.WIDTH / 2 - 100, Game.HEIGHT - 50 - 10 - 50 - 10, 200, 50))
                {
                    game.gameState = STATE.Game;
                    hud.generate(customise.boardSize[0], customise.boardSize[1]);
                }
                if (mouseOver(mx, my, Game.WIDTH / 2 - 100, Game.HEIGHT - 50 - 10, 200, 50))
                {
                    game.gameState = STATE.Menu;
                    customise.changingName = 0;
                    customise.colorPicker = 0;
                }
                if (mouseOver(mx, my, 10, Game.HEIGHT - 10 - 60, 30, 30))
                {
                    if (customise.colorPicker != 1)
                    {
                        customise.r = customise.playerColor[0].getRed();
                        customise.g = customise.playerColor[0].getGreen();
                        customise.b = customise.playerColor[0].getBlue();

                        customise.colorPicker = 1;
                    }
                    else customise.colorPicker = 0;
                }
                if (mouseOver(mx, my, Game.WIDTH - 45, Game.HEIGHT - 10 - 60, 30, 30))
                {
                    if (customise.colorPicker != 2)
                    {
                        customise.r = customise.playerColor[1].getRed();
                        customise.g = customise.playerColor[1].getGreen();
                        customise.b = customise.playerColor[1].getBlue();

                        customise.colorPicker = 2;
                    }
                    else customise.colorPicker = 0;
                }
                if (mouseOver(mx, my, 10 + 30 + 10, Game.HEIGHT - 10 - 60, 30, 30))
                {
                    Random r = new Random();
                    customise.playerColor[0] = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
                }
                if (mouseOver(mx, my, Game.WIDTH - 15 - 30 - 10 - 30, Game.HEIGHT - 10 - 60, 30, 30))
                {
                    Random r = new Random();
                    customise.playerColor[1] = new Color(r.nextInt(255), r.nextInt(255), r.nextInt(255));
                }
                if (mouseOver(mx, my, 0, Game.HEIGHT - 35, 160, 35))
                {
                    customise.playerName[0] = "";
                    customise.changingName = 1;
                }
                if (mouseOver(mx, my, Game.WIDTH - 7 - 160, Game.HEIGHT - 35, 160, 35))
                {
                    customise.playerName[1] = "";
                    customise.changingName = 2;
                }
                if (mouseOver(mx, my, Game.WIDTH / 2 - 10 - 5 - 100, 120, 20, 20))
                {
                    if (customise.boardSize[0] < 100) customise.boardSize[0]++;
                }
                if (mouseOver(mx, my, Game.WIDTH / 2 - 10 - 5 - 100, 120 + 20, 20, 20))
                {
                    if (customise.boardSize[0] > 1) customise.boardSize[0]--;
                }
                if (mouseOver(mx, my, Game.WIDTH / 2 + 10 + 100 - 20 + 1, 120, 20, 20))
                {
                    if (customise.boardSize[1] < 50) customise.boardSize[1]++;
                }
                if (mouseOver(mx, my, Game.WIDTH / 2 + 10 + 100 - 20 + 1, 120 + 20, 20, 20))
                {
                    if (customise.boardSize[1] > 1) customise.boardSize[1]--;
                }
                break;
            case Settings :
                if (mouseOver(mx, my, Game.WIDTH / 2 - 290, Game.HEIGHT / 2 - 20 - 13, 40, 40)) if (Settings.settings.get("theme").equals("dark")) Settings.settings.replace("theme", "light");
                if (mouseOver(mx, my, Game.WIDTH / 2 - 290, Game.HEIGHT / 2 - 20 + 80 - 13, 40, 40)) if (Settings.settings.get("resolution").equals("1280x720")) Settings.settings.replace("resolution", "960x540");
                if (mouseOver(mx, my, Game.WIDTH / 2 - 290, Game.HEIGHT / 2 - 20 + 160 - 13, 40, 40)) if (Settings.settings.get("printfps").equals("true")) Settings.settings.replace("printfps", "false");
                if (mouseOver(mx, my, Game.WIDTH / 2 + 290 - 40, Game.HEIGHT / 2 - 20 - 13, 40, 40)) if (Settings.settings.get("theme").equals("light")) Settings.settings.replace("theme", "dark");
                if (mouseOver(mx, my, Game.WIDTH / 2 + 290 - 40, Game.HEIGHT / 2 - 20  + 80 - 13, 40, 40)) if (Settings.settings.get("resolution").equals("960x540")) Settings.settings.replace("resolution", "1280x720");
                if (mouseOver(mx, my, Game.WIDTH / 2 + 290 - 40, Game.HEIGHT / 2 - 20  + 160 - 13, 40, 40)) if (Settings.settings.get("printfps").equals("false")) Settings.settings.replace("printfps", "true");
                if (mouseOver(mx, my, Game.WIDTH / 2 - 100, Game.HEIGHT - 50 - 10, 200, 50))
                {
                    game.gameState = STATE.Menu;
                }
                break;
            case Game :
                if (mx == startX && my == startY)
                {
                    for (int i = 0; i < handler.object.size(); i++)
                    {
                        GameObject tempObject = handler.object.get(i);

                        if (tempObject instanceof Tile)
                        {
                            if (mx + dragX > tempObject.x && my + dragY > tempObject.y && mx + dragX < tempObject.x + 32 && my + dragY < tempObject.y + 32)
                            {
                                if (((Tile) tempObject).player == 0)
                                {
                                    ((Tile) tempObject).player = hud.currentPlayer;
                                    System.out.println("Player " + hud.currentPlayer + " (" + customise.playerName[hud.currentPlayer - 1] + ") has taken tile " + ((Tile) tempObject).posX + ", " + ((Tile) tempObject).posY);

                                    hud.currentPlayer++;
                                    if (hud.currentPlayer > 2) hud.currentPlayer = 1;
                                }
                            }
                        }
                    }
                }
                else
                {
                    panX = dragX;
                    panY = dragY;
                }
                break;
        }


    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
        int mx = e.getX();
        int my = e.getY();

        switch (game.gameState)
        {
            case Menu :
                break;
            case Customise :
                switch (customise.colorPicker)
                {
                    case 1 :
                        if (mouseOver(mx, my, 10 + 30 + customise.r - 5, Game.HEIGHT - 100 - 10 - 120 + 30 - 10, 10, 20))
                        {
                            customise.r = mx - (10 + 30);
                            if (customise.r > 255) customise.r = 255;
                            if (customise.r < 0) customise.r = 0;
                        }
                        if (mouseOver(mx, my, 10 + 30 + customise.g - 5, Game.HEIGHT - 100 - 10 - 120 + 60 - 10, 10, 20))
                        {
                            customise.g = mx - (10 + 30);
                            if (customise.g > 255) customise.g = 255;
                            if (customise.g < 0) customise.g = 0;
                        }
                        if (mouseOver(mx, my, 10 + 30 + customise.b - 5, Game.HEIGHT - 100 - 10 - 120 + 90 - 10, 10, 20))
                        {
                            customise.b = mx - (10 + 30);
                            if (customise.b > 255) customise.b = 255;
                            if (customise.b < 0) customise.b = 0;
                        }
                        break;
                    case 2 :
                        if (mouseOver(mx, my, Game.WIDTH - 15 - (30 + 255 + 30) + 30 + customise.r - 5, Game.HEIGHT - 100 - 10 - 120 + 30 - 10, 10, 20))
                        {
                            customise.r = mx - (Game.WIDTH - 15 - (30 + 255 + 30) + 30);
                            if (customise.r > 255) customise.r = 255;
                            if (customise.r < 0) customise.r = 0;
                        }
                        if (mouseOver(mx, my, Game.WIDTH - 15 - (30 + 255 + 30) + 30 + customise.g - 5, Game.HEIGHT - 100 - 10 - 120 + 60 - 10, 10, 20))
                        {
                            customise.g = mx - (Game.WIDTH - 15 - (30 + 255 + 30) + 30);
                            if (customise.g > 255) customise.g = 255;
                            if (customise.g < 0) customise.g = 0;
                        }
                        if (mouseOver(mx, my, Game.WIDTH - 15 - (30 + 255 + 30) + 30 + customise.b - 5, Game.HEIGHT - 100 - 10 - 120 + 90 - 10, 10, 20))
                        {
                            customise.b = mx - (Game.WIDTH - 15 - (30 + 255 + 30) + 30);
                            if (customise.b > 255) customise.b = 255;
                            if (customise.b < 0) customise.b = 0;
                        }
                        break;
                }
                break;
            case Game :
                dragX = panX + (startX - mx);
                dragY = panY + (startY - my);
                break;
        }
    }

    private boolean mouseOver(int mx, int my, int x, int y, int width, int height)
    {
        return (((mx > x) && (mx < x + width)) && ((my > y) && (my < y + height)));
    }
}