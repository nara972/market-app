import { useState } from "react";
import { createCoupon } from "../../services/couponService";

export const useCouponCreate = (accessToken: string) => {
    const [error, setError] = useState<string | null>(null);
    const [successMsg, setSuccessMsg] = useState<string | null>(null);

    const submitCoupon = async (couponData: Record<string, any>) => {
        setError(null);
        setSuccessMsg(null);
        try {
            await createCoupon(couponData, accessToken);
            setSuccessMsg("쿠폰이 생성되었습니다.");
            setTimeout(() => {
                window.location.href = "/coupon/manage";
            }, 1200);
        } catch (err) {
            setError((err as Error).message);
        }
    };

    return { error, successMsg, submitCoupon };
};
