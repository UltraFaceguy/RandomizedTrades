/**
 * The MIT License
 * Copyright (c) 2015 Teal Cube Games
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package info.faceland.SafeDrop;

import com.tealcube.minecraft.bukkit.facecore.plugin.FacePlugin;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.HandlerList;
import se.ranzdo.bukkit.methodcommand.CommandHandler;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SafeDropPlugin extends FacePlugin {

    protected List<String> playersWithDropEnabled;
    protected List<String> playersInInventory;

    @Override
    public void enable() {
        playersWithDropEnabled = getConfig().getStringList("PlayersWithDropEnabled");
        playersInInventory = new ArrayList<String>();
        Bukkit.getPluginManager().registerEvents(new SafeDropListener(this), this);
        CommandHandler commandHandler = new CommandHandler(this);
        commandHandler.registerCommands(new SafeDropCommand(this));
    }

    @Override
    public void disable() {
        HandlerList.unregisterAll(this);
    }

}
