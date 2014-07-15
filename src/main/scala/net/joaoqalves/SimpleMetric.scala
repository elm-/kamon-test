package net.joaoqalves

import kamon.metric.instrument.{Counter, Histogram}
import kamon.metric.{MetricSnapshot, Scale}

object SimpleMetric {

  def toSimpleMetricMetric(scale: Scale)(name: String, scope: Option[String], snapshot: MetricSnapshot): SimpleMetric.Metric = {
    snapshot match {
      case hs: Histogram.Snapshot ⇒
        var total: Double = 0D
        var sumOfSquares: Double = 0D
        val scaledMin = Scale.convert(hs.scale, scale, hs.min)
        val scaledMax = Scale.convert(hs.scale, scale, hs.max)

        hs.recordsIterator.foreach { record ⇒
          val scaledValue = Scale.convert(hs.scale, scale, record.level)

          total += scaledValue * record.count
          sumOfSquares += (scaledValue * scaledValue) * record.count
        }

        Metric(name, scope, hs.numberOfMeasurements, total, total, scaledMin, scaledMax, sumOfSquares)

      case cs: Counter.Snapshot ⇒
        Metric(name, scope, cs.count, cs.count, cs.count, 0, cs.count, cs.count * cs.count)
    }
  }

  case class Metric(name: String, scope: Option[String], callCount: Long, total: Double, totalExclusive: Double,
                    min: Double, max: Double, sumOfSquares: Double) {

    def merge(that: Metric): Metric = {
      Metric(name, scope,
        callCount + that.callCount,
        total + that.total,
        totalExclusive + that.totalExclusive,
        math.min(min, that.min),
        math.max(max, that.max),
        sumOfSquares + that.sumOfSquares)
    }


    val averageTime = total / callCount

    def render(): String = {
      val sb = new StringBuilder()
      sb.append(name)
      sb.append("\t")
      sb.append(callCount)
      sb.append("\t")
      sb.append(averageTime)
      sb.toString
    }
  }

}
