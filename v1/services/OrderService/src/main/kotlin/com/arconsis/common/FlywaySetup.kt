package com.arconsis.common

import io.quarkus.runtime.StartupEvent
import org.eclipse.microprofile.config.inject.ConfigProperty
import org.flywaydb.core.Flyway
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class FlywaySetup {
    @ConfigProperty(name = "datasource.url")
    var datasourceUrl: String? = null

    @ConfigProperty(name = "quarkus.datasource.username")
    var datasourceUsername: String? = null

    @ConfigProperty(name = "quarkus.datasource.password")
    var datasourcePassword: String? = null
    fun runFlywayMigration(@Observes event: StartupEvent?) {
        val flyway = Flyway.configure().dataSource("jdbc:$datasourceUrl", datasourceUsername, datasourcePassword).load()
        flyway.migrate()
    }
}