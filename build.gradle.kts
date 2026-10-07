import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    kotlin("jvm") version "2.4.0"
    id("org.jetbrains.intellij.platform") version "2.19.0"
}

group = "com.flashexpress"
version = "0.1.0"

repositories {
    mavenCentral()

    intellijPlatform {
        defaultRepositories()
    }
}

dependencies {
    intellijPlatform {
        local("/Applications/PhpStorm.app/Contents")
        bundledPlugin("com.jetbrains.php")
        bundledPlugin("org.jetbrains.plugins.terminal")
        pluginVerifier()
        testFramework(TestFrameworkType.Platform)
    }

    testImplementation(kotlin("test"))
    testRuntimeOnly("junit:junit:4.13.2")
}

kotlin {
    jvmToolchain(25)
}

intellijPlatform {
    pluginConfiguration {
        id = "com.flashexpress.oa-task-action-runner"
        name = "Task Action Runner"
        version = project.version.toString()

        ideaVersion {
            sinceBuild = "262.10315.130"
        }
    }
}

tasks {
    test {
        useJUnitPlatform()
    }
}
