import {useCallback, useEffect, useMemo, useState}  from "react";
import { Category } from "../../types/category";
import { getAllCategories } from "../../services/categoryService";

export const useCategories = () =>{

    const [categories, setCategories] = useState<Category[]>([]);
    const [parentCategories, setParentCategories] = useState<Category[]>([]);
    const [childCategories, setChildCategories] = useState<Category[]>([]);

    const [selectedParent, setSelectedParent] = useState<number | "">("");
    const [selectedChild, setSelectedChild] = useState<number | "">("");

    const [error, setError] = useState<string | null>(null);

    const fetchCategories = useCallback(async () => {
        setError(null);
        try{
            const data = await getAllCategories();
            setCategories(data);
            setParentCategories(data.filter((c) => c.parentId === null));

            setSelectedParent("");
            setSelectedChild("");
            setChildCategories([]);
        }catch (e: any){
            setError(e?.message ?? "카테고리 로딩 실패");
        }
    }, []);

    useEffect(() => {
        fetchCategories();
    }, [fetchCategories]);

    /** 상위 카테고리 변경 */
    const handleParentChange = useCallback(
        (value: string) => {
            const pid = value === "" ? "" : parseInt(value, 10);
            setSelectedParent(pid);
            setSelectedChild("");

            if (value === "") {
                setChildCategories([]);
                return;
            }
            setChildCategories(categories.filter((cat) => cat.parentId === Number(pid)));
        },
        [categories]
    );

    /** 하위 카테고리 변경 */
    const handleChildChange = useCallback((value: string) => {
        setSelectedChild(value === "" ? "" : parseInt(value, 10));
    }, []);

    /** 특정 parentId의 자식 카테고리 */
    const childrenOf = useCallback(
        (parentId: number) => categories.filter((c) => c.parentId === parentId),
        [categories]
    );

    type CategoryNode = Category & { children: Category[] };
    const tree = useMemo<CategoryNode[]>(() => {

        return parentCategories.map((p) => ({
            ...p,
            children: categories.filter((c) => c.parentId === p.id),
        }));
    }, [parentCategories, categories]);

    return {
        categories,
        parentCategories,
        childCategories,
        tree,

        selectedParent,
        selectedChild,

        fetchCategories,
        handleParentChange,
        handleChildChange,
        childrenOf,

        error,
    };
};



