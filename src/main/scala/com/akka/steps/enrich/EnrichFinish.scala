package com.akka.steps.enrich

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.akka.steps.model.Address

class EnrichFinish extends Actor with ActorLogging {
  def receive = {
    case address: Address =>
      val replyTo = sender()
      replyTo ! address
  }
}
