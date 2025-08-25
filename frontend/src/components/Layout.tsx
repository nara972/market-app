import React, { useEffect, useState } from "react";
import { useCategories } from "../hooks/product/useCategories";
import { useNavigate } from "react-router-dom";
import {
    Container,
    Navbar,
    Nav,
    Form,
    FormControl,
    Button,
    Dropdown,
} from "react-bootstrap";
import { HiOutlineShoppingCart, HiOutlineHeart } from "react-icons/hi";
import { IoMenu } from "react-icons/io5";
import { ComponentType, ReactNode } from "react";
import "./CategoryNav.css";

const ShoppingCartIcon = HiOutlineShoppingCart as ComponentType<{
    size?: number;
    className?: string;
}>;
const HeartIcon = HiOutlineHeart as ComponentType<{
    size?: number;
    className?: string;
}>;
const MenuIcon = IoMenu as ComponentType<{ size?: number; className?: string }>;

interface LayoutProps {
    children: ReactNode;
}

interface UserInfo {
    username: string;
    role: string;
}

const Layout = ({ children }: LayoutProps) => {
    // 로그인 사용자
    const [user, setUser] = useState<UserInfo | null>(null);

    useEffect(() => {
        const storedUser = localStorage.getItem("userInfo");
        if (storedUser) {
            setUser(JSON.parse(storedUser));
        }
    }, []);

    const handleLogout = () => {
        localStorage.removeItem("userInfo");
        alert("로그아웃 되었습니다.");
        setUser(null);
        window.location.replace("/");
    };

    const { parentCategories, childrenOf, error } = useCategories();
    const [openParentId, setOpenParentId] = useState<number | null>(null);
    const navigate = useNavigate();

    if (error) return <nav className="catnav">오류: {error}</nav>;

    return (
        <>
            <div className="d-flex flex-column min-vh-100">
                {/* 상단 로그인/회원가입 */}
                <Navbar
                    bg="light"
                    variant="light"
                    className="py-1 border-bottom"
                    style={{ fontSize: "0.9rem" }}
                >
                    <Container className="justify-content-end">
                        {user ? (
                            <Dropdown align="end">
                                <Dropdown.Toggle
                                    variant="link"
                                    className="text-dark text-decoration-none p-0"
                                    style={{ fontSize: "0.9rem" }}
                                >
                                    👤 <strong>{user.username}</strong>님
                                </Dropdown.Toggle>

                                <Dropdown.Menu>
                                    {/* 공통 메뉴 */}
                                    <Dropdown.Item href="/orders">주문 관리</Dropdown.Item>
                                    <Dropdown.Item href="/profile">회원 정보 관리</Dropdown.Item>

                                    {/* 관리자 전용 메뉴 */}
                                    {user?.role === "ROLE_ADMIN" && (
                                        <>
                                            <Dropdown.Divider />
                                            <Dropdown.Item href="/product/manage">
                                                상품 관리
                                            </Dropdown.Item>
                                            <Dropdown.Item href="/coupon/manage">
                                                쿠폰 관리
                                            </Dropdown.Item>
                                        </>
                                    )}

                                    <Dropdown.Divider />
                                    <Dropdown.Item onClick={handleLogout}>로그아웃</Dropdown.Item>
                                </Dropdown.Menu>
                            </Dropdown>
                        ) : (
                            <Nav>
                                <Nav.Link href="/login">로그인</Nav.Link>
                                <Nav.Link href="/signup">회원가입</Nav.Link>
                            </Nav>
                        )}
                    </Container>
                </Navbar>

                {/* 로고 + 검색 + 아이콘 */}
                <Navbar bg="white" className="py-3 shadow-sm border-bottom">
                    <Container className="d-flex align-items-center justify-content-between">
                        <Navbar.Brand href="/" className="fs-3" style={{ fontWeight: "700" }}>
                            Market
                        </Navbar.Brand>

                        <Form
                            className="d-flex mx-3 flex-grow-1"
                            style={{ maxWidth: "500px" }}
                            onSubmit={(e) => {
                                e.preventDefault();
                                const input = (e.currentTarget.elements[0] as HTMLInputElement).value;
                                if (input.trim()) {
                                    navigate(`/product/search?keyword=${encodeURIComponent(input)}`);
                                }
                            }}
                        >
                            <FormControl type="search" placeholder="검색어를 입력하세요" className="me-2" />
                            <Button variant="outline-secondary" type="submit">검색</Button>
                        </Form>

                        <div className="d-flex align-items-center">
                            <HeartIcon size={24} className="me-3" />
                            <ShoppingCartIcon size={24} />
                        </div>
                    </Container>
                </Navbar>

                {/* 카테고리/메뉴 */}
                <Navbar bg="white" variant="light" className="py-3 border-bottom">
                    <Container>
                        <Nav className="mx-auto d-flex justify-content-center align-items-center gap-4">
                            <Dropdown>
                                <Dropdown.Toggle
                                    id="category-dropdown"
                                    variant="link"
                                    className="text-dark d-flex align-items-center p-0 border-0 bg-transparent"
                                >
                                    <MenuIcon size={18} className="me-1" /> 전체 카테고리
                                </Dropdown.Toggle>

                                <Dropdown.Menu className="p-2" style={{ minWidth: "260px" }}>

                                        <div
                                            onMouseLeave={() => setOpenParentId(null)}
                                            style={{ maxHeight: 420, overflowY: "auto" }}
                                        >
                                            {parentCategories.map((category) => {
                                                const children = childrenOf(category.id);
                                                const isOpen = openParentId === category.id;

                                                return (
                                                    <div key={category.id} className="mb-1">
                                                        <Dropdown.Item
                                                            className="fw-bold d-flex justify-content-between align-items-center"
                                                            onMouseEnter={() => setOpenParentId(category.id)}
                                                            onClick={() => {

                                                                navigate(`/product/category?id=${category.id}`);
                                                            }}
                                                        >
                                                            <span>{category.name}</span>
                                                            {children.length > 0 && (
                                                                <span className="text-muted" style={{ fontSize: 12 }}>
                                  {isOpen ? "▲" : "▼"}
                                </span>
                                                            )}
                                                        </Dropdown.Item>

                                                        {/* 하위 카테고리 목록 */}
                                                        {isOpen && children.length > 0 && (
                                                            <div className="ps-3">
                                                                {children.map((child) => (
                                                                    <Dropdown.Item
                                                                        key={child.id}
                                                                        onClick={() => {
                                                                            navigate(`/product/category?id=${child.id}`);
                                                                        }}
                                                                        className="text-secondary"
                                                                    >
                                                                        {child.name}
                                                                    </Dropdown.Item>
                                                                ))}
                                                            </div>
                                                        )}
                                                    </div>
                                                );
                                            })}
                                        </div>
                                </Dropdown.Menu>
                            </Dropdown>

                            <Nav.Link href="#new">신상품</Nav.Link>
                            <Nav.Link href="#best">베스트</Nav.Link>
                            <Nav.Link href="#sale">할인</Nav.Link>
                        </Nav>
                    </Container>
                </Navbar>

                <main className="flex-grow-1">{children}</main>

                {/* Footer */}
                <footer className="bg-light text-center py-4 mt-5">
                    <Container>
                        <p>© 2025 MarketNara. All rights reserved.</p>
                    </Container>
                </footer>
            </div>
        </>
    );
};

export default Layout;
