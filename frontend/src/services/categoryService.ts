import { Category } from "../types/category";

/** 카테고리 전체 - GET /product/categories */
export const getAllCategories = async (): Promise<Category[]> => {
    const res = await fetch("/product/categories");
    if (!res.ok) throw new Error("카테고리를 불러오지 못했습니다.");
    return res.json();
};