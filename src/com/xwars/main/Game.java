package com.xwars.main;

import com.xwars.gameobjects.Tile;
import com.xwars.main.input.KeyInput;
import com.xwars.main.input.MouseInput;
import com.xwars.online.Client;
import com.xwars.online.Server;
import com.xwars.states.Customise;
import com.xwars.states.HUD;
import com.xwars.states.Rules;
import com.xwars.states.Settings;
import com.xwars.states.Menu;
import net.arikia.dev.drpc.DiscordEventHandlers;
import net.arikia.dev.drpc.DiscordRPC;
import net.arikia.dev.drpc.DiscordRichPresence;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Objects;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * The main class of the application.
 *
 * @author soni801
 */

public class Game extends Canvas implements Runnable
{
    public static final String BRAND = "Redsea Productions";
    public static final String PRODUCT = "The Great X Wars";
    public static final String VERSION = "beta-0.1";
    
    public static int WIDTH, HEIGHT;
    public static long firstTick = System.currentTimeMillis();

    public static ResourceBundle BUNDLE;

    private final Handler handler;
    private Thread thread;

    private boolean running = false;
    public static boolean PAUSED = false;
    public static boolean ready = false;

    public int selected_close_operation;
    private String fps;

    public BufferedImage icon;
    public BufferedImage redsea;
    public BufferedImage dice;
    public BufferedImage pencil;

    public BufferedImage close_operations_default;
    public BufferedImage close_operations_close_select_dark;
    public BufferedImage close_operations_close_select_light;
    public BufferedImage close_operations_minimise_select_dark;
    public BufferedImage close_operations_minimise_select_light;

    public BufferedImage arrows_dark;
    public BufferedImage arrows_light;

    public BufferedImage arrow_left;
    public BufferedImage arrow_right;

    private final MouseInput mouseInput;
    private final HUD hud;
    private final Settings settings;
    private final Menu menu;
    private final Customise customise;
    private final Rules rules;

    public State gameState = State.Menu;

    public Server server;
    public Client client;
    public static int PORT = 14242;

    public static Font font;

    public Window window;

    public Game()
    {
        // Create Font
        try
        {
            font = Font.createFont(Font.TRUETYPE_FONT, Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("fonts/font.ttf")));
        }
        catch (IOException |FontFormatException e)
        {
            e.printStackTrace();
        }

        // Show loading window
        Window.showLoading();

        // Load images
        BufferedImageLoader loader = new BufferedImageLoader();
        icon = loader.loadImage("/images/icon.png");
        redsea = loader.loadImage("/images/redsea.png");
        dice = loader.loadImage("/images/dice.png");
        pencil = loader.loadImage("/images/pencil.png");

        close_operations_default = loader.loadImage("/images/close_operations/default.png");
        close_operations_close_select_dark = loader.loadImage("/images/close_operations/close_select_dark.png");
        close_operations_close_select_light = loader.loadImage("/images/close_operations/close_select_light.png");
        close_operations_minimise_select_dark = loader.loadImage("/images/close_operations/minimise_select_dark.png");
        close_operations_minimise_select_light = loader.loadImage("/images/close_operations/minimise_select_light.png");

        arrows_dark = loader.loadImage("/images/arrows_dark.png");
        arrows_light = loader.loadImage("/images/arrows_light.png");

        arrow_left = loader.loadImage("/images/settings/arrow_left.png");
        arrow_right = loader.loadImage("/images/settings/arrow_right.png");

        // Add shutdown hook
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable()
        {
            public void run()
            {
                DiscordRPC.discordShutdown();
                System.out.println("Disconnected from Discord");
    
                settings.save();
    
                server.stopServer();
            }
        }, "Shutdown-thread"));

        // Initialise objects
        handler = new Handler();
    
        settings = new Settings(this);
        customise = new Customise(this, settings);
        hud = new HUD(handler, customise, settings);
        menu = new Menu(this, settings);
        rules = new Rules(settings);

        server = new Server(customise, hud, handler);
        client = new Client(this, customise, hud, handler);

        mouseInput = new MouseInput(handler, hud, this, customise, settings, rules);

        // Add input listeners
        this.addKeyListener(new KeyInput(this, customise));
        this.addMouseListener(mouseInput);
        this.addMouseMotionListener(mouseInput);
        this.addMouseWheelListener(mouseInput);

        // Load settings
        settings.load();
    
        // Set resolution
        try
        {
            switch (settings.settings.get("resolution"))
            {
                case "960x540"  : WIDTH = 960;  break;
                case "1280x720" : WIDTH = 1280; break;
                case "1600x900" : WIDTH = 1600; break;
                default :
                    WIDTH = 1280;
                    System.out.println("Could not load resolution correctly. Using default resolution at 1280x720.");
            }
            HEIGHT = WIDTH / 16 * 9;

            BUNDLE = ResourceBundle.getBundle("lang.lang_" + settings.settings.get("language"));
        }
        catch (Exception e)
        {
            settings.reset();

            JOptionPane.showMessageDialog(null, "Failed to load settings. Please re-launch the game", "Error", JOptionPane.INFORMATION_MESSAGE);

            System.exit(1);
        }

        // Initialise Discord
        initDiscord();
        while (!ready) DiscordRPC.discordRunCallbacks();
        
        // Start game
        System.out.println("Starting in resolution " + WIDTH + "x" + HEIGHT);
        window = new Window(WIDTH, HEIGHT, "The Great X Wars", this);
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
        DiscordRPC.discordRunCallbacks();
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
                // System.out.println("FPS: " + frames);
                fps = String.valueOf(frames);
                frames = 0;
            }
        }
        stop();
    }

    public void startGame(int pos1, int pos2)
    {
        handler.tiles = new Tile[customise.boardSize[0]][customise.boardSize[1]];
        hud.generate(customise.boardSize[0], customise.boardSize[1]);
        
        if (customise.online)
        {
            if (customise.onlineMode == 1)
            {
                int[] foundationPos = createFoundations();
    
                try
                {
                    if (server.connectionActive)
                    {
                        String nameLength = String.valueOf(customise.playerName[0].length());
                        while (nameLength.length() < 2) nameLength = "0" + nameLength;
            
                        String name = customise.playerName[0];
            
                        String r = String.valueOf(customise.playerColor[0].getRed());
                        String g = String.valueOf(customise.playerColor[0].getGreen());
                        String b = String.valueOf(customise.playerColor[0].getBlue());
            
                        while (r.length() < 3) r = "0" + r;
                        while (g.length() < 3) g = "0" + g;
                        while (b.length() < 3) b = "0" + b;
            
                        String y1 = String.valueOf(foundationPos[0]);
                        String y2 = String.valueOf(foundationPos[1]);
            
                        while (y1.length() < 3) y1 = "0" + y1;
                        while (y2.length() < 3) y2 = "0" + y2;
            
                        server.sendUTF("s" + nameLength + name + r + g + b + y1 + y2);
                    }
                }
                catch (Exception ignored) {}
            }
            else
            {
                createFoundations(pos1, pos2);
            }
        }
        else
        {
            createFoundations();
        }
        
        gameState = State.Game;
    }

    private int[] createFoundations()
    {
        Random r = new Random();
        int y1 = r.nextInt(customise.boardSize[1] - 2);
        int y2 = r.nextInt(customise.boardSize[1] - 2);
        
        handler.tiles[0][y1].player = 1;
        handler.tiles[0][y1].foundation = 1;
    
        handler.tiles[1][y1].player = 1;
        handler.tiles[1][y1].foundation = 2;
    
        handler.tiles[0][y1 + 1].player = 1;
        handler.tiles[0][y1 + 1].foundation = 3;
    
        handler.tiles[1][y1 + 1].player = 1;
        handler.tiles[1][y1 + 1].foundation = 4;
    
        handler.tiles[customise.boardSize[0] - 2][y2].player = 2;
        handler.tiles[customise.boardSize[0] - 2][y2].foundation = 1;
    
        handler.tiles[customise.boardSize[0] - 1][y2].player = 2;
        handler.tiles[customise.boardSize[0] - 1][y2].foundation = 2;
    
        handler.tiles[customise.boardSize[0] - 2][y2 + 1].player = 2;
        handler.tiles[customise.boardSize[0] - 2][y2 + 1].foundation = 3;
    
        handler.tiles[customise.boardSize[0] - 1][y2 + 1].player = 2;
        handler.tiles[customise.boardSize[0] - 1][y2 + 1].foundation = 4;
        
        return new int[]{y1, y2};
    }
    
    private void createFoundations(int y1, int y2)
    {
        handler.tiles[0][y1].player = 2;
        handler.tiles[0][y1].foundation = 1;
        
        handler.tiles[1][y1].player = 2;
        handler.tiles[1][y1].foundation = 2;
        
        handler.tiles[0][y1 + 1].player = 2;
        handler.tiles[0][y1 + 1].foundation = 3;
        
        handler.tiles[1][y1 + 1].player = 2;
        handler.tiles[1][y1 + 1].foundation = 4;
        
        handler.tiles[customise.boardSize[0] - 2][y2].player = 1;
        handler.tiles[customise.boardSize[0] - 2][y2].foundation = 1;
        
        handler.tiles[customise.boardSize[0] - 1][y2].player = 1;
        handler.tiles[customise.boardSize[0] - 1][y2].foundation = 2;
        
        handler.tiles[customise.boardSize[0] - 2][y2 + 1].player = 1;
        handler.tiles[customise.boardSize[0] - 2][y2 + 1].foundation = 3;
        
        handler.tiles[customise.boardSize[0] - 1][y2 + 1].player = 1;
        handler.tiles[customise.boardSize[0] - 1][y2 + 1].foundation = 4;
    }
    
    private void tick()
    {
        switch (gameState)
        {
            case Menu      : menu.tick();      break;
            case Customise : customise.tick(); break;
            case Rules     : rules.tick();     break;
            case Settings  : settings.tick();  break;
            case Game      : hud.tick();       break;
        }
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

        switch (settings.settings.get("theme"))
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
            case Rules     : rules.render(g);     break;
            case Settings  : settings.render(g);  break;
            case Game      : hud.render(g);       break;
        }

        mouseInput.render(g);

        switch (settings.settings.get("theme"))
        {
            case "light" : g.setColor(Color.LIGHT_GRAY); break;
            case "dark"  : g.setColor(Color.GRAY);       break;
        }

        g.setFont(font.deriveFont(15f));
        g.drawString(VERSION, 10, 10 + 10);
        if (settings.settings.get("showfps").equals("true")) g.drawString("FPS: " + fps, 10, 10 + 10 + 10 + 10);

        g.drawImage(close_operations_default, WIDTH - 10 - close_operations_default.getWidth(), 10, null);

        switch (selected_close_operation)
        {
            case 0 : g.drawImage(close_operations_default, WIDTH - 10 - close_operations_default.getWidth(), 10, null); break;
            case 1 :
                switch (settings.settings.get("theme"))
                {
                    case "light" :
                        g.drawImage(close_operations_close_select_light, WIDTH - 10 - close_operations_default.getWidth(), 10, null);
                        break;
                    case "dark" :
                        g.drawImage(close_operations_close_select_dark, WIDTH - 10 - close_operations_default.getWidth(), 10, null);
                        break;
                }
                break;
            case 2 :
                switch (settings.settings.get("theme"))
                {
                    case "light" :
                        g.drawImage(close_operations_minimise_select_light, WIDTH - 10 - close_operations_default.getWidth(), 10, null);
                        break;
                    case "dark" :
                        g.drawImage(close_operations_minimise_select_dark, WIDTH - 10 - close_operations_default.getWidth(), 10, null);
                        break;
                }
                break;
        }

        g.dispose();
        bs.show();
    }

    private static void initDiscord()
    {
        DiscordEventHandlers handlers = new DiscordEventHandlers.Builder().setReadyEventHandler((user) ->
        {
            ready = true;
            System.out.println("Found Discord user " + user.username + "#" + user.discriminator + ".");

            updateDiscord("In menu", "Main menu");
        }).build();
        DiscordRPC.discordInitialize("733261832948678666", handlers, false);
        DiscordRPC.discordRegister("733261832948678666", "");
    }

    public static void updateDiscord(String details, String state)
    {
        DiscordRichPresence.Builder presence = new DiscordRichPresence.Builder(state);
        presence.setDetails(details);

        presence.setBigImage("icon", "");
        presence.setStartTimestamps(firstTick);

        DiscordRPC.discordUpdatePresence(presence.build());
    }

    public static void main(String[] args)
    {
        new Game();
    }
}