import { ProductList, ProductRequest, ProductResponse } from "../types/product";

/** 상품 전체 조회 - GET /product/all */
export const getAllProducts = async (): Promise<ProductList[]> => {
    const res = await fetch("/product/all");
    if (!res.ok) throw new Error("상품 목록을 불러오지 못했습니다.");
    return res.json();
};

/** 카테고리별 상품 조회 - GET /product/category/{categoryId} **/
export const getProductsByCategoryId = async (categoryId : number): Promise<ProductList[]> => {
    const res = await fetch(`/product/category/${categoryId}`);
    if (!res.ok) throw new Error("상품 목록을 불러오지 못했습니다.");
    return res.json();
}

/** 상품 상세 조회 - GET /product/detail/{id}  */
export const getProductDetail = async (id: number | string): Promise<ProductResponse> => {
    const res = await fetch(`/product/detail/${id}`);
    if (!res.ok) throw new Error("상품 상세를 불러오지 못했습니다.");
    return res.json();
};

/** 상품 생성 - POST /admin/product/create */
export const createProduct = async (data: ProductRequest, token: string): Promise<void> => {
    const res = await fetch("/admin/product/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(data),
    });
    if (!res.ok) {
        const errText = await res.text();
        throw new Error("등록 실패: " + errText);
    }
};

/** 상품 수정 - PUT /admin/product/update/{id} */
export const updateProduct = async (
    id: number,
    data: ProductRequest,
    token: string
): Promise<void> => {
    const res = await fetch(`/admin/product/update/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(data),
    });
    if (!res.ok) {
        const errText = await res.text();
        throw new Error("수정 실패: " + errText);
    }
};

/** 상품 삭제 - DELETE /admin/product/delete/{id} */
export const deleteProductById = async (productId: number, token: string): Promise<void> => {
    const res = await fetch(`/admin/product/delete/${productId}`, {
        method: "DELETE",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
    });
    if (!res.ok) {
        const errText = await res.text();
        throw new Error("삭제 실패: " + errText);
    }
};