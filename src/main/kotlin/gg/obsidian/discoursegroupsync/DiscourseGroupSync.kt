package gg.obsidian.discoursegroupsync

import net.milkbowl.vault.permission.Permission
import org.bukkit.event.EventHandler
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

    @EventHandler
    fun onJoin(e: PlayerJoinEvent) {
        userManager.syncGroups(e.player)
    }

    fun setupPermissions(): Boolean {
        val rsp: RegisteredServiceProvider<Permission> = getServer().getServicesManager().getRegistration(Permission::class.java)
        permissions = rsp.provider
        return permissions != null
    }
}
