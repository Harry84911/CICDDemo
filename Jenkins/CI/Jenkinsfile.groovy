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
		
		DISCORD_WEBHOOK = credentials('DC_WEBHOOK')
	}
	
	// 定義構建時候的各個階段
	stages {
//		stage('Checkout') {
//			
//			steps {
//				git 'https://github.com/Harry84911/CICDDemo.git'
//			}
//		}
		
		stage('Build') {
			
			steps {
				sh "mvn clean package"
			}
		}
		// 將產物歸檔 供後續查看
		// 原先是使用 ${ARTIFACT_ID}/${JAR_FILE} 去撈 jar檔案
		// 但因為mvn package 會將jar檔放在 target資料夾 >>故改動
		stage('Archive') {
			
			steps {
				archiveArtifacts artifacts: "target/${JAR_FILE}"
			}
		}
		
		stage('Unit Tests') {
			steps {
				echo 'Running tests'
				sh "mvn test"
			}
		}
		
		stage('Send To Discord') {
			steps {
				echo 'Send Endup to Discord'
				discordSend(
					title: "Jenkins Pipeline",
					description: """
					## Job Name : ${env.JOB_NAME}
					### Build: [${env.BUILD_NUMBER}](${env.BUILD_URL})
					footer: 'Jenkins pipeline Notification',
					result: currentBuild.currentResult,
					webhookURL: env.DC_WEBHOOK
					"""
				)
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