import React from "react";
import { Container, Form, Button, Row, Col, Card } from "react-bootstrap";
import { useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import { useProductCreate } from "../../hooks/product/useProductCreate";

const ProductCreate: React.FC = () => {
    const navigate = useNavigate();
    const {
        parentCategories,
        childCategories,
        selectedParent,
        selectedChild,
        name,
        price,
        stock,
        content,
        setName,
        setPrice,
        setStock,
        setContent,
        handleParentChange,
        handleChildChange,
        createProduct,
    } = useProductCreate();

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        const ok = await createProduct();
        if (ok) navigate("/product/manage");
    };

    const contentLines = content
        .split("\n")
        .map((v) => v.trim())
        .filter((v) => v.length > 0);

    return (
        <Layout>
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={6}>
                        <Card className="shadow-sm">
                            <Card.Body>
                                <Card.Title className="mb-4 text-center fs-2">상품 등록</Card.Title>
                                <Form onSubmit={handleSubmit}>
                                    <Form.Group className="mb-3">
                                        <Form.Label>상품명</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={name}
                                            onChange={(e) => setName(e.target.value)}
                                            required
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Label>가격</Form.Label>
                                        <Form.Control
                                            type="number"
                                            value={price}
                                            onChange={(e) => setPrice(e.target.value === "" ? "" : Number(e.target.value))}
                                            required
                                            min={0}
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Label>재고</Form.Label>
                                        <Form.Control
                                            type="number"
                                            value={stock}
                                            onChange={(e) => setStock(e.target.value === "" ? "" : Number(e.target.value))}
                                            required
                                            min={0}
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3">
                                        <Form.Label>상위 카테고리</Form.Label>
                                        <Form.Select
                                            value={selectedParent}
                                            onChange={(e) => handleParentChange(e.target.value)}
                                            required
                                        >
                                            <option value="">선택</option>
                                            {parentCategories.map((c) => (
                                                <option key={c.id} value={c.id}>
                                                    {c.name}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>

                                    <Form.Group className="mb-4">
                                        <Form.Label>하위 카테고리</Form.Label>
                                        <Form.Select
                                            value={selectedChild}
                                            onChange={(e) => handleChildChange(e.target.value)}
                                            required
                                            disabled={childCategories.length === 0}
                                        >
                                            <option value="">선택</option>
                                            {childCategories.map((c) => (
                                                <option key={c.id} value={c.id}>
                                                    {c.name}
                                                </option>
                                            ))}
                                        </Form.Select>
                                    </Form.Group>

                                    <Form.Group className="mb-2">
                                        <Form.Label>내용</Form.Label>
                                        <Form.Control
                                            as="textarea"
                                            rows={6}
                                            placeholder={"한 줄에 하나씩 입력하면\n아래에 목록(ul)로 미리보기가 표시됩니다."}
                                            value={content}
                                            onChange={(e) => setContent(e.target.value)}
                                        />
                                    </Form.Group>

                                    <div className="mb-4">
                                        <div className="small text-muted mb-1">내용 미리보기</div>
                                        {contentLines.length === 0 ? (
                                            <div className="text-muted">입력된 내용이 없습니다.</div>
                                        ) : (
                                            <ul className="mb-0">
                                                {contentLines.map((line, idx) => <li key={idx}>{line}</li>)}
                                            </ul>
                                        )}
                                    </div>

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

export default ProductCreate;
