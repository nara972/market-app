import { useEffect, useState, ChangeEvent } from "react";
import { Coupon } from "../../types/coupon";
import { fetchCouponDetail, updateCoupon } from "../../services/couponService";

export function useCouponUpdate(couponId: number, token: string | undefined) {
    const [coupon, setCoupon] = useState<Coupon>({
        id: 0,
        name: "",
        couponType: "",
        quantity: 0,
        expiredDate: "",
        isActive: true,
        minimumMoney: 0,
        discountPrice: 0,
    });
    const [loading, setLoading] = useState(true);

    useEffect(() => {
        (async () => {
            if (!couponId) return;
            try {
                const data = await fetchCouponDetail(couponId);
                setCoupon(data);
            } catch (e) {
                alert((e as Error).message);
            } finally {
                setLoading(false);
            }
        })();
    }, [couponId]);

    const handleChange = (e: ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
        const { name, value } = e.target;
        setCoupon((prev) => ({
            ...prev,
            [name]:
                name === "isActive"
                    ? value === "true"
                    : name === "quantity" || name === "minimumMoney" || name === "discountPrice"
                        ? Number(value)
                        : value,
        }));
    };

    const submitUpdate = async () => {
        if (!couponId || !token) return false;
        return await updateCoupon(couponId, coupon, token);
    };

    return { coupon, loading, handleChange, submitUpdate };
}
