import React, { useEffect, useState } from "react";
import { useGlobalStore } from "../../store/globalStore";
import { useAuth } from '../../context/AuthContext';
import { customerService } from '../../services/customerService';
import { Tag } from "../../types/Tag";
import '../../styles/Popup.css';

interface EditCustomerPopupProps {
  customerId: number;
  open: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

const EditCustomerPopup: React.FC<EditCustomerPopupProps> = ({ customerId, open, onClose, onSuccess }) => {
  const segments = useGlobalStore((s) => s.segments);
  const tags = useGlobalStore((s) => s.tags);
  const [name, setName] = useState<string | undefined>(undefined);
  const [email, setEmail] = useState<string | undefined>(undefined);
  const [selectedSegment, setSelectedSegment] = useState<number | null>(
    segments.length > 0 ? segments[0].id : null
  );
  const [selectedTags, setSelectedTags] = useState<Tag[]>([]);
  const [message, setMessage] = useState("");
  const { user } = useAuth();

  useEffect(() => {
    if (!open || !customerId) return;

    customerService.getById(customerId).then(customer => {
      setName(customer.name);
      setEmail(customer.email);
      setSelectedSegment(customer.segmentId);
      setSelectedTags(customer.tags || []);
    });
  }, [open, customerId]);

  if (!open) return null;

  const handleTagsChange = (tagId: number, tagName: string) => {
    setSelectedTags(prev => {
      if (prev.find(t => t.id === tagId)) {
        return prev.filter(t => t.id !== tagId);
      } 
      return [...prev, { id: tagId, name: tagName }];
    });
  };

  const handleSaveCustomer = async () => {
    try {
      if (!user) {
        setMessage("No user logged in!");
        return;
      }

      if (!name) {
        setMessage("Name is required");
        return;
      }

      const payload = {
        id: customerId,
        name: name,
        email: email,
        ownerId: user.id,
        ownerName: user.username,
        segmentId: selectedSegment,
        tags: selectedTags
      };

      await customerService.update(payload);

      setMessage("Edit customer successfully!");
      onSuccess && onSuccess();
    } catch (err: any) {
      setMessage(err.response?.data?.message || "Failed to add customer!");
    }
  };

  return (
    <div className="overlay">
      <div className="modal">
        <h2>Edit Customer (ID: {customerId})</h2>

        {/* Owner */}
        <div className="field">
          <label>Owner: {user?.username} (ID-{user?.id})</label>
        </div>

        {/* Name */}
        <div className="field">
          <label>Name：</label>
          <input
            type="text"
            value={name ?? ""}
            onChange={e => setName(e.target.value)}
            className="input"
          />
        </div>

        {/* Email */}
        <div className="field">
          <label>Email：</label>
          <input
            type="text"
            value={email ?? ""}
            onChange={e => setEmail(e.target.value)}
            className="input"
          />
        </div>

        {/* Segments & Tags side by side */}
        <div className="section-row">
          {/* Segments */}
          <div className="field">
            <label>Segments：</label>
            <div className="radio-group">
              {segments.map(segment => (
                <label key={segment.id}>
                  <input
                    type="radio"
                    name="segment"
                    value={segment.id}
                    checked={selectedSegment === segment.id}
                    onChange={() => setSelectedSegment(segment.id)}
                  /> {segment.name}
                </label>
              ))}
            </div>
          </div>

          {/* Tags */}
          <div className="field">
            <label>Tags：</label>
            <div className="checkbox-group">
              {tags.map(tag => (
                <label key={tag.id}>
                  <input
                    type="checkbox"
                    value={tag.id}
                    checked={selectedTags.find(t => t.id === tag.id) !== undefined}
                    onChange={() => handleTagsChange(tag.id, tag.name)}
                  />
                  <span>{tag.name}</span>
                </label>
              ))}
            </div>
          </div>
        </div>

        {/* Response message */}
        {message && <p style={{ color: message.includes("Failed") ? "red" : "green" }}>{message}</p>}

        {/* Action buttons */}
        <div className="actions">
          <button onClick={onClose} className="button">Cancel</button>
          <button onClick={handleSaveCustomer} className="buttonPrimary">Save</button>
        </div>
      </div>
    </div>
  );
};

export default EditCustomerPopup;