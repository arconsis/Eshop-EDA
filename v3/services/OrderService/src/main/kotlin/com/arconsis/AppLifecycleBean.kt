package com.arconsis

import io.quarkus.runtime.StartupEvent
import javax.enterprise.context.ApplicationScoped
import javax.enterprise.event.Observes

@ApplicationScoped
class AppLifecycleBean{
  fun onStart(@Observes ev: StartupEvent?) {
    // start streams for order-validations
  }
}