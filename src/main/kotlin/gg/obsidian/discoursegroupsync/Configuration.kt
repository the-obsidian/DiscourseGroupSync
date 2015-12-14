package gg.obsidian.discoursegroupsync

import java.util.*

class Configuration(val plugin: DiscourseGroupSync) {

    var DISCOURSE_URL = ""
    var KICK_MESSAGE = ""
    var GROUPS: HashSet<Group> = HashSet<Group>()

    fun load() {
        plugin.reloadConfig()

        DISCOURSE_URL = plugin.getConfig().getString("discourse-url")
        KICK_MESSAGE = plugin.getConfig().getString("kick-message", "You are not on the whitelist")

        for (rawDefinition in plugin.getConfig().getMapList("groups")) {
            val definition = rawDefinition as Map<String, Any>
            var discourseGroup: Int? = null
            var minecraftGroup: String? = null
            var remove = false
            var whitelist = false

            if (definition.contains("discourse")) {
                discourseGroup = definition["discourse"] as Int
            }

            if (definition.contains("minecraft")) {
                minecraftGroup = definition["minecraft"] as String
            }

            if (definition.contains("remove")) {
                remove = definition["remove"] as Boolean
            }

            if (definition.contains("whitelist")) {
                whitelist = definition["whitelist"] as Boolean
            }

            if (discourseGroup == null || minecraftGroup == null) continue

            val group = Group(
                    discourseGroup = discourseGroup,
                    minecraftGroup = minecraftGroup,
                    remove = remove,
                    whitelist = whitelist
            )
            GROUPS.add(group)
        }
    }
}
