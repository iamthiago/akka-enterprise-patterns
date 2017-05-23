package com.akka.steps.test

import akka.actor.{ActorSystem, PoisonPill, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.akka.steps.enrich.{EnrichNeighborhood, EnrichNumber, EnrichStreet, EnrichZipcode}
import com.akka.steps.model.Address
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}

class EnrichSpec extends TestKit(ActorSystem("MyTestSystem")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  def fixture = new {
    val address = Address(
      state = Some("SP"),
      city = Some("Barueri")
    )
  }

  "The enrichment system" must {

    "enrich neighborhood" in {
      val geocodeProbe = TestProbe()

      val enrichNeighborhood = system.actorOf(Props(new EnrichNeighborhood(geocodeProbe.ref)))

      enrichNeighborhood ! fixture.address
      geocodeProbe.expectMsg(fixture.address.copy(neighborhood = Some("my neighborhood")))
    }

    "enrich neighborhood and street" in {
      val geocodeProbe = TestProbe()

      val enrichStreet = system.actorOf(Props(new EnrichStreet(geocodeProbe.ref)))
      val enrichNeighborhood = system.actorOf(Props(new EnrichNeighborhood(enrichStreet)))

      enrichNeighborhood ! fixture.address
      geocodeProbe.expectMsg(fixture.address.copy(neighborhood = Some("my neighborhood"), street = Some("my street")))
    }

    "enrich neighborhood, street and number" in {
      val geocodeProbe = TestProbe()

      val enrichNumber = system.actorOf(Props(new EnrichNumber(geocodeProbe.ref)))
      val enrichStreet = system.actorOf(Props(new EnrichStreet(enrichNumber)))
      val enrichNeighborhood = system.actorOf(Props(new EnrichNeighborhood(enrichStreet)))

      enrichNeighborhood ! fixture.address
      geocodeProbe.expectMsg(fixture.address.copy(neighborhood = Some("my neighborhood"), street = Some("my street"), number = Some("123 A")))
    }

    "enrich neighborhood, street, number and zip" in {
      val geocodeProbe = TestProbe()

      val enrichZip = system.actorOf(Props(new EnrichZipcode(geocodeProbe.ref)))
      val enrichNumber = system.actorOf(Props(new EnrichNumber(enrichZip)))
      val enrichStreet = system.actorOf(Props(new EnrichStreet(enrichNumber)))
      val enrichNeighborhood = system.actorOf(Props(new EnrichNeighborhood(enrichStreet)))

      enrichNeighborhood ! fixture.address
      geocodeProbe.expectMsg(fixture.address.copy(neighborhood = Some("my neighborhood"), street = Some("my street"), number = Some("123 A"), zipCode = Some("01050-130")))
    }

    "enrich failed" in {
      val geocodeProbe = TestProbe()

      val enrichStreet = system.actorOf(Props(new EnrichStreet(geocodeProbe.ref)))
      val enrichNeighborhood = system.actorOf(Props(new EnrichNeighborhood(enrichStreet)))

      enrichNeighborhood ! (fixture.address, Some(new Exception))
      geocodeProbe.expectMsg(fixture.address.copy(street = Some("my street")))
    }
  }
}
