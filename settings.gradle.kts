pluginManagement {
  repositories {
    google {
      content {
        includeGroupByRegex("com\\.android.*")
        includeGroupByRegex("com\\.google.*")
        includeGroupByRegex("androidx.*")
      }
    }
    mavenCentral()
    gradlePluginPortal()
  }
}

dependencyResolutionManagement {
  repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
  repositories {
    google()
    mavenCentral()

    val localPropertiesFile = file("local.properties")
    val localProperties = java.util.Properties()

    if (localPropertiesFile.exists()) {
      localProperties.load(localPropertiesFile.inputStream())

      val githubUsername = localProperties.getProperty("github.username") ?: ""
      val githubToken = localProperties.getProperty("github.token") ?: ""

      maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/bullitt-mobile/bullitt_android_sdk")
        credentials {
          username = githubUsername
          password = githubToken
        }
      }
    } else {
      logger.warn(
        "local.properties file not found. GitHub Packages repository will not be configured properly."
      )
    }

    mavenLocal()
  }
}

rootProject.name = "Bullitt Sample App"

include(":app")
