package net.joaoqalves

import akka.actor.{ActorLogging, Actor}
import kamon.metric.Subscriptions.TickMetricSnapshot
import kamon.metric.{Scale, TraceMetrics}
import kamon.metric.TraceMetrics.TraceMetricsSnapshot
import net.joaoqalves.SimpleMetric.Metric

class KamonListenerActor extends Actor with ActorLogging {
  var aggregates: Map[String, Metric] = Map.empty

  def receive = {
    case tickSnapshot: TickMetricSnapshot =>
      println(aggregates)
      log.info(tickSnapshot.metrics.toString())
      tickSnapshot.metrics.get(TraceMetrics("tracing")) match {
        case Some(traceSnapshot: TraceMetricsSnapshot) =>
          val metrics = traceSnapshot.segments.map { case (id, snapshot) =>
            SimpleMetric.toSimpleMetricMetric(Scale.Unit)(id.name, None, snapshot)
          }

          metrics.foreach { metric =>
            val newMetric = aggregates.get(metric.name) match {
              case Some(oldMetric) => metric.merge(oldMetric)
              case None => metric
            }
            aggregates = aggregates ++ Map(newMetric.name -> newMetric)
          }
          
          aggregates.foreach {
            case (k, v) => log.info("Metric: " + v.render())
          }
        case _ =>
          // ignore
      }
  }

}
