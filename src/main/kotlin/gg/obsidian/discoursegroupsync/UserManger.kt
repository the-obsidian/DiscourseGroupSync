package gg.obsidian.discoursegroupsync

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.bukkit.entity.Player
import org.json.JSONObject
import java.util.*

class UserManager(val plugin: DiscourseGroupSync) {

    val httpClient = OkHttpClient()

    fun onJoin(player: Player): Boolean {
        val user = getDiscourseUser(player)

        syncGroups(player, user)
        return checkWhitelist(user)
    }

    fun getDiscourseUser(player: Player): User? {
        val username = UUIDHelper.uuidToUsername(player.uniqueId)
        if (username == "") return null

        val url = plugin.config.DISCOURSE_URL + "/users/" + username + ".json"
        val request = Request.Builder().url(url).get().build();
        val response = httpClient.newCall(request).execute()

        if (response.code() != 200) {
            return User(exists = false)
        }

        val bodyString = response.body().string()
        val body = JSONObject(bodyString)
        val user = body.getJSONObject("user")
        val customGroups = user.getJSONArray("custom_groups")

        val discourseGroups = HashSet<Int>()

        for (g in customGroups) {
            val group = g as JSONObject
            val id = group.getLong("id")
            discourseGroups.add(id.toInt())
        }

        if (customGroups.length() == 0) {
            discourseGroups.add(0)
        }

        return User(username = username, discourseGroups = discourseGroups)
    }

    fun checkWhitelist(user: User?): Boolean {
        if (user == null) {
            for (group in plugin.config.GROUPS) {
                if (group.whitelist == true)
                    return false
            }
            return true
        }

        var canJoin = true

        for (group in plugin.config.GROUPS) {
            if (group.whitelist != true) continue

            if (user.discourseGroups.contains(group.discourseGroup)) {
                return true
            } else {
                canJoin = false
            }
        }

        return canJoin
    }

    fun syncGroups(player: Player, user: User?) {
        if (user == null) return
        val groupsToAdd = HashSet<String>()
        val groupsToRemove = HashSet<String>()

        for (group in plugin.config.GROUPS) {
            val absence = group.discourseGroup < 0
            val discordGroup = if (absence) Math.abs(group.discourseGroup) else group.discourseGroup
            val hasGroup = user.discourseGroups.contains(discordGroup)

            if (group.discourseGroup == 0 && user.discourseGroups.size == 0) {
                if (group.remove) {
                    groupsToRemove.add(group.minecraftGroup)
                } else {
                    groupsToAdd.add(group.minecraftGroup)
                }
                continue
            }

            if (hasGroup && !absence) {
                if (group.remove) {
                    groupsToRemove.add(group.minecraftGroup)
                } else {
                    groupsToAdd.add(group.minecraftGroup)
                }
            }

            if (!hasGroup && absence) {
                if (group.remove) {
                    groupsToRemove.add(group.minecraftGroup)
                } else {
                    groupsToAdd.add(group.minecraftGroup)
                }
            }
        }

        for (group in groupsToAdd) {
            plugin.logger.info("Adding ${user.username} to group $group")
            plugin.permissions?.playerAddGroup(player, group)
        }

        for (group in groupsToRemove) {
            plugin.logger.info("Removing ${user.username} from group $group")
            plugin.permissions?.playerRemoveGroup(player, group)
        }
    }
}
