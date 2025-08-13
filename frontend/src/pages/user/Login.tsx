import React from "react";
import {
    Container,
    Button,
    Form,
    Card,
    Row,
    Col,
} from "react-bootstrap";
import Layout from '../../components/Layout';
import { useLogin } from "../../hooks/user/useLogin";  // useLogin 훅 경로에 맞게 수정하세요

const Login = () => {
    const { form, updateField, handleSubmit } = useLogin();

    return (
        <Layout>
            <Container className="py-5">
                <Row className="justify-content-center">
                    <Col md={6}>
                        <Card className="shadow-sm">
                            <Card.Body>
                                <Card.Title className="text-center mb-4 fs-2">
                                    로그인
                                </Card.Title>
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

                                    <Form.Group className="mb-4" controlId="password">
                                        <Form.Label>비밀번호</Form.Label>
                                        <Form.Control
                                            type="password"
                                            value={form.password}
                                            onChange={(e) => updateField("password", e.target.value)}
                                            required
                                        />
                                    </Form.Group>

                                    <div className="d-grid">
                                        <Button variant="outline-secondary" type="submit">
                                            로그인하기
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

export default Login;