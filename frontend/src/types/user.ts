export interface SignUpRequest {
    loginId: string;
    password: string;
    username: string;
    address: string;
}

export interface ApiResponse {
    success: boolean;
    message: string;
}

export interface LoginRequest {
    loginId: string;
    password: string;
}

export interface LoginResponse {
    accessToken: string;
    refreshToken?: string;
    username: string;
}