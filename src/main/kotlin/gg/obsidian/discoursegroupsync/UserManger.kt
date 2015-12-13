package gg.obsidian.discoursegroupsync

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.bukkit.entity.Player
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.JSONValue
import java.util.*

class UserManager(val plugin: DiscourseGroupSync) {

    val httpClient = OkHttpClient()

    fun getDiscourseUser(username: String): User {
        val url = plugin.config.DISCOURSE_URL + "/users/" + username + ".json"
        val request = Request.Builder().url(url).get().build();
        val response = httpClient.newCall(request).execute()

        if (response.code() != 200) {
            return User(exists = false)
        }

        val bodyString = response.body().string()
        val body = JSONValue.parse(bodyString) as JSONObject
        val user = body.getRaw("user") as JSONObject
        val customGroups = user.getRaw("custom_groups") as JSONArray

        val discourseGroups = HashSet<Int>()

        for (g in customGroups) {
            val group = g as JSONObject
            val id = group.getRaw("id") as Long
            discourseGroups.add(id.toInt())
        }

        if (customGroups.size == 0) {
            discourseGroups.add(0)
        }

        return User(username = username, discourseGroups = discourseGroups)
    }

    fun syncGroups(player: Player) {
        val username = UUIDHelper.uuidToUsername(player.uniqueId)
        if (username == "") return

        val user = getDiscourseUser(username)

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
            plugin.logger.info("Adding $username to group $group")
            plugin.permissions?.playerAddGroup(player, group)
        }

        for (group in groupsToRemove) {
            plugin.logger.info("Removing $username from group $group")
            plugin.permissions?.playerRemoveGroup(player, group)
        }
    }
}
