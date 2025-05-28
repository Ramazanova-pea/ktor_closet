package look

import kotlinx.serialization.Serializable

@Serializable
data class CreateLookRequest(
    val name: String,
    val notes: String?,
    val items: List<String>
)
