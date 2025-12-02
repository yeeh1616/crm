import React, { useEffect, useState, useRef } from "react";
import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from "recharts";
import { ConversionRate } from "../../types/ConversionRate";
import { customerService } from '../../services/customerService';
import '../../styles/StatsPopup.css';

interface StatsPopupProps {
  open: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

const StatsPopup: React.FC<StatsPopupProps> = ({ open, onClose, onSuccess }) => {
  const [data, setData] = useState<ConversionRate[]>([]);
  const [containerWidth, setContainerWidth] = useState<number>(800);
  const modalRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    if (open) {
      customerService.getConversionRate().then((response) => {
        setData(response);
      }).catch((error) => {
        console.error("Error fetching conversion rate data:", error);
      });
    }
  }, [open, onSuccess]);

  useEffect(() => {
    if (open && modalRef.current) {
      const updateWidth = () => {
        if (modalRef.current) {
          const width = modalRef.current.offsetWidth;
          if (width > 0) {
            setContainerWidth(width - 40); // Subtract padding
          }
        }
      };
      updateWidth();
      window.addEventListener('resize', updateWidth);
      return () => window.removeEventListener('resize', updateWidth);
    }
  }, [open, data]);

  if (!open) return null;

  // Prepare chart data for overall comparison
  const overallChartData = data.map(item => ({
    name: item.source || "Unknown",
    totalLeads: item.totalLeads || 0,
    convertedLeads: item.convertedLeads || 0,
    conversionRate: item.conversionRate || 0
  }));

  // Colors for different sources
  const colors = ['#8884d8', '#82ca9d', '#ffc658', '#ff7300', '#00ff00', '#0088fe'];

  return (
    <div className="statsOverlay">
      <div ref={modalRef} className="statsModalWide">
        <h2>Conversion Rate Statistics</h2>

        {/* Overall comparison chart */}
        {data.length > 0 && containerWidth > 0 && (
          <div className="statsOverallChart">
            <h3 className="statsOverallChartTitle">Overall Comparison</h3>
            <ResponsiveContainer width="100%" height={280}>
              <BarChart data={overallChartData}>
                <XAxis dataKey="name" />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar dataKey="totalLeads" fill="#8884d8" name="Total Leads" />
                <Bar dataKey="convertedLeads" fill="#82ca9d" name="Converted Leads" />
              </BarChart>
            </ResponsiveContainer>
          </div>
        )}

        {/* Individual charts for each ConversionRate */}
        {data.map((item, index) => {
          const chartData = [
            {
              name: item.source || "Unknown",
              totalLeads: item.totalLeads || 0,
              convertedLeads: item.convertedLeads || 0
            }
          ];

          return (
            <div key={index} className="statsChartContainer">
              <h3 className="statsChartTitle">{item.source || "Unknown Source"}</h3>
              {containerWidth > 0 && (
                <div className="statsIndividualChart">
                  <ResponsiveContainer width="100%" height={230}>
                    <BarChart data={chartData}>
                      <XAxis dataKey="name" />
                      <YAxis />
                      <Tooltip />
                      <Legend />
                      <Bar dataKey="totalLeads" fill={colors[index % colors.length]} name="Total Leads" />
                      <Bar dataKey="convertedLeads" fill={colors[(index + 1) % colors.length]} name="Converted Leads" />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              )}
              <div className="statsDetails">
                <p><strong>Source:</strong> {item.source || "N/A"}</p>
                <p><strong>Total Leads:</strong> {item.totalLeads || 0}</p>
                <p><strong>Converted Leads:</strong> {item.convertedLeads || 0}</p>
                <p><strong>Conversion Rate:</strong> {item.conversionRate ? `${item.conversionRate.toFixed(2)}%` : "0%"}</p>
                {/* <p><strong>Average Value:</strong> {item.averageValue ? item.averageValue.toFixed(2) : "N/A"}</p> */}
              </div>
            </div>
          );
        })}

        {data.length === 0 && (
          <p className="statsNoData">No conversion rate data available</p>
        )}

        <div className="statsButtonContainer">
          <button onClick={onClose} className="statsButton">Close</button>
        </div>
      </div>
    </div>
  );
};

export default StatsPopup;