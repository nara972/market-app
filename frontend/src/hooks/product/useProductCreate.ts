import { useState, useEffect, useCallback } from "react";
import { Category } from "../../types/category";
import { createProduct as createProductService } from "../../services/productService";
import { getAllCategories } from "../../services/categoryService";

export const useProductCreate = () => {
    const [categories, setCategories] = useState<Category[]>([]);
    const [parentCategories, setParentCategories] = useState<Category[]>([]);
    const [childCategories, setChildCategories] = useState<Category[]>([]);
    const [selectedParent, setSelectedParent] = useState<number | "">("");
    const [selectedChild, setSelectedChild] = useState<number | "">("");

    const [name, setName] = useState("");
    const [price, setPrice] = useState<number | "">("");
    const [stock, setStock] = useState<number | "">("");
    const [content, setContent] =useState<string>("");
    const [isDeleted] = useState<boolean>(false);

    const fetchCategories = useCallback(async () => {
        try {
            const data = await getAllCategories();
            setCategories(data);
            setParentCategories(data.filter((cat) => cat.parentId === null));
        } catch (error) {
            alert((error as Error).message);
        }
    }, []);

    useEffect(() => {
        fetchCategories();
    }, [fetchCategories]);

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

    const createProduct = useCallback(async () => {
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

        const data = {
            name: name.trim(),
            price: Number(price),
            stock: Number(stock),
            product_category_id: Number(selectedChild),
            content: content.trim(),
            isDeleted: false,
        };

        try {
            await createProductService(data, token);
            alert("등록되었습니다.");
            return true;
        } catch (error) {
            alert((error as Error).message);
            return false;
        }
    }, [name, price, stock, selectedChild, content, isDeleted]);

    return {
        categories,
        parentCategories,
        childCategories,
        selectedParent,
        selectedChild,
        name,
        price,
        stock,
        content,
        setName,
        setPrice,
        setStock,
        setContent,
        handleParentChange,
        handleChildChange,
        createProduct,
    };
};
