package com.akka.steps.enrich

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.akka.steps.model.Address

class EnrichZipcode(pipe: ActorRef) extends Actor with ActorLogging {
  def receive = {
    case address: Address =>
      pipe forward address.copy(zipCode = Some("01050-130"))
  }
}
