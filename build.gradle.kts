plugins {
    id("java-library")
    id("com.github.ben-manes.versions") version "0.53.0"
    id("maven-publish")
    signing
}

repositories {
    mavenCentral()
    mavenLocal()
    maven("https://central.sonatype.com/repository/maven-snapshots/")
}

group = "io.calimero"
version = "3.0-SNAPSHOT"

val junitJupiterVersion by rootProject.extra { "6.0.0" }

tasks.compileJava { options.encoding = "UTF-8" }
tasks.compileTestJava { options.encoding = "UTF-8" }
tasks.javadoc { options.encoding = "UTF-8" }

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<Jar> {
    from("${projectDir}/LICENSE") {
        into("META-INF")
    }
    if (name == "sourcesJar") {
        from("${projectDir}/README.md")
    }
}

dependencies {
    api("io.calimero:calimero-core:$version")
    implementation("io.calimero:serial-ffm:0.3-SNAPSHOT")
}

testing {
    suites {
        val test by getting(JvmTestSuite::class) {
            useJUnitJupiter("${rootProject.extra.get("junitJupiterVersion")}")
        }
    }
}

val enableNativeAccess = listOf(
    "--enable-native-access", "serial.ffm"
)

tasks.withType<JavaExec>().configureEach {
    jvmArgs(enableNativeAccess)
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            artifactId = rootProject.name
            from(components["java"])
            pom {
                name.set("Calimero serial communication service provider")
                description.set("Serial communication provider using the serial-ffm library")
                url.set("https://github.com/calimero-project/calimero-serial-ffm")
                inceptionYear.set("2024")
                licenses {
                    license {
                        name.set("GNU General Public License, version 2, with the Classpath Exception")
                        url.set("LICENSE")
                    }
                }
                developers {
                    developer {
                        name.set("Boris Malinowsky")
                        email.set("b.malinowsky@gmail.com")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/calimero-project/calimero-serial-ffm.git")
                    url.set("https://github.com/calimero-project/calimero-serial-ffm.git")
                }
            }
        }
    }
    repositories {
        maven {
            name = "maven"
            val releasesRepoUrl = uri("https://ossrh-staging-api.central.sonatype.com/service/local/staging/deploy/maven2/")
            val snapshotsRepoUrl = uri("https://central.sonatype.com/repository/maven-snapshots/")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    if (project.hasProperty("signing.keyId")) {
        sign(publishing.publications["mavenJava"])
    }
}
