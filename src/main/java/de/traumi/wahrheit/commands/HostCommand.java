package de.traumi.wahrheit.commands;

import com.onarandombox.MultiverseCore.MultiverseCore;
import com.onarandombox.MultiverseCore.api.MVWorldManager;
import de.traumi.wahrheit.Wahrheit;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.bukkit.Bukkit.getOnlinePlayers;

public class StartCommand implements CommandExecutor, TabCompleter {

    private final Wahrheit plugin;

    MultiverseCore core = (MultiverseCore) Bukkit.getServer().getPluginManager().getPlugin("Multiverse-Core");
    MVWorldManager worldManager = core.getMVWorldManager();



    public StartCommand(Wahrheit plugin) {
        this.plugin = plugin;
    }


    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {

        int MaxPlayer = 0;

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("Dieser Befehl kann nur von Spielern ausgeführt werden.").color(NamedTextColor.RED));
            return true;
        }

        if(args.length == 0){
            player.sendMessage(Component.text("Du hast die Spieleranzahl nicht angegeben.").color(NamedTextColor.RED));
            return true;
        }

        if(args[0].equals("0") || args[0].equals("1")){
            sender.sendMessage(Component.text("Maximale SPIELER ANZAHL VON ." + args[0]).color(NamedTextColor.RED));
            return true;
        }

        MaxPlayer = Integer.parseInt(args[0]);


        FileConfiguration config =  plugin.getConfig();

        if(config.contains("wahrheit.running")){
            if(config.getBoolean("wahrheit.running")){
                sender.sendMessage(Component.text("Ein Spiel läuft bereits.").color(NamedTextColor.RED));
                return true;
            }
        }



        config.set("wahrheit.running", false);
        config.set("wahrheit.maxplayer", MaxPlayer);
        config.set("wahrheit.owner", player.getName());
        this.plugin.saveConfig();
        if(!config.contains("lobby.wahrheit.name")){
            config.set("lobby.wahrheit.name", "lobby");
            this.plugin.saveConfig();
        }

        String lobbyname = config.getString("lobby.wahrheit.name");



        if(!worldManager.loadWorld(lobbyname)){
            worldManager.addWorld(
                    lobbyname, // The worldname
                    World.Environment.NORMAL, // The overworld environment type.
                    null, // The world seed. Any seed is fine for me, so we just pass null.
                    WorldType.NORMAL, // Nothing special. If you want something like a flat world, change this.
                    true, // This means we want to structures like villages to generator, Change to false if you don't want this.
                    null // Specifies a custom generator. We are not using any so we just pass null.
            );
        }

        player.teleport(worldManager.getMVWorld(lobbyname).getSpawnLocation());
        player.sendMessage(Component.text("Du bist nun in der Wartelobby.").color(NamedTextColor.GREEN));

        int playerCount = Bukkit.getWorld(lobbyname).getPlayers().size();
        player.sendMessage(Component.text( "Es warten derzeit " + playerCount + " von " + MaxPlayer + " Spieler in der Wartelobby.").color(NamedTextColor.GOLD));

        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        String[] maxplayeranzahlvorschlaege = {"2", "3", "4", "5", "6", "7", "8", "9"};
        if (args.length == 1) return Arrays.stream(maxplayeranzahlvorschlaege).toList();
        return new ArrayList<>();
    }
}
