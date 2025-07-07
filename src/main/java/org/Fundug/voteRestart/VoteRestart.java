package org.Fundug.voteRestart;

import org.Fundug.voteRestart.Command.VoteRestartCommand;
import org.bukkit.plugin.java.JavaPlugin;

public final class VoteRestart extends JavaPlugin {

    private static VoteRestart inst;
    @Override
    public void onEnable() {
        inst = this;
        new VoteRestartCommand();

    }

    @Override
    public void onDisable() {

    }

    public static VoteRestart getInst() {
        return inst;
    }
}
