plugins {
	id("org.gradle.toolchains.foojay-resolver-convention").version("1.0.0")
}

fun safeIncludeBuild(dir: String) {
	if (file(dir).exists()) includeBuild(dir)
}

safeIncludeBuild("../calimero-core")
safeIncludeBuild("../serial-ffm")
