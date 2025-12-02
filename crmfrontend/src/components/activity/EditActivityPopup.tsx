import React, { useEffect, useState } from "react";
import { useGlobalStore } from "../../store/globalStore";
import { useAuth } from '../../context/AuthContext';
import '../../styles/Popup.css';
import { Activity } from "../../types/Activity";
import { activityService } from "../../services/activityService";
import DatePicker from "react-datepicker";
import "react-datepicker/dist/react-datepicker.css";

interface EditActivityPopupProps {
  activityOld: Activity;
  open: boolean;
  onClose: () => void;
  onSuccess?: () => void;
}

const EditActivityPopup: React.FC<EditActivityPopupProps> = ({ activityOld, open, onClose, onSuccess }) => {
  const [activityNew, setActivityNew] = useState<Activity | null>(null);
  const [message, setMessage] = useState("");
  const { user } = useAuth();

  useEffect(() => {
    if (open && activityOld?.id) {
      activityService.getById(activityOld.id).then(activity => {
        // Ensure subscribedReminder defaults to false if not set
        setActivityNew({
          ...activity,
          subscribedReminder: activity.subscribedReminder ?? false
        });
      });
    }
  }, [open, activityOld]);

  if (!open || !activityNew) return null;

  const handleSaveActivity = async () => {
    try {
      if (!user) {
        setMessage("No user logged in!");
        return;
      }
      if (!activityNew.type) {
        setMessage("Type is required");
        return;
      }

      const payload = {
        id: activityNew.id,
        leadId: activityNew.leadId,
        customerId: activityNew.customerId,
        ownerId: activityNew.ownerId,
        type: activityNew.type,
        content: activityNew.content,
        outcome: activityNew.outcome,
        nextFollowUpAt: activityNew.nextFollowUpAt,
        sentReminder: activityNew.sentReminder,
        subscribedReminder: activityNew.subscribedReminder
      };

      await activityService.update(activityNew.id, payload);

      setMessage("Edit lead successfully!");
      onSuccess && onSuccess();
      onClose();
    } catch (err: any) {
      setMessage(err.response?.data?.message || "Failed to edit lead!");
    }
  };

  return (
    <div className="overlay">
      <div className="modal">
        <h2>Edit Activity (ID: {activityOld.id})</h2>

        {/* Owner */}
        <div className="field">
          <label>Owner: {user?.username} (ID-{user?.id})</label>
          <label>Lead ID: {activityNew.leadId}</label>
        </div>

        {/* Activity Type */}
        <div className="field">
          <label>Type：</label>
          <div>
            <label>
              <input type="radio" name="type" value="CALL"
                checked={activityNew.type === "CALL"}
                onChange={() => setActivityNew({ ...activityNew, type: "CALL" } as Activity)}
              /> Call
            </label>

            <label>
              <input type="radio" name="type" value="EMAIL"
                checked={activityNew.type === "EMAIL"}
                onChange={() => setActivityNew({ ...activityNew, type: "EMAIL" } as Activity)}
              /> Email
            </label>

            <label>
              <input type="radio" name="type" value="MEETING"
                checked={activityNew.type === "MEETING"}
                onChange={() => setActivityNew({ ...activityNew, type: "MEETING" } as Activity)}
              /> Meeting
            </label>

            <label>
              <input type="radio" name="type" value="NOTE"
                checked={activityNew.type === "NOTE"}
                onChange={() => setActivityNew({ ...activityNew, type: "NOTE" } as Activity)}
              /> Note
            </label>
          </div>
        </div>

        {/* Content */}
        <div className="field">
          <label>Content：</label>
          <input
            type="text"
            value={activityNew.content}
            onChange={e => setActivityNew({ ...activityNew, content: e.target.value })}
            placeholder="test"
            className="input"
          />
        </div>

        {/* Outcome */}
        <div className="field">
          <label>Outcome：</label>
          <input
            type="text"
            value={activityNew.outcome}
            onChange={e => setActivityNew({ ...activityNew, outcome: e.target.value })}
            placeholder="test@gmail.com"
            className="input"
          />
        </div>

        {/* Activity Date */}
        <div className="field">
          <label>Date & Time：</label>
          <DatePicker
            selected={activityNew.nextFollowUpAt ? new Date(activityNew.nextFollowUpAt) : null}
            onChange={(date: Date | null) => setActivityNew({ ...activityNew, nextFollowUpAt: date } as Activity)}
            showTimeSelect
            dateFormat="yyyy-MM-dd HH:mm"
            className="input"
          />
        </div>

        {/* Subscribe reminder */}
        <div className="field">
          <label>Subscribe reminder</label>
          <div className="radio-group">
            <label>
              <input type="radio" name="reminder" value="false"
                checked={activityNew?.subscribedReminder === false}
                onChange={() => setActivityNew({ ...activityNew, subscribedReminder: false } as Activity)}
              /> No
            </label>

            <label>
              <input type="radio" name="reminder" value="true"
                checked={activityNew?.subscribedReminder === true}
                onChange={() => setActivityNew({ ...activityNew, subscribedReminder: true } as Activity)}
              /> Yes
            </label>
          </div>
        </div>

        {/* Response message */}
        {message && <p style={{ color: message.includes("Failed") ? "red" : "green" }}>{message}</p>}

        {/* Action buttons */}
        <div className="actions">
          <button onClick={onClose} className="button">Cancel</button>
          <button onClick={handleSaveActivity} className="buttonPrimary">Save</button>
        </div>
      </div>
    </div>
  );
};

export default EditActivityPopup;