plugins {
    alias(libs.plugins.androidApplication)
    id("checkstyle")
}



android {
    namespace = "ch.zli.aj.notebeam"
    compileSdk = 34

    defaultConfig {
        applicationId = "ch.zli.aj.notebeam"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    val checkstyle by tasks.registering(Checkstyle::class) {
        configFile = rootProject.file("app/config/checkstyle/checkstyle.xml")
        source("src/main/java")
        include("**/*.java")
        exclude("**/gen/**")
        classpath = files()
    }

    tasks.named("check") {
        dependsOn(checkstyle)
    }

    tasks.withType<Checkstyle>().configureEach {
        reports {
            xml.required = true
            html.required = true
        }
        classpath = files()
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
    buildFeatures {
        viewBinding = true
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
    implementation("com.journeyapps:zxing-android-embedded:4.1.0")
    implementation("com.google.zxing:core:3.5.3")
    implementation("com.google.zxing:zxing-parent:3.5.3")
    implementation("com.google.zxing:javase:3.5.3")
}