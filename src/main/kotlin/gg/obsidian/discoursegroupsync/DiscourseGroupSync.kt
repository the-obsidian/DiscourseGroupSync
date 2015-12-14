package gg.obsidian.discoursegroupsync

import net.milkbowl.vault.permission.Permission
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.plugin.RegisteredServiceProvider
import org.bukkit.plugin.java.JavaPlugin
import java.io.File

class DiscourseGroupSync : JavaPlugin(), Listener {

    val config = Configuration(this)
    val userManager = UserManager(this)
    var permissions: Permission? = null

    override fun onEnable() {
        val configFile = File(dataFolder, "config.yml")
        if (!configFile.exists()) {
            getConfig().options().copyDefaults(true)
            saveConfig()
        }

        config.load()

        if (!setupPermissions()) {
            logger.severe("Disabled due to no Vault dependency found!")
            server.pluginManager.disablePlugin(this)
            return
        }

        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler(priority = EventPriority.HIGH)
    fun onJoin(e: PlayerJoinEvent) {
        val canJoin = userManager.onJoin(e.player)
        if (!canJoin) {
            e.player.kickPlayer(config.KICK_MESSAGE)
        }
    }

    fun setupPermissions(): Boolean {
        val rsp: RegisteredServiceProvider<Permission> = server.servicesManager.getRegistration(Permission::class.java)
        permissions = rsp.provider
        return permissions != null
    }
}
