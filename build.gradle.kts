import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat

//    permits to start the build setting the javac release parameter, no parameter means build for java8:
// gradle clean build -PjavacRelease=8
// gradle clean build -PjavacRelease=17
val javacRelease = (project.findProperty("javacRelease") ?: "11") as String

plugins {
  id("fr.brouillard.oss.gradle.jgitver") version "0.9.1"
  id("jacoco")
  id("java")
  id("maven-publish")
  // __KOTLIN_COMPOSE_VERSION__
  kotlin("jvm") version "1.4.32"
  // __LATEST_COMPOSE_RELEASE_VERSION__
  id("org.jetbrains.compose") version (System.getenv("COMPOSE_TEMPLATE_COMPOSE_VERSION") ?: "0.4.0-build180")
}

repositories {
  mavenLocal()
  mavenCentral()
  // TODO both repos temporary, jcenter is decommitted, compose will be released later on
  maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

dependencies {
  implementation(compose.desktop.currentOs)
  api("com.arkivanov.decompose:decompose:0.2.3")
  api("com.arkivanov.decompose:extensions-compose-jetbrains:0.2.3")
  implementation("com.lmax:disruptor:3.4.4")
  implementation("org.java-websocket:Java-WebSocket:1.5.2")
  implementation("org.jsoup:jsoup:1.14.3")
  implementation("org.json:json:20211205")
  implementation("com.j2html:j2html:1.5.0")
  implementation("commons-configuration:commons-configuration:1.10")
  implementation("commons-cli:commons-cli:1.5.0")
  implementation("commons-io:commons-io:2.11.0")
  implementation("org.apache.httpcomponents:httpclient:4.5.13")
  implementation("org.apache.httpcomponents:httpmime:4.5.13")
  implementation("org.apache.logging.log4j:log4j-api:2.17.1")
  implementation("org.apache.logging.log4j:log4j-core:2.17.1")
  implementation("org.graalvm.js:js:22.0.0.2")
  testImplementation(enforcedPlatform("org.junit:junit-bom:5.8.2"))
  testImplementation("org.junit.jupiter:junit-jupiter")
}

group = "com.rarchives.ripme"
version = "1.7.94"
description = "ripme"

compose.desktop {
  application {
    mainClass = "MainKt"

    nativeDistributions {
      targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
      packageName = "ripme"
      nativeDistributions.linux.debPackageVersion = "2.0.1"
      nativeDistributions.macOS.dmgPackageVersion = "2.0.1"
      nativeDistributions.windows.msiPackageVersion = "2.0.1"
    }
  }
}

jgitver {
  gitCommitIDLength = 8
  nonQualifierBranches = "main,master"
  useGitCommitID = true
}

tasks.compileJava {
  options.release.set(Integer.parseInt(javacRelease))
}

tasks.withType<Jar> {
  duplicatesStrategy = DuplicatesStrategy.INCLUDE
  manifest {
    attributes["Main-Class"] = "com.rarchives.ripme.App"
    attributes["Implementation-Version"] =  archiveVersion
    attributes["Multi-Release"] = "true"
  }
 
  // To add all of the dependencies otherwise a "NoClassDefFoundError" error
  from(sourceSets.main.get().output)

  dependsOn(configurations.runtimeClasspath)
  from({
    configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
  })
}

publishing {
  publications {
    create<MavenPublication>("maven") {
      from(components["java"])
    }
  }
}

tasks.withType<JavaCompile> {
  options.encoding = "UTF-8"
}

tasks.test {
  testLogging {
    showStackTraces = true
  }
  useJUnitPlatform {
    // gradle-6.5.1 not yet allows passing this as parameter, so exclude it
    excludeTags("flaky","slow")
    includeEngines("junit-jupiter")
    includeEngines("junit-vintage")
  }
  finalizedBy(tasks.jacocoTestReport) // report is always generated after tests run
}

tasks.register<Test>("testAll") {
  useJUnitPlatform {
    includeTags("any()", "none()")
  }
}

tasks.register<Test>("testFlaky") {
  useJUnitPlatform {
    includeTags("flaky")
  }
}

tasks.register<Test>("testSlow") {
  useJUnitPlatform {
    includeTags("slow")
  }
}

tasks.register<Test>("testTagged") {
  useJUnitPlatform {
    includeTags("any()")
  }
}

// make all archive tasks in the build reproducible
tasks.withType<AbstractArchiveTask>().configureEach {
  isPreserveFileTimestamps = false
  isReproducibleFileOrder = true
}

tasks.jacocoTestReport {
  dependsOn(tasks.test) // tests are required to run before generating the report
  reports {
    xml.required.set(false)
    csv.required.set(false)
    html.outputLocation.set(file("${buildDir}/jacocoHtml"))
  }
}

