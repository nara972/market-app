import { Nav } from "react-bootstrap";
import { Link, useLocation } from "react-router-dom";

const NAV_TABS = [
    { path: "/product/manage", label: "상품 관리" },
    { path: "/coupon/manage", label: "쿠폰 관리" },
];

export function ManagementTabs() {
    const location = useLocation();
    return (
        <Nav variant="tabs" className="mb-4">
            {NAV_TABS.map(({ path, label }) => (
                <Nav.Item key={path}>
                    <Nav.Link as={Link} to={path} active={location.pathname === path}>
                        {label}
                    </Nav.Link>
                </Nav.Item>
            ))}
        </Nav>
    );
}