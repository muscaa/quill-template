plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

// set about info as you wish or modify this file completly
extra["about"] = mapOf(
    "id" to System.getenv("QUILL_ID") ?: throw GradleException("QUILL_ID environment variable is required"),
    "name" to System.getenv("QUILL_NAME") ?: throw GradleException("QUILL_NAME environment variable is required"),
    "authorId" to System.getenv("QUILL_AUTHOR_ID") ?: throw GradleException("QUILL_AUTHOR_ID environment variable is required"),
    "authorName" to System.getenv("QUILL_AUTHOR_NAME") ?: throw GradleException("QUILL_AUTHOR_NAME environment variable is required"),
    "version" to System.getenv("QUILL_VERSION") ?: throw GradleException("QUILL_VERSION environment variable is required"),
    "description" to System.getenv("QUILL_DESCRIPTION") ?: throw GradleException("QUILL_DESCRIPTION environment variable is required"),
)
val about: Map<String, String> by extra

group = "dev.musca"
version = about.getValue("version")

val shade = configurations.create("shade")
configurations.api.get().extendsFrom(shade)

val bootstrap = configurations.create("bootstrap")
configurations.api.get().extendsFrom(bootstrap)

repositories {
    mavenCentral()
}

dependencies {
    bootstrap("dev.musca:quill-core:1.0.9") // use latest version
}

tasks.register<Sync>("preBundle") {
    dependsOn("jar")
    into(layout.buildDirectory.dir("quill/pre-bundle"))

    into("java") {
        from(tasks.jar)
        into("libs") {
            from(configurations.runtimeClasspath.get()
                .minus(shade)
                .minus(bootstrap))
        }
    }
}

tasks.processResources {
    exclude("bundle/**")
}

tasks.withType<Jar> {
    from(layout.projectDirectory) {
        include("LICENSE", "NOTICE")
        into("META-INF")
    }
}

afterEvaluate {
    tasks.jar {
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE

        from(shade.map { if (it.isDirectory) it else zipTree(it) })
    }
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

mavenPublishing {
    publishToMavenCentral(/*automaticRelease = true*/)
    signAllPublications()
    coordinates(project.group.toString(), project.name, project.version.toString())

    val developerId = about["authorId"]
    val developerName = about["authorName"]
    val projectId = project.name

    pom {
        name.set(about["name"])
        description.set(about["description"])
        inceptionYear.set("2026")
        url.set("https://github.com/${developerId}/${projectId}/")
        licenses {
            license {
                name.set("Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set(developerId)
                name.set(developerName)
                url.set("https://github.com/${developerId}/")
            }
        }
        scm {
            url.set("https://github.com/${developerId}/${projectId}/")
            connection.set("scm:git:git://github.com/${developerId}/${projectId}.git")
            developerConnection.set("scm:git:ssh://git@github.com/${developerId}/${projectId}.git")
        }
    }
}
