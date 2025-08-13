import { useState, useEffect, useCallback } from "react";
import { ProductList } from "../../types/product";
import { getAllProducts, deleteProductById } from "../../services/productService";

export const useProducts = () => {
    const [products, setProducts] = useState<ProductList[]>([]);
    const [loading, setLoading] = useState(false);

    const fetchProducts = useCallback(async () => {
        setLoading(true);
        try {
            const data = await getAllProducts();
            setProducts(data);
        } catch (error) {
            alert((error as Error).message);
        } finally {
            setLoading(false);
        }
    }, []);

    const deleteProduct = useCallback(async (productId: number) => {
        const userInfo = JSON.parse(localStorage.getItem("userInfo") || "null");
        const token = userInfo?.accessToken || "";
        if (!token) {
            alert("로그인이 필요합니다.");
            return;
        }
        if (!window.confirm("정말 삭제하시겠습니까?")) return;

        try {
            await deleteProductById(productId, token);
            alert("삭제되었습니다.");
            setProducts((prev) => prev.filter((p) => p.id !== productId));
        } catch (error) {
            alert((error as Error).message);
        }
    }, []);

    useEffect(() => {
        fetchProducts();
    }, [fetchProducts]);

    return { products, loading, fetchProducts, deleteProduct };
};