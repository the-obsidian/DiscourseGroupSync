package gg.obsidian.discoursegroupsync

import com.squareup.okhttp.OkHttpClient
import com.squareup.okhttp.Request
import org.json.JSONObject
import java.util.*

object UUIDHelper {

    val PROFILE_URL = "https://sessionserver.mojang.com/session/minecraft/profile/"
    val httpClient = OkHttpClient()

    fun uuidToUsername(uuid: UUID): String {
        val url = PROFILE_URL + uuid.toString().replace("-", "")
        val request = Request.Builder().url(url).get().build();
        val response = httpClient.newCall(request).execute()
        val body = JSONObject(response.body().string())

        if (body.has("error")) {
            return ""
        }

        return body.getString("name")
    }
}
