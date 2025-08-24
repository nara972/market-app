// 상품 리스트에서 사용하는 최소 정보
export interface ProductList {
    id: number;
    name: string;
    price: number;
    stock: number;
    categoryName: string;
    isDeleted: boolean;
}

// 상품 상세 조회 응답
export interface ProductResponse {
    id: number;
    name: string;
    price: number;
    stock: number;
    categoryId: number;
    categoryName: string;
    content: string;
    isDeleted: boolean;
}

// 상품 등록 / 수정 요청 DTO
export interface ProductRequest {
    name: string;
    price: number;
    stock: number;
    product_category_id: number;
    content: string;
    isDeleted: boolean;
}