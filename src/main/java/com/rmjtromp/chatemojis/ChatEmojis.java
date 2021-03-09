package com.rmjtromp.chatemojis;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.regex.Pattern;

import com.rmjtromp.chatemojis.exceptions.UnsupportedVersionException;
import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.permissions.Permission;
import org.bukkit.plugin.java.JavaPlugin;

import com.rmjtromp.chatemojis.exceptions.ConfigException;
import com.rmjtromp.chatemojis.utils.Config;
import com.rmjtromp.chatemojis.utils.Lang;
import com.rmjtromp.chatemojis.utils.bukkit.Version;
import org.jetbrains.annotations.NotNull;

/**
 * {@link ChatEmojis} main plugin class.
 * @author Melvin
 */
public final class ChatEmojis extends JavaPlugin {

    static final List<String> RESERVED_NAMES = Arrays.asList("emoticon", "emoji", "regex", "enabled");
    static final Pattern NAME_PATTERN = Pattern.compile("(?<=\\.)?([^.]+?)$", Pattern.CASE_INSENSITIVE);
    static final Random RANDOM = new Random();

	// plugin stuff
    private static ChatEmojis plugin = null;
    private EmojiGroup emojis = null;
    private Settings settings = null;

    // soft dependencies
    boolean papiIsLoaded = false;
    boolean essentialsIsLoaded = false;
    
    // configs
    Config config = null;
    Config emojisConfig = null;

    public ChatEmojis() throws IOException {
        plugin = this;

        // load base (also default) language
		try(InputStream in = getResource("lang/en_US.yml");
			InputStreamReader reader = new InputStreamReader(in, StandardCharsets.UTF_8)) {
			Lang.load(reader);
		}
    }
    
    @Override
    public void onLoad() {
        try {
        	// load configs
			config = Config.init(new File(getDataFolder(), "config.yml"), "config.yml");
			emojisConfig = Config.init(new File(getDataFolder(), "emojis.yml"), "emojis.yml");
			
			// load settings
	        settings = new Settings();
	        
	        // load emojis
            emojis = EmojiGroup.init(emojisConfig);
            
            // load layered language
            if(config.isString("lang")) {
            	String lang = config.getString("lang");
            	if(!lang.equals("en_US")) {
            		// TODO check for a language & load it if available
            	}
            }
		} catch (IOException | InvalidConfigurationException e) {
			System.out.println("[ChatEmojis] "+Lang.translate("error.load.config"));
			getServer().getPluginManager().disablePlugin(this);
			e.printStackTrace();
		} catch (ConfigException e) {
			System.out.println("[ChatEmojis] "+Lang.translate("error.load.emojis"));
			getServer().getPluginManager().disablePlugin(this);
			e.printStackTrace();
		}
    }

	@Override
	public void onEnable() {
    	try {
			Version.getServerVersion();
		} catch(UnsupportedVersionException e) {
			System.out.println("[ChatEmojis] "+Lang.translate("error.load.unsupported-version"));
			System.out.println("[ChatEmojis] "+Lang.translate("error.load.disabling-plugin"));
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		
		// check for soft dependencies
        papiIsLoaded = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
        essentialsIsLoaded = Bukkit.getPluginManager().getPlugin("Essentials") != null;
        
        // load handlers
        EventHandler.init();
        CommandHandler.init();
    }
	
	@Override
	public void onDisable() {
		config.forceSave();
		CommandHandler.disable();
		
		/*
		 * emojis config is not being modified by the plugin right now
		 * therefore saving it is pointless at the moment
		 * // emojisConfig.forceSave();
		 */
	}
    
	/**
	 * Returns {@link ChatEmojis} settings instance. The settings contains everything that is toggle-able.
	 * @return the {@link ChatEmojis} plugin's {@link Settings}
	 */
    public Settings getSettings() {
    	return settings;
    }
    
    /**
     * Returns the {@link EmojiGroup} which is parent to all emojis.
     * This group does not have a name, and will return <code>null</code>.
     * @return The parent {@link EmojiGroup} 
     */
    public EmojiGroup getEmojis() {
    	return emojis;
    }
    
    /**
     * Reloads emojis config, then reloads all emojis from config
     */
    private void reloadEmojis() {
    	try {
    		emojisConfig.reload();
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        } finally {
            try {
				emojis = EmojiGroup.init(emojisConfig);
			} catch (ConfigException e) {
				e.printStackTrace();
			}
        }
    }
    
    /**
     * Reloads everything that is configurable
     */
    void reload() {
    	// reload configuration files
    	try {
			config.reload();
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		} finally {
	    	// reload layered language
			String lang = config.getString("lang", "en_US");
			if(!lang.equals("en_US")) {
				// TODO check for a language & load it if available
			}
	        
	        // reload emojis
	    	reloadEmojis();
		}
    	
    	// reload settings
    	settings.reload();
    	
    	
    	// reload command executor, tab-completer, and GUIs
        CommandHandler.reload();
    }
    
    /**
     * Returns the {@link Config} instance of {@link ChatEmojis}'s <code>config.yml</code>
     * @see YamlConfiguration
     * @return {@link Config}
     */
    @Override
    public @NotNull Config getConfig() {
		return config;
	}

    /**
     * @return {@link ChatEmojis} Instance
     */
    public static ChatEmojis getInstance() {
        return plugin;
    }
    
    /**
     * {@link AbstractEmoji} interface implemented by {@link Emoji} and {@link EmojiGroup},
     * and is used in {@link EmojiGroup} to iterate over all child instances
     * @since 2.2.1
     * @author Melvin
	 * @see Emoji
	 * @see EmojiGroup
     */
    interface AbstractEmoji {

    	/**
    	 * @return the name of the {@link AbstractEmoji}
    	 * @see Emoji
    	 * @see EmojiGroup
    	 */
    	String getName();
    	
    	/**
    	 * @return The required {@link Permission} node required to use this {@link AbstractEmoji}
    	 * @see Emoji
    	 * @see EmojiGroup
    	 */
    	Permission getPermission();
    	
    }

}
