package net.joaoqalves

import kamon.metrics.{MetricSnapshotLike, MetricSnapshot, Scale}

object SimpleMetric {

  def toSimpleMetricMetric(scale: Scale)(name: String, scope: Option[String], snapshot: MetricSnapshotLike): SimpleMetric.Metric = {
    var total: Double = 0D
    var sumOfSquares: Double = 0D

    val measurementLevels = snapshot.measurements.iterator
    while (measurementLevels.hasNext) {
      val level = measurementLevels.next()
      val scaledValue = Scale.convert(snapshot.scale, scale, level.value)

      total += scaledValue * level.count
      sumOfSquares += (scaledValue * scaledValue) * level.count
    }

    val scaledMin = Scale.convert(snapshot.scale, scale, snapshot.min)
    val scaledMax = Scale.convert(snapshot.scale, scale, snapshot.max)

    Metric(name, scope, snapshot.numberOfMeasurements, total, total, scaledMin, scaledMax, sumOfSquares)
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
