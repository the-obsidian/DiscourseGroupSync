package gg.obsidian.discoursegroupsync

import java.util.*

class Configuration(val plugin: DiscourseGroupSync) {

    var DISCOURSE_URL = ""
    var GROUPS: MutableMap<Int, Group> = HashMap<Int, Group>()

    fun load() {
        plugin.reloadConfig()

        DISCOURSE_URL = plugin.getConfig().getString("discourse-url")

        if (plugin.getConfig().isList("groups")) {
            for (rawDefinition in plugin.getConfig().getMapList("groups")) {
                val definition = rawDefinition as Map<String, Any>
                var discourseGroup: Int? = null
                var minecraftGroup: String? = null

                if (definition.contains("discourse") && definition.getRaw("discourse") is String) {
                    discourseGroup = definition["discourse"] as Int
                }

                if (definition.contains("minecraft")) {
                    minecraftGroup = definition["minecraft"] as String
                }

                if (discourseGroup == null || minecraftGroup == null) return

                val group = Group(discourseGroup, minecraftGroup)
                GROUPS.put(discourseGroup, group)
            }
        }
    }
}
