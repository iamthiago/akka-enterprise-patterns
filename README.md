# What is it?

In this project I tried to implement a common enterprise integration pattern called Pipe and Filter using akka.

# How does it work?

Suppose you have several steps before geocode an address within Google Maps Api.

`
address entrypoint -> sanitize -> validation -> geocoding
`

Each step in this case, the Sanitize and the Validation, should do some quick validation and forward the message to the next step.
In this case after we have received the address request to out entrypoint(whatever you want), we forward the message to the
Sanitize step which validates the street number, then it forwards the message to the next step, now the Validation step that checks
some required fields and then it should forward the message to the final step, the geocoding(which is not implemented, but simulated in the test spec).

# Show me some code

Ok, let's take a look at some test:

```
"An Address" must {

    "be successfully validated" in {
      val geocodeProbe = TestProbe() //1

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref))) //2
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor))) //3

      sanitizeActor ! fixture.address //4
      geocodeProbe.expectMsg(fixture.address) //5
    }
}
```
Let's describe step by step:

- 1: Here we declare our final step, the geocoding, which should receive a valid address
- 2: Create the validation actor passing as the next step the `geocodeProbe.ref`
- 3: Create the sanitize actor passing as the next step the already created `validation actor`
- 4: Start the step process sending an address message
- 5: The `geocoding actor` should expect the valid address sent

### One more

```
"An Address" must {

    "not be validated because of an empty state" in {
      val geocodeProbe = TestProbe()

      val validationActor = system.actorOf(Props(new ValidationActor(geocodeProbe.ref)))
      val sanitizeActor = system.actorOf(Props(new SanitizeActor(validationActor)))

      sanitizeActor ! fixture.address.copy(state = "") //1
      expectMsgType[akka.actor.Status.Failure] //2
      geocodeProbe.expectNoMsg(500.millis) //3
    }
}
```

- 1: We send an invalid address to our first step
- 2: The `sender` expects a `Failure` message
- 3: The `geocoding actor` expects nothing, since we sent an invalid address
