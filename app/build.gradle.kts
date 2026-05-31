import java.util.Properties

plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.serialization)
	alias(libs.plugins.ksp)
}

val localProperties = Properties().apply {
	val f = rootProject.file("local.properties")
	if (f.exists()) f.inputStream().use { load(it) }
}

android {
	namespace = "com.example.checkpoint"
	compileSdk {
		version = release(36) {
			minorApiLevel = 1
		}
	}

	defaultConfig {
		applicationId = "com.example.checkpoint"
		minSdk = 36
		//noinspection OldTargetApi
		targetSdk = 36
		versionCode = 1
		versionName = "1.0"

		testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
	}

	buildTypes {
		release {
			isMinifyEnabled = true
			isShrinkResources = true
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures {
		compose = true
		buildConfig = true
	}
	defaultConfig {
		buildConfigField(
			"String", "IGDB_CLIENT_ID", "\"${localProperties["IGDB_CLIENT_ID"] ?: ""}\""
		)
		buildConfigField(
			"String", "IGDB_ACCESS_TOKEN", "\"${localProperties["IGDB_ACCESS_TOKEN"] ?: ""}\""
		)
	}

}

dependencies {
	implementation(platform(libs.androidx.compose.bom))
	implementation(libs.androidx.activity.compose)
	implementation(libs.androidx.compose.material3)
	implementation(libs.androidx.compose.ui)
	implementation(libs.androidx.compose.ui.graphics)
	implementation(libs.androidx.compose.ui.tooling.preview)
	implementation(libs.androidx.core.ktx)
	implementation(libs.androidx.lifecycle.runtime.ktx)
	// Split button
	// implementation("androidx.compose.material3:material3:1.5.0-alpha19")
	// Icons
	// implementation(libs.androidx.compose.material.icons.core)
	implementation(libs.androidx.compose.material.icons.extended)
	// Navigation
	implementation(libs.androidx.navigation.compose)
	//implementation(libs.ktor.serialization.kotlinx.json)
	// ViewModel
	implementation(libs.androidx.lifecycle.viewmodel.compose)
	implementation(libs.androidx.lifecycle.runtime.compose)
	// DataStore
	implementation(libs.androidx.datastore.preferences)
	// Dependency Injection
	implementation(libs.koin.androidx.compose)
	// AsyncImage
	implementation(libs.coil.compose)
	// Location
	// implementation("com.google.android.gms:play-services-location:21.3.0")
	// implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.10.0")
	// HTTP requests
	// Ktor
	implementation(libs.ktor.client.android)
	implementation(libs.ktor.client.content.neg)
	implementation(libs.ktor.serialization.json)
	implementation(libs.ktor.client.logging)
	// Room
	implementation(libs.androidx.room.ktx)
	implementation(libs.androidx.room.runtime)
	ksp(libs.androidx.room.compiler)

	// Koin
	implementation(libs.koin.android)
	implementation(libs.koin.androidx.compose)

	testImplementation(libs.junit)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.androidx.junit)
	debugImplementation(libs.androidx.compose.ui.test.manifest)
	debugImplementation(libs.androidx.compose.ui.tooling)
}