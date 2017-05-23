package com.akka.steps.enrich

import akka.actor.{Actor, ActorLogging, ActorRef, ActorSystem, Props}
import com.akka.steps.model.Address

import scala.annotation.tailrec
import scala.util.Random

object EnrichOrganizer extends App {

  val system = ActorSystem()

  val endActor = system.actorOf(Props[EnrichFinish], "finish-actor")

  val p1 = getPath(Address(Some("state"), Some("city")))
  val p2 = getPath(Address(None, None))
  val p3 = getPath(Address(Some("state"), None, Some("neighborhood")))
  val p4 = getPath(Address(Some("state"), Some("city"), Some("neighborhood")))
  val p5 = getPath(Address(Some("state"), Some("city"), Some("neighborhood"), Some("street")))

  println(createActor(p1))
  println(createActor(p2))
  println(createActor(p3))
  println(createActor(p4))
  println(createActor(p5))

  type ActorPosition = (Int, Class[_ <: Actor with ActorLogging])

  def getPath(address: Address): Seq[ActorPosition] = {

    val initialAcc = if (address.state.isDefined && address.city.isDefined) {
      Seq((1, classOf[EnrichStateAndCity]))
    } else Seq.empty[ActorPosition]

    @tailrec
    def accumulator(map: Map[String, Any], acc: Seq[ActorPosition]): Seq[ActorPosition] = {

      if (map.isEmpty) acc else {
        val key = map.head._1

        val steps: Seq[ActorPosition] = key match {
          case "neighborhood" => acc.+:(2, classOf[EnrichNeighborhood])
          case "street" => acc.+:(3, classOf[EnrichStreet])
          case "number" => acc.+:(4, classOf[EnrichNumber])
          case "zipCode" => acc.+:(5, classOf[EnrichZipcode])
        }

        accumulator(map.tail, steps)
      }
    }

    accumulator(address.toMap, initialAcc).sortBy(_._1).reverse
  }

  def createActor(seq: Seq[ActorPosition]): ActorRef = {
    @tailrec
    def inner(seq: Seq[ActorPosition], nextStep: ActorRef): ActorRef = {
      if (seq.isEmpty) nextStep else {
        val clazz = seq.head._2
        val random = Random.nextInt().toString
        val actor = system.actorOf(Props.apply(clazz, nextStep), s"${clazz.getCanonicalName}-$random")
        inner(seq.tail, actor)
      }
    }

    inner(seq, endActor)
  }
}
