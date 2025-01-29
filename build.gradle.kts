plugins {
    id("java-library")
    id("com.github.ben-manes.versions") version "0.52.0"
    id("maven-publish")
    signing
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots")
}

group = "io.calimero"
version = "3.0-SNAPSHOT"

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
    implementation("io.calimero:serial-ffm:0.1-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.11.4"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
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
            val releasesRepoUrl = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2")
            val snapshotsRepoUrl = uri("https://s01.oss.sonatype.org/content/repositories/snapshots")
            url = uri(if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl)
            credentials(PasswordCredentials::class)
        }
    }
}

signing {
    if (project.hasProperty("signing.keyId")) {
        sign(publishing.publications["mavenJava"])
    }
}
