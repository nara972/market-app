import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { signUp } from "../../services/userService";
import { SignUpRequest } from "../../types/user";

export const useSignUp = () => {
    const [form, setForm] = useState<SignUpRequest>({
        loginId: "",
        password: "",
        username: "",
        address: "",
    });

    const navigate = useNavigate();

    const updateField = (field: keyof SignUpRequest, value: string) => {
        setForm((prev) => ({ ...prev, [field]: value }));
    };

    const handleSubmit = async (e: any) => {
        e.preventDefault();

        const result = await signUp(form);

        if (result.success) {
            alert("회원가입 성공: " + result.message);
            navigate("/login");
        } else {
            alert("회원가입 실패: " + result.message);
        }
    };

    return {
        form,
        updateField,
        handleSubmit,
    };
};