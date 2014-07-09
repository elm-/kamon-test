package net.joaoqalves.tracing

import akka.actor.{ActorLogging, Actor, Props, ActorSystem}
import kamon.trace.TraceRecorder
import kamon.metrics.ActorMetrics.ActorMetricRecorder
import kamon.metrics.{TraceMetrics, ActorMetrics, Metrics}
import kamon.Kamon
import net.joaoqalves.KamonListenerActor

trait KamonApp {
  implicit val system = ActorSystem("kamon-test")

  val listener = system.actorOf(Props[KamonListenerActor], "kamon-listener")

  Kamon(Metrics).subscribe(ActorMetrics, "*", listener, permanently = true)
  Kamon(Metrics).subscribe(TraceMetrics, "*", listener, permanently = true)

  system.registerOnTermination({
    System.exit(0)
  })

  def shutdown() {
    system.shutdown()
  }
}

