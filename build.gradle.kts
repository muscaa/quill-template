plugins {
    id("java-library")
    id("com.vanniktech.maven.publish") version "0.36.0"
}

group = "dev.musca"
version = System.getenv("GITHUB_REF_NAME")?.removePrefix("v") ?: "0.0.1-SNAPSHOT"

// set info as you wish or modify this file completly
extra["info"] = mapOf(
    "id" to project.name,
    "name" to "Quill Template",
    "authorId" to "muscaa",
    "authorName" to "musca",
    "description" to "Quill java package template",
)
val info: Map<String, String> by extra

val shade = configurations.create("shade")
configurations.api.get().extendsFrom(shade)

val bootstrap = configurations.create("bootstrap")
configurations.api.get().extendsFrom(bootstrap)

repositories {
    mavenCentral()
}

dependencies {
    bootstrap("dev.musca:quill-core:1.0.6") // use latest version
}

tasks.register("generatePackageJson") {
	val outputFile = layout.buildDirectory.file("quill/generated/package.json")
	outputs.file(outputFile)
	
	doLast {
		val file = outputFile.get().asFile
		file.parentFile.mkdirs()
		file.writeText(
			"""
			{
				"id": "${info["id"]}",
				"author": "${info["authorId"]}",
				"version": "${project.version}",
				"description": "${info["description"]}"
			}
			""".trimIndent()
		)
	}
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

    from("src/main/resources/bundle")
}

tasks.register<Zip>("bundle") {
    group = "quill"
    description = "Bundles the project into an installable quill java package."

    destinationDirectory.set(layout.buildDirectory.dir("quill/bundle"))
    archiveFileName.set("${project.name}-bundle.zip")

    val preBundleTask = tasks.named("preBundle")
    val generatePackageJsonTask = tasks.named("generatePackageJson")
    dependsOn(preBundleTask, generatePackageJsonTask)

    from(preBundleTask.map { it.outputs.files })
    from(generatePackageJsonTask)
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

    val developerId = info["authorId"]
    val developerName = info["authorName"]
    val projectId = project.name

    pom {
        name.set(info["name"])
        description.set(info["description"])
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
