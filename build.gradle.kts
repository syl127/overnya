plugins {
    id("java")
    id("application")
    id("com.gradleup.shadow") version "9.3.0"
}

group = "net.sylv"
version = "1.0-SNAPSHOT"

val lwjglVersion = "3.3.6"
val lwjglNatives = "natives-linux"

repositories {
    mavenCentral()

    maven { url = uri("https://jitpack.io") }
}

dependencies {
    implementation(platform("org.lwjgl:lwjgl-bom:$lwjglVersion"))


    implementation("org.lwjgl", "lwjgl")
    implementation("org.lwjgl", "lwjgl-assimp")
    implementation("org.lwjgl", "lwjgl-glfw")
    implementation("org.lwjgl", "lwjgl-openal")
    implementation("org.lwjgl", "lwjgl-opengl")
    implementation("org.lwjgl", "lwjgl-stb")
    implementation ("org.lwjgl", "lwjgl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-assimp", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-openal", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-opengl", classifier = lwjglNatives)
    implementation ("org.lwjgl", "lwjgl-stb", classifier = lwjglNatives)

    //! WINDOWS + MAC
    implementation ("org.lwjgl", "lwjgl", classifier = "natives-windows")
    implementation ("org.lwjgl", "lwjgl-assimp", classifier = "natives-windows")
    implementation ("org.lwjgl", "lwjgl-glfw", classifier = "natives-windows")
    implementation ("org.lwjgl", "lwjgl-openal", classifier = "natives-windows")
    implementation ("org.lwjgl", "lwjgl-opengl", classifier = "natives-windows")
    implementation ("org.lwjgl", "lwjgl-stb", classifier = "natives-windows")

    implementation("org.joml:joml:1.10.8")

    implementation("com.github.LeverClient:ElverAPI:afd3290")
}

tasks.shadowJar {
    configurations = project.configurations.compileClasspath.map { listOf(it) }
}

tasks.jar {
    manifest {
        attributes.put("Main-Class", "net.sylv.Main")
    }
}

tasks.test {
    useJUnitPlatform()
}