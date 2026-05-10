plugins {
	alias(libs.plugins.android.application)
	alias(libs.plugins.kotlin.compose)
	alias(libs.plugins.serialization)
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
			isMinifyEnabled = false
			proguardFiles(
				getDefaultProguardFile("proguard-android-optimize.txt"),
				"proguard-rules.pro"
			)
		}
	}
	compileOptions {
		sourceCompatibility = JavaVersion.VERSION_11
		targetCompatibility = JavaVersion.VERSION_11
	}
	buildFeatures {
		compose = true
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
	// Icons
	implementation(libs.androidx.compose.material.icons.core)
	// implementation("androidx.compose.material:material-icons-extended")
	// Navigation
	implementation(libs.androidx.navigation.compose)
	implementation(libs.ktor.serialization.kotlinx.json)
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
	implementation(libs.ktor.client.core)
	implementation(libs.ktor.client.okhttp)
	implementation(libs.ktor.client.content.negotiation)
	implementation(libs.ktor.serialization.kotlinx.json)
	testImplementation(libs.junit)
	androidTestImplementation(platform(libs.androidx.compose.bom))
	androidTestImplementation(libs.androidx.compose.ui.test.junit4)
	androidTestImplementation(libs.androidx.espresso.core)
	androidTestImplementation(libs.androidx.junit)
	debugImplementation(libs.androidx.compose.ui.test.manifest)
	debugImplementation(libs.androidx.compose.ui.tooling)
}