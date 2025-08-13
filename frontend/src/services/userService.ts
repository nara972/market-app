import { SignUpRequest, ApiResponse, LoginRequest, LoginResponse } from "../types/user";

export const signUp = async (data: SignUpRequest): Promise<ApiResponse> => {
    try {
        const response = await fetch("/user", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        });

        const message = await response.text();
        return {
            success: response.ok,
            message,
        };
    } catch (error: any) {
        return {
            success: false,
            message: error.message || "알 수 없는 오류",
        };
    }
};

export const login = async (data: LoginRequest): Promise<{ success: boolean; message: string; userInfo?: LoginResponse }> => {
    try {
        const response = await fetch("/login", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(data),
        });

        if (response.ok) {
            const userInfo: LoginResponse = await response.json();
            localStorage.setItem("userInfo", JSON.stringify(userInfo));
            return { success: true, message: "로그인 성공하였습니다.", userInfo };
        } else {
            const message = await response.text();
            return { success: false, message };
        }
    } catch (error: any) {
        return { success: false, message: error.message };
    }
};