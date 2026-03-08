import React, { useState } from "react";
import StateCard from "./StateCard";
import { statsConfig } from "./StateConfig";
import { AutoAwesome } from "@mui/icons-material";
import LinearProgress from "@mui/material/LinearProgress";
import Box from "@mui/material/Box";
import Tabs from "@mui/material/Tabs";
import Tab from "@mui/material/Tab";
import CurrentLoan from "./CurrentLoan";
import Reservation from "./Reservation";
import ReadingHistory from "./ReadingHistory";
import Recommendation from "./Recommendation";

const Dashboard = () => {
  const [tabValue, setTabValue] = useState(0);

  const stateData = statsConfig({
    myLoans: [1, 2, 3],
    reservations: [1, 2],
    stats: { readingStreak: 5 },
  });

  return (
    <div className="min-h-screen bg-gradient-to-r from-gray-50 via-white to-purple-100 py-8">
      
      {/* MAIN CONTAINER */}
      <div className="w-full px-6 md:px-10 lg:px-12">

        {/* HEADER */}
        <div className="mb-8">
          <h1 className="text-5xl font-bold text-gray-900 mb-2">
            My{" "}
            <span className="bg-gradient-to-r from-indigo-600 to-purple-600 bg-clip-text text-transparent">
              Dashboard
            </span>
          </h1>

          <p className="text-lg text-gray-600">
            Track your reading journey and manage your library
          </p>
        </div>

        {/* STATE CARDS */}
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-5 mb-8">
          {stateData.map((item) => (
            <StateCard key={item.id} {...item} />
          ))}
        </div>

        {/* READING GOAL CARD */}
        <div className="bg-white rounded-2xl shadow-md p-6 md:p-8 mb-8">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-xl font-bold text-gray-900 mb-1">
                2026 Reading Goal
              </h3>
              <p className="text-gray-600">25 of 30 books read</p>
            </div>

            <div className="p-3 bg-gradient-to-br from-indigo-100 to-purple-100 rounded-full">
              <AutoAwesome sx={{ fontSize: 30, color: "#4F46E5" }} />
            </div>
          </div>

          <LinearProgress
            variant="determinate"
            value={(25 / 30) * 100}
            sx={{
              height: 10,
              borderRadius: 5,
              backgroundColor: "#e5e7eb",
              "& .MuiLinearProgress-bar": {
                backgroundColor: "#6D28D9",
              },
            }}
          />

          <p className="text-sm text-gray-600 mt-2">70%</p>
        </div>

        {/* TABS SECTION */}
        <div className="bg-white rounded-2xl shadow-md overflow-hidden">
          <Box sx={{ borderBottom: 1, borderColor: "divider" }}>
            <Tabs
              value={tabValue}
              onChange={(e, newValue) => setTabValue(newValue)}
              textColor="primary"
              indicatorColor="primary"
              aria-label="dashboard tabs"
            >
              <Tab label="Current Loans" />
              <Tab label="Reservations" />
              <Tab label="Reading History" />
              <Tab label="Recommendations" />
            </Tabs>
          </Box>

          <div className="p-6">
            {tabValue === 0 && <CurrentLoan />}
            {tabValue === 1 && <Reservation />}
            {tabValue === 2 && <ReadingHistory />}
            {tabValue === 3 && <Recommendation />}
          </div>
        </div>

      </div>
    </div>
  );
};

export default Dashboard;