package net.joaoqalves.tracing

import akka.actor._
import kamon.metrics.MetricSnapshot
import kamon.metrics.Scale
import kamon.metrics.instruments._
import kamon.metrics.MetricIdentity
import kamon.trace.TraceContext._
import kamon.trace.{TraceContext, TraceRecorder}

object TracingApp extends App with KamonApp {

  val rootActor = system.actorOf(Props(new RootActor()), "init-actor")

  TraceRecorder.withNewTraceContext("tracing") {
    rootActor ! "Init"
    rootActor ! "Init"
  }

  Thread.sleep(5000)

  shutdown()
}




class RootActor extends Actor with ActorLogging {
  val childActor1 = context.actorOf(Props(new ChildActor()), "child-actor1")
  val childActor2 = context.actorOf(Props(new ChildActor()), "child-actor2")

  override def receive = {
    case "Init" =>
      trace("root") {
        Thread.sleep(100)
        childActor1 ! "str1"
        childActor2 ! "str2"
      }
  }
}


class ChildActor extends Actor with ActorLogging {

  case object Segment extends MetricIdentity {
    val name, tag = "segment2"
  }

  override def receive = {
    case str: String =>
      trace(str) {
        Thread.sleep(200)
      }

      TraceRecorder.finish()
  }
}


