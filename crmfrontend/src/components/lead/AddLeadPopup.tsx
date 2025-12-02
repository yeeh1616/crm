import { useGlobalStore } from "../../store/globalStore";
import { useAuth } from '../../context/AuthContext';
import { leadService } from '../../services/leadService';
import '../../styles/Popup.css';
import { useState } from "react";

interface AddLeadPopupProps {
  customerId: number;
  open: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

const AddLeadPopup: React.FC<AddLeadPopupProps> = ({ customerId, open, onClose, onSuccess }) => {
  const [contactName, setContactName] = useState("test");
  const [contactEmail, setContactEmail] = useState("test@gmail.com");
  const [contactPhone, setContactPhone] = useState("1234567890");
  const [selectedStage, setSelectedStage] = useState("NEW");
  const [selectedStatus, setSelectedStatus] = useState("ACTIVE");
  const [message, setMessage] = useState("");
  const { user } = useAuth();

  if (!open) return null;

  const handleSaveLead = async () => {
    try {
      if (!user) {
        setMessage("No user logged in!");
        return;
      }

      if (!contactName) {
        setMessage("Contact Name is required");
        return;
      }

      const payload = {
        customerId: customerId,
        contactName: contactName,
        contactEmail: contactEmail,
        contactPhone: contactPhone,
        ownerId: user.id,
        ownerName: user.username,
        stage: selectedStage,
        status: selectedStatus
      };

      await leadService.create(payload);

      setMessage("Add lead successfully!");
      onSuccess && onSuccess();

      setContactName("");
      setContactEmail("");
      setContactPhone("");
      setSelectedStage("New");
      setSelectedStatus("Active");
    } catch (err: any) {
      setMessage(err.response?.data?.message || "Failed to add lead!");
    }
  };

  return (
    <div className="overlay">
      <div className="modal">
        <h2>Create Lead</h2>

        {/* Owner */}
        <div className="field">
          <label>Customer: {user?.username} (ID-{user?.id})</label>
          <label>Owner: {user?.username} (ID-{user?.id})</label>
        </div>

        {/* Contact Name */}
        <div className="field">
          <label>Contact Name：</label>
          <input
            type="text"
            value={contactName}
            onChange={e => setContactName(e.target.value)}
            placeholder="test"
            className="input"
          />
        </div>

        {/* Contact Email */}
        <div className="field">
          <label>Contact Email：</label>
          <input
            type="text"
            value={contactEmail}
            onChange={e => setContactEmail(e.target.value)}
            placeholder="test@gmail.com"
            className="input"
          />
        </div>

        {/* Contact Phone */}
        <div className="field">
          <label>Contact Phone：</label>
          <input
            type="text"
            value={contactPhone}
            onChange={e => setContactPhone(e.target.value)}
            placeholder="test@gmail.com"
            className="input"
          />
        </div>

        {/* Segments & Tags side by side */}
        <div className="section-row">
          {/* Stage */}
          <div className="field">
            <label>Stage：</label>
            <div className="radio-group">
              <label>
                <input type="radio" name="stage" value="NEW"
                  checked={selectedStage === "NEW"}
                  onChange={() => setSelectedStage("NEW")}
                /> New
              </label>

              <label>
                <input type="radio" name="stage" value="CONTACTED"
                  checked={selectedStage === "CONTACTED"}
                  onChange={() => setSelectedStage("CONTACTED")}
                /> Contacted
              </label>

              <label>
                <input type="radio" name="stage" value="QUALIFIED"
                  checked={selectedStage === "QUALIFIED"}
                  onChange={() => setSelectedStage("QUALIFIED")}
                /> Qualified
              </label>

              <label>
                <input type="radio" name="stage" value="PROPOSAL"
                  checked={selectedStage === "PROPOSAL"}
                  onChange={() => setSelectedStage("PROPOSAL")}
                /> Proposal
              </label>

              <label>
                <input type="radio" name="stage" value="WON"
                  checked={selectedStage === "WON"}
                  onChange={() => setSelectedStage("WON")}
                /> Won
              </label>

              <label>
                <input type="radio" name="stage" value="LOST"
                  checked={selectedStage === "LOST"}
                  onChange={() => setSelectedStage("LOST")}
                /> Lost
              </label>
            </div>
          </div>

          {/* Status */}
          <div className="field">
            <label>Status：</label>
            <div className="radio-group">
              <label>
                <input type="radio" name="status" value="ACTIVE"
                  checked={selectedStatus === "ACTIVE"}
                  onChange={() => setSelectedStatus("ACTIVE")}
                /> Active
              </label>

              <label>
                <input type="radio" name="status" value="LOST"
                  checked={selectedStatus === "LOST"}
                  onChange={() => setSelectedStatus("LOST")}
                /> Lost
              </label>

              <label>
                <input type="radio" name="status" value="ARCHIVED"
                  checked={selectedStatus === "ARCHIVED"}
                  onChange={() => setSelectedStatus("ARCHIVED")}
                /> Archived
              </label>
            </div>
          </div>
        </div>

        {/* Response message */}
        {message && <p style={{ color: message.includes("Failed") ? "red" : "green" }}>{message}</p>}

        {/* Action buttons */}
        <div className="actions">
          <button onClick={onClose} className="button">Cancel</button>
          <button onClick={handleSaveLead} className="buttonPrimary">Save</button>
        </div>
      </div>
    </div>
  );
};

export default AddLeadPopup;