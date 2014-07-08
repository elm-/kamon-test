package net.joaoqalves.tracing

import akka.actor._
import kamon.trace.{TraceContext, TraceRecorder}

object TracingApp extends App with KamonApp {
  val rootActor = system.actorOf(Props(new RootActor()), "init-actor")

  TraceRecorder.withNewTraceContext("tracing") {
    rootActor ! "Init"
  }

  Thread.sleep(5000)

  shutdown()
}



class RootActor extends Actor with ActorLogging  {
  val childActor = context.actorOf(Props(new ChildActor()), "child-actor")

  override def receive = {
    case "Init" =>
      log.info(TraceRecorder.currentContext.get.token)
      childActor ! "str"
  }
}


class ChildActor extends Actor with ActorLogging {
  override def receive = {
    case str: String =>
      log.info(TraceRecorder.currentContext.get.token)
      println(str)
      TraceRecorder.finish(Map("string" -> str))
  }
}
