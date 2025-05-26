package tags

import io.ktor.server.application.Application
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.routing
import ru.fanofstars.register.RegisterController

fun Route.configureTagsRouting() {
    post("/create-tags") {
        TagsController(call).createNewTag()
    }
    get("/create-tags") {
        call.respond("Tags")
    }
}
