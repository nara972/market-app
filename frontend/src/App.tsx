import React from "react";
import {BrowserRouter as Router, Routes, Route} from "react-router-dom";
import MainPage from "./pages/MainPage";
import Login from "./pages/user/Login";
import SignUpPage from "./pages/user/SignUp";
import ProductList from "./pages/ProductList";
import ProductManage from "./pages/product/ProductManage";
import ProductCreate from "./pages/product/ProductCreate";
import ProductDetail from "./pages/ProductDetail";
import ProductUpdate from "./pages/product/ProductUpdate";
import ProductCategoryList from "./pages/product/ProductCategoryList";
import ProductSearchList from "./pages/product/ProductSearchList";
import CouponManage from "./pages/coupon/CouponManage";
import CouponCreate from "./pages/coupon/CouponCreate";
import CouponUpdate from "./pages/coupon/CouponUpdate";
import OrderCreate from "./pages/OrderCreate";

const App: React.FC = () => {
  return (
      <Router>
        <Routes>
          <Route path="/" element={<MainPage />} />
          <Route path="/login" element={<Login />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/product/manage" element={<ProductManage />} />
          <Route path="/product/list" element={<ProductList />} />
          <Route path="/product/create" element={<ProductCreate />} />
          <Route path="/product/detail" element={<ProductDetail />} />
          <Route path="/product/update" element={<ProductUpdate />} />
          <Route path="/product/category" element={<ProductCategoryList />} />
          <Route path="/product/search" element={<ProductSearchList />} />
          <Route path="/coupon/manage" element={<CouponManage />} />
          <Route path="/coupon/create" element={<CouponCreate />} />
          <Route path="/coupon/update" element={<CouponUpdate />} />
          <Route path="/order/create" element={<OrderCreate />} />
        </Routes>
      </Router>
  );
};

export default App;