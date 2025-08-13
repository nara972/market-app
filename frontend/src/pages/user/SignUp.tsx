import React from "react";
import { Container, Button, Form, Card, Row, Col } from "react-bootstrap";
import Layout from "../../components/Layout";
import { useSignUp } from "../../hooks/user/useSignUp";

const SignUp = () => {
    const { form, updateField, handleSubmit } = useSignUp();

    return (
        <Layout>
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={6}>
                        <Card className="shadow-sm">
                            <Card.Body>
                                <Card.Title className="text-center mb-4 fs-2">회원가입</Card.Title>
                                <Form onSubmit={handleSubmit}>
                                    <Form.Group className="mb-3" controlId="loginId">
                                        <Form.Label>아이디</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={form.loginId}
                                            onChange={(e) => updateField("loginId", e.target.value)}
                                            required
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3" controlId="password">
                                        <Form.Label>비밀번호</Form.Label>
                                        <Form.Control
                                            type="password"
                                            value={form.password}
                                            onChange={(e) => updateField("password", e.target.value)}
                                            required
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-3" controlId="username">
                                        <Form.Label>이름</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={form.username}
                                            onChange={(e) => updateField("username", e.target.value)}
                                            required
                                        />
                                    </Form.Group>

                                    <Form.Group className="mb-4" controlId="address">
                                        <Form.Label>주소</Form.Label>
                                        <Form.Control
                                            type="text"
                                            value={form.address}
                                            onChange={(e) => updateField("address", e.target.value)}
                                            required
                                        />
                                    </Form.Group>

                                    <div className="d-grid">
                                        <Button variant="outline-secondary" type="submit">
                                            가입하기
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

export default SignUp;