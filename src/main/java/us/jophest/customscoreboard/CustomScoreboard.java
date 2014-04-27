package us.jophest.customscoreboard;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;

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
    public static HashMap<String, Integer> values = new HashMap<String, Integer>();
    public List<String> myTop5 = new ArrayList();
    ScoreboardManager manager2;
    String section;
    Objective obj;
    Objective listobj;
    Scoreboard board;
    Scoreboard list;


    public void onDisable() {
        // TODO: Place any custom disable code here.
    }

    public void onEnable() {
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

        if (Bukkit.getServer().getOnlinePlayers().length > 0) {
            for (Player plr : Bukkit.getServer().getOnlinePlayers()) {
                reloadConfig();

                bal = (int) econ.getBalance(playaa.getName());
                if (!getConfig().contains("deaths." + playaa.getName())){
                    getConfig().set("deaths." + playaa.getName(), 0);
                }
                death = getConfig().getInt("deaths." + playaa.getName());



                // commentstart
                Map scoreMap = new HashMap();
                List finalScore = new ArrayList();

                int amount = getConfig().getInt("kills.Kits." + playaa.getName()) + getConfig().getInt("kills.Arena." + playaa.getName()) + getConfig().getInt("kills.Stone." + playaa.getName()) + getConfig().getInt("kills.pvp." + playaa.getName());
                kill = amount;

                section = "kills." + playaa.getWorld().getName();

                ConfigurationSection score = getConfig().getConfigurationSection(section);
               if (!getConfig().contains("kills." + playaa.getWorld().getName() + "." + playaa.getName())){
                 getConfig().set("kills." + playaa.getWorld().getName() + "." + playaa.getName(), 0);
                     }
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
                        parts0[1] = parts0[1].replace("?","§");
                        parts1[1] = parts1[1].replace("?","§");
                        parts2[1] = parts2[1].replace("?","§");
                        parts0[0] = parts0[0].replace("?", "§");
                        parts1[0] = parts1[0].replace("?", "§");
                        parts2[0] = parts2[0].replace("?", "§");
                         if(parts0[1].equals(parts1[1])){
                             parts0[1] = parts0[1].replace("§b", "§4§b");
                         }
                        if(parts0[1].equals(parts2[1])){
                            parts0[1] = parts0[1].replace("§b", "§4§b");
                        }
                        if(parts1[1].equals(parts2[1])){
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
                        parts0[1] = parts0[1].replace("?","§");
                        parts1[1] = parts1[1].replace("?","§");
                        parts0[0] = parts0[0].replace("?","§");
                        parts1[0] = parts1[0].replace("?","§");
                        if(parts0[1].equals(parts1[1])){
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
                        parts0[1] = parts0[1].replace("?","§");
                        parts0[0] = parts0[0].replace("?","§");

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
                obj.setDisplayName(ChatColor.DARK_RED.toString() + ChatColor.BOLD.toString() + ChatColor.UNDERLINE + "    STATS    ");
                listobj.setDisplaySlot(DisplaySlot.SIDEBAR);
                listobj.setDisplayName("§f§lAce§8§lPvP");

                Score coins = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GOLD.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "Coins:")); //Get a fake offline player
                Score coinsAmount = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.YELLOW.toString() + bal)); //Get a fake offline player
                Score kills = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_GREEN.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "Kills:"));
                Score killsAmount = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN.toString() + kill));
                Score deaths = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_RED.toString() + ChatColor.UNDERLINE + ChatColor.BOLD + "Deaths:"));
                Score deathsAmount = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.RED.toString() + death));
                Score line = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.GREEN.toString() + ChatColor.BOLD + "------------"));
                Score best = obj.getScore(Bukkit.getOfflinePlayer(ChatColor.DARK_BLUE.toString() + ChatColor.BOLD + "Best Players"));

                Score list1 = listobj.getScore(Bukkit.getOfflinePlayer("§4§lKitPvP"));
                Score list2 = listobj.getScore(Bukkit.getOfflinePlayer("§fPlayers: §8" + Bukkit.getWorld("Kits").getPlayers().size()));
                Score list3 = listobj.getScore(Bukkit.getOfflinePlayer("§4§lWood"));
                Score list4 = listobj.getScore(Bukkit.getOfflinePlayer("§7Players: §f" + Bukkit.getWorld("Stone").getPlayers().size()));
                Score list5 = listobj.getScore(Bukkit.getOfflinePlayer("§4§lArena"));
                Score list6 = listobj.getScore(Bukkit.getOfflinePlayer("§8Players: §7" + Bukkit.getWorld("Arena").getPlayers().size()));
                list1.setScore(6);
                list2.setScore(5);
                list3.setScore(4);
                list4.setScore(3);
                list5.setScore(2);
                list6.setScore(1);
                coins.setScore(14);


                coinsAmount.setScore(13);
                kills.setScore(12);
                killsAmount.setScore(11);
                deaths.setScore(10);
                deathsAmount.setScore(9);
                line.setScore(8);
                best.setScore(7);
                if (playaa.getWorld().getName().equalsIgnoreCase("stone")){
                    playaa.setScoreboard(board);
                }
                if (playaa.getWorld().getName().equalsIgnoreCase("arena")){
                    playaa.setScoreboard(board);
                }
                if (playaa.getWorld().getName().equalsIgnoreCase("kits")){
                    playaa.setScoreboard(board);
                }
                if (playaa.getWorld().getName().equalsIgnoreCase("pvp")){
                    playaa.setScoreboard(list);
               }


            }


        }

    }


    @EventHandler
    public void join(PlayerJoinEvent e) {

            setupScoreboard(e.getPlayer());


    }

    @EventHandler
    public void kill(PlayerDeathEvent e) {
        if (e.getEntity().getKiller() instanceof Player && e.getEntity() instanceof Player) {
            String world = e.getEntity().getWorld().getName();
            int kills = getConfig().getInt("kills." + world + "." + e.getEntity().getKiller().getName());
            int deaths = getConfig().getInt("deaths." + e.getEntity().getName());
            int killz = kills + 1;
            int deathz = deaths + 1;
            getConfig().set("kills." + world + "." + e.getEntity().getKiller().getName(), killz);
            getConfig().set("deaths." + e.getEntity().getName(), deathz);
            saveConfig();
            reloadConfig();

        }


        setupScoreboard(e.getEntity());
        setupScoreboard(e.getEntity().getKiller());
    }

    @EventHandler
    public void swap(PlayerChangedWorldEvent e){
        setupScoreboard(e.getPlayer());
    }


}