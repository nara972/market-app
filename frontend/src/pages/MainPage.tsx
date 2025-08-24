import React from "react";
import { Container, Row, Col, Card, Button, Carousel } from 'react-bootstrap';
import "bootstrap/dist/css/bootstrap.min.css";
import Layout from '../components/Layout';

const MainPage: React.FC = () => {
    const products = [
        { id: 1, name: '유기농 사과', price: '4,500원' },
        { id: 2, name: '친환경 달걀', price: '3,200원' },
        { id: 3, name: '한우 불고기', price: '15,000원' },
        { id: 4, name: '제철 배추', price: '2,000원' },
    ];

    return (
        <Layout>
            <Carousel className="mt-3">
                <Carousel.Item>
                    <img className="d-block w-100" src="/img/banner1.png" alt="이벤트1" style={{ width: '400px', height: '300px' }} />
                </Carousel.Item>
                <Carousel.Item>
                    <img className="d-block w-100" src="/img/banner2.jpg" alt="이벤트2" style={{ width: '400px', height: '300px' }} />
                </Carousel.Item>
            </Carousel>

            <Container className="mt-5">
                <h4 className="mb-4">지금 인기 있는 상품</h4>
                <Row>
                    {products.map((product) => (
                        <Col key={product.id} sm={6} md={3} className="mb-4">
                            <Card>
                                <Card.Body>
                                    <Card.Title>{product.name}</Card.Title>
                                    <Card.Text>{product.price}</Card.Text>
                                    <Button variant="primary">장바구니</Button>
                                </Card.Body>
                            </Card>
                        </Col>
                    ))}
                </Row>
            </Container>
        </Layout>
    );
};

export default MainPage;