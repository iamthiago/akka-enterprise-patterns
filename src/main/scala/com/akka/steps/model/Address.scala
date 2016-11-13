package com.akka.steps.model

/**
  * Created by thiago on 13/11/2016.
  */
case class Address(state: String, city: String, neighborhood: Option[String], street: Option[String], number: Option[String], zipCode: Option[String])