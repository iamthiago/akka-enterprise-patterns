package com.akka.steps.enrich

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.akka.steps.model.Address

class EnrichNeighborhood(pipe: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case address: Address =>
      pipe forward address.copy(neighborhood = Some("my neighborhood"))
  }
}
