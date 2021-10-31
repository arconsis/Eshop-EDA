package com.arconsis

import com.arconsis.domain.inventory.KafkaService
import io.quarkus.runtime.ShutdownEvent
import io.quarkus.runtime.StartupEvent
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes


@ApplicationScoped
class AppLifecycleBean(
  val kafkaService: KafkaService
) {
  fun onStart(@Observes ev: StartupEvent?) {
    kafkaService.consumeStream()
  }

  fun onStop(@Observes ev: ShutdownEvent?) {
    kafkaService.stopConsumeStream()
  }
}