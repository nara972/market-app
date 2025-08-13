import React from "react";
import { Container, Row, Col, Button, Table, Spinner } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import Layout from "../../components/Layout";
import { ManagementTabs } from "../../components/ManagementTabs";
import { useCoupons } from "../../hooks/coupon/useCoupons";

const CouponManage: React.FC = () => {
    const location = useLocation();

    const userInfoStr = localStorage.getItem("userInfo");
    const userInfo = userInfoStr ? JSON.parse(userInfoStr) : null;
    const accessToken = userInfo?.accessToken || "";

    const { coupons, loading } = useCoupons(accessToken);

    return (
        <Layout>
            <Container className="py-5">
                <Row className="mb-4">
                    <Col className="text-end">
                        <Button variant="primary" href="/coupon/create">
                            + 신규 쿠폰 등록
                        </Button>
                    </Col>
                </Row>

                <ManagementTabs />

                {loading ? (
                    <div className="text-center py-5">
                        <Spinner animation="border" />
                    </div>
                ) : (
                    <Table striped bordered hover responsive>
                        <thead>
                        <tr>
                            <th>#</th>
                            <th>쿠폰명</th>
                            <th>유형</th>
                            <th>유효기간</th>
                            <th>최소 금액</th>
                            <th>할인 금액</th>
                            <th>사용 가능 여부</th>
                            <th>관리</th>
                        </tr>
                        </thead>
                        <tbody>
                        {coupons.map((c) => (
                            <tr key={c.id}>
                                <td>{c.id}</td>
                                <td>{c.name}</td>
                                <td>{c.couponType}</td>
                                <td>{c.expiredDate}</td>
                                <td>{c.minimumMoney.toLocaleString()}원</td>
                                <td>{c.discountPrice.toLocaleString()}원</td>
                                <td>{c.isActive ? "사용 가능" : "비활성"}</td>
                                <td>
                                    <Button variant="warning" size="sm" className="me-2" href={`/coupon/update?id=${c.id}`}>
                                        수정
                                    </Button>
                                    <Button variant="danger" size="sm">
                                        삭제
                                    </Button>
                                </td>
                            </tr>
                        ))}
                        </tbody>
                    </Table>
                )}
            </Container>
        </Layout>
    );
};

export default CouponManage;
