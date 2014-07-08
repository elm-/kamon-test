package net.joaoqalves

import akka.actor.{ActorLogging, Actor}
import kamon.metrics.Subscriptions.TickMetricSnapshot
import kamon.metrics.{Scale, TraceMetrics}
import kamon.metrics.TraceMetrics.TraceMetricSnapshot

class KamonListenerActor extends Actor with ActorLogging {

  def receive = {
    case tickSnapshot: TickMetricSnapshot =>
      log.info(tickSnapshot.metrics.toString())
      tickSnapshot.metrics.get(TraceMetrics("tracing")) match {
        case Some(traceSnapshot: TraceMetricSnapshot) =>
          val metrics = traceSnapshot.segments.map { case (id, snapshot) =>
            SimpleMetric.toSimpleMetricMetric(Scale.Unit)(id.name, None, snapshot)

          }
          metrics.foreach(m => log.info("Metric: " + m.render()))
        case _ =>
          // ignore
      }
  }

}
