import React, { FormEvent } from "react";
import { Container, Form, Button, Alert, Row, Col, Card } from "react-bootstrap";
import Layout from "../../components/Layout";
import { useCouponCreate } from "../../hooks/coupon/useCouponCreate";
import { useAdminGuard } from "../../hooks/user/useAdminGuard";

const CouponCreate: React.FC = () => {
    const userInfo = useAdminGuard();
    const { error, successMsg, submitCoupon } = useCouponCreate(userInfo?.accessToken || "");

    const handleSubmit = (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const formData = new FormData(e.currentTarget);
        submitCoupon({
            name: formData.get("name") as string,
            couponType: formData.get("couponType") as string,
            quantity: Number(formData.get("quantity")),
            expiredDate: formData.get("expiredDate") as string,
            minimumMoney: Number(formData.get("minimumMoney")),
            discountPrice: Number(formData.get("discountPrice")),
        });
    };

    if (!userInfo) return null;

    return (
        <Layout>
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={6}>
                        <Card className="shadow-sm">
                            <Card.Body>
                                <Card.Title className="mb-4 text-center fs-2">쿠폰 생성</Card.Title>

                                {error && <Alert variant="danger">{error}</Alert>}
                                {successMsg && <Alert variant="success">{successMsg}</Alert>}

                                <Form onSubmit={handleSubmit}>
                                    <Form.Group className="mb-3" controlId="couponName">
                                        <Form.Label>쿠폰명</Form.Label>
                                        <Form.Control type="text" name="name" required />
                                    </Form.Group>

                                    <Form.Group className="mb-3" controlId="couponType">
                                        <Form.Label>쿠폰 타입</Form.Label>
                                        <Form.Select name="couponType" required defaultValue="FIRST_COME_FIRST_SERVE">
                                            <option value="FIRST_COME_FIRST_SERVE">선착순 쿠폰</option>
                                            <option value="AUTO_ISSUE">자동 발급 쿠폰</option>
                                        </Form.Select>
                                    </Form.Group>

                                    <Form.Group className="mb-3" controlId="quantity">
                                        <Form.Label>발급 수량</Form.Label>
                                        <Form.Control type="number" name="quantity" min={1} required />
                                    </Form.Group>

                                    <Form.Group className="mb-3" controlId="expiredDate">
                                        <Form.Label>유효기간</Form.Label>
                                        <Form.Control type="text" name="expiredDate" placeholder="YYYY-MM-DDTHH:mm:ss" required />
                                    </Form.Group>

                                    <Form.Group className="mb-3" controlId="minimumMoney">
                                        <Form.Label>최소 사용 금액</Form.Label>
                                        <Form.Control type="number" name="minimumMoney" min={0} required />
                                    </Form.Group>

                                    <Form.Group className="mb-4" controlId="discountPrice">
                                        <Form.Label>할인 금액</Form.Label>
                                        <Form.Control type="number" name="discountPrice" min={0} required />
                                    </Form.Group>

                                    <div className="d-grid">
                                        <Button variant="outline-secondary" type="submit">
                                            등록
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

export default CouponCreate;
