pipeline {
  agent any
  environment {
    PATH = "/var/lib/jenkins/.ebcli-virtual-env/executables:/var/lib/jenkins/.pyenv/versions/3.7.2/bin:$PATH"
  }
  stages {

    stage('Build') {
      steps {
          script{
              dir('/var/lib/jenkins/workspace/ShortURLServiceJob'){
                   sh './gradlew clean'
                   sh './gradlew test'
                   sh './gradlew assemble'
                   sh 'eb deploy ShortUrlConvertor-env'
                }
            }
        }
    }

    stage('EB Deployment') {
          steps {
              script{
                  dir('/var/lib/jenkins/workspace/ShortURLServiceJob'){
                       sh 'eb deploy Shortenurlservice-env'
                    }
                }
            }
        }


    stage('Post Deployment Test'){
        steps {
            script{
                def appStatus = sh(script:'eb status ShortUrlConvertor-env | grep Health', returnStdout:true)
                if(!appStatus.contains('Green')){
                    error '"Post Deployment Test fail!!!"'
                }
            }
        }
    }
  }
}