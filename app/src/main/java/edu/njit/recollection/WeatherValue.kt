import org.json.JSONArray
import org.json.JSONObject

data class WeatherValue(
    val number: Int,
    val name: String,
    val isDaytime: Boolean,
    val temperature: Int,
    val shortForecast: String
) : java.io.Serializable