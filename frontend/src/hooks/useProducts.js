import { useState, useEffect } from 'react'
import { productService } from '../services/api/productService'

export const useProducts = (params = {}) => {
  const [products, setProducts] = useState([])
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState(null)
  const [pagination, setPagination] = useState({})

  useEffect(() => {
    const fetchProducts = async () => {
      try {
        setLoading(true)
        setError(null)
        const response = await productService.getProducts(params)
        setProducts(response.products || [])
        setPagination({
          currentPage: response.currentPage,
          totalPages: response.totalPages,
          totalProducts: response.totalProducts
        })
      } catch (err) {
        setError(err.message)
      } finally {
        setLoading(false)
      }
    }

    fetchProducts()
  }, [params])

  return { products, loading, error, pagination }
}