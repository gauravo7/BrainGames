plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
//    id("com.google.firebase.crashlytics")
}

android {
    namespace = "com.o7solutions.braingames"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.o7solutions.braingames"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        viewBinding = true
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
    implementation(libs.androidx.lifecycle.livedata.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

//    circular image view
    implementation("de.hdodenhof:circleimageview:3.1.0")

//    firestore
    implementation(platform("com.google.firebase:firebase-bom:33.16.0"))
//    implementation("com.google.firebase:firebase-crashlytics")
    implementation("com.google.firebase:firebase-analytics")
    implementation("com.google.firebase:firebase-firestore-ktx:24.4.5")
    implementation("com.google.firebase:firebase-auth:22.3.0")

//    Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

//    lottie
    implementation("com.airbnb.android:lottie:6.4.0")

    // Retrofit core
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // Converter (e.g., Gson for JSON)
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // (Optional) OkHttp logging interceptor for debugging
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")


}