pipeline {
	// 指名可以在任何可用的Jenkins節點上執行此pipeline
	agent any
	
	// 環境變數
	environment {
		
		MAVEN_OPTS = '-Dmaven.repo.local=.m2/repository'
		
		APP_NAME = 'CICDDemo'
		
		ARTIFACT_ID = 'CICDDemo'
		
		VERSION = '0.0.1'
		
		JAR_FILE = "${APP_NAME}-${VERSION}.jar"
	}
	
	// 定義構建時候的各個階段
	stages {
		stage('Checkout') {
			
			steps {
				git 'https://github.com/Harry84911/CICDDemo.git'
			}
		}
		
		stage('Build') {
			
			steps {
				sh "mvn clean package"
			}
		}
		// 將產物歸檔 供後續查看
		stage('Archive') {
			
			steps {
				archiveArtifacts artifacts: "${ARTIFACT_ID}/${JAR_FILE}"
			}
		}
		
		stage('Unit Tests') {
			steps {
				echo 'Running tests'
				sh "mvn test"
			}
		}
	}
	
	post {
		
		success {
			echo 'Success!'
		}
		
		unstable {
			echo 'Unstable!'
		}
		
		failure {
			echo 'Fail!'
		}
		
		changed {
			echo 'Changed!'
		}
	}
}