pipeline {
    agent any

    tools {
        git "Default"
        jdk "OpenJDK 21"
        // We're using Gradle Wrapper
    }

    stages {
        stage("Apply patches") {
            steps {
                withGradle {
                    sh "./gradlew applyAllPatches --info --stacktrace"
                }
            }
        }
        stage("Build sources") {
            steps {
                withGradle {
                    sh "./gradlew build --info --stacktrace"
                }
            }
        }
        stage("Create server jar") {
            steps {
                withGradle {
                    sh "./gradlew createMojmapPaperclipJar --info --stacktrace"
                    //sh "./gradlew createReobfPaperclipJar --info --stacktrace"
                }
            }
        }
        stage("Publish API to Maven") {
            steps {
                withGradle {
                    sh "./gradlew generateDevelopmentBundle --info --stacktrace"
                    sh "./gradlew publishAllPublicationsToCodemcRepository --info --stacktrace"
                }
            }
        }
    }
    post {
        always {
            junit testResults: 'plazma-server/build/test-results/test/TEST-*.xml', keepTestNames: true, keepProperties: true, stdioRetention: 'ALL'
            junit testResults: 'plazma-api/build/test-results/test/TEST-*.xml', keepTestNames: true, keepProperties: true, stdioRetention: 'ALL'

            // To easily distinguish test results
            publishHTML([reportDir: 'plazma-server/build/reports/tests/test', reportFiles: 'index.html', reportName: 'Server Test Summary', keepAll: true, allowMissing: false, alwaysLinkToLastBuild: false])
            publishHTML([reportDir: 'plazma-api/build/reports/tests/test', reportFiles: 'index.html', reportName: 'API Test Summary', keepAll: true, allowMissing: false, alwaysLinkToLastBuild: false])

            discordSend webhookURL: env.DISCORD_WEBHOOK_URL, title: env.JOB_NAME, link: env.BUILD_URL, result: currentBuild.currentResult, showChangeset: true, enableArtifactsList: true
        }
        success {
            javadoc javadocDir: 'plazma-api/build/docs/javadoc', keepAll: true
            archiveArtifacts artifacts: 'plazma-server/build/libs/plazma-paperclip-*-mojmap.jar, plazma-server/build/libs/plazma-bundler-*-mojmap.jar, plazma-server/build/libs/plazma-paperclip-*-reobf.jar, plazma-server/build/libs/plazma-bundler-*-reobf.jar,', followSymlinks: false, allowEmptyArchive: false, fingerprint: true, onlyIfSuccessful: true
        }
    }
}