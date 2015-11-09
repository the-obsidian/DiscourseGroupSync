package gg.obsidian.discoursegroupsync

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import org.bukkit.entity.Player
import java.util.*

class UserManager(val plugin: DiscourseGroupSync) {

    fun getDiscourseUser(username: String): User {
        val url = plugin.config.DISCOURSE_URL + "/users/" + username + ".json"
        val jsonResponse: HttpResponse<JsonNode> = Unirest.get(url).asJson()

        if (jsonResponse.status != 200) {
            return User(exists = false)
        }

        val body = jsonResponse.body.`object`

        val minecraftGroups = HashSet<String>()

        val customGroups = body.getJSONArray("custom_groups")

        for (i in (0..customGroups.length() - 1)) {
            val group = customGroups.get(i) as JsonNode
            val groupId = group.getObject().getInt("id")

            if (plugin.config.GROUPS.containsKey(groupId)) {
                minecraftGroups.add(plugin.config.GROUPS[groupId].minecraftGroup)
            }
        }

        return User(username = username, minecraftGroups = HashSet<String>())
    }

    fun syncGroups(player: Player) {
        val username = UUIDHelper.uuidToUsername(player.uniqueId)
        if (username == "") return

        val discourseUser = getDiscourseUser(username)
    }
}