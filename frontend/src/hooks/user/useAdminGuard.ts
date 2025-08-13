import { useEffect, useState } from "react";

export type UserInfo = {
    role: string;
    accessToken: string;
} | null;

export const useAdminGuard = () => {
    const [userInfo, setUserInfo] = useState<UserInfo>(null);

    useEffect(() => {
        const storedUser = localStorage.getItem("userInfo");
        if (storedUser) {
            const parsedUser: UserInfo = JSON.parse(storedUser);
            if (!parsedUser || parsedUser.role !== "ROLE_ADMIN") {
                alert("접근 권한이 없습니다.");
                window.location.href = "/";
            } else {
                setUserInfo(parsedUser);
            }
        } else {
            alert("로그인이 필요합니다.");
            window.location.href = "/login";
        }
    }, []);

    return userInfo;
};