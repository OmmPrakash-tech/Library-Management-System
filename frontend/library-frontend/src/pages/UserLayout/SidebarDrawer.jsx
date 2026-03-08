import React from "react";
import { Box, Avatar, Typography, List, Divider, Button } from "@mui/material";
import { useLocation } from "react-router-dom";

import {
  Dashboard,
  MenuBook,
  LibraryBooks,
  EventNote,
  Receipt,
  Subscriptions,
  Favorite,
  Person,
  Settings,
  Logout,
} from "@mui/icons-material";

import NavigationItem from "./NavigationItems";

/* ================= NAVIGATION DATA ================= */

const navigationItems = [
  {
    title: "Dashboard",
    path: "/",
    icon: <Dashboard />,
    description: "User Dashboard",
  },
  {
    title: "Browse Books",
    path: "/books",
    icon: <LibraryBooks />,
    description: "Browse Books",
  },
  {
    title: "My Loans",
    path: "/my-loans",
    icon: <EventNote />,
    description: "Your borrowed books",
  },
  {
    title: "My Reservations",
    path: "/my-reservations",
    icon: <EventNote />,
    description: "Reserved books",
  },
  {
    title: "My Fines",
    path: "/my-fines",
    icon: <Receipt />,
    description: "Pending fines",
  },
  {
    title: "Subscriptions",
    path: "/subscriptions",
    icon: <Subscriptions />,
    description: "Membership plans",
  },
  {
    title: "Wishlist",
    path: "/wishlist",
    icon: <Favorite />,
    description: "Saved books",
  },
];

const bottomItems = [
  {
    title: "Profile",
    path: "/profile",
    icon: <Person />,
    description: "User profile",
  },
  {
    title: "Settings",
    path: "/settings",
    icon: <Settings />,
    description: "Account settings",
  },
];

/* ================= COMPONENT ================= */

const SidebarDrawer = () => {
  const location = useLocation();

  /* ACTIVE MENU CHECK */
  const isActive = (path) => {
    if (path === "/") {
      return location.pathname === "/";
    }
    return location.pathname.startsWith(path);
  };

  return (
    <Box
      sx={{
        width: 260,
        height: "100vh",
        display: "flex",
        flexDirection: "column",
        background: "linear-gradient(180deg, #1e293b 0%, #0f172a 100%)",
        color: "white",
        position: "fixed",
        left: 0,
        top: 0,
        overflow: "hidden",
      }}
    >
      {/* HEADER */}
      <Box sx={{ p: 3, display: "flex", alignItems: "center", gap: 2 }}>
        <Avatar
          sx={{
            width: 46,
            height: 46,
            background: "linear-gradient(135deg,#6366f1,#a855f7)",
          }}
        >
          <MenuBook />
        </Avatar>

        <Box>
          <Typography variant="h6" sx={{ fontWeight: 700 }}>
            ZoshBook
          </Typography>
          <Typography
            variant="caption"
            sx={{ opacity: 0.65, letterSpacing: 1.2 }}
          >
            LIBRARY HUB
          </Typography>
        </Box>
      </Box>

      {/* MAIN NAVIGATION */}
      <List sx={{ px: 1.5 }}>
        {navigationItems.map((item) => (
          <NavigationItem
            key={item.title}
            item={{ ...item, active: isActive(item.path) }}
          />
        ))}
      </List>

      <Divider sx={{ my: 2, borderColor: "rgba(255,255,255,0.1)" }} />

      {/* BOTTOM NAVIGATION */}
      <List sx={{ px: 1.5 }}>
        {bottomItems.map((item) => (
          <NavigationItem
            key={item.title}
            item={{ ...item, active: isActive(item.path) }}
          />
        ))}
      </List>

      {/* PUSH LOGOUT */}
      <Box sx={{ flexGrow: 1 }} />

      {/* LOGOUT BUTTON */}
      <Box sx={{ p: 2 }}>
        <Button
          fullWidth
          startIcon={<Logout />}
          sx={{
            color: "#ff9b9b",
            background: "rgba(255,0,0,0.08)",
            borderRadius: 2,
            py: 1.2,
            textTransform: "none",
            fontWeight: 600,
            "&:hover": {
              background: "rgba(255,0,0,0.18)",
            },
          }}
        >
          Logout
        </Button>
      </Box>

      {/* FOOTER */}
      <Typography
        variant="caption"
        sx={{
          textAlign: "center",
          opacity: 0.45,
          pb: 2,
        }}
      >
        © 2026 ZoshBook. All rights reserved.
      </Typography>
    </Box>
  );
};

export default SidebarDrawer;