package space.siy.msa

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.authenticate
import io.ktor.auth.basic
import io.ktor.features.ContentNegotiation
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.locations.Location
import io.ktor.locations.Locations
import io.ktor.locations.get
import io.ktor.locations.put
import io.ktor.request.receive
import io.ktor.request.receiveParameters
import io.ktor.response.respond
import io.ktor.routing.get
import io.ktor.routing.put
import io.ktor.routing.routing
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.bukkit.Bukkit

class ApiServer(private val plugin: org.bukkit.plugin.java.JavaPlugin) {

    private val bukkitServer: org.bukkit.Server = plugin.server

    @Location("/")
    class Index

    @Location("/server/shutdown")
    class Shutdown

    @Location("/server/say")
    class Say {
        data class Body(val msg: String)
    }

    @Location("/users/{user}/tell")
    class Tell(val user: String) {
        data class Body(val msg: String)
    }

    @Location("/users/{user}/kick")
    class Kick(val user: String) {
        data class Body(val msg: String)
    }

    @Location("/users")
    class ListUser

    private val server = embeddedServer(Netty, 8080) {
        install(ContentNegotiation) {
            gson {
            }
        }
        install(Locations) {
        }
        install(Authentication) {
            basic {
                bukkitServer.logger.info("user: ${plugin.config["client-name"]}")
                bukkitServer.logger.info("user: ${plugin.config["client-password"]}")
                realm = "Ktor Server"
                validate { credentials ->
                    if (credentials.name == plugin.config["client-name"] && credentials.password == plugin.config["client-password"]) {
                        UserIdPrincipal(credentials.name)
                    } else {
                        null
                    }
                }
            }
        }
        routing {
            get<Index> {
                call.respond(
                    mapOf(
                        "msg" to "The GusyattoCraft Server API"
                    )
                )
            }
            authenticate {
                get<ListUser> {
                    call.respond(bukkitServer.onlinePlayers.map { it.name })
                }
                put<Shutdown> {
                    bukkitServer.shutdown()
                    call.successResponse()
                }
                put<Kick> {
                    val body = call.receive<Kick.Body>()
                    val player = bukkitServer.getPlayer(it.user)
                    if (player == null) {
                        call.onlineUserNotFoundResponse(it.user)
                        return@put
                    }
                    Bukkit.getScheduler().runTask(bukkitServer.pluginManager.getPlugin("GusyattoCraftApi")!!, Runnable {
                        player.kickPlayer(body.msg)
                    })
                    call.successResponse()
                }
                put<Tell> {
                    val body = call.receive<Tell.Body>()
                    bukkitServer.logger.info(body.msg)
                    val player = bukkitServer.getPlayer(it.user)
                    if (player == null) {
                        call.onlineUserNotFoundResponse(it.user)
                        return@put
                    }
                    player.sendMessage(body.msg)
                    call.successResponse()
                }
                put<Say> {
                    val body = call.receive<Say.Body>()
                    Bukkit.broadcastMessage(body.msg)
                    call.successResponse()
                }
            }
        }
    }

    fun start() = server.start(wait = false)

    fun stop() = server.stop(5000, 5000)

    private suspend fun ApplicationCall.onlineUserNotFoundResponse(user: String) {
        response.status(HttpStatusCode.NotFound)
        respond(
            mapOf(
                "msg" to "Online player called $user is not found"
            )
        )
    }

    private suspend fun ApplicationCall.successResponse() = respond(
        mapOf(
            "msg" to "success"
        )
    )
}
