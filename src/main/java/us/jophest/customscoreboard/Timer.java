package us.jophest.customscoreboard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Timer {


	    private final CustomScoreboard plugin;
	 //   private String prefix = ChatColor.translateAlternateColorCodes('&', "&3[&6Prefix&3] &6");
	    private int countdownTimer;
	 
	    public Timer(CustomScoreboard i)
	    {
	        this.plugin = i;
	    }
	 
	    public void startCountdown(final String string, final boolean all, final int time, final String msg)
	    {
	        this.countdownTimer = Bukkit.getServer().getScheduler().scheduleSyncRepeatingTask(this.plugin, new Runnable()
	        {
	            int i = time;
	 
	            public void run()
	            {
	                if(all)
	                {
	                   // Bukkit.broadcastMessage(Timer.this.prefix + ChatColor.translateAlternateColorCodes('&', msg.replace("#", Integer.toString(i))));
	                }
	                else
	                {
	                //    p.sendMessage(Timer.this.prefix + ChatColor.translateAlternateColorCodes('&', msg.replace("#", Integer.toString(i))));
	                }
	                this.i--;
	                if (this.i <= 0) Timer.this.cancel();
	            }
	        }
	        , 0L, 20L);
	    }
	 
	    public void cancel()
	    {
	        Bukkit.getScheduler().cancelTask(this.countdownTimer);
            for (Player player : Bukkit.getOnlinePlayers()) {
                plugin.setupScoreboard(player);
            }

	        plugin.startTimer();
	    }
	}
