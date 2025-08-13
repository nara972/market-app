import { useState, useEffect, useCallback } from "react";
import { Category } from "../../types/category";
import { ProductRequest } from "../../types/product";
import { getAllCategories } from "../../services/categoryService";
import { getProductDetail, updateProduct } from "../../services/productService";

export const useProductUpdate = (productId: string | null) => {
    const [categories, setCategories] = useState<Category[]>([]);
    const [parentCategories, setParentCategories] = useState<Category[]>([]);
    const [childCategories, setChildCategories] = useState<Category[]>([]);
    const [selectedParent, setSelectedParent] = useState<number | "">("");
    const [selectedChild, setSelectedChild] = useState<number | "">("");
    const [loading, setLoading] = useState(true);

    const [name, setName] = useState("");
    const [price, setPrice] = useState<number | "">("");
    const [stock, setStock] = useState<number | "">("");
    const [content, setContent] = useState("");

    const handleParentChange = (value: string) => {
        const pid = value === "" ? "" : parseInt(value, 10);
        setSelectedParent(pid);
        setSelectedChild("");
        if (value === "") {
            setChildCategories([]);
            return;
        }
        setChildCategories(categories.filter((cat) => cat.parentId === Number(pid)));
    };

    const handleChildChange = (value: string) => {
        setSelectedChild(value === "" ? "" : parseInt(value, 10));
    };

    const fetchProductDetailData = async (all: Category[]) => {
        if (!productId) return;
        const p = await getProductDetail(productId);
        setName(p.name);
        setPrice(p.price);
        setStock(p.stock);
        setContent(p.content);

        const child = all.find((c) => c.id === p.categoryId);
        if (child) {
            setSelectedChild(child.id);
            const parent = all.find((c) => c.id === child.parentId);
            if (parent) {
                setSelectedParent(parent.id);
                setChildCategories(all.filter((c) => c.parentId === parent.id));
            } else {
                setChildCategories([]);
            }
        } else {
            setChildCategories([]);
        }
    };

    const load = useCallback(async () => {
        if (!productId) return;
        try {
            const all = await getAllCategories();
            setCategories(all);
            setParentCategories(all.filter((c) => c.parentId === null));
            await fetchProductDetailData(all);
        } catch (error) {
            alert((error as Error).message);
        } finally {
            setLoading(false);
        }
    }, [productId]);

    useEffect(() => {
        load();
    }, [load]);

    const submitUpdate = useCallback(async () => {
        if (!productId) return false;
        if (selectedChild === "" || name.trim() === "" || price === "" || stock === "") {
            alert("필수 입력란을 모두 채워주세요.");
            return false;
        }

        const userInfoStr = localStorage.getItem("userInfo");
        if (!userInfoStr) {
            alert("로그인이 필요합니다.");
            return false;
        }
        const token = JSON.parse(userInfoStr).accessToken;

        const data: ProductRequest = {
            name: name.trim(),
            price: Number(price),
            stock: Number(stock),
            product_category_id: Number(selectedChild),
            content: content.trim(),
            isDeleted: false,
        };

        try {
            await updateProduct(Number(productId), data, token);
            alert("수정되었습니다.");
            return true;
        } catch (error) {
            alert((error as Error).message);
            return false;
        }
    }, [productId, name, price, stock, selectedChild]);

    return {
        loading,
        name,
        price,
        stock,
        content,
        parentCategories,
        childCategories,
        selectedParent,
        selectedChild,
        setName,
        setPrice,
        setStock,
        setContent,
        handleParentChange,
        handleChildChange,
        submitUpdate,
    };
};
