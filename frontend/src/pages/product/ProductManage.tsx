import React from "react";
import { Container, Row, Col, Button, Table, Spinner } from "react-bootstrap";
import Layout from "../../components/Layout";
import { ManagementTabs } from "../../components/ManagementTabs";
import { useProducts } from "../../hooks/product/useProducts";
import { useNavigate } from "react-router-dom";

const ProductManage: React.FC = () => {
    const navigate = useNavigate();
    const { products, loading, deleteProduct } = useProducts();

    return (
        <Layout>
            <Container className="py-5">
                <Row className="mb-4">
                    <Col className="text-end">
                        <Button variant="primary" onClick={() => navigate("/product/create")}>
                            + 신규 상품 등록
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
                            <th>상품명</th>
                            <th>가격</th>
                            <th>재고</th>
                            <th>카테고리</th>
                            <th>관리</th>
                        </tr>
                        </thead>
                        <tbody>
                        {products.map((p) => (
                            <tr key={p.id}>
                                <td>{p.id}</td>
                                <td>{p.name}</td>
                                <td>{p.price.toLocaleString()}원</td>
                                <td>{p.stock}</td>
                                <td>{p.categoryName}</td>
                                <td>
                                    <Button
                                        variant="warning"
                                        size="sm"
                                        className="me-2"
                                        onClick={() => navigate(`/product/update?id=${p.id}`)}
                                    >
                                        수정
                                    </Button>
                                    <Button variant="danger" size="sm" onClick={() => deleteProduct(p.id)}>
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

export default ProductManage;