package gg.obsidian.discoursegroupsync

import org.bukkit.event.Listener
import org.bukkit.plugin.java.JavaPlugin
import java.io.File
import java.util.*

class DiscourseGroupSync : JavaPlugin(), Listener {

    val config = Configuration(this)

    override fun onEnable() {
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            getConfig().options().copyDefaults(true)
            saveConfig()
        }

        config.load()

        server.pluginManager.registerEvents(this, this)
    }

    fun getUser(username: String): List<Int> {
        return ArrayList<Int>();
    }
}
