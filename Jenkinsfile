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
                        sh "mvn clean compile"
                    } else {
                        bat "mvn clean compile"
                    }
                }
            }
        }

        stage('Static Code Analysis') {
            steps {
                script {
                    if(isUnix()) {
                        sh "mvn checkstyle:checkstyle"
                    } else {
                        bat "mvn checkstyle:checkstyle"
                    }
                }
            }
        }

        // Both Unit Tests and Mutation Tests can and should be ran in parallel
        // as there is no intrinsic benefit in doing them separately.
        stage('Unit + Mutation Testing w/ Report Recording') {
            parallel {
                stage('Unit Tests') {
                    stages {
                        stage('Run Unit Tests') {
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
                        stage('Record Unit Test Reports') {
                            steps {
                                script {
                                    def reportName = "Unit Test Coverage Report - Build #${env.BUILD_NUMBER}"
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
                                    junit 'target/surefire-reports/*.xml'
                                }
                            }
                        }
                    }
                }
                stage('Mutation Tests') {
                    stages {
                        stage('Run Mutation Tests') {
                            steps {
                                script {
                                    if(isUnix()) {
                                        sh "mvn test-compile org.pitest:pitest-maven:mutationCoverage"
                                    } else {
                                        bat "mvn test-compile org.pitest:pitest-maven:mutationCoverage"
                                    }
                                }
                            }
                        }
                        stage('Record Mutation Test Reports') {
                            steps {
                                script {
                                    def reportName = "Mutation Test Coverage Report - Build #${env.BUILD_NUMBER}"
                                    publishHTML(target: [
                                        allowMissing: false,
                                        alwaysLinkToLastBuild: false,
                                        keepAll: true,
                                        reportDir: 'target/pit-reports',
                                        reportFiles: 'index.html',
                                        reportName: reportName
                                    ])
                                }
                            }
                        }
                    }
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
                        tools: [checkStyle()]
                    )
                }
            }
        }

        stage('Package') {
            steps {
                script {
                    if(isUnix()) {
                        sh "mvn install -DskipTests"
                    } else {
                        bat "mvn install -DskipTests"
                    }
                }
            }
        }

        // For rollback purposes it is always valuable to keep the artifact of
        // the built binary.
        stage('Archive JAR Artifact') {
             steps {
                  archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: false
             }
        }

        stage('Deploy') {
            steps {
                script {
                    if(isUnix()) {
                        sh "JENKINS_NODE_COOKIE=dontKillMe java -jar ./target/psoft-g1-0.0.1-SNAPSHOT.jar --server.port=2228 > output.log 2>&1 &"
                    } else {
                        bat "set JENKINS_NODE_COOKIE=dontKillMe && start java -jar .\\target\\psoft-g1-0.0.1-SNAPSHOT.jar --server.port=2228 > output.log 2>&1"
                    }
                }
            }
        }
    }
}
