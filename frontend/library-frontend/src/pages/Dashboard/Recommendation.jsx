import React from "react";
import { Button } from "@mui/material";
import startWithWhyCover from "../../assets/startwithwhy.png";
import thinkAndGrowCover from "../../assets/thinkandgrow.png"; // add this line

const Recommendation = () => {
  const recommendations = [
    {
      bookTitle: "Start with Why",
      bookAuthor: "Simon Sinek",
      bookCoverImage: startWithWhyCover,
    },
    {
      bookTitle: "Think and Grow Rich",
      bookAuthor: "Napoleon Hill",
      bookCoverImage: thinkAndGrowCover, // use local image
    },
  ];

  return (
    <div className="p-6">
      <h3 className="text-2xl font-bold text-gray-900 mb-6">
        Recommended for You
      </h3>

      <div className="space-y-4">
        {recommendations.map((book, index) => (
          <div
            key={index}
            className="flex items-center justify-between p-6 border border-gray-200 rounded-2xl"
          >
            <div className="flex items-center space-x-4">
              <img
                src={book.bookCoverImage}
                alt={book.bookTitle}
                className="w-16 h-24 rounded-lg object-cover"
              />

              <div>
                <h4 className="text-lg font-bold text-gray-900">
                  {book.bookTitle}
                </h4>
                <p className="text-gray-600">
                  by {book.bookAuthor}
                </p>
              </div>
            </div>

            <Button variant="contained">
              Borrow
            </Button>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Recommendation;
