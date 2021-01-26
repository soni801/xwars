package com.xwars.states;

import com.xwars.main.Game;
import com.xwars.main.Setting;
import com.xwars.main.loaders.ResourceLoader;

import java.awt.*;
import java.io.*;
import java.util.HashMap;
import java.util.Map;

/**
 * The <code>Settings</code> class is used when the application is in the Settings state,
 * as well as saving, loading and resetting settings on the hard drive using its
 * <code>save()</code>, <code>load()</code> and <code>reset()</code> methods
 *
 * @author soni801
 */

public class Settings
{
    private final Game game;

    public HashMap<Setting, Integer> settings;
    
    public int page = 1;

    public Settings(Game game)
    {
        this.game = game;
        
        reset();
    }
    
    private HashMap<Setting, Integer> init()
    {
        HashMap<Setting, Integer> temp = new HashMap<>();
        
        temp.put(new Setting("theme", new String[]{"light", "dark"}, 0), 0);
        temp.put(new Setting("resolution", new String[]{"540", "720", "900", "fullscreen"}, 0), 1);
        temp.put(new Setting("displayFPS", new String[]{"true", "false"}, 0), 1);
        temp.put(new Setting("language", new String[]{"en_US"}, 0), 0);
        temp.put(new Setting("volume", new String[]{"0", ".1", ".2", ".3", ".4", ".5", ".6", ".7", ".8", ".9", "1"}, 1), 10);
        
        return temp;
    }
    
    public void reset()
    {
        settings = init();
    }
    
    public String get(String name)
    {
        for (Map.Entry<Setting, Integer> entry : settings.entrySet())
        {
            if (entry.getKey().name.equals(name)) return entry.getKey().values[entry.getValue()];
        }
        
        return null;
    }
    
    public void update(String setting, Integer value)
    {
       HashMap<Setting, Integer> temp = new HashMap<>();
    
        for (Map.Entry<Setting, Integer> loop : settings.entrySet())
        {
            if (loop.getKey().name.equals(setting))
            {
                temp.put(loop.getKey(), value);
            }
            else
            {
                temp.put(loop.getKey(), loop.getValue());
            }
        }
        
        settings = temp;
    }
    
    public void save()
    {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
        {
            try
            {
                FileOutputStream file = new FileOutputStream(System.getenv("AppData") + "\\" + Game.BRAND + "\\" + Game.PRODUCT + "\\" + "settings.xcfg");
                ObjectOutputStream out = new ObjectOutputStream(file);
        
                out.writeObject(settings);
        
                out.close();
                file.close();
        
                System.out.println("Saved settings");
            }
            catch (IOException e)
            {
                System.out.println("Failed to save settings.");
                e.printStackTrace();
            }
        }
        else System.out.println("Unknown operating system. Cannot save settings.");
    }
    
    public void load()
    {
        if (System.getProperty("os.name").toLowerCase().contains("win"))
        {
            try
            {
                FileInputStream file = new FileInputStream(System.getenv("AppData") + "\\" + Game.BRAND + "\\" + Game.PRODUCT + "\\" + "settings.xcfg");
                ObjectInputStream in = new ObjectInputStream(file);
            
                settings = (HashMap<Setting, Integer>) in.readObject();
            
                in.close();
                file.close();
            
                System.out.println("Loaded settings");
            }
            catch (IOException | ClassNotFoundException e)
            {
                System.out.println("Failed to load settings.");
                e.printStackTrace();
            }
        }
        else System.out.println("Unknown operating system. Cannot load settings.");
    }

    public void tick()
    {

    }

    public void render(Graphics g)
    {
        switch (get("theme"))
        {
            case "light" -> g.setColor(Color.BLACK);
            case "dark"  -> g.setColor(Color.WHITE);
        }

        g.setFont(Game.font.deriveFont(60f));
        g.drawString(ResourceLoader.nameOf("settings.title"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(60f)).stringWidth(ResourceLoader.nameOf("settings.title")) / 2, Game.HEIGHT / 2 - 170);

        g.setFont(Game.font.deriveFont(40f));
        g.drawString(ResourceLoader.nameOf("settings.settings"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(40f)).stringWidth(ResourceLoader.nameOf("settings.settings")) / 2, Game.HEIGHT / 2 - 170 + 40);
    
        switch (get("theme"))
        {
            case "light" -> g.setColor(new Color(80, 80, 80));
            case "dark"  -> g.setColor(new Color(160, 160, 160));
        }
    
        switch (page)
        {
            case 1 -> {
                g.setFont(Game.font.deriveFont(30f));
                g.drawString(ResourceLoader.nameOf("settings.theme"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(ResourceLoader.nameOf("settings.theme")) / 2, Game.HEIGHT / 2 - 70);
                g.drawString(ResourceLoader.nameOf("settings.resolution"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(ResourceLoader.nameOf("settings.resolution")) / 2, Game.HEIGHT / 2 - 70 + 80);
                g.drawString(ResourceLoader.nameOf("settings.displayFPS"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(ResourceLoader.nameOf("settings.displayFPS")) / 2, Game.HEIGHT / 2 - 70 + 160);
                g.setColor(new Color(120, 120, 120));
                g.setFont(Game.font.deriveFont(20f));
                g.drawString(get("theme"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(20f)).stringWidth(get("theme")) / 2, Game.HEIGHT / 2 - 70 + 30);
                g.drawString(get("resolution"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(20f)).stringWidth(get("resolution")) / 2, Game.HEIGHT / 2 - 70 + 80 + 30);
                g.drawString(get("displayFPS"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(20f)).stringWidth(get("displayFPS")) / 2, Game.HEIGHT / 2 - 70 + 160 + 30);
                
                if (get("theme").equals("dark")) g.drawImage(game.arrow_left, Game.WIDTH / 2 - 290, Game.HEIGHT / 2 - 70 - 13, null);
                if (!get("resolution").equals("540")) g.drawImage(game.arrow_left, Game.WIDTH / 2 - 290, Game.HEIGHT / 2 - 70 + 80 - 13, null);
                if (get("displayFPS").equals("true")) g.drawImage(game.arrow_left, Game.WIDTH / 2 - 290, Game.HEIGHT / 2 - 70 + 160 - 13, null);
                if (get("theme").equals("light")) g.drawImage(game.arrow_right, Game.WIDTH / 2 + 290 - 40, Game.HEIGHT / 2 - 70 - 13, null);
                if (!get("resolution").equals("fullscreen")) g.drawImage(game.arrow_right, Game.WIDTH / 2 + 290 - 40, Game.HEIGHT / 2 - 70 + 80 - 13, null);
                if (get("displayFPS").equals("false")) g.drawImage(game.arrow_right, Game.WIDTH / 2 + 290 - 40, Game.HEIGHT / 2 - 70 + 160 - 13, null);
            }
            case 2 -> {
                g.setFont(Game.font.deriveFont(30f));
                g.drawString(ResourceLoader.nameOf("settings.volume"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(ResourceLoader.nameOf("settings.volume")) / 2, Game.HEIGHT / 2 - 70);
                g.setColor(new Color(120, 120, 120));
                g.drawLine(Game.WIDTH / 2 - 200, Game.HEIGHT / 2 - 70 + 30, Game.WIDTH / 2 + 200, Game.HEIGHT / 2 - 70 + 30);
                g.drawLine(Game.WIDTH / 2 - 200, Game.HEIGHT / 2 - 70 + 30 - 1, Game.WIDTH / 2 + 200, Game.HEIGHT / 2 - 70 + 30 - 1);
                g.fillOval((int) (Game.WIDTH / 2 - 200 + (Float.parseFloat(get("volume")) * 400) - 8), Game.HEIGHT / 2 - 70 + 30 - 8, 15, 15);
            }
        }

        g.setColor(new Color(120, 120, 120));
        g.setFont(Game.font.deriveFont(20f));
        g.drawString(ResourceLoader.nameOf("settings.notice"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(20f)).stringWidth(ResourceLoader.nameOf("settings.notice")) / 2, Game.HEIGHT - 50 - 10 + 35 - 50 - 40);

        g.setFont(Game.font.deriveFont(30f));
        g.drawString(ResourceLoader.nameOf("settings.reset").toUpperCase(), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(ResourceLoader.nameOf("settings.reset").toUpperCase()) / 2, Game.HEIGHT - 50 - 10 + 35 - 50);
        g.drawString(ResourceLoader.nameOf("settings.back").toUpperCase(), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(ResourceLoader.nameOf("settings.back").toUpperCase()) / 2, Game.HEIGHT - 50 - 10 + 35);

        if (page > 1) g.drawString("<", Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth("<") / 2 - 100, Game.HEIGHT - 50 - 10 + 35);
        if (page < 2) g.drawString(">", Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(">") / 2 + 100, Game.HEIGHT - 50 - 10 + 35);
    }
}