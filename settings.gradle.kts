fun safeIncludeBuild(dir: String) {
	if (file(dir).exists()) includeBuild(dir)
}

safeIncludeBuild("../calimero-core")
safeIncludeBuild("../serial-ffm")
