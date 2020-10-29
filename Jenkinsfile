pipeline {
  agent any
  environment {
    PATH = "/var/lib/jenkins/.ebcli-virtual-env/executables:/var/lib/jenkins/.pyenv/versions/3.7.2/bin:$PATH"
  }
  stages {
   stage('SCM Init') {
      steps {
       script{
           sh 'env | grep PATH'
        dir('/var/lib/jenkins/workspace/ShortURLService') {
            def exists = fileExists './ShortURLConvertor/'
            if(exists){
              dir('ShortURLConvertor') {
                sh 'git pull'
              }
            }else{
            sh 'git clone git@github.com:steven19861130/ShortURLConvertor.git'
            }
          }
        }
      }
    }

    stage('Build & EB Deployment') {
      steps {
          script{
              dir('/var/lib/jenkins/workspace/ShortURLService/ShortURLConvertor'){
                   sh './gradlew clean'
                   sh './gradlew test'
                   sh './gradlew assemble'
                   sh 'eb deploy ShortUrlConvertor-env'
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