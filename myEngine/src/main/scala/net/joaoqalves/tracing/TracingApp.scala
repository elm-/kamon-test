package net.joaoqalves.tracing

import akka.actor._
import etm.core.configuration.EtmManager
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


case class GenericSegment(name: String) extends SegmentIdentity {
  val tag = "segment"
}


class RootActor extends Actor with ActorLogging {
  val childActor = context.actorOf(Props(new ChildActor()), "child-actor")

  override def receive = {
    case "Init" =>
      val segment = TraceRecorder.startSegment(GenericSegment("root"), Map.empty).get
      Thread.sleep(500)
      childActor ! "str"
      segment.finish(Map.empty)
  }
}


class ChildActor extends Actor with ActorLogging {

  case object Segment extends MetricIdentity {
    val name, tag = "segment2"
  }

  override def receive = {
    case str: String =>
      val segment = TraceRecorder.startSegment(GenericSegment("child"), Map.empty).get
      Thread.sleep(1000)
      segment.finish(Map.empty)
      TraceRecorder.finish()
  }
}


