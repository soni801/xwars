package com.xwars.main;

import java.awt.*;
import java.util.ConcurrentModificationException;
import java.util.LinkedList;

/**
 * This class, when instantiated, handles ticking and rendering of all GameObjects,
 * using its <code>tick()</code> and <code>render()</code> methods
 *
 * @author soni801
 */

public class Handler
{
    public LinkedList<GameObject> object = new LinkedList<>();

    public void tick()
    {
        for (GameObject object : object)
        {
            object.tick();
        }
    }

    public void render(Graphics g)
    {
        try
        {
            for (GameObject object : object)
            {
                object.render(g);
            }
        }
        catch (ConcurrentModificationException e)
        {
            e.printStackTrace();
        }
    }
}