package us.jophest.customscoreboard;


import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CustomScoreboard extends JavaPlugin implements Listener {
    public static Economy econ = null;
    public static Permission perms = null;
    Logger log = this.getLogger();
    int bal = 0;
    int kill = 0;
    int death = 0;
    int pvedeath = 0;
    int pvekill = 0;
    List<String> cunts;
    public static HashMap<String, Integer> values = new HashMap<String, Integer>();
    public List<String> myTop5 = new ArrayList();
    ScoreboardManager manager2;
    String section;
    String killSection;
    Objective obj;
    Objective listobj;
    Scoreboard board;
    Scoreboard list;
    ConfigurationSection score;


    public void onDisable() {
        // TODO: Place any custom disable code here.

    }
    public void onEnable(){
        getServer().getPluginManager().registerEvents(this, this);

        if (!setupEconomy()) {
            getLogger().log(Level.SEVERE,
                    " Disabled due to no Vault dependency found!");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        SetupConfig();

        startTimer();


    }

    public void startTimer() {
        int howoften = getConfig().getInt("howoften");
        Timer countdown = new Timer(this);
        countdown.startCountdown("", false, howoften/**Amount in seconds*/, "");
    }

    private void SetupConfig() {
        getConfig().options().copyDefaults(true);
        saveDefaultConfig();
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer()
                .getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public void setupScoreboard(Player playaa) {
        manager2 = Bukkit.getScoreboardManager();
        board = manager2.getNewScoreboard();
        list = manager2.getNewScoreboard();
        obj = board.registerNewObjective("STATS", "dummy");
        listobj = list.registerNewObjective("TheFrontThing", "dummy");
               reloadConfig();
       cunts = getConfig().getStringList("cunts");
        if (Bukkit.getServer().getOnlinePlayers().length > 0) {
            for (Player plr : Bukkit.getServer().getOnlinePlayers()) {
               if (cunts.contains(plr.getName())){
                   // do fuck all
               }   else{
                reloadConfig();

                bal = (int) econ.getBalance(playaa.getName());
                if (!getConfig().contains("deaths." + playaa.getName())) {
                    getConfig().set("deaths." + playaa.getName(), 0);
                }
                death = getConfig().getInt("deaths." + playaa.getName());
                pvekill = getConfig().getInt("pvekills." + playaa.getName());
                         int blocksPlaced = getConfig().getInt("place." + playaa.getName());
                   int blocksBroken = getConfig().getInt("break." + playaa.getName());


                // commentstart
                Map scoreMap = new HashMap();
                List finalScore = new ArrayList();
// total kills








                if (!getConfig().contains("kills." + playaa.getName())) {
                    getConfig().set("kills." + playaa.getName(), 0);
                }
                   reloadConfig();
                   kill = getConfig().getInt("kills." + playaa.getName());
                   score = getConfig().getConfigurationSection("kills");

                for (String playerName : score.getKeys(false)) {

                    int kills = score.getInt(playerName);


                    scoreMap.put(playerName, Integer.valueOf(kills));
                }


                for (int i = 0; i < 4; i++) {
                    String topName = "";
                    int topScore = 0;

                    for (Object playerName : scoreMap.keySet()) {
                        int myScore = ((Integer) scoreMap.get(playerName)).intValue();

                        if (myScore <= topScore)
                            continue;
                        topName = (String) playerName;
                        topScore = myScore;
                    }

                    if (((String) topName).equals(""))
                        break;
                    scoreMap.remove(topName);

                    int kills = score.getInt(topName);

                    int position = i + 1;

                    String finalString = ChatColor.BLUE + (String) topName + "-" + ChatColor.AQUA + kills;

                    finalScore.add(finalString);
                    int f = 1;
                    while (f == 1) {
                        getConfig().set("highscores", finalScore);
                        f--;
                        saveConfig();
                        reloadConfig();
                    }


                }

                this.myTop5 = finalScore;

                for (Object topName = this.myTop5.iterator(); ((Iterator) topName).hasNext(); ) {
                    String blah = (String) ((Iterator) topName).next();
                    List<String> scorez = getConfig().getStringList("highscores");

                    if (scorez.size() >= 3) {

                        String[] parts0 = scorez.get(0).split("-");
                        String[] parts1 = scorez.get(1).split("-");
                        String[] parts2 = scorez.get(2).split("-");
                        parts0[1] = parts0[1].replace("?", "§");
                        parts1[1] = parts1[1].replace("?", "§");
                        parts2[1] = parts2[1].replace("?", "§");
                        parts0[0] = parts0[0].replace("?", "§");
                        parts1[0] = parts1[0].replace("?", "§");
                        parts2[0] = parts2[0].replace("?", "§");
                        if (parts0[1].equals(parts1[1])) {
                            parts0[1] = parts0[1].replace("§b", "§4§b");
                        }
                        if (parts0[1].equals(parts2[1])) {
                            parts0[1] = parts0[1].replace("§b", "§4§b");
                        }
                        if (parts1[1].equals(parts2[1])) {
                            parts1[1] = parts1[1].replace("§b", "§4§b");
                        }

                        if (parts0[0].length() >= 16) {

                            parts0[0] = parts0[0].substring(0, 15);
                        }
                        if (parts1[0].length() >= 16) {

                            parts1[0] = parts1[0].substring(0, 15);
                        }
                        if (parts2[0].length() >= 16) {

                            parts2[0] = parts2[0].substring(0, 15);
                        }

                        Score best1 = obj.getScore(Bukkit.getOfflinePlayer(parts0[0]));
                        best1.setScore(6);
                        Score best2 = obj.getScore(Bukkit.getOfflinePlayer(parts1[0]));
                        best2.setScore(4);
                        Score best3 = obj.getScore(Bukkit.getOfflinePlayer(parts2[0]));
                        best3.setScore(2);

                        Score best1a = obj.getScore(Bukkit.getOfflinePlayer(parts0[1]));
                        best1a.setScore(5);
                        Score best2a = obj.getScore(Bukkit.getOfflinePlayer(parts1[1]));
                        best2a.setScore(3);
                        Score best3a = obj.getScore(Bukkit.getOfflinePlayer(parts2[1]));
                        best3a.setScore(1);
                    } else if (scorez.size() == 2) {

                        String[] parts0 = scorez.get(0).split("-");
                        String[] parts1 = scorez.get(1).split("-");
                        parts0[1] = parts0[1].replace("?", "§");
                        parts1[1] = parts1[1].replace("?", "§");
                        parts0[0] = parts0[0].replace("?", "§");
                        parts1[0] = parts1[0].replace("?", "§");
                        if (parts0[1].equals(parts1[1])) {
                            parts0[1] = parts0[1].replace("§b", "§4§b");
                        }

                        if (parts0[0].length() >= 16) {

                            parts0[0] = parts0[0].substring(0, 15);
                        }
                        if (parts1[0].length() >= 16) {

                            parts1[0] = parts1[0].substring(0, 15);
                        }

                        Score best1 = obj.getScore(Bukkit.getOfflinePlayer(parts0[0]));
                        best1.setScore(6);
                        Score best2 = obj.getScore(Bukkit.getOfflinePlayer(parts1[0]));
                        best2.setScore(4);
                        Score best1a = obj.getScore(Bukkit.getOfflinePlayer(parts0[1]));
                        best1a.setScore(5);
                        Score best2a = obj.getScore(Bukkit.getOfflinePlayer(parts1[1]));
                        best2a.setScore(3);
                    } else if (scorez.size() == 1) {

                        String[] parts0 = scorez.get(0).split("-");
                        parts0[1] = parts0[1].replace("?", "§");
                        parts0[0] = parts0[0].replace("?", "§");

                        if (parts0[0].length() >= 16) {

                            parts0[0] = parts0[0].substring(0, 15);
                        }

                        Score best1 = obj.getScore(Bukkit.getOfflinePlayer(parts0[0]));
                        best1.setScore(6);
                        Score best1a = obj.getScore(Bukkit.getOfflinePlayer(parts0[1]));
                        best1a.setScore(5);
                    }

                }


                //comment end
                saveConfig();
                reloadConfig();
                obj.setDisplaySlot(DisplaySlot.SIDEBAR);
                String title = getConfig().getString("title");
                title = title.replace("&", "§");
                obj.setDisplayName(title);

                Score kills = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GREEN.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "PVP Kills:"));
                Score killsAmount = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN.toString() + kill));
                Score deaths = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_RED.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "Deaths:"));
                Score deathamt = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.RED.toString() + death));
                Score pvehkills = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "Mob Kills:"));
                Score pvehkillsamt = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA.toString() + pvekill));

                   Score place = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "Blocks Placed:"));
                   Score placeamt = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA.toString() + blocksPlaced));
                   Score broke = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_AQUA.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "Blocks Broken:"));
                   Score brokeamt = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.AQUA.toString() + blocksBroken));

                Score line = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN.toString() + ChatColor.BOLD + "------------"));
                Score best = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "Best PVPers"));




                kills.setScore(20);
                killsAmount.setScore(19);
                deaths.setScore(18);
                   deathamt.setScore(17);
                  
                pvehkills.setScore(16);
                pvehkillsamt.setScore(15);

                   broke.setScore(14);
                   brokeamt.setScore(13);
                   place.setScore(12);
                   placeamt.setScore(11);


                line.setScore(10);
                best.setScore(9);
                playaa.setScoreboard(board);


            }
        }

        }

    }


    @EventHandler
    public void join(PlayerJoinEvent e) {

        setupScoreboard(e.getPlayer());


    }

    public boolean onCommand(CommandSender sender, Command command,
                             String label, String[] args) {
        // TODO Auto-generated method stub

                  reloadConfig();
        if (command.getName().equalsIgnoreCase("customscoreboard")) {
            if (args.length == 0) {
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "~~~~~~~~~~~~~~~~~~~~~~");
                sender.sendMessage(ChatColor.GREEN + "CustomScoreboard v" + this.getDescription().getVersion().toString());
                sender.sendMessage(ChatColor.BLUE + "By JOPHESTUS");
                sender.sendMessage(ChatColor.RED + "/cs reload" + ChatColor.GREEN + " - reloads all scoreboards");
                sender.sendMessage(ChatColor.LIGHT_PURPLE + "~~~~~~~~~~~~~~~~~~~~~~");
            } else if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("customscoreboard.reload")) {


                    for (Player plr : Bukkit.getServer().getOnlinePlayers()) {
                        setupScoreboard(plr);
                    }
                    sender.sendMessage(ChatColor.GREEN + "All scoreboards reloaded");
                } else {
                    sender.sendMessage(ChatColor.GREEN + "You can't do that");
                }
            }      else if (args.length == 1 && args[0].equalsIgnoreCase("disable") && !cunts.contains(sender.getName())) {

                cunts.add(sender.getName());
                getConfig().set("cunts", cunts);
                saveConfig();
                reloadConfig();

            }   else if (args.length == 1 && args[0].equalsIgnoreCase("enable")) {

            cunts.remove(sender.getName());
            getConfig().set("cunts", cunts);
            saveConfig();
            reloadConfig();

        }
        }


        return super.onCommand(sender, command, label, args);

    }

    @EventHandler
    public void die(EntityDeathEvent e) {
        int pveHKills;

        if(e.getEntity().getKiller() instanceof Player){
            reloadConfig();
         pveHKills = getConfig().getInt("pvekills." + e.getEntity().getKiller().getName());
            pveHKills ++;
            getConfig().set("pvekills." + e.getEntity().getKiller().getName(), pveHKills);
                  saveConfig();
            reloadConfig();
            setupScoreboard(e.getEntity().getKiller());

               }

    }


    @EventHandler
    public void kill(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player && e.getEntity() instanceof Player) {
            reloadConfig();
            int kills;
            kills = getConfig().getInt("kills." + e.getEntity().getKiller().getName());

            int deaths = getConfig().getInt("deaths." + e.getEntity().getName());
            kills ++;
            deaths ++;
            getConfig().set("kills." + e.getEntity().getKiller().getName(), kills);


            getConfig().set("deaths." + e.getEntity().getPlayer().getName(), deaths);
            saveConfig();
            reloadConfig();
            setupScoreboard(e.getEntity().getPlayer());
            setupScoreboard(e.getEntity().getKiller().getPlayer());
        }       else{
            int pvedeaths;
            pvedeaths = getConfig().getInt("deaths." + e.getEntity().getPlayer().getName());
            pvedeaths ++;
            getConfig().set("deaths." + e.getEntity().getPlayer().getName(), pvedeaths);
            saveConfig();
            reloadConfig();
            setupScoreboard(e.getEntity().getPlayer());

        }


        }


    @EventHandler
    public void blockbreak(BlockBreakEvent e) {
       reloadConfig();
        int broken = getConfig().getInt("break." + e.getPlayer().getName());
        broken ++;
        getConfig().set("break." + e.getPlayer().getName(), broken);
        saveConfig();
        reloadConfig();
        setupScoreboard(e.getPlayer());



    }

    @EventHandler
    public void blockplace(BlockPlaceEvent e) {
        reloadConfig();
        int placed = getConfig().getInt("place." + e.getPlayer().getName());
        placed ++;
        getConfig().set("place." + e.getPlayer().getName(), placed);
        saveConfig();
        reloadConfig();
        setupScoreboard(e.getPlayer());



    }



    @EventHandler
    public void swap(PlayerChangedWorldEvent e) {
        setupScoreboard(e.getPlayer());
    }


}
