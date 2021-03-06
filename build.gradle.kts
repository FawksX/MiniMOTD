import com.github.jengelman.gradle.plugins.shadow.ShadowPlugin
import net.kyori.indra.IndraCheckstylePlugin
import net.kyori.indra.IndraLicenseHeaderPlugin
import net.kyori.indra.IndraPlugin
import net.kyori.indra.sonatypeSnapshots
import java.io.ByteArrayOutputStream

plugins {
  `java-library`
  id("net.kyori.indra") version "1.3.1"
  id("com.github.johnrengelman.shadow") version "6.1.0"
  id("net.kyori.blossom") version "1.1.0" apply false
}

allprojects {
  group = "xyz.jpenilla"
  version = "2.0.1+${lastCommitHash()}-SNAPSHOT"
  description = "Use MiniMessage text formatting in your servers MOTD."
}

ext["url"] = "https://github.com/jpenilla/MiniMOTD/"

subprojects {
  apply<JavaLibraryPlugin>()
  apply<ShadowPlugin>()
  apply<IndraPlugin>()
  apply<IndraCheckstylePlugin>()
  apply<IndraLicenseHeaderPlugin>()

  repositories {
    //mavenLocal()
    mavenCentral()
    sonatypeSnapshots()
    jcenter()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://nexus.velocitypowered.com/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.spongepowered.org/repository/maven-public/")
    maven("https://repo.jpenilla.xyz/snapshots/")
    maven("https://jitpack.io")
    maven("https://repo.codemc.org/repository/maven-public")
  }

  indra {
    javaVersions.target.set(8)
    github("jpenilla", "MiniMOTD") {
      issues = true
    }
    mitLicense()
  }

  tasks {
    shadowJar {
      minimize()
      if (!project.name.contains("fabric")) {
        archiveClassifier.set("")
        doLast {
          val output = outputs.files.singleFile
          output.copyTo(rootProject.buildDir.resolve("libs").resolve(output.name), overwrite = true)
        }
      }
    }
    withType<JavaCompile> {
      options.compilerArgs.add("-Xlint:-processing")
    }
    withType<Jar> {
      onlyIf { archiveClassifier.get() != "javadoc" }
    }
    withType<Javadoc> {
      onlyIf { false }
    }
  }
}

tasks.withType<Jar> {
  onlyIf { false }
}

fun lastCommitHash(): String = ByteArrayOutputStream().apply {
  exec {
    commandLine = listOf("git", "rev-parse", "--short", "HEAD")
    standardOutput = this@apply
  }
}.toString(Charsets.UTF_8.name()).trim()
