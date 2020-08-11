package com.xwars.states;

import com.xwars.main.Game;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * The <code>Rules</code> class is used when the application is in the rules state
 *
 * @author soni801
 */

public class Rules
{
    public String rules;
    public int offset;

    public Rules()
    {
        try
        {
            String webPage = "https://raw.githubusercontent.com/soni801/xwars/master/rules.txt";
            URL url = new URL(webPage);
            URLConnection urlConnection = url.openConnection();
            InputStream is = urlConnection.getInputStream();
            InputStreamReader isr = new InputStreamReader(is);

            int numCharsRead;
            char[] charArray = new char[1024];
            StringBuffer sb = new StringBuffer();
            while ((numCharsRead = isr.read(charArray)) > 0)
            {
                sb.append(charArray, 0, numCharsRead);
            }
            rules = sb.toString();
            System.out.println("Loaded rules");
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public void tick()
    {

    }

    public void render(Graphics g)
    {
        g.setFont(Game.font.deriveFont(20f));
        switch (Settings.settings.get("theme"))
        {
            case "light" : g.setColor(new Color(80, 80, 80));    break;
            case "dark"  : g.setColor(new Color(160, 160, 160)); break;
        }

        drawString(g, rules, 10, Game.HEIGHT / 2 - 170 + 40 + 50 - offset);

        switch (Settings.settings.get("theme"))
        {
            case "light" : g.setColor(Color.WHITE);     break;
            case "dark"  : g.setColor(Color.DARK_GRAY); break;
        }

        g.fillRect(0, 0, Game.WIDTH, Game.HEIGHT / 2 - 170 + 40 + 50 - 20);
        g.fillRect(0, Game.HEIGHT - 50 - 10 - 20, Game.WIDTH, 50 + 10 + 20);

        switch (Settings.settings.get("theme"))
        {
            case "light" : g.setColor(Color.BLACK); break;
            case "dark"  : g.setColor(Color.WHITE); break;
        }

        g.setFont(Game.font.deriveFont(60f));
        g.drawString(Game.BUNDLE.getString("rules.title"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(60f)).stringWidth(Game.BUNDLE.getString("rules.title")) / 2, Game.HEIGHT / 2 - 170);

        g.setFont(Game.font.deriveFont(40f));
        g.drawString(Game.BUNDLE.getString("rules.rules"), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(40f)).stringWidth(Game.BUNDLE.getString("rules.rules")) / 2, Game.HEIGHT / 2 - 170 + 40);

        g.setColor(new Color(120, 120, 120));
        g.setFont(Game.font.deriveFont(30f));
        g.drawString(Game.BUNDLE.getString("rules.back").toUpperCase(), Game.WIDTH / 2 - g.getFontMetrics(Game.font.deriveFont(30f)).stringWidth(Game.BUNDLE.getString("rules.back").toUpperCase()) / 2, Game.HEIGHT - 50 - 10 + 35);

    }

    private void drawString(Graphics g, String text, int x, int y)
    {
        for (String line : text.split("\n")) g.drawString(line, x, y += g.getFontMetrics().getHeight());
    }
}