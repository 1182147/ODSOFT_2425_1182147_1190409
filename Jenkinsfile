pipeline {
    agent any

    tools {
        maven 'MVN'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                script {
                    if(isUnix()) {
                        sh "mvn clean:clean validate compiler:compile"
                    } else {
                        bat "mvn clean:clean validate compiler:compile"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if(isUnix()) {
                        sh "mvn test"
                    } else {
                        bat "mvn test"
                    }
                }
            }
        }

        stage('Record Coverage Report') {
            steps {
                script {
                    def reportName = "Coverage Report - Build #${env.BUILD_NUMBER}"
                    // This gives visibility within Jenkins as to the state of the Coverage Reports
                    // and allows analyzing Coverage Trends
                    jacoco(
                        execPattern: 'target/*.exec',
                        classPattern: 'target/classes',
                        sourcePattern: 'src/main/java',
                        exclusionPattern: 'src/test*'
                    )
                    publishHTML(target: [
                        allowMissing: false,
                        alwaysLinkToLastBuild: false,
                        keepAll: true,
                        reportDir: 'target/site/jacoco',
                        reportFiles: 'index.html',
                        reportName: reportName
                    ])
                }
            }
        }

        stage('Record Test Report') {
            steps {
                script {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        stage('Record Static Analysis Report') {
            steps {
                script {
                    recordIssues(
                        enabledForFailure: false,
                        aggregatingResults: false,
                        // This array will contain the tools we utilise for Static Analysis (e.g., Java Compiler, Checkstyle, ...)
                        // For more information regarding the tools natively available to the plugin: https://www.jenkins.io/doc/pipeline/steps/warnings-ng/
                        tools: [java()]
                    )
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    if(isUnix()) {
                        sh "mvn jar:jar"
                    } else {
                        bat "mvn jar:jar"
                    }
                }
            }
        }
    }
}
