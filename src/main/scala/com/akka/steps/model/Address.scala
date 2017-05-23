package com.akka.steps.model

case class Address(state: Option[String], city: Option[String], neighborhood: Option[String] = None, street: Option[String] = None, number: Option[String] = None, zipCode: Option[String] = None) {
  def toMap: Map[String, Any] = {
    Map(
      "neighborhood" -> this.neighborhood,
      "street" -> this.street,
      "number" -> this.number,
      "zipCode" -> this.zipCode
    ).filter(_._2.isDefined).mapValues(_.get)
  }
}