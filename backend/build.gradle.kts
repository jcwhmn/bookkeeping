plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "9.0.0"
}

group = "com.bookkeeping"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Handle duplicates
tasks.withType<Copy>().configureEach {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Main dependencies
dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core:11.14.1")
    implementation("org.flywaydb:flyway-database-postgresql:11.14.1")
    
    // API Documentation (springdoc OpenAPI with Scalar UI)
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-scalar:3.0.3")
    
    // Security
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    // Caching
    implementation("com.github.ben-manes.caffeine:caffeine")
    
    // Utilities
    implementation("org.apache.commons:commons-lang3:3.15.0")
    implementation("commons-codec:commons-codec:1.16.1")
    
    // Lombok + MapStruct (order matters: Lombok first for lombok-mapstruct-binding)
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    implementation("org.mapstruct:mapstruct:1.6.3")
    annotationProcessor("org.mapstruct:mapstruct-processor:1.6.3")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    
    // MapStructPlus (custom annotation processor - for entity to DTO mapping)
    implementation("com.jcwhmn:mapstruct-plus:1.0.0-SNAPSHOT")
    annotationProcessor("com.jcwhmn:mapstruct-plus:1.0.0-SNAPSHOT")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("com.h2database:h2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.named<Test>("test") {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        displayGranularity = 0
        showExceptions = true
        showCauses = true
    }
}

// Integration test source set
sourceSets {
    create("integrationTest") {
        java.srcDir("src/integrationTest/java")
        resources.srcDir("src/integrationTest/resources")
        compileClasspath += main.get().runtimeClasspath
        runtimeClasspath += main.get().runtimeClasspath
    }
}

// Integration test dependencies
dependencies {
    "integrationTestImplementation"(sourceSets.main.get().output)
    "integrationTestImplementation"(sourceSets.main.get().runtimeClasspath)
    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-test")
    "integrationTestImplementation"("org.springframework.boot:spring-boot-starter-web")
    "integrationTestImplementation"("org.springframework.boot:spring-boot-test")
    "integrationTestImplementation"("org.springframework.security:spring-security-test")
    // Apache HttpClient - required for PATCH method support in RestTemplate
    // JDK's HttpURLConnection doesn't support PATCH, throws ProtocolException
    "integrationTestImplementation"("org.apache.httpcomponents.client5:httpclient5")
    "integrationTestRuntimeOnly"("org.junit.platform:junit-platform-launcher")
}

// Integration test task
val integrationTest by tasks.registering(Test::class) {
    group = "verification"
    description = "Runs integration tests against real database"
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    classpath = sourceSets["integrationTest"].runtimeClasspath
    useJUnitPlatform()
    shouldRunAfter("test")
    maxParallelForks = 1
    systemProperty("junit.jupiter.execution.parallel.enabled", "false")
    testLogging {
        events("passed", "skipped", "failed")
        displayGranularity = 0
        showExceptions = true
        showCauses = true
    }
}