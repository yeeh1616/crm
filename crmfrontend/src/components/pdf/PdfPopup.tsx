import React, { useEffect, useState } from "react";
import { pdfService } from "../../services/pdfService";
import { useAuth } from "../../context/AuthContext";
import '../../styles/Popup.css';

interface PdfPopupProps {
  open: boolean;
  onClose: () => void;
}

const PdfPopup: React.FC<PdfPopupProps> = ({ open, onClose }) => {
  const [pdfUrl, setPdfUrl] = useState<string>("");
  const { user } = useAuth();

  useEffect(() => {
    if (!open || !user?.id) return;

    const fetchPdf = async () => {
      try {
        const pdfBytes = await pdfService.getPdfPreview(user.id);
        const blob = new Blob([pdfBytes], { type: "application/pdf" });
        const url = URL.createObjectURL(blob);
        setPdfUrl(url);
      } catch (err) {
        console.error("Failed to load PDF", err);
      }
    };

    fetchPdf();

    return () => {
      if (pdfUrl) {
        URL.revokeObjectURL(pdfUrl);
      }
    };
  }, [open, user?.id]);

  if (!open) return null;

  async function handleDownload(): Promise<void> {
    await pdfService.getPdfDownload(user.id);
  }

  return (
    <div className="overlay">
      <div className="modalWide">
        <iframe src={pdfUrl} width="100%" height="600px" title="PDF Preview" />
        
        {/* Action buttons */}
        <div className="actions">
          <button onClick={onClose} className="button">Cancel</button>
          <button onClick={handleDownload} className="buttonPrimary">Download</button>
        </div>
      </div>
    </div>
  );
};

export default PdfPopup;
