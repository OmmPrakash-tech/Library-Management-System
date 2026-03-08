import React from "react";
import AccessTime from "@mui/icons-material/AccessTime";
import { Chip } from "@mui/material";
import GetStatusChip from "./GetStatusChip";

const CurrentLoanCard = ({ loan }) => {
  // Calculate days
  const today = new Date();
  const due = new Date(loan.dueDate);
  const diffTime = due - today;
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  return (
    <div className="flex items-center justify-between p-6 border border-gray-200 rounded-2xl">
      <div className="flex items-center space-x-4 flex-1">
        <img
          src={loan.bookCoverImage}
          alt={loan.bookTitle}
          className="w-16 h-24 rounded-lg object-cover"
        />

        <div className="flex-1">
          <h4 className="text-lg font-bold text-gray-900 mb-1">
            {loan.bookTitle}
          </h4>

          <p className="text-gray-600 mb-2">
            by {loan.bookAuthor}
          </p>

          <div className="flex items-center space-x-4 text-sm">
            <div className="flex items-center space-x-2 text-gray-600">
              <AccessTime sx={{ fontSize: 16 }} />
              <span>
                Due: {new Date(loan.dueDate).toLocaleDateString()}
              </span>
            </div>

            <GetStatusChip status={loan.status} />

            <Chip
              label={`${
                diffDays >= 0 ? diffDays : Math.abs(diffDays)
              } days ${diffDays >= 0 ? "remaining" : "overdue"}`}
              size="small"
              variant="outlined"
            />
          </div>
        </div>
      </div>
    </div>
  );
};

export default CurrentLoanCard;
