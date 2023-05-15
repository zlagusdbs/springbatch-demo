plugins {
    java
    id("org.springframework.boot") version "2.5.14"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    // springframework
    implementation("org.springframework.boot:spring-boot-starter-batch")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")

    // opensource
    runtimeOnly("com.h2database:h2")

    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // development
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.batch:spring-batch-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
