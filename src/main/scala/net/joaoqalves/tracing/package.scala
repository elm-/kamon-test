package net.joaoqalves

import akka.actor.ActorContext
import kamon.trace.TraceContext._
import kamon.trace.{TraceRecorder, TraceContext}

package object tracing {
  case class GenericSegment(name: String) extends SegmentIdentity {
    val tag = "segment"
  }

  def trace[T](name: String)(f: => T): T = {
    val segment = TraceRecorder.startSegment(GenericSegment(name), Map.empty)
    try {
      f
    } finally {
      segment.map(_.finish(Map.empty))
    }
  }
}
