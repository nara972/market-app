export interface Coupon {
    id: number;
    name: string;
    couponType: string;
    quantity: number;
    expiredDate: string;
    isActive: boolean;
    minimumMoney: number;
    discountPrice: number;
}