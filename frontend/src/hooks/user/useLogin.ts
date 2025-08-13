import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { login } from "../../services/userService";
import { LoginRequest } from "../../types/user";

export const useLogin = () => {
    const [form, setForm] = useState<LoginRequest>({
        loginId: "",
        password: "",
    });

    const navigate = useNavigate();

    const updateField = (field: keyof LoginRequest, value: string) => {
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    const handleSubmit = async (e : any) => {
        e.preventDefault();

        const result = await login(form);

        if (result.success) {
            alert(result.message);
            navigate("/");
        } else {
            alert("로그인 실패: " + result.message);
        }
    };

    return {
        form,
        updateField,
        handleSubmit,
    };
};