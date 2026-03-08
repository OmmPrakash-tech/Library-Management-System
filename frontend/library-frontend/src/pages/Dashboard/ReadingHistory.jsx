import React from "react";
import CheckCircle from "@mui/icons-material/CheckCircle";

const ReadingHistory = () => {
  const history = [
    {
      bookTitle: "The Alchemist",
      bookAuthor: "Paulo Coelho",
      bookCoverImage:
        "https://images-na.ssl-images-amazon.com/images/I/51Z0nLAfLmL.jpg",
      completedDate: "2026-01-10",
    },
    {
      bookTitle: "Rich Dad Poor Dad",
      bookAuthor: "Robert Kiyosaki",
      bookCoverImage:
        "https://images-na.ssl-images-amazon.com/images/I/81bsw6fnUiL.jpg",
      completedDate: "2025-12-20",
    },
  ];

  return (
    <div className="p-6">
      <h3 className="text-2xl font-bold text-gray-900 mb-6">
        Reading History
      </h3>

      <div className="space-y-4">
        {history.map((book, index) => (
          <div
            key={index}
            className="flex items-center space-x-4 p-6 border border-gray-200 rounded-2xl"
          >
            <img
              src={book.bookCoverImage}
              alt={book.bookTitle}
              className="w-16 h-24 rounded-lg object-cover"
            />

            <div className="flex-1">
              <h4 className="text-lg font-bold text-gray-900">
                {book.bookTitle}
              </h4>
              <p className="text-gray-600 mb-2">
                by {book.bookAuthor}
              </p>

              <div className="flex items-center space-x-2 text-green-600">
                <CheckCircle sx={{ fontSize: 18 }} />
                <span>
                  Completed on{" "}
                  {new Date(book.completedDate).toLocaleDateString()}
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default ReadingHistory;
