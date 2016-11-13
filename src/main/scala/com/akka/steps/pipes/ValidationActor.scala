package com.akka.steps.pipes

import akka.actor.{Actor, ActorLogging, ActorRef}
import com.akka.steps.model.Address
import com.akka.steps.validation.ValidationUtils
import com.wix.{accord => validation}

/**
  * Created by thiago on 13/11/2016.
  */
class ValidationActor(pipe: ActorRef) extends Actor with ActorLogging with ValidationUtils {

  import AddressValidation._

  def receive = {
    case address: Address =>
      val replyTo = sender()

      validateAddress(address) match {
        case validation.Success =>
          log.info("Address validated successfully")
          pipe forward address

        case validation.Failure(violations) =>
          log.error("Invalid Address: {}", formatErrorMessage(violations.head))
          replyTo ! akka.actor.Status.Failure(new Exception(formatErrorMessage(violations.head)))
      }

  }
}

object AddressValidation {

  import com.wix.accord._
  import com.wix.accord.dsl._

  implicit val addressValidation = validator[Address] { address =>
    address.state is notEmpty
    address.city is notEmpty
    (address.neighborhood is notEmpty) or (address.street is notEmpty)
  }

  def validateAddress(address: Address): Result = validate(address)
}