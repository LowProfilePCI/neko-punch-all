package moe.nekochan.punchall;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.plugin.java.JavaPlugin;

public class PunchAll extends JavaPlugin implements Listener {

	private FileConfiguration config;
	private String prefix;
	private String suffix;
	private List<Message> messages = new ArrayList<>();
	private int chanceSum;
	private static final Random rnd = new Random();
	private static final String PREFIX = "§8§l[§d§lNekoPunchAll§8§l]§r ";
	private static final String CMD_HELP =
			PREFIX+"サブコマンド一覧にゃ！\n"
			+ "§6/nekopunch list§f: 設定されているメッセージ一覧を表示するにゃ！\n"
			//+ "§6/nekopunch add§f: メッセージを追加するにゃ！\n"
			//+ "§6/nekopunch remove <ID>§f: メッセージを削除するにゃ！\n"
			+ "§6/nekopunch prefix [プレフィックス]§f: プレフィックスを表示/設定するにゃ！\n"
			+ "§6/nekopunch suffix [サフィックス]§f: サフィックスを表示/設定するにゃ！";


	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (!(sender instanceof ConsoleCommandSender)) {
			sender.sendMessage(PREFIX+"コンソール以外からは お断りします～♪断固！");
			return true;
		}
		if (command.getName().equals("nekopunch")) {
			if (args.length == 0) {
				sender.sendMessage(CMD_HELP);
			} else {
				String subcmd = args[0];
				if (subcmd.equalsIgnoreCase("list")) {
					StringBuilder builder = new StringBuilder(PREFIX+"現在設定されてるメッセージ一覧にゃ！\n");
					for(Message msg : messages)
						builder.append("§7[").append(String.format("%.1f", 100.0*msg.getWeight()/chanceSum)).append("%]§r ").append(msg.getMessage()).append("\n");
					sender.sendMessage(builder.toString());
				} else if (subcmd.equalsIgnoreCase("prefix")) {
					if (args.length == 1) {
						sender.sendMessage(PREFIX+"現在のプレフィックス: "+prefix);
					} else {
						setPrefix(args[1]);
						sender.sendMessage(PREFIX+"新しいプレフィックスを設定したにゃ！");
					}
				} else if (subcmd.equalsIgnoreCase("suffix")) {
					if (args.length == 1) {
						sender.sendMessage(PREFIX+"現在のサフィックス: "+suffix);
					} else {
						setSuffix(args[1]);
						sender.sendMessage(PREFIX+"新しいサフィックスを設定したにゃ！");
					}
				}
			}
		}
		return true;
	}


	@Override
	public void onDisable() {
		super.onDisable();
	}

	@Override
	public void onEnable() {
		super.onEnable();

		saveDefaultConfig();
		config = getConfig();

		prefix = config.getString("prefix");
		suffix = config.getString("suffix");

		ConfigurationSection msgsSection = config.getConfigurationSection("messages");
		Set<String> keyNames = msgsSection.getKeys(false);
		for(String keyName : keyNames) {
			ConfigurationSection msgSection = msgsSection.getConfigurationSection(keyName);
			String body = msgSection.getString("body");
			int weight = msgSection.getInt("weight");
			messages.add(new Message(weight, body));
		}

		for(Message msg : messages)
			chanceSum += msg.getWeight();

		Bukkit.getPluginManager().registerEvents(this, this);
	}


	@EventHandler
	public void onLogin(PlayerLoginEvent e) {
		String msg = prefix+getRandomMessage().getMessage()+suffix;
		msg = msg.replace("<player>", e.getPlayer().getName());

		e.disallow(Result.KICK_OTHER, msg);
	}


	public Message getRandomMessage() {
		int rndNum = rnd.nextInt(chanceSum);
		for(Message msg : messages) {
			if ((rndNum-=msg.getWeight()) < 0)
				return msg;
		}
		return null;
	}


	public void setPrefix(String prefix) {
		this.prefix = prefix;
		config.set("prefix", prefix);
		saveConfig();
	}


	public void setSuffix(String suffix) {
		this.suffix = suffix;
		config.set("suffix", suffix);
		saveConfig();
	}
}
