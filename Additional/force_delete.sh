echo "强制移除 finalizers..."
kubectl patch svc microservices-nlb -n ecommerce -p '{"metadata":{"finalizers":[]}}' --type=merge
