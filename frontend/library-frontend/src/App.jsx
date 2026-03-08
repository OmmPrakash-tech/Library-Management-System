import React from "react";
import { useState } from "react";
import "./App.css";

import { Routes, Route } from "react-router-dom";

import Dashboard from "./pages/Dashboard/Dashboard";
import UserLayout from "./pages/UserLayout/UserLayout";

function App() {
  const [count, setCount] = useState(0);

  return (
    <Routes>
      {/* user routes */}
      <Route element={<UserLayout />}>
        <Route path="/" element={<Dashboard />} />
      </Route>
    </Routes>
  );
}

export default App;
