import com.typesafe.sbt.SbtAspectj.AspectjKeys._
import com.typesafe.sbt.SbtAspectj._
import sbt._
import Keys._

object Build extends Build {
  import BuildSettings._
  import Dependencies._

  lazy val kamonTest = Project(id = "kamon-test",
                              base = file("."))
    .settings(basicSettings: _*)
    .settings(sprayRunSettings: _*)
    .settings(aspectJSettings: _*)
    .settings(libraryDependencies ++=
      asCompile(logstashEncoder) ++
      asCompile(slf4j) ++
      asCompile(slf4j_log4j) ++
      asCompile(typesafeConfig) ++
      asCompile(nscalaTime) ++
      asCompile(casbah) ++
      asCompile(sprayRouting) ++
      asCompile(sprayCaching) ++
      asCompile(sprayClient) ++
      asCompile(sprayJson) ++
      asCompile(kamon) ++
      asCompile(akkaActor) ++
      asCompile(akkaSlf4j) ++
      asCompile(aspectJ) ++
      asCompile(aspectjWeaver) ++
      asTest(specs2) ++
      asTest(sprayTestKit) ++
      asTest(akkaTestKit) ++
      asTest(scalaMeter)
    )
}
