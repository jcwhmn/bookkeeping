import org.gradle.api.tasks.SourceSet

plugins {
    java
    id("org.springframework.boot") version "4.0.6"
    id("io.spring.dependency-management") version "1.1.7"
    id("io.freefair.lombok") version "9.0.0"
}

group = "com.bookkeeping"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_25
    targetCompatibility = JavaVersion.VERSION_25
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

// Create integration test configurations FIRST
val intTestImpl = configurations.create("intTestImpl") {
    extendsFrom(configurations.implementation.get())
}

val intTestRuntime = configurations.create("intTestRuntime") {
    extendsFrom(configurations.runtimeOnly.get())
}

// Define integrationTest source set
sourceSets {
    val main = getByName("main")
    create("integrationTest") {
        compileClasspath = main.output + intTestImpl
        runtimeClasspath = main.output + intTestRuntime
    }
}

// Main dependencies
dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-security")
    
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-database-postgresql")
    
    // OpenAPI/Swagger
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.8")
    
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    implementation("com.github.ben-manes.caffeine:caffeine")
    
    implementation("org.apache.commons:commons-lang3:3.15.0")
    implementation("commons-codec:commons-codec:1.16.1")
    
    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")
    
    // Unit test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core:3.25.3")
    testImplementation("org.junit.jupiter:junit-jupiter-api")
    testImplementation("org.junit.jupiter:junit-jupiter-params")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")
    
    // Integration test dependencies
    intTestImpl("org.springframework.boot:spring-boot-starter-test")
    intTestImpl("org.springframework.boot:spring-boot-starter-web")
    intTestImpl("org.springframework.boot:spring-boot-test")
    intTestImpl("org.springframework.security:spring-security-test")
    
    intTestRuntime("com.h2database:h2")
    
    intTestRuntime("org.junit.platform:junit-platform-launcher")
}

// Configure unit test task
tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        displayGranularity = 0
        showExceptions = true
        showCauses = true
    }
}

// Integration test task
tasks.register<Test>("integrationTest") {
    description = "Runs integration tests"
    group = "verification"
    
    useJUnitPlatform()
    shouldRunAfter("test")
    
    // Use integrationTest source set output
    testClassesDirs = sourceSets["integrationTest"].output.classesDirs
    
    // Build classpath from source sets and configurations
    classpath = sourceSets["integrationTest"].output + 
                sourceSets["main"].output + 
                intTestImpl + 
                intTestRuntime
    
    // Use integrationtest profile
    systemProperty("spring.profiles.active", "integrationtest")
    
    // Enable test logging
    testLogging {
        events("passed", "skipped", "failed")
        displayGranularity = 0
        showExceptions = true
        showCauses = true
    }
}

// Combined test summary task
tasks.register("allTests") {
    group = "verification"
    description = "Run all tests (unit + integration)"
    dependsOn(tasks.test, tasks.named("integrationTest"))
}

// Generate combined HTML test report
tasks.register("allTestsReport") {
    group = "verification"
    description = "Generate combined HTML test report"
    dependsOn(tasks.test, tasks.named("integrationTest"))
    
    doLast {
        val reportDir = file("build/reports/all-tests")
        if (!reportDir.exists()) reportDir.mkdirs()
        
        // Copy unit test HTML if exists
        val unitSrc = file("build/reports/tests/test")
        if (unitSrc.exists() && unitSrc.isDirectory) {
            unitSrc.copyRecursively(file("${reportDir}/unit-tests"), true)
        }
        
        // Copy integration test HTML if exists
        val intSrc = file("build/reports/tests/integrationTest")
        if (intSrc.exists() && intSrc.isDirectory) {
            intSrc.copyRecursively(file("${reportDir}/integration-tests"), true)
        }
        
        // Count tests
        fun countTests(dir: String): Int {
            val f = file(dir)
            if (!f.exists()) return 0
            return f.listFiles()
                ?.filter { it.name.startsWith("TEST-") && it.name.endsWith(".xml") }
                ?.sumOf { xmlFile ->
                    Regex("""tests="(\d+)"""").find(xmlFile.readText())?.groupValues?.get(1)?.toInt() ?: 0
                } ?: 0
        }
        
        val unitCount = countTests("build/test-results/test")
        val intCount = countTests("build/test-results/integrationTest")
        val total = unitCount + intCount
        
        // Create index.html
        val html = """
<!DOCTYPE html>
<html>
<head>
    <title>Test Report - All Tests</title>
    <style>
        body { font-family: Arial, sans-serif; margin: 40px; }
        .summary { background: #f5f5f5; padding: 20px; border-radius: 8px; margin-bottom: 20px; }
        .pass { color: green; font-size: 18px; }
        h1 { color: #333; }
        .link { margin: 10px 0; font-size: 16px; }
        a { color: #0066cc; text-decoration: none; }
        a:hover { text-decoration: underline; }
    </style>
</head>
<body>
    <h1>All Tests Report</h1>
    <div class="summary">
        <h2>Summary</h2>
        <p>Total Tests: <strong>${total}</strong></p>
        <p>Unit Tests: ${unitCount}</p>
        <p>Integration Tests: ${intCount}</p>
        <p class="pass">All tests passed!</p>
    </div>
    <div class="links">
        <h3>Detailed Reports</h3>
        <p class="link"><a href="unit-tests/index.html">Unit Tests Report</a></p>
        <p class="link"><a href="integration-tests/index.html">Integration Tests Report</a></p>
    </div>
</body>
</html>
        """.trimIndent()
        
        file("${reportDir}/index.html").writeText(html)
        
        println()
        println("===========================================")
        println("  TEST REPORT GENERATED")
        println("===========================================")
        println("  Location: ${reportDir.absolutePath}/index.html")
        println("  Unit Tests: ${unitCount}")
        println("  Integration Tests: ${intCount}")
        println("  Total: ${total}")
        println("===========================================")
        println()
    }
}