pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven("https://zebratech.jfrog.io/artifactory/EMDK-Android/")
    }
}
rootProject.name = "kotlin-emdk"
include(":app")
include(":kemdk")
