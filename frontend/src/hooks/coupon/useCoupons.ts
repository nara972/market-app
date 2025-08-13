import { useState, useEffect } from "react";
import { Coupon } from "../../types/coupon";
import { getCoupons} from "../../services/couponService";

export const useCoupons = (accessToken: string) => {
    const [coupons, setCoupons] = useState<Coupon[]>([]);
    const [loading, setLoading] = useState(true);

    const fetchCoupons = async () => {
        try {
            const data = await getCoupons(accessToken);
            setCoupons(data);
        } catch (error) {
            alert((error as Error).message);
        } finally {
            setLoading(false);
        }
    };

    useEffect(() => {
        fetchCoupons();
    }, []);

    return { coupons, loading, fetchCoupons };
};
