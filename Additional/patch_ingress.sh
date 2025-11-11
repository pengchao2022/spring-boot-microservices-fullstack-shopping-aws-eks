# 修正 Ingress 配置，指向正确的 Service 端口 8080
kubectl patch ingress ecommerce-ingress -n ecommerce --type='json' -p='[
  {
    "op": "replace",
    "path": "/spec/rules/0/http/paths/3/backend/service/port/number",
    "value": 8080
  },
  {
    "op": "replace", 
    "path": "/spec/rules/0/http/paths/4/backend/service/port/number",
    "value": 8080
  }
]'
