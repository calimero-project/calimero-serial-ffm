plugins {
    id("java-library")
    id("com.github.ben-manes.versions") version "0.51.0"
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
        languageVersion.set(JavaLanguageVersion.of(22))
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
    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}
