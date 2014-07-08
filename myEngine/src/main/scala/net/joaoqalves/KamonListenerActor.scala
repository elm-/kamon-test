package net.joaoqalves

import akka.actor.{ActorLogging, Actor}
import kamon.metrics.Subscriptions.TickMetricSnapshot
import kamon.metrics.TraceMetrics
import kamon.metrics.TraceMetrics.TraceMetricSnapshot

class KamonListenerActor extends Actor with ActorLogging {

  def receive = {
    case tickSnapshot: TickMetricSnapshot =>
      log.info(tickSnapshot.metrics.toString())
      tickSnapshot.metrics.get(TraceMetrics("tracing")) match {
        case Some(traceSnapshot: TraceMetricSnapshot) =>
          println(traceSnapshot.segments)
        case _ =>
          // ignore
      }
  }

}
