plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.saksharudaan"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.saksharudaan"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildFeatures{
        viewBinding = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {


    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation(libs.play.services.auth)
    implementation(libs.chip.navigation.bar)


    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.22")


    //glide dependency for loading image
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //Exoplayer for video player
    implementation("com.google.android.exoplayer:exoplayer:2.14.1")
    implementation("com.google.android.exoplayer:exoplayer-dash:2.14.1")
    implementation("com.google.android.exoplayer:exoplayer-smoothstreaming:2.14.1")
    implementation("com.google.android.exoplayer:exoplayer-core:2.14.1")
    implementation("com.github.norulab:android-exoplayer-fullscreen:1.2.1")
    implementation("com.google.android.exoplayer:exoplayer-ui:2.14.1")


}