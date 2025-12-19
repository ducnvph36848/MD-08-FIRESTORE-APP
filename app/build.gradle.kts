import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
}

// Đọc local.properties
val localProperties = Properties()
localProperties.load(FileInputStream(rootProject.file("local.properties")))
val openAiKey: String = localProperties.getProperty("OPENAI_API_KEY") ?: ""

android {
    namespace = "com.example.md_08_ungdungfivestore"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.md_08_ungdungfivestore"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // 🔥 Thêm BuildConfig field (OK)
        buildConfigField("String", "OPENAI_API_KEY", "\"$openAiKey\"")
    }

    // 🔥 BẮT BUỘC PHẢI CÓ, nếu không sẽ lỗi (bạn bị lỗi này)
    buildFeatures {
        buildConfig = true
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Picasso
    implementation("com.squareup.picasso:picasso:2.8")

    // OkHttp
    implementation("com.squareup.okhttp3:okhttp:4.9.3")

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation(libs.gson)
    implementation(libs.converter.gson)

    implementation("com.google.android.gms:play-services-auth:20.7.0")
    //bo góc
    implementation("com.google.android.material:material:1.12.0")
}
