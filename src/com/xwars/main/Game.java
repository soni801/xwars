package com.xwars.main;

/*
 * Author: soni801
 */

import com.xwars.main.input.KeyInput;
import com.xwars.main.input.MouseInput;
import com.xwars.states.*;
import com.xwars.states.Menu;

import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

public class Game extends Canvas implements Runnable
{
    private static final long serialVersionUID = 1L;

    public static int WIDTH, HEIGHT;
    public static final String VERSION = "alpha-0.0.5.4";

    private Handler handler;
    private Thread thread;

    private boolean running = false;
    public static boolean PAUSED = false;

    public BufferedImage icon;
    public BufferedImage dice;
    public BufferedImage pencil;

    public BufferedImage arrows_dark;
    public BufferedImage arrows_light;

    public BufferedImage arrow_left;
    public BufferedImage arrow_right;

    private MouseInput mouseInput;
    private HUD hud;
    private Settings settings;
    private com.xwars.states.Menu menu;
    private Customise customise;
    private Online online;

    public STATE gameState = STATE.Menu;

    public static Font font;

    public Game()
    {
        BufferedImageLoader loader = new BufferedImageLoader();
        icon = loader.loadImage("/images/icon.png");
        dice = loader.loadImage("/images/dice.png");
        pencil = loader.loadImage("/images/pencil.png");

        arrows_dark = loader.loadImage("/images/arrows_dark.png");
        arrows_light = loader.loadImage("/images/arrows_light.png");

        arrow_left = loader.loadImage("/images/settings/arrow_left.png");
        arrow_right = loader.loadImage("/images/settings/arrow_right.png");

        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            public void run()
            {
                settings.save();
            }
        }, "Shutdown-thread"));

        handler = new Handler();

        customise = new Customise(this);
        hud = new HUD(handler, customise);
        settings = new Settings(this);
        menu = new Menu();
        online = new Online();

        mouseInput = new MouseInput(handler, hud, this, customise, settings);

        this.addKeyListener(new KeyInput(this, customise));
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);

        settings.load();

        switch (Settings.settings.get("resolution"))
        {
            case "960x540"  : WIDTH = 960;  break;
            case "1280x720" : WIDTH = 1280; break;
            case "1600x900" : WIDTH = 1600; break;
            default :
                WIDTH = 1280;
                System.out.println("Could not load resolution correctly. Using default resolution at 1280x720.");
        }
        HEIGHT = WIDTH / 16 * 9;

        System.out.println("Starting in resolution " + WIDTH + "x" + HEIGHT + ".");
        new Window(WIDTH, HEIGHT, "The Great X Wars", this, settings);
    }

    public synchronized void start()
    {
        thread = new Thread(this);
        thread.start();
        running = true;
    }

    public synchronized void stop()
    {
        try
        {
            thread.join();
            running = false;
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public void run()
    {
        this.requestFocus();
        long lastTime = System.nanoTime();
        double amountOfTicks = 60.0;
        double ns = 1000000000 / amountOfTicks;
        double delta = 0;
        long timer = System.currentTimeMillis();
        int frames = 0;
        while (running)
        {
            long now = System.nanoTime();
            delta += (now - lastTime) / ns;
            lastTime = now;
            while (delta >= 1)
            {
                tick();
                delta--;
            }
            if (running)
                render();
            frames++;

            if (System.currentTimeMillis() - timer > 1000)
            {
                timer += 1000;
                if (Settings.settings.get("printfps").equals("true")) System.out.println("FPS: " + frames);
                frames = 0;
            }
        }
        stop();
    }

    private void tick()
    {
        switch (gameState)
        {
            case Menu      : menu.tick();      break;
            case Customise : customise.tick(); break;
            case Settings  : settings.tick();  break;
            case Game      : hud.tick();       break;
            case Online    : online.tick();    break;
        }

        handler.tick();
    }

    public void render()
    {
        BufferStrategy bs = this.getBufferStrategy();
        if (bs == null)
        {
            this.createBufferStrategy(3);
            return;
        }

        Graphics g = bs.getDrawGraphics();

        int offX = mouseInput.dragX;
        int offY = mouseInput.dragY;

        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("fonts/font.ttf")));
        }
        catch (IOException|FontFormatException e)
        {
            e.printStackTrace();
        }

        switch (Settings.settings.get("theme"))
        {
            case "light" : g.setColor(Color.WHITE);     break;
            case "dark"  : g.setColor(Color.DARK_GRAY); break;
        }

        g.fillRect(0, 0, WIDTH, HEIGHT);

        g.translate(-offX, -offY);

        handler.render(g);

        g.translate(offX, offY);

        switch (gameState)
        {
            case Menu      : menu.render(g);      break;
            case Customise : customise.render(g); break;
            case Settings  : settings.render(g);  break;
            case Game      : hud.render(g);       break;
            case Online    : online.render(g);    break;
        }

        mouseInput.render(g);

        switch (Settings.settings.get("theme"))
        {
            case "light" : g.setColor(Color.LIGHT_GRAY); break;
            case "dark"  : g.setColor(Color.GRAY);       break;
        }

        g.setFont(font.deriveFont(15f));
        g.drawString(VERSION, 10, 10 + 10);

        g.dispose();
        bs.show();
    }

    public int clamp(int var, int min, int max)
    {
        if (var < min) return min;
        else if (var > max) return max;
        else return var;
    }

    public static void main(String[] args)
    {
        new Game();
    }
}