import cl.franciscosolis.sonatypecentralupload.SonatypeCentralUploadTask

plugins {
    java
    `maven-publish`
    id("cl.franciscosolis.sonatype-central-upload") version "1.0.2"
}

group = "com.tksimeji"
version = "0.2.1"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/") {
        name = "papermc-repo"
    }
    maven("https://oss.sonatype.org/content/groups/public/") {
        name = "sonatype"
    }
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.3-R0.1-SNAPSHOT")
}

publishing {
    publications {
        register<MavenPublication>("maven") {
            pom {
                name.set("tksimeji")
                description.set("A Minecraft library that links objects in the world with Java objects")
                url.set("https://github.com/tksimeji/wobject")
                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://github.com/tksimeji/wobject/blob/master/LICENSE")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("tksimeji")
                        name.set("tksimeji")
                        email.set("tksimeji<at>outlook.com")
                    }
                }
                scm {
                    url.set("https://github.com/tksimeji/wobject")
                }
            }
        }
    }
}

val targetJavaVersion = 21
java {
    val javaVersion = JavaVersion.toVersion(targetJavaVersion)
    sourceCompatibility = javaVersion
    targetCompatibility = javaVersion
    if (JavaVersion.current() < javaVersion) {
        toolchain.languageVersion = JavaLanguageVersion.of(targetJavaVersion)
    }

    withSourcesJar()
    withJavadocJar()
}

tasks.named<SonatypeCentralUploadTask>("sonatypeCentralUpload") {
    dependsOn("jar", "sourcesJar", "javadocJar", "generatePomFileForMavenPublication")

    username = System.getenv("SONATYPE_CENTRAL_USERNAME")
    password = System.getenv("SONATYPE_CENTRAL_PASSWORD")

    archives = files(
        tasks.named("jar"),
        tasks.named("sourcesJar"),
        tasks.named("javadocJar")
    )

    pom = file(
        tasks.named("generatePomFileForMavenPublication").get().outputs.files.single()
    )

    signingKey.set(File("key.asc").readText())
    signingKeyPassphrase = System.getenv("PGP_SIGNING_KEY_PASSPHRASE")
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"

    if (targetJavaVersion >= 10 || JavaVersion.current().isJava10Compatible) {
        options.release.set(targetJavaVersion)
    }
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}