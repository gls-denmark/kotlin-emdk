plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

val libraryGroupId = "dk.gls"
val libraryArtifactId = "kotlin-emdk"
val libraryVersion = "1.0.2"

android {
    namespace = "dk.gls.kemdk"
    compileSdk = 33

    defaultConfig {
        minSdk = 30

        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

kotlin {
    jvmToolchain(17)
}

dependencies {

    //Scanner
    val emdk_version = "9.1.1"
    compileOnly("com.symbol:emdk:$emdk_version")

    implementation("androidx.appcompat:appcompat:1.6.1")
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/gls-denmark/kotlin-emdk")
            credentials {
                username = getEnvironmentVariable("GIT_USERNAME")
                password = getEnvironmentVariable("GIT_AUTH_TOKEN")
            }
        }
    }
    publications {
        register<MavenPublication>("release") {
            groupId = libraryGroupId
            artifactId = libraryArtifactId
            version = libraryVersion

            afterEvaluate {
                from(components["release"])
            }
        }
    }
}

fun getEnvironmentVariable(variableName: String): String {
    return try {
        //Try to get the variable from gradle.properties
        extra[variableName].toString()
    } catch (e: ExtraPropertiesExtension.UnknownPropertyException) {
        //Try to get the variable from the environment
        System.getenv(variableName)
            .let {
                if (it?.isNotEmpty() == true) {
                    "$it"
                } else {
                    "\"\""
                }
            }
    }
}