package tags

import kotlinx.serialization.Serializable

@Serializable
data class Tags(
    val id_tags: String,
    val name: String
)