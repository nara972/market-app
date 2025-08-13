import { Coupon } from "../types/coupon";

/** 전체 조회 - GET /coupon/all */
export const getCoupons = async (accessToken?: string): Promise<Coupon[]> => {
    const headers: Record<string, string> = {};
    if (accessToken) headers.Authorization = `Bearer ${accessToken}`;

    const res = await fetch("/coupon/all", { headers });
    if (!res.ok) throw new Error("쿠폰 목록을 불러오지 못했습니다.");
    return res.json();
};

/** 상세 조회 - GET /coupon/detail/{id} */
export const fetchCouponDetail = async (couponId: number): Promise<Coupon> => {
    const res = await fetch(`/coupon/detail/${couponId}`);
    if (!res.ok) throw new Error("쿠폰 상세를 불러오지 못했습니다.");
    return res.json();
};

/** 생성 - POST /admin/coupon/create */
export const createCoupon = async (coupon: Partial<Coupon>, token: string): Promise<void> => {
    const res = await fetch("/admin/coupon/create", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify(coupon),
    });
    if (!res.ok) {
        const msg = await res.text().catch(() => "");
        throw new Error(msg || "쿠폰 생성에 실패했습니다.");
    }
};

/** 수정 - PUT /admin/coupon/update/{id} */
export const updateCoupon = async (couponId: number, coupon: Coupon, token: string): Promise<boolean> => {
    try {
        const res = await fetch(`/admin/coupon/update/${couponId}`, {
            method: "PUT",
            headers: {
                "Content-Type": "application/json",
                Authorization: `Bearer ${token}`,
            },
            body: JSON.stringify(coupon),
        });
        return res.ok;
    } catch {
        return false;
    }
};
