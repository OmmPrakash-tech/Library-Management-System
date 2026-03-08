import React from 'react';
import Card from "@mui/material/Card";
import CardContent from "@mui/material/CardContent";



const StateCard = ({ bgColor, icon, value, title, subtitle, textColor }) => {
  return (
    <Card>
      <CardContent>
        <div className="flex items-center justify-between mb-4">
          <div className={`p-3 rounded-lg ${bgColor}`}>
            {icon}
          </div>
          <span className={`text-3xl font-bold ${textColor}`}>
            {value}
          </span>
        </div>

        <p className="text-gray-700 font-semibold mb-1">{title}</p>
        <p>{subtitle}</p>
      </CardContent>
    </Card>
  );
};

export default StateCard;
