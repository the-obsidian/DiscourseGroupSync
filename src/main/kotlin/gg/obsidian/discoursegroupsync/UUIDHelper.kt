package gg.obsidian.discoursegroupsync

import com.mashape.unirest.http.HttpResponse
import com.mashape.unirest.http.JsonNode
import com.mashape.unirest.http.Unirest
import java.util.*

object UUIDHelper {
    val PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/"

    fun uuidToUsername(uuid: UUID): String {
        val url = PROFILE_URL + uuid.toString().replace("-", "")
        val jsonResponse: HttpResponse<JsonNode> = Unirest.get(url).asJson()
        val body = jsonResponse.body.`object`

        if (body.has("error")) {
            return ""
        }

        return body.getString("name")
    }
}