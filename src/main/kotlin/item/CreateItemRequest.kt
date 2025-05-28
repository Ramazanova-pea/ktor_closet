package item

data class CreateItemRequest(
    val token: String,
    val name: String,
    val imagePath: String,
    val notes: String?,
    val tags: List<String>
)
