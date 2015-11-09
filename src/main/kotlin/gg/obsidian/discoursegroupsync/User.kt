package gg.obsidian.discoursegroupsync

import java.util.*

data class User(val username: String = "", val discourseGroups: Set<Int> = HashSet(), val exists: Boolean = true)
