package gg.obsidian.discoursegroupsync

import java.util.*

data class User(val username: String = "", val minecraftGroups: Set<String> = HashSet(), val exists: Boolean = true)
