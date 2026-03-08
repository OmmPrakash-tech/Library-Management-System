import React from "react";
import { Chip } from "@mui/material";
import EventAvailable from "@mui/icons-material/EventAvailable";
import pragmaticCover from "../../assets/pragmatic.png"; // local image

const Reservation = () => {
  const reservations = [
    {
      bookTitle: "Clean Code",
      bookAuthor: "Robert C. Martin",
      bookCoverImage:
        "https://m.media-amazon.com/images/I/41SH-SvWPxL.jpg",
      status: "READY",
    },
    {
      bookTitle: "The Pragmatic Programmer",
      bookAuthor: "Andrew Hunt",
      bookCoverImage: pragmaticCover, // local image used here
      status: "PENDING",
    },
  ];

  return (
    <div className="p-6">
      <h3 className="text-2xl font-bold text-gray-900 mb-6">
        Your Reservations
      </h3>

      <div className="space-y-4">
        {reservations.map((book, index) => (
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

              <div className="flex items-center space-x-2">
                <EventAvailable sx={{ fontSize: 18 }} />
                <Chip
                  label={book.status}
                  color={book.status === "READY" ? "success" : "warning"}
                  size="small"
                />
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};

export default Reservation;
