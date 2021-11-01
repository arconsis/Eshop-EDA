package com.arconsis.data

import com.arconsis.domain.User
import io.smallrye.reactive.messaging.kafka.Record
import org.eclipse.microprofile.reactive.messaging.Channel
import org.eclipse.microprofile.reactive.messaging.Emitter
import javax.enterprise.context.ApplicationScoped

@ApplicationScoped
class EventBusRepository(@Channel("users-out") private val emitter: Emitter<Record<String, User>>) {

  fun sendUserEvent(event: UserEvent) {
    emitter.send(Record.of(event.key, event.value)).toCompletableFuture().get()
  }
}