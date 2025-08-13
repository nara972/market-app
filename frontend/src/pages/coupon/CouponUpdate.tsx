import React, { useEffect } from "react";
import { Container, Form, Button, Row, Col, Card, Spinner } from "react-bootstrap";
import Layout from "../../components/Layout";
import { useLocation, useNavigate } from "react-router-dom";
import { useCouponUpdate } from "../../hooks/coupon/useCouponUpdate";

const CouponUpdate: React.FC = () => {
    const location = useLocation();
    const navigate = useNavigate();
    const query = new URLSearchParams(location.search);
    const couponId = Number(query.get("id"));

    const user = localStorage.getItem("userInfo");
    const parsedUser = user ? JSON.parse(user) : null;
    const token = parsedUser?.accessToken;

    // ✅ 훅은 조건 없이 최상단에서 호출
    const { coupon, loading, handleChange, submitUpdate } = useCouponUpdate(couponId, token);

    // ✅ 권한 체크는 effect에서 처리 (조건부 훅 호출 방지)
    useEffect(() => {
        if (!parsedUser || parsedUser.role !== "ROLE_ADMIN") {
            alert("접근 권한이 없습니다.");
            navigate("/");
        }
    }, [parsedUser, navigate]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const success = await submitUpdate();
        if (success) {
            alert("쿠폰이 수정되었습니다.");
            navigate("/coupon/manage");
        } else {
            alert("쿠폰 수정 실패");
        }
    };

    if (!couponId) {
        return (
            <Container className="py-5 text-center">
                <p>잘못된 접근입니다.</p>
            </Container>
        );
    }

    if (loading) {
        return (
            <Container className="py-5 text-center">
                <Spinner animation="border" />
            </Container>
        );
    }

    return (
        <Layout>
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={6}>
                        <Card className="shadow-sm">
                            <Card.Body>
                                <Card.Title className="mb-4 text-center fs-2">쿠폰 수정</Card.Title>
                                <Form onSubmit={handleSubmit}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>쿠폰명</Form.Label>
                                        <Form.Control
                                            type="text"
                                            name="name"
                                            value={coupon.name}
                                            onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleChange(e)}
                                            required
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Label>발급 수량</Form.Label>
                                        <Form.Control
                                            type="number"
                                            name="quantity"
                                            value={coupon.quantity}
                                            onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleChange(e)}
                                            required
                                            min={0}
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Label>유효기간</Form.Label>
                                        <Form.Control
                                            type="text"
                                            name="expiredDate"
                                            value={coupon.expiredDate}
                                            onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleChange(e)}
                                            required
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Label>활성화 여부</Form.Label>
                                        <Form.Select
                                            name="isActive"
                                            value={coupon.isActive ? "true" : "false"}
                                            onChange={(e: React.ChangeEvent<HTMLSelectElement>) => handleChange(e)}
                                            required
                                        >
                                            <option value="true">활성화</option>
                                            <option value="false">비활성화</option>
                                        </Form.Select>
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Label>최소 사용 금액</Form.Label>
                                        <Form.Control
                                            type="number"
                                            name="minimumMoney"
                                            value={coupon.minimumMoney}
                                            onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleChange(e)}
                                            required
                                            min={0}
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-4">
                                        <Form.Label>할인 금액</Form.Label>
                                        <Form.Control
                                            type="number"
                                            name="discountPrice"
                                            value={coupon.discountPrice}
                                            onChange={(e: React.ChangeEvent<HTMLInputElement>) => handleChange(e)}
                                            required
                                            min={0}
                                        />
                                    </Form.Group>

                                    <div className="d-grid">
                                        <Button variant="outline-secondary" type="submit">
                                            수정
                                        </Button>
                                    </div>
                                </Form>
                            </Card.Body>
                        </Card>
                    </Col>
                </Row>
            </Container>
        </Layout>
    );
};

export default CouponUpdate;