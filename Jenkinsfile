pipeline {
    agent any

    tools {
        // TODO - Remove this and use the Maven binary path
        maven 'MVN'
    }

    stages {
        stage('Checkout') {
            steps {
                // Get some code from a GitHub repository
                git branch: 'main', url: 'https://github.com/1182147/ODSOFT_2425_1182147_1190409'
            }
        }

        stage('Build') {
            steps {
                script {
                    if(isUnix()) {
                        // Run Maven on a Unix agent.
                        sh "mvn clean:clean validate compiler:compile"
                    } else {
                        // To run Maven on a Windows agent, use
                        bat "mvn clean:clean validate compiler:compile"
                    }
                }
            }
        }

        stage('Test') {
            steps {
                script {
                    if(isUnix()) {
                        // Run Maven on a Unix agent.
                        sh "mvn test"
                    } else {
                        // To run Maven on a Windows agent, use
                        bat "mvn test"
                    }
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
