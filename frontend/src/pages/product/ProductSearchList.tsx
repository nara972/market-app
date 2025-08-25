import React, { useEffect, useMemo, useState } from "react";
import { useLocation, useNavigate } from "react-router-dom";
import Layout from "../../components/Layout";
import {
    Container, Button, Breadcrumb, Row, Col, Card, ButtonGroup, Alert
} from "react-bootstrap";
import { ProductResponse } from "../../types/product";
import { useCategories } from "../../hooks/product/useCategories";

type SortKey = "RECOMMENDED" | "SALES_DESC" | "PRICE_ASC" | "PRICE_DESC";
const SORT_LABEL: Record<SortKey, string> = {
    RECOMMENDED: "추천순",
    SALES_DESC: "판매량순",
    PRICE_ASC: "낮은가격순",
    PRICE_DESC: "높은가격순"
};

function useQuery() {
    const { search } = useLocation();
    return useMemo(() => new URLSearchParams(search), [search]);
}

export default function ProductSearchList() {
    const query = useQuery();
    const navigate = useNavigate();
    const keyword = query.get("keyword") || "";
    const [rawProducts, setRawProducts] = useState<ProductResponse[]>([]);
    const [sort, setSort] = useState<SortKey>("RECOMMENDED");
    const { categories } = useCategories();

    useEffect(() => {
        if (!keyword) return;
        fetch(`/product/search?keyword=${encodeURIComponent(keyword)}`, { credentials: "include" })
            .then(async (res) => {
                if (!res.ok) throw new Error(`Failed: ${res.status}`);
                const data = await res.json();
                setRawProducts(data ?? []);
            })
            .catch((e) => {
                console.error(e);
                setRawProducts([]);
            });
    }, [keyword]);

    const products = useMemo(() => {
        const arr = [...rawProducts];
        if (sort === "PRICE_ASC") arr.sort((a, b) => a.price - b.price);
        if (sort === "PRICE_DESC") arr.sort((a, b) => b.price - a.price);
        return arr;
    }, [rawProducts, sort]);

    return (
        <Layout>
            <Container className="py-4">
                <Breadcrumb className="mb-3">
                    <Breadcrumb.Item onClick={() => navigate("/")}>홈</Breadcrumb.Item>
                    <Breadcrumb.Item active>검색: "{keyword}"</Breadcrumb.Item>
                </Breadcrumb>

                <div className="d-flex justify-content-between align-items-center mb-3">
                    <h5 className="mb-0">검색 결과 ({rawProducts.length}건)</h5>
                    <ButtonGroup>
                        {(["RECOMMENDED", "SALES_DESC", "PRICE_ASC", "PRICE_DESC"] as SortKey[]).map((key) => (
                            <Button
                                key={key}
                                variant={sort === key ? "dark" : "outline-dark"}
                                size="sm"
                                onClick={() => setSort(key)}
                            >
                                {SORT_LABEL[key]}
                            </Button>
                        ))}
                    </ButtonGroup>
                </div>

                {products.length === 0 ? (
                    <Alert variant="light" className="text-center">
                        검색 결과가 없습니다 😢 <br />
                        다른 검색어를 입력해보세요.
                    </Alert>
                ) : (
                    <Row>
                        {products.map((product) => {
                            const category = categories.find((c) => c.id === product.categoryId);
                            return (
                                <Col key={product.id} sm={6} md={3} className="mb-4">
                                    <Card className="h-100 p-2">
                                        <Card.Body className="d-flex flex-column justify-content-between">
                                            <div>
                                                <Card.Title title={product.name}>
                                                    {product.name}
                                                </Card.Title>
                                                <Card.Text className="fw-bold">
                                                    {Number(product.price).toLocaleString()}원
                                                </Card.Text>
                                                <small className="text-muted">
                                                    {category?.name}
                                                </small>
                                            </div>
                                            <Button
                                                variant="outline-primary"
                                                size="sm"
                                                onClick={() => navigate(`/product/detail?id=${product.id}`)}
                                            >
                                                장바구니
                                            </Button>
                                        </Card.Body>
                                    </Card>
                                </Col>
                            );
                        })}
                    </Row>
                )}
            </Container>
        </Layout>
    );
}