import React from "react";
import CurrentLoanCard from "./CurrentLoanCard";

const CurrentLoans = () => {
  const loans = [
  {
    bookTitle: "Atomic Habits",
    bookAuthor: "James Clear",
    bookCoverImage:
      "https://images-na.ssl-images-amazon.com/images/I/81ANaVZk5LL.jpg",
    dueDate: "2026-02-15",
  },
  {
    bookTitle: "Deep Work",
    bookAuthor: "Cal Newport",
    bookCoverImage:
      "https://images-na.ssl-images-amazon.com/images/I/71g2ednj0JL.jpg",
    dueDate: "2026-02-18",
  },
];


  return (
    <div className="p-6">
      <h3 className="text-2xl font-bold text-gray-900 mb-6">
        Books You're Currently Reading
      </h3>

      <div className="space-y-4">
        {loans.map((loan, index) => (
          <CurrentLoanCard key={index} loan={loan} />
        ))}
      </div>
    </div>
  );
};

export default CurrentLoans;
