pipeline {
    agent {
        label 'jenkins-agent'
    }

    environment {
        // AWS Configuration
        AWS_ACCOUNT_ID = '319998871902'
        AWS_REGION = 'us-east-1'
        EKS_CLUSTER_NAME = 'comic-website-prod'
        DOCKER_REGISTRY = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
        
        // Project Configuration
        PROJECT_NAME = 'ecommerce'
        NAMESPACE = 'ecommerce'
        
        // Docker Images
        FRONTEND_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}-frontend"
        USER_SERVICE_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}-user-service"
        PRODUCT_SERVICE_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}-product-service"
        ORDER_SERVICE_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}-order-service"
        PAYMENT_SERVICE_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}-payment-service"
        INVENTORY_SERVICE_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}-inventory-service"
        NOTIFICATION_SERVICE_IMAGE = "${DOCKER_REGISTRY}/${PROJECT_NAME}-notification-service"
        
        // Versioning - 现在从versions.yaml读取
        BUILD_VERSION = "${env.BUILD_NUMBER}"
        GIT_COMMIT_SHORT = "${env.GIT_COMMIT?.substring(0, 7) ?: 'unknown'}"
        
        // Version Control Configuration
        VERSION_FILE = 'versions.yaml'
        VERSION_CONFIGMAP_FILE = 'version-configmap.yaml'

        // Terraform Configuration
        TF_API_GATEWAY_DIR = 'terraform/aws-api-gateway'

        // Updated Environment Paths - 根据实际路径修改
        JAVA_HOME = '/opt/java/openjdk'
        GRADLE_HOME = '/opt/gradle/gradle-8.5'
        // 修改 Node.js 和 npm 路径为实际路径
        NODE_HOME = '/usr/local'
        NODE_BIN = '/usr/local/bin'
        // 修改 Gradle 路径为实际路径
        GRADLE_BIN = '/opt/gradle/gradle-8.5/bin'
    }

    options {
        buildDiscarder(logRotator(numToKeepStr: '10'))
        timeout(time: 60, unit: 'MINUTES')
        disableConcurrentBuilds()
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Setup Environment') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh '''
                            # 设置正确的环境变量 - 使用更新后的路径
                            export JAVA_HOME=''' + JAVA_HOME + '''
                            export GRADLE_HOME=''' + GRADLE_HOME + '''
                            export NODE_HOME=''' + NODE_HOME + '''
                            export PATH=$JAVA_HOME/bin:''' + GRADLE_BIN + ''':''' + NODE_BIN + ''':$PATH
        
                            echo "=== Environment Setup ==="
                            echo "Build Version: ''' + BUILD_VERSION + '''"
                            echo "Git Commit: ''' + env.GIT_COMMIT + '''"
                            echo "Java Home: $JAVA_HOME"
                            echo "Gradle Home: $GRADLE_HOME"
                            echo "Node Home: $NODE_HOME"
                            
                            echo "Java Version:"
                            java -version
                            echo "Gradle Version:"
                            gradle --version
                            echo "Node Version:"
                            node --version
                            echo "NPM Version:"
                            npm --version
                            
                            # Configure AWS and EKS
                            aws configure set region ''' + AWS_REGION + '''
                            aws configure set output json
                            aws eks update-kubeconfig --region ''' + AWS_REGION + ''' --name ''' + EKS_CLUSTER_NAME + '''
                            
                            echo "Kubernetes cluster info:"
                            kubectl cluster-info
                            
                            echo "Available disk space:"
                            df -h
                            
                            echo "=== Environment setup completed ==="
                        '''
                    }
                }
            }
        }

        stage('Clean All Caches') {
            steps {
                sh '''
                    echo "🧹 Performing thorough cache cleanup..."
                    
                    # 检查当前磁盘空间
                    echo "=== Disk space before cleanup ==="
                    df -h
                    
                    # 清理 Docker
                    echo "Cleaning Docker..."
                    docker system prune -a -f 2>/dev/null || true
                    
                    # 清理前端缓存
                    echo "Cleaning frontend caches..."
                    rm -rf frontend/node_modules frontend/dist frontend/build frontend/.cache frontend/.npm 2>/dev/null || true
                    
                    # 清理 npm 全局缓存
                    echo "Cleaning npm cache..."
                    npm cache clean --force 2>/dev/null || true
                    
                    # 清理后端构建缓存
                    echo "Cleaning backend build caches..."
                    find microservices -name "build" -type d -exec rm -rf {} + 2>/dev/null || true
                    find microservices -name ".gradle" -type d -exec rm -rf {} + 2>/dev/null || true
                    
                    # 清理系统临时文件
                    echo "Cleaning system temp files..."
                    rm -rf /tmp/* /var/tmp/* 2>/dev/null || true
                    
                    # 清理 Jenkins 工作空间缓存
                    echo "Cleaning workspace temp files..."
                    find /home/jenkins/agent/workspace -name "*.tmp" -delete 2>/dev/null || true
                    find /home/jenkins/agent/workspace -name "*.log" -delete 2>/dev/null || true
                    
                    echo "✅ All caches cleaned"
                    
                    # 显示清理后的磁盘空间
                    echo "=== Disk space after cleanup ==="
                    df -h
                '''
            }
        }

        stage('Version Control') {
            steps {
                script {
                    try {
                        loadVersionConfig()
                        generateVersionConfigMap()
                        
                        // 设置构建显示名称和描述
                        currentBuild.displayName = "#${BUILD_VERSION}-${APPLICATION_VERSION}"
                        currentBuild.description = "Version: ${APPLICATION_VERSION} | Commit: ${env.GIT_COMMIT}"
                        
                        echo "✅ Version control completed successfully"
                    } catch (Exception e) {
                        echo "❌ Version control failed: ${e.getMessage()}"
                        error "Version control failed: ${e.getMessage()}"
                    }
                }
            }
        }

        stage('Terraform Create ECR') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh """
                            echo "🚀 Starting Terraform ECR creation..."
                            cd terraform/ecr
                            
                            # Initialize Terraform
                            echo "Initializing Terraform..."
                            terraform init -input=false
                            
                            # Validate Terraform configuration
                            echo "Validating Terraform configuration..."
                            terraform validate
                            
                            # Plan Terraform changes
                            echo "Planning Terraform changes..."
                            terraform plan -out=tfplan -input=false
                            
                            # Apply Terraform changes
                            echo "Applying Terraform changes to create ECR repositories..."
                            terraform apply -input=false -auto-approve tfplan
                            
                            echo "✅ Terraform ECR creation completed successfully"
                            
                            # Display created ECR repositories
                            echo "Created ECR repositories:"
                            aws ecr describe-repositories --region ${AWS_REGION} --query "repositories[?contains(repositoryName, 'ecommerce')].repositoryName" --output table
                        """
                    }
                }
            }
        }

        stage('Build User Service') {
            steps {
                script {
                    buildServiceOptimized('user-service')
                }
            }
        }

        stage('Build Product Service') {
            steps {
                script {
                    buildServiceOptimized('product-service')
                }
            }
        }

        stage('Build Order Service') {
            steps {
                script {
                    buildServiceOptimized('order-service')
                }
            }
        }

        stage('Build Payment Service') {
            steps {
                script {
                    buildServiceOptimized('payment-service')
                }
            }
        }

        stage('Build Inventory Service') {
            steps {
                script {
                    buildServiceOptimized('inventory-service')
                }
            }
        }

        // stage('Build Notification Service') {
        //     steps {
        //         script {
        //             buildServiceOptimized('notification-service')
        //         }
        //     }
        // }

        stage('Build Frontend') {
            steps {
                dir('frontend') {
                    sh '''
                        # 设置 Node.js 环境变量 - 使用更新后的路径
                        export NODE_HOME=''' + NODE_HOME + '''
                        export PATH=''' + NODE_BIN + ''':$PATH
                        
                        echo "Building Frontend..."
                        
                        # 检查资源状态
                        echo "=== Resource Status ==="
                        df -h
                        
                        # 彻底清理缓存
                        rm -rf node_modules/ dist/ build/ .cache/
                        npm cache clean --force
                        
                        # 安装依赖
                        npm ci --no-cache --no-audit --prefer-offline
                        
                        # 构建
                        npm run build
                        
                        echo "✅ Frontend build completed"
                    '''
                }
            }
        }

        stage('ECR Login') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh """
                            echo "Logging into ECR..."
                            aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${DOCKER_REGISTRY}
                            echo "✅ ECR login successful"
                        """
                    }
                }
            }
        }

        stage('Docker Build Frontend') {
            steps {
                script {
                    dockerBuildPushOptimized('frontend', "${FRONTEND_IMAGE}")
                }
            }
        }

        stage('Docker Build User Service') {
            steps {
                script {
                    dockerBuildPushOptimized('user-service', "${USER_SERVICE_IMAGE}")
                }
            }
        }

        stage('Docker Build Product Service') {
            steps {
                script {
                    dockerBuildPushOptimized('product-service', "${PRODUCT_SERVICE_IMAGE}")
                }
            }
        }

        stage('Docker Build Order Service') {
            steps {
                script {
                    dockerBuildPushOptimized('order-service', "${ORDER_SERVICE_IMAGE}")
                }
            }
        }

        stage('Docker Build Payment Service') {
            steps {
                script {
                    dockerBuildPushOptimized('payment-service', "${PAYMENT_SERVICE_IMAGE}")
                }
            }
        }

        stage('Docker Build Inventory Service') {
            steps {
                script {
                    dockerBuildPushOptimized('inventory-service', "${INVENTORY_SERVICE_IMAGE}")
                }
            }
        }

        // stage('Docker Build Notification Service') {
        //     steps {
        //         script {
        //             dockerBuildPushOptimized('notification-service', "${NOTIFICATION_SERVICE_IMAGE}")
        //         }
        //     }
        // }

        stage('Create Namespace') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh """
                            echo "🏗️ Creating namespace if not exists..."
                            
                            # 创建命名空间（如果不存在）
                            kubectl create namespace ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                            
                            echo "✅ Namespace ${NAMESPACE} created/verified"
                        """
                    }
                }
            }
        }

        stage('Create Image Pull Secret') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh '''
                            echo "🔑 Creating ECR image pull secret..."
                            
                            # 获取 ECR 登录密码
                            ECR_PASSWORD=$(aws ecr get-login-password --region us-east-1)
                            
                            # 创建或更新镜像拉取 Secret
                            kubectl create secret docker-registry regcred \\
                              --docker-server=319998871902.dkr.ecr.us-east-1.amazonaws.com \\
                              --docker-username=AWS \\
                              --docker-password="$ECR_PASSWORD" \\
                              --namespace ecommerce --dry-run=client -o yaml | kubectl apply -f -
                            
                            echo "✅ ECR image pull secret created/updated successfully"
                        '''
                    }
                }
            }
        }

        stage('Generate App Version ConfigMap') {
            steps {
                script {
                    // 动态生成 app-version-configmap.yaml
                    def configMapContent = """
apiVersion: v1
kind: ConfigMap
metadata:
  name: app-version-info
  namespace: ${NAMESPACE}
  labels:
    app.kubernetes.io/name: app-version-info
    app.kubernetes.io/part-of: ecommerce
    app.kubernetes.io/version: "${APPLICATION_VERSION}"
data:
  # 应用版本信息
  application.version: "${APPLICATION_VERSION}"
  application.name: "${APPLICATION_NAME}"
  application.description: "${APPLICATION_DESCRIPTION}"
  
  # 构建信息
  build.timestamp: "${new Date().format("yyyy-MM-dd'T'HH:mm:ss'Z'", TimeZone.getTimeZone('UTC'))}"
  build.number: "${BUILD_VERSION}"
  build.commit: "${env.GIT_COMMIT}"
  build.commit.short: "${GIT_COMMIT_SHORT}"
  build.branch: "${env.BRANCH_NAME}"
  build.java.version: "17"
  build.node.version: "18.20.4"
  build.gradle.version: "8.5"
  
  # 服务版本信息
  service.user-service.version: "${USER_SERVICE_VERSION}"
  service.user-service.description: "User Management Service"
  service.user-service.component: "backend"
  service.user-service.team: "identity-team"
  
  service.product-service.version: "${PRODUCT_SERVICE_VERSION}"
  service.product-service.description: "Product Management Service"
  service.product-service.component: "backend"
  service.product-service.team: "catalog-team"
  
  service.order-service.version: "${ORDER_SERVICE_VERSION}"
  service.order-service.description: "Order Management Service"
  service.order-service.component: "backend"
  service.order-service.team: "order-team"
  
  service.payment-service.version: "${PAYMENT_SERVICE_VERSION}"
  service.payment-service.description: "Payment Processing Service"
  service.payment-service.component: "backend"
  service.payment-service.team: "payment-team"
  
  service.inventory-service.version: "${INVENTORY_SERVICE_VERSION}"
  service.inventory-service.description: "Inventory Management Service"
  service.inventory-service.component: "backend"
  service.inventory-service.team: "inventory-team"
  
  service.notification-service.version: "${NOTIFICATION_SERVICE_VERSION}"
  service.notification-service.description: "Notification Service"
  service.notification-service.component: "backend"
  service.notification-service.team: "notification-team"
  
  service.frontend.version: "${FRONTEND_VERSION}"
  service.frontend.description: "React Frontend Application"
  service.frontend.component: "frontend"
  service.frontend.team: "frontend-team"
"""
                    
                    writeFile file: 'kubernetes/app-version-configmap.yaml', text: configMapContent
                    echo "✅ App version ConfigMap generated dynamically"
                }
            }
        }

        stage('Generate JWT Secret') {
            steps {
                script {
                    // 生成 JWT Secret - 使用安全的随机值
                    def randomSuffix = (System.currentTimeMillis() + new Random().nextInt(1000)).toString()
                    def jwtKey = "ecommerce-jwt-${APPLICATION_VERSION}-${BUILD_VERSION}-${randomSuffix}"
                    
                    // 使用 shell 命令进行 base64 编码
                    sh """
                        echo -n "${jwtKey}" | base64 > /tmp/jwt-key.txt
                    """
                    
                    def encodedJwtKey = readFile('/tmp/jwt-key.txt').trim()
                    
                    def jwtSecretContent = """
apiVersion: v1
kind: Secret
metadata:
  name: jwt-secret
  namespace: ${NAMESPACE}
  labels:
    app.kubernetes.io/name: jwt-secret
    app.kubernetes.io/part-of: ecommerce
type: Opaque
data:
  secret: ${encodedJwtKey}
"""
                    
                    writeFile file: 'kubernetes/jwt-secret.yaml', text: jwtSecretContent
                    echo "✅ JWT Secret generated dynamically"
                    
                    // 清理临时文件
                    sh "rm -f /tmp/jwt-key.txt"
                }
            }
        }

        stage('Update Deployment Versions') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh '''
                            echo "🔧 Updating deployment versions to: ''' + DEPLOYMENT_VERSION + '''"
                            
                            # Update kubeconfig
                            aws eks update-kubeconfig --region ''' + AWS_REGION + ''' --name ''' + EKS_CLUSTER_NAME + '''
                            
                            echo "Updating deployment files with version variables..."
                            
                            # 处理微服务 - 更新所有服务
                            services="user-service product-service order-service payment-service inventory-service notification-service"
                            
                            for service in $services; do
                                echo "Processing $service..."
                                deployment_file="kubernetes/microservices/$service/deployment.yaml"
                                
                                if [ -f "$deployment_file" ]; then
                                    # 创建临时文件
                                    temp_file=$(mktemp)
                                    
                                    # 根据服务名称选择正确的镜像
                                    case "$service" in
                                        "user-service")
                                            image_name="''' + USER_SERVICE_IMAGE + '''" ;;
                                        "product-service")
                                            image_name="''' + PRODUCT_SERVICE_IMAGE + '''" ;;
                                        "order-service")
                                            image_name="''' + ORDER_SERVICE_IMAGE + '''" ;;
                                        "payment-service")
                                            image_name="''' + PAYMENT_SERVICE_IMAGE + '''" ;;
                                        "inventory-service")
                                            image_name="''' + INVENTORY_SERVICE_IMAGE + '''" ;;
                                        "notification-service")
                                            image_name="''' + NOTIFICATION_SERVICE_IMAGE + '''" ;;
                                        *)
                                            image_name="''' + USER_SERVICE_IMAGE + '''" ;;
                                    esac
                                    
                                    echo "Updating $service deployment variables..."
                                    
                                    # 替换所有变量到临时文件
                                    sed -e "s|\\$(APP_VERSION)|''' + DEPLOYMENT_VERSION + '''|g" \
                                        -e "s|319998871902.dkr.ecr.us-east-1.amazonaws.com/ecommerce-$service:.*|$image_name:''' + DEPLOYMENT_VERSION + '''|g" \
                                        -e "s|\\$(BUILD_VERSION)|''' + BUILD_VERSION + '''|g" \
                                        -e "s|\\$(GIT_COMMIT)|''' + env.GIT_COMMIT + '''|g" \
                                        "$deployment_file" > "$temp_file"
                                    
                                    # 用临时文件替换原始文件
                                    mv "$temp_file" "$deployment_file"
                                    
                                    echo "✅ $service deployment variables updated"
                                else
                                    echo "❌ Deployment file not found: $deployment_file"
                                fi
                            done
                            
                            # 处理 frontend
                            frontend_file="kubernetes/frontend/deployment.yaml"
                            if [ -f "$frontend_file" ]; then
                                temp_file=$(mktemp)
                                echo "Updating frontend deployment variables..."
                                sed -e "s|\\$(APP_VERSION)|''' + DEPLOYMENT_VERSION + '''|g" \
                                    -e "s|319998871902.dkr.ecr.us-east-1.amazonaws.com/ecommerce-frontend:.*|''' + FRONTEND_IMAGE + ''':''' + DEPLOYMENT_VERSION + '''|g" \
                                    -e "s|\\$(BUILD_VERSION)|''' + BUILD_VERSION + '''|g" \
                                    -e "s|\\$(GIT_COMMIT)|''' + env.GIT_COMMIT + '''|g" \
                                    "$frontend_file" > "$temp_file"
                                mv "$temp_file" "$frontend_file"
                                echo "✅ frontend deployment variables updated"
                            else
                                echo "❌ Frontend deployment file not found: $frontend_file"
                            fi
                            
                            echo "✅ All deployment files updated with version: ''' + DEPLOYMENT_VERSION + '''"
                        '''
                    }
                }
            }
        }

        stage('Apply Version ConfigMap') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh """
                            echo "📝 Applying version ConfigMap to Kubernetes..."
                            
                            # Update kubeconfig
                            aws eks update-kubeconfig --region ${AWS_REGION} --name ${EKS_CLUSTER_NAME}
                            
                            # Apply the generated version ConfigMap
                            kubectl apply -f ${VERSION_CONFIGMAP_FILE} -n ${NAMESPACE}
                            
                            echo "✅ Version ConfigMap applied successfully"
                        """
                    }
                }
            }
        }

        stage('Record Version Info') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh """
                            echo "📝 Recording version information..."
                            
                            # Create version info ConfigMap
                            kubectl create configmap version-info \\
                              --from-literal=build-number=${BUILD_VERSION} \\
                              --from-literal=git-commit=${env.GIT_COMMIT} \\
                              --from-literal=git-commit-short=${GIT_COMMIT_SHORT} \\
                              --from-literal=docker-tag=${DOCKER_TAG} \\
                              --from-literal=deployment-version=${DEPLOYMENT_VERSION} \\
                              --from-literal=application-version=${APPLICATION_VERSION} \\
                              --from-literal=build-time=\$(date -u +"%Y-%m-%dT%H:%M:%SZ") \\
                              --from-literal=deploy-time=\$(date -u +"%Y-%m-%dT%H:%M:%SZ") \\
                              -n ${NAMESPACE} --dry-run=client -o yaml | kubectl apply -f -
                              
                            echo "✅ Version information recorded"
                        """
                    }
                }
            }
        }

        stage('Clean Up Previous Deployments') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        sh '''
                            echo "🧹 Cleaning up previous deployments for disabled services..."
                            
                            # Update kubeconfig
                            aws eks update-kubeconfig --region ''' + AWS_REGION + ''' --name ''' + EKS_CLUSTER_NAME + '''
                            
                            # 删除之前可能已经部署的服务的 Deployment
                            services_to_clean="notification-service"
                            
                            for service in $services_to_clean; do
                                echo "Checking if $service deployment exists..."
                                if kubectl get deployment $service -n ''' + NAMESPACE + ''' >/dev/null 2>&1; then
                                    echo "Deleting $service deployment..."
                                    kubectl delete deployment $service -n ''' + NAMESPACE + ''' --ignore-not-found=true
                                    echo "✅ $service deployment deleted"
                                else
                                    echo "ℹ️  $service deployment not found, skipping"
                                fi
                            done
                            
                            echo "✅ Previous deployments cleanup completed"
                        '''
                    }
                }
            }
        }

        stage('Deploy to EKS') {
            options {
                timeout(time: 10, unit: 'MINUTES')  // 增加超时时间
            }
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        catchError(buildResult: 'SUCCESS', stageResult: 'UNSTABLE') {
                            sh '''#!/bin/bash
                                echo "🚀 Starting deployment to EKS..."
                                echo "Application Version: ''' + APPLICATION_VERSION + '''"
                                echo "Deployment Version: ''' + DEPLOYMENT_VERSION + '''"
                                
                                # Update kubeconfig
                                aws eks update-kubeconfig --region ''' + AWS_REGION + ''' --name ''' + EKS_CLUSTER_NAME + '''
                                
                                # === 第一步：先应用 Networking 资源 ===
                                echo "=== Step 1: Applying Networking Resources First ==="
                                if [ -d "kubernetes/networking" ]; then
                                    echo "Applying ALL networking configurations..."
                                    
                                    # 检查 networking 目录内容
                                    echo "Networking directory contents:"
                                    ls -la kubernetes/networking/
                                    
                                    # 应用所有 networking YAML 文件
                                    find kubernetes/networking -name "*.yaml" -o -name "*.yml" | while read file; do
                                        if [ -f "$file" ]; then
                                            echo "Applying networking file: $file"
                                            kubectl apply -f "$file" -n ''' + NAMESPACE + '''
                                        fi
                                    done
                                    
                                    echo "✅ All networking resources applied"
                                    
                                    # 等待 LoadBalancer 创建
                                    echo "Waiting for LoadBalancer services to initialize (45 seconds)..."
                                    sleep 45
                                    
                                    # 检查 LoadBalancer 状态
                                    echo "LoadBalancer services status:"
                                    kubectl get svc -n ''' + NAMESPACE + ''' | grep LoadBalancer || echo "No LoadBalancer services found"
                                    
                                    # 检查 Ingress 状态
                                    echo "Ingress resources status:"
                                    kubectl get ingress -n ''' + NAMESPACE + ''' || echo "No ingress resources found"
                                    
                                else
                                    echo "❌ kubernetes/networking directory not found"
                                    echo "Current directory structure:"
                                    ls -la kubernetes/
                                fi
                                
                                # === 第二步：应用 ConfigMaps 和 Secrets ===
                                echo "=== Step 2: Applying ConfigMaps and Secrets ==="
                                kubectl apply -f kubernetes/app-version-configmap.yaml -n ''' + NAMESPACE + '''
                                kubectl apply -f kubernetes/jwt-secret.yaml -n ''' + NAMESPACE + '''
                                
                                # 应用支付宝密钥 Secret
                                echo "Applying Alipay Secret..."
                                if [ -f "kubernetes/secrets/alipay-secret.yaml" ]; then
                                    echo "Found alipay-secret.yaml, applying..."
                                    kubectl apply -f kubernetes/secrets/alipay-secret.yaml -n ''' + NAMESPACE + '''
                                    echo "✅ Alipay secret applied successfully"
                                    
                                    # 验证支付宝 Secret 是否创建成功
                                    echo "Verifying alipay secret creation..."
                                    kubectl get secret alipay-secret -n ''' + NAMESPACE + ''' || echo "Alipay secret not found"
                                else
                                    echo "⚠️ alipay-secret.yaml not found at kubernetes/secrets/alipay-secret.yaml"
                                    echo "Current secrets directory contents:"
                                    ls -la kubernetes/secrets/ || echo "Secrets directory not found"
                                fi
                                
                                # 应用应用 Secrets
                                echo "Applying application secrets..."
                                if [ -f "kubernetes/secrets/app-secrets.yaml" ]; then
                                    echo "Found app-secrets.yaml, applying..."
                                    kubectl apply -f kubernetes/secrets/app-secrets.yaml -n ''' + NAMESPACE + '''
                                    echo "✅ Application secrets applied successfully"
                                    
                                    # 验证 Secrets 是否创建成功
                                    echo "Verifying secrets creation..."
                                    kubectl get secrets -n ''' + NAMESPACE + ''' | grep -E "(postgresql|mongodb|redis|rabbitmq|jwt|elasticsearch|alipay)"
                                else
                                    echo "⚠️ app-secrets.yaml not found at kubernetes/secrets/app-secrets.yaml"
                                    echo "Current secrets directory contents:"
                                    ls -la kubernetes/secrets/ || echo "Secrets directory not found"
                                fi
                                
                                # === 第三步：应用数据库和消息队列（包括 Elasticsearch）===
                                echo "=== Step 3: Applying Databases, Message Queue and Elasticsearch ==="
                                
                                # 应用所有数据库配置
                                if [ -d "kubernetes/databases" ]; then
                                    echo "Applying ALL database configurations (including Elasticsearch)..."
                                    kubectl apply -f kubernetes/databases/ -n ''' + NAMESPACE + ''' --recursive=true
                                    echo "✅ All database resources applied"
                                else
                                    echo "⚠️ No databases configuration found"
                                fi
                                
                                # 等待数据库就绪
                                echo "⏳ Waiting for databases to be ready (60 seconds)..."
                                sleep 60
                                
                                # 检查 Elasticsearch 状态
                                echo "🔍 Checking Elasticsearch status..."
                                if kubectl get pods -n ''' + NAMESPACE + ''' -l app=elasticsearch &>/dev/null; then
                                    echo "Elasticsearch pod found, checking status..."
                                    kubectl get pods -n ''' + NAMESPACE + ''' -l app=elasticsearch -o wide
                                    
                                    # 等待 Elasticsearch 就绪（不阻塞后续部署）
                                    echo "⏳ Waiting for Elasticsearch to be ready (非阻塞等待)..."
                                    timeout 120s bash -c 'until kubectl wait --for=condition=ready pod -l app=elasticsearch -n ''' + NAMESPACE + ''' --timeout=60s 2>/dev/null; do 
                                        echo "Elasticsearch still starting, checking again..."
                                        kubectl get pods -n ''' + NAMESPACE + ''' -l app=elasticsearch
                                        sleep 10
                                    done' || echo "⚠️ Elasticsearch ready check timeout, continuing deployment..."
                                    
                                    # 检查 Elasticsearch 日志
                                    echo "📝 Elasticsearch logs (last 15 lines):"
                                    kubectl logs -n ''' + NAMESPACE + ''' -l app=elasticsearch --tail=15 || echo "Could not get Elasticsearch logs"
                                else
                                    echo "⚠️ Elasticsearch pod not found, skipping Elasticsearch checks"
                                fi
                                
                                if [ -d "kubernetes/message-queue" ]; then
                                    echo "Applying message queue configurations..."
                                    kubectl apply -f kubernetes/message-queue/ -n ''' + NAMESPACE + ''' --recursive=true
                                    echo "✅ Message queue resources applied"
                                else
                                    echo "⚠️ No message queue configuration found"
                                fi
                                
                                # 等待基础设施就绪
                                echo "=== Waiting for infrastructure to be ready (30 seconds) ==="
                                sleep 30
                                
                                # === 第四步：应用微服务 ===
                                echo "=== Step 4: Applying Microservices ==="
                                # 部署所有服务（包括 inventory-service，除了被注销的 notification-service）
                                services_list="user-service product-service order-service payment-service inventory-service"
                                
                                for service in $services_list; do
                                    echo "Applying all resources for $service..."
                                    if [ -d "kubernetes/microservices/$service" ]; then
                                        find "kubernetes/microservices/$service" -name "*.yaml" -o -name "*.yml" 2>/dev/null | while read file; do
                                            if [ -f "$file" ]; then
                                                echo "Applying: $file"
                                                kubectl apply -f "$file" -n ''' + NAMESPACE + '''
                                            fi
                                        done
                                        echo "✅ $service resources applied"
                                    else
                                        echo "⚠️ Directory not found: kubernetes/microservices/$service"
                                    fi
                                done
                                
                                # === 第五步：应用前端 ===
                                echo "=== Step 5: Applying Frontend ==="
                                if [ -d "kubernetes/frontend" ]; then
                                    find "kubernetes/frontend" -name "*.yaml" -o -name "*.yml" | while read file; do
                                        echo "Applying frontend: $file"
                                        kubectl apply -f "$file" -n ''' + NAMESPACE + '''
                                    done
                                    echo "✅ Frontend resources applied"
                                else
                                    echo "⚠️ Frontend directory not found"
                                fi
                                
                                # 等待核心服务就绪
                                echo "=== Waiting for core services to be ready (90 seconds) ==="
                                sleep 90
                                
                                # 检查所有资源状态
                                echo "=== Final Deployment Status ==="
                                echo "--- Deployments ---"
                                kubectl get deployments -n ''' + NAMESPACE + ''' -o wide
                                echo ""
                                echo "--- Pods ---"
                                kubectl get pods -n ''' + NAMESPACE + ''' -o wide
                                echo ""
                                echo "--- Services ---"
                                kubectl get services -n ''' + NAMESPACE + ''' -o wide
                                echo ""
                                echo "--- Ingress ---"
                                kubectl get ingress -n ''' + NAMESPACE + ''' -o wide
                                echo ""
                                echo "--- Events (recent) ---"
                                kubectl get events -n ''' + NAMESPACE + ''' --sort-by='.lastTimestamp' | tail -15
                                
                                # 等待核心部署完成
                                echo "=== Waiting for core deployments to be ready ==="
                                core_deployments="user-service product-service order-service payment-service inventory-service frontend"
                                
                                for deployment in $core_deployments; do
                                    echo "Waiting for $deployment rollout..."
                                    if kubectl rollout status deployment/$deployment -n ''' + NAMESPACE + ''' --timeout=300s; then
                                        echo "✅ $deployment rollout completed"
                                    else
                                        echo "⚠️ $deployment rollout failed or timed out, checking details..."
                                        kubectl describe deployment/$deployment -n ''' + NAMESPACE + '''
                                        kubectl get pods -n ''' + NAMESPACE + ''' -l app=$deployment
                                        # 继续而不是失败，让部署完成
                                    fi
                                done
                                
                                echo "✅ Deployment process completed!"
                                
                                # 最终状态检查
                                echo "=== Final Health Check ==="
                                kubectl get deployments -n ''' + NAMESPACE + '''
                                kubectl get pods -n ''' + NAMESPACE + '''
                                kubectl get services -n ''' + NAMESPACE + '''
                                
                                # 检查 API Gateway 服务状态
                                echo "=== API Gateway Service Status ==="
                                kubectl get svc -n ''' + NAMESPACE + ''' | grep gateway || echo "No gateway service found"
                                
                                # 测试服务发现
                                echo "=== Testing Service Discovery ==="
                                kubectl run test-service-discovery -n ''' + NAMESPACE + ''' --image=busybox --rm -it --restart=Never -- nslookup api-gateway-service || echo "Service discovery test failed"
                                
                                # 测试 Elasticsearch 服务发现
                                echo "=== Testing Elasticsearch Service Discovery ==="
                                kubectl run test-elasticsearch-discovery -n ''' + NAMESPACE + ''' --image=busybox --rm -it --restart=Never -- nslookup elasticsearch || echo "Elasticsearch service discovery test failed"
                            '''
                        }
                    }
                }
            }
        }

        stage('Terraform Create API Gateway') {
            steps {
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    script {
                        echo "🚀 Proceeding with API Gateway creation regardless of previous stage status..."
                        sh """
                            cd ${TF_API_GATEWAY_DIR}
                            
                            # Initialize Terraform
                            echo "Initializing Terraform for API Gateway..."
                            terraform init -input=false
                            
                            # Validate Terraform configuration
                            echo "Validating Terraform configuration..."
                            terraform validate
                            
                            # Plan Terraform changes
                            echo "Planning Terraform changes for API Gateway..."
                            terraform plan -out=tfplan-api-gateway -input=false
                            
                            # Apply Terraform changes
                            echo "Applying Terraform changes to create API Gateway..."
                            terraform apply -input=false -auto-approve tfplan-api-gateway
                            
                            echo "✅ API Gateway creation completed successfully"
                            
                            # 获取输出信息
                            echo "=== API Gateway Outputs ==="
                            terraform output -json
                            
                            # 显示 API Gateway 信息
                            echo "=== API Gateway Information ==="
                            API_URL=\$(terraform output -raw api_custom_domain_url 2>/dev/null || echo "Not available")
                            INVOKE_URL=\$(terraform output -raw api_gateway_invoke_url 2>/dev/null || echo "Not available")
                            
                            echo "Custom Domain URL: \$API_URL"
                            echo "Invoke URL: \$INVOKE_URL"
                            
                            # 测试 API Gateway 连接
                            echo "=== Testing API Gateway ==="
                            if [ "\$API_URL" != "Not available" ]; then
                                echo "Testing API Gateway endpoint: \$API_URL/actuator/health"
                                curl -s -o /dev/null -w "HTTP Status: %{http_code}\\n" \$API_URL/actuator/health || echo "API Gateway test failed"
                            fi
                        """
                    }
                }
            }
        }
    }

    post {
        always {
            // Archive build artifacts
            archiveArtifacts artifacts: '**/build/libs/*.jar', fingerprint: true
            
            // Clean up Docker to free space
            sh '''
                echo "Cleaning up Docker resources..."
                docker system prune -f || true
                echo "Cleanup completed"
            '''
            
            // Log disk space after build
            sh '''
                echo "Disk space after build:"
                df -h
            '''
        }
        success {
            echo "🎉 Build and deployment successful!"
            script {
                sh '''
                    echo "✅ All tasks completed successfully"
                    echo "📊 Build Information:"
                    echo "   Application Version: '${APPLICATION_VERSION}'"
                    echo "   Build Number: '${BUILD_VERSION}'"
                    echo "   Docker Tag: '${DOCKER_TAG}'"
                    echo "   Deployment Version: '${DEPLOYMENT_VERSION}'"
                    echo "   Git Commit: '${GIT_COMMIT}'"
                    echo "📊 Final resource usage:"
                    df -h
                    echo "🚀 Application deployed successfully!"
                '''
                
                // 显示 API Gateway 信息
                dir('terraform/aws-api-gateway') {
                    sh '''
                        echo "🌐 API Gateway Information:"
                        terraform output -json 2>/dev/null || echo "API Gateway outputs not available"
                    '''
                }
                
                // 显示 Elasticsearch 状态
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'dev-user-aws-credentials',
                    accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                    secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                ]]) {
                    sh '''
                        echo "🔍 Elasticsearch Status:"
                        aws eks update-kubeconfig --region ''' + AWS_REGION + ''' --name ''' + EKS_CLUSTER_NAME + '''
                        kubectl get pods,svc -n ''' + NAMESPACE + ''' -l app=elasticsearch 2>/dev/null || echo "Elasticsearch resources not found"
                    '''
                }
            }
        }
        failure {
            echo "❌ Build failed! Check the logs for details."
            sh '''
                echo "💡 Troubleshooting tips:"
                echo "1. Check disk space: df -h"
                echo "2. Check Docker: docker system df"
                echo "3. Check Gradle cache: du -sh ~/.gradle/"
                echo "4. Check Kubernetes resources: kubectl get pods -n ecommerce"
                echo "5. Check ECR repositories: aws ecr describe-repositories --region us-east-1"
                echo "6. Check specific service logs: kubectl logs -n ecommerce <pod-name>"
                echo "7. Check deployment status: kubectl get deployments -n ecommerce"
                echo "8. Check version info: kubectl get configmap version-info -n ecommerce -o yaml"
                echo "9. Check networking resources: kubectl get svc,ingress -n ecommerce"
                echo "10. Check service discovery: kubectl run test-nslookup -n ecommerce --image=busybox --rm -it -- nslookup api-gateway-service"
                echo "11. Check API Gateway Terraform state: cd terraform/aws-api-gateway && terraform show"
                echo "12. Check nginx.conf in failed image: docker run --rm <image> cat /etc/nginx/nginx.conf"
                echo "13. Check application secrets: kubectl get secrets -n ecommerce | grep -E '(postgresql|mongodb|redis|rabbitmq|jwt|elasticsearch|alipay)'"
                echo "14. Check Elasticsearch specifically:"
                echo "    - kubectl get pods -n ecommerce -l app=elasticsearch"
                echo "    - kubectl logs -n ecommerce -l app=elasticsearch"
                echo "    - kubectl describe pod -n ecommerce -l app=elasticsearch"
                echo "15. Check Alipay secret specifically:"
                echo "    - kubectl get secret alipay-secret -n ecommerce"
                echo "    - kubectl describe secret alipay-secret -n ecommerce"
            '''
        }
    }
}

// 优化的串行构建微服务函数
def buildServiceOptimized(serviceName) {
    echo "🔨 Building ${serviceName}..."
    dir("microservices/${serviceName}") {
        retry(2) {
            timeout(time: 15, unit: 'MINUTES') {
                sh '''
                    # 设置环境变量
                    export JAVA_HOME=''' + JAVA_HOME + '''
                    export GRADLE_HOME=''' + GRADLE_HOME + '''
                    export PATH=$JAVA_HOME/bin:''' + GRADLE_BIN + ''':$PATH
                    
                    echo "=== Starting Gradle build for ''' + serviceName + ''' ==="
                    
                    # 检查当前资源状态
                    echo "=== Current Resources ==="
                    df -h
                    
                    # 步骤1: 仅清理
                    echo "Step 1: Cleaning project..."
                    gradle clean --no-daemon --stacktrace
                    rm -rf build/
                    
                    # 步骤2: 仅编译Java（减少内存使用）
                    echo "Step 2: Compiling Java..."
                    gradle compileJava --no-daemon --stacktrace \
                        -Dorg.gradle.jvmargs="-Xmx512m -Xms256m" \
                        -Dorg.gradle.parallel=true \
                        -Dorg.gradle.configureondemand=true
                    
                    # 步骤3: 处理资源
                    echo "Step 3: Processing resources..."
                    gradle processResources --no-daemon --stacktrace
                    
                    # 步骤4: 仅构建JAR（跳过测试）
                    echo "Step 4: Building JAR..."
                    gradle bootJar -x test --no-daemon --stacktrace \
                        -Dorg.gradle.jvmargs="-Xmx512m -Xms256m" \
                        -Dorg.gradle.parallel=true
                    
                    echo "✅ ''' + serviceName + ''' build completed"
                    echo "Build artifacts:"
                    ls -la build/libs/
                '''
            }
        }
    }
    echo "✅ ${serviceName} build completed successfully"
}

// 优化的 Docker 构建函数
def dockerBuildPushOptimized(serviceDir, imageName) {
    echo "🐳 Building and pushing Docker image for ${serviceDir}..."
    
    def contextDir = serviceDir == 'frontend' ? 'frontend' : "microservices/${serviceDir}"
    def serviceVersion = env."${serviceDir.toUpperCase().replace('-', '_')}_VERSION"
    
    dir(contextDir) {
        retry(2) {
            timeout(time: 10, unit: 'MINUTES') {
                sh """
                    echo "=== Building Docker image for ${serviceDir} ==="
                    echo "Image: ${imageName}"
                    echo "Service Version: ${serviceVersion}"
                    echo "Build context: \$(pwd)"
                    
                    # 检查 Dockerfile 是否存在
                    echo "Checking for Dockerfile..."
                    ls -la Dockerfile || echo "Dockerfile not found, listing all files:"
                    ls -la
                    
                    # 构建 Docker 镜像 - 使用语义化版本号
                    echo "Building Docker image with semantic versioning..."
                    docker build --no-cache -t ${imageName}:${serviceVersion} -t ${imageName}:latest .
                    
                    # 推送版本标签到 ECR
                    echo "Pushing version tag: ${serviceVersion}"
                    docker push ${imageName}:${serviceVersion}
                    
                    # 推送最新标签
                    echo "Pushing latest tag"
                    docker push ${imageName}:latest
                    
                    echo "✅ ${serviceDir} Docker images pushed successfully"
                    echo "   - ${imageName}:${serviceVersion}"
                    echo "   - ${imageName}:latest"
                    
                    # 清理本地镜像以节省空间
                    echo "Cleaning up local Docker images..."
                    docker rmi ${imageName}:${serviceVersion} || true
                    docker rmi ${imageName}:latest || true
                    
                    echo "=== ${serviceDir} Docker build completed ==="
                """
            }
        }
    }
}

// 版本控制相关函数
def loadVersionConfig() {
    echo "📖 Loading version configuration from ${VERSION_FILE}"
    
    if (!fileExists(VERSION_FILE)) {
        error "Version file ${VERSION_FILE} not found!"
    }
    
    // 读取版本文件内容
    def versionContent = readYaml file: VERSION_FILE
    
    // 设置全局版本信息
    env.APPLICATION_VERSION = "${versionContent.versions.application.major}.${versionContent.versions.application.minor}.${versionContent.versions.application.patch}"
    env.APPLICATION_NAME = versionContent.versions.application.name
    env.APPLICATION_DESCRIPTION = versionContent.versions.application.description
    
    // 设置服务版本信息
    versionContent.versions.services.each { serviceName, serviceConfig ->
        def envVarName = serviceName.toUpperCase().replace('-', '_') + "_VERSION"
        env."${envVarName}" = serviceConfig.version
        echo "Set ${envVarName} = ${serviceConfig.version}"
    }
    
    // 设置基础设施版本
    versionContent.versions.infrastructure.each { infraName, infraConfig ->
        env."${infraName.toUpperCase()}_VERSION" = infraConfig.version
    }
    
    // 设置Docker标签和部署版本 - 现在使用语义化版本号
    env.DOCKER_TAG = "${env.APPLICATION_VERSION}-${env.GIT_COMMIT_SHORT}"
    env.DEPLOYMENT_VERSION = env.APPLICATION_VERSION
    
    echo "✅ Version configuration loaded:"
    echo "   Application: ${env.APPLICATION_VERSION} - ${env.APPLICATION_NAME}"
    echo "   Docker Tag: ${env.DOCKER_TAG}"
    echo "   Deployment Version: ${env.DEPLOYMENT_VERSION}"
    versionContent.versions.services.each { serviceName, serviceConfig ->
        echo "   ${serviceName}: ${serviceConfig.version}"
    }
}

def generateVersionConfigMap() {
    echo "📝 Generating version ConfigMap"
    
    try {
        def versionContent = readYaml file: VERSION_FILE
        
        // 使用shell命令直接生成ConfigMap文件，避免writeYaml的问题
        sh """
            cat > ${VERSION_CONFIGMAP_FILE} << EOF
apiVersion: v1
kind: ConfigMap
metadata:
  name: ecommerce-version-info
  namespace: ${NAMESPACE}
data:
  application.version: "${APPLICATION_VERSION}"
  application.name: "${APPLICATION_NAME}"
  application.description: "${APPLICATION_DESCRIPTION}"
  build.timestamp: "`date -u +"%Y-%m-%dT%H:%M:%SZ"`"
  build.number: "${BUILD_VERSION}"
  build.commit: "${env.GIT_COMMIT}"
  build.commit.short: "${GIT_COMMIT_SHORT}"
  build.branch: "${env.BRANCH_NAME}"
  docker.tag: "${DOCKER_TAG}"
  deployment.version: "${DEPLOYMENT_VERSION}"
  build.java.version: "17"
  build.node.version: "${versionContent.versions.build.node_version}"
  build.gradle.version: "${versionContent.versions.build.gradle_version}"
EOF
        """
        
        // 添加服务版本信息
        versionContent.versions.services.each { serviceName, serviceConfig ->
            def serviceKey = serviceName.replace('-', '.').toLowerCase()
            sh """
                echo "  service.${serviceKey}.version: \\\"${serviceConfig.version}\\\"" >> ${VERSION_CONFIGMAP_FILE}
                echo "  service.${serviceKey}.description: \\\"${serviceConfig.description}\\\"" >> ${VERSION_CONFIGMAP_FILE}
                echo "  service.${serviceKey}.component: \\\"${serviceConfig.component}\\\"" >> ${VERSION_CONFIGMAP_FILE}
                echo "  service.${serviceKey}.team: \\\"${serviceConfig.team}\\\"" >> ${VERSION_CONFIGMAP_FILE}
            """
        }
        
        // 添加基础设施版本信息
        versionContent.versions.infrastructure.each { infraName, infraConfig ->
            sh """
                echo "  infrastructure.${infraName}.version: \\\"${infraConfig.version}\\\"" >> ${VERSION_CONFIGMAP_FILE}
                echo "  infrastructure.${infraName}.description: \\\"${infraConfig.description}\\\"" >> ${VERSION_CONFIGMAP_FILE}
            """
        }
        
        echo "✅ Version ConfigMap generated: ${VERSION_CONFIGMAP_FILE}"
        sh "cat ${VERSION_CONFIGMAP_FILE}"
        
    } catch (Exception e) {
        echo "❌ Failed to generate version ConfigMap: ${e.getMessage()}"
        throw e
    }
}