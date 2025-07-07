package org.Fundug.voteRestart.Command;

import org.Fundug.voteRestart.VoteRestart;
import org.bukkit.command.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractCommand implements CommandExecutor, TabCompleter {

    public AbstractCommand(String command){
        PluginCommand pluginCommand = VoteRestart.getInst().getCommand(command);

        if(pluginCommand != null){
            pluginCommand.setExecutor(this);
            pluginCommand.setTabCompleter(this);
        }
    }

    public abstract void execute(CommandSender sender, Command command, String s, String[] strings);

    public List<String> complete(CommandSender sender, String[] args){
        return null;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        execute(commandSender,command,s,strings);
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender var1, @NotNull Command var2, @NotNull String var3, @NotNull String[] var4){
        return filter(complete(var1, var4), var4);
    }

    private List<String> filter(List<String> list, String[] args){
        if(list==null) return null;
        String last = args[args.length-1];

        List<String> result = new ArrayList<>();
        for(String arg: list){
            if(arg.toLowerCase().startsWith(last.toLowerCase())) result.add(arg);
        }
        return result;
    }
}
