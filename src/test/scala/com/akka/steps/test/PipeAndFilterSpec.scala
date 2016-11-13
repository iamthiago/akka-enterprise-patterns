package com.akka.steps.test

import akka.actor.{ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit, TestProbe}
import com.akka.steps.model.Address
import com.akka.steps.pipes.{SanitizeActor, ValidationActor}
import org.scalatest.{BeforeAndAfterAll, Matchers, WordSpecLike}
import scala.concurrent.duration._

/**
  * Created by thiago on 13/11/2016.
  */
class PipeAndFilterSpec extends TestKit(ActorSystem("MyTestSystem")) with ImplicitSender
  with WordSpecLike with Matchers with BeforeAndAfterAll {

  override def afterAll(): Unit = TestKit.shutdownActorSystem(system)

  def fixture = new {
    val address = Address(
      state = "SP",
      city = "Barueri",
      neighborhood = Some("Alphaville"),
      street = Some("Alameda Itapecuru"),
      number = Some("515"),
      zipCode = Some("06454-080")
    )
  }

  "An Address" must {

    "be successfully validated" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      sanitizeActor ! fixture.address
      geocodeProbe.expectMsg(fixture.address)
    }

    "be successfully validated with an empty number" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      val address = fixture.address.copy(number = None)

      sanitizeActor ! address
      geocodeProbe.expectMsg(address)
    }

    "be successfully validated with an empty neighborhood" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      val address = fixture.address.copy(neighborhood = None)

      sanitizeActor ! address
      geocodeProbe.expectMsg(address)
    }

    "be successfully validated with an empty street" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      val address = fixture.address.copy(street = None)

      sanitizeActor ! address
      geocodeProbe.expectMsg(address)
    }

    "not be validated because of an empty state" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      sanitizeActor ! fixture.address.copy(state = "")
      expectMsgType[akka.actor.Status.Failure]
      geocodeProbe.expectNoMsg(500.millis)
    }

    "not be validated because of an empty city" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      sanitizeActor ! fixture.address.copy(city = "")
      expectMsgType[akka.actor.Status.Failure]
      geocodeProbe.expectNoMsg(500.millis)
    }

    "not be validated because of an invalid number" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      sanitizeActor ! fixture.address.copy(number = Some("001"))
      expectMsgType[akka.actor.Status.Failure]
      geocodeProbe.expectNoMsg(500.millis)
    }

    "not be validate because neighborhood and street are both empty" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      sanitizeActor ! fixture.address.copy(neighborhood = None, street = None)
      expectMsgType[akka.actor.Status.Failure]
      geocodeProbe.expectNoMsg(500.millis)
    }

  }
}
