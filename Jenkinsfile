pipeline {
    agent any

    environment {
        // --- Docker Properties ---
        DOCKERHUB_CREDENTIALS = 'dockerhubcred'
        DOCKER_IMAGE = 'raghavendra76/cloudcompare-ai'
        DOCKER_TAG = "v${env.BUILD_ID}"
        
        // --- EC2 Deployment Properties ---
        EC2_USER = 'ubuntu'
        EC2_IP = '3.80.62.223' // Update this to your target EC2 IP if different
        SSH_CREDENTIALS_ID = 'ec2-pem-keyyyy'
    }

    tools {
        maven 'cseMaven' // Ensure Maven is configured as 'cseMaven' in Global Tool Configuration
        jdk 'javacse'    // Ensure JDK 17 or 21 is configured as 'javacse'
    }

    stages {
        stage('Clone Code') {
            steps {
                echo 'Cloning from Git repository...'
                checkout scm
            }
        }

        stage('Build Jar') {
            steps {
                echo 'Building Spring Boot application...'
                // Using wrapper mvnw.cmd for better compatibility
                bat 'mvnw.cmd clean package'
            }
        }

        stage('Sonar Analysis') {
            steps {
                // Using 'SonarCse' as requested
                withSonarQubeEnv('SonarCse') {
                    bat 'mvnw.cmd sonar:sonar -Dsonar.projectKey=cloud-compare-ai'
                }
            }
        }

        stage('Docker Build') {
            steps {
                retry(3) {
                    echo 'Building Docker image...'
                    bat "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
                    bat "docker tag ${DOCKER_IMAGE}:${DOCKER_TAG} ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Docker Login') {
            steps {
                echo 'Logging into DockerHub...'
                withCredentials([usernamePassword(credentialsId: env.DOCKERHUB_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                    // Windows CMD syntax for environment variables
                    bat 'echo %DOCKER_PASS%| docker login -u %DOCKER_USER% --password-stdin'
                }
            }
        }

        stage('Docker Push') {
            steps {
                echo 'Pushing image to DockerHub...'
                bat "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
                bat "docker push ${DOCKER_IMAGE}:latest"
            }
        }

        stage('Deploy Locally') {
            steps {
                catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
                    echo 'Deploying container to local Docker Desktop...'
                    // Stop and remove existing container if it exists
                    bat "docker rm -f cloud-compare-app || exit 0"
                    // Start the new container
                    bat "docker run -d -p 3000:5000 --name cloud-compare-app -e DB_HOST=host.docker.internal ${DOCKER_IMAGE}:latest"
                }
            }
        }

        stage('Deploy to EC2') {
            steps {
                echo 'Deploying application to EC2 instance...'
                withCredentials([sshUserPrivateKey(credentialsId: env.SSH_CREDENTIALS_ID, keyFileVariable: 'SSH_KEY')]) {
                    // Fix Windows SSH key permissions
                    bat 'for /f "delims=" %%a in (\'whoami\') do icacls "%SSH_KEY%" /inheritance:r /grant "%%a:F"'
                    bat 'icacls "%SSH_KEY%" /remove "BUILTIN\\Users" || exit 0'

                    // Copy docker-compose.yml to EC2 Server
                    bat "scp -o StrictHostKeyChecking=no -i \"%SSH_KEY%\" docker-compose.yml ${EC2_USER}@${EC2_IP}:~/docker-compose.yml"
                    
                    // Run deployment via SSH and auto-install docker-compose if missing
                    bat """
                        ssh -o StrictHostKeyChecking=no -i "%SSH_KEY%" ${EC2_USER}@${EC2_IP} "if [ ! -f /usr/local/bin/docker-compose ]; then sudo curl -sSL 'https://github.com/docker/compose/releases/download/v2.20.2/docker-compose-Linux-x86_64' -o /usr/local/bin/docker-compose && sudo chmod +x /usr/local/bin/docker-compose; fi && DOCKER_IMAGE=${DOCKER_IMAGE}:latest sudo -E /usr/local/bin/docker-compose down && DOCKER_IMAGE=${DOCKER_IMAGE}:latest sudo -E /usr/local/bin/docker-compose pull && DOCKER_IMAGE=${DOCKER_IMAGE}:latest sudo -E /usr/local/bin/docker-compose up -d"
                    """
                }
            }
        }
    }

    post {
        always {
            echo 'Cleaning up Jenkins workspace...'
            cleanWs()
        }
        success {
            echo 'Pipeline completed successfully! CloudCompare AI is live.'
        }
        failure {
            echo 'Pipeline failed! Please check the logs in Jenkins.'
        }
    }
}
