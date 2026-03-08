import React from "react";
import { ListItemButton, ListItemIcon, ListItemText, Typography } from "@mui/material";

import {
  Menu as MenuIcon,
  Dashboard as DashboardIcon,
  MenuBook as MenuBookIcon,
  EventNote as EventNoteIcon,
  CardMembership as CardMembershipIcon,
  Favorite as FavoriteIcon,
  Person as PersonIcon,
  Settings as SettingsIcon,
  Logout as LogoutIcon,
  Notifications as NotificationsIcon,
  Search as SearchIcon,
  ChevronLeft as ChevronLeftIcon,
  Receipt as ReceiptIcon,
} from "@mui/icons-material";

/* ================= NAVIGATION ITEM COMPONENT ================= */

const NavigationItem = ({ item }) => {
  return (
    <ListItemButton
      sx={{
        borderRadius: 2,
        mb: 0.8,
        px: 2,
        py: 1,
        color: "white",
        background: item.active
          ? "rgba(99,102,241,0.25)"
          : "transparent",
        transition: "all 0.3s ease",
        "&:hover": {
          background: "rgba(255,255,255,0.08)",
          transform: "translateX(4px)",
        },
      }}
    >
      <ListItemIcon
        sx={{
          color: item.active ? "#a78bfa" : "rgba(255,255,255,0.8)",
          minWidth: 36,
        }}
      >
        {item.icon}
      </ListItemIcon>

      <ListItemText
        primary={
          <Typography sx={{ fontWeight: 600, fontSize: "0.95rem" }}>
            {item.title}
          </Typography>
        }
        secondary={
          item.description && (
            <Typography
              sx={{
                fontSize: "0.72rem",
                color: "rgba(255,255,255,0.6)",
              }}
            >
              {item.description}
            </Typography>
          )
        }
      />
    </ListItemButton>
  );
};

export default NavigationItem;

/* ================= SECONDARY ITEMS ================= */

export const secondaryItems = [
  {
    title: "Profile",
    path: "/profile",
    icon: <PersonIcon />,
  },
  {
    title: "Settings",
    path: "/settings",
    icon: <SettingsIcon />,
  },
];

/* ================= MAIN NAVIGATION ================= */

export const navigationItems = [
  {
    title: "Dashboard",
    path: "/",
    icon: <DashboardIcon />,
    description: "Overview & Stats",
    active: true,
  },
  {
    title: "Browse Books",
    path: "/books",
    icon: <MenuBookIcon />,
    description: "Explore Library",
  },
  {
    title: "My Loans",
    path: "/my-loans",
    icon: <EventNoteIcon />,
    description: "Active & History",
    badge: "loans",
  },
  {
    title: "My Reservations",
    path: "/my-reservations",
    icon: <EventNoteIcon />,
    description: "Active & History",
    badge: "reservations",
  },
  {
    title: "My Fines",
    path: "/my-fines",
    icon: <ReceiptIcon />,
    description: "Pending & Paid",
    badge: "fines",
  },
  {
    title: "Subscriptions",
    path: "/subscriptions",
    icon: <CardMembershipIcon />,
    description: "Manage Plans",
    badge: "subscription",
  },
  {
    title: "Wishlist",
    path: "/wishlist",
    icon: <FavoriteIcon />,
    description: "Saved Books",
  },
];