plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "dk.gls.kotlin_emdk"
    compileSdk = 33

    defaultConfig {
        applicationId = "dk.gls.kotlin_emdk"
        minSdk = 30
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("test") {
            keyAlias = "key0"
            keyPassword = "123456"
            storeFile = file("testapp.keystore")
            storePassword = "123456"
        }
    }

    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("test")
        }
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.2"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    /*
    This project will not build if the desired version does not exist in repository
    Therefore we can start building with direct reference to the project
    Once build and published, we can test the uploaded library by referring to the repository instead
    See https://jitpack.io/#gls-denmark/kotlin-emdk for available versions
     */

//    def emdk_version = "x.y.z"
//    implementation "com.github.gls-denmark:kotlin-emdk:$emdk_version"
    implementation(project(":kemdk"))

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.1")
    implementation("androidx.lifecycle:lifecycle-runtime-compose:2.6.1")

    implementation(platform("org.jetbrains.kotlin:kotlin-bom:1.8.0"))
    implementation("androidx.activity:activity-compose:1.7.2")
    implementation(platform("androidx.compose:compose-bom:2022.10.00"))
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
}