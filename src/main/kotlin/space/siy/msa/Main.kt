package space.siy.msa


import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerLoginEvent
import org.bukkit.plugin.java.JavaPlugin

class Main : JavaPlugin(), Listener {

    lateinit var apiServer: ApiServer

    override fun onEnable() {
        apiServer = ApiServer(this)
        apiServer.start()
        server.pluginManager.registerEvents(this, this)
        saveDefaultConfig()
    }

    override fun onDisable() {
        apiServer.stop()
    }

    @EventHandler
    fun onLogin(event: PlayerLoginEvent) {
    }

    @EventHandler
    fun onDead(event: PlayerDeathEvent) {
    }
}
