export const getAccessToken = (): string => {
    const userInfo = JSON.parse(localStorage.getItem("userInfo") || "null");
    return userInfo?.accessToken || "";
};