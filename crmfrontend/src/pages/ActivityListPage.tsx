import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import '../styles/Table.css';
import '../styles/GlobalButton.css';
import { activityService } from '../services/activityService';
import { Activity } from '../types/Activity';
import AddActivityPopup from '../components/activity/AddActivityPopup';
import EditActivityPopup from '../components/activity/EditActivityPopup';
import { useLocation } from "react-router-dom";

const ActivityListPage: React.FC = () => {
  const { leadId } = useParams<{ leadId: string }>();
  const [isAuthenticated, setIsAuthenticated] = useState(true);
  const { user, logout } = useAuth();
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [activities, setActivities] = useState<Activity[]>([]);
  const [selectedActivity, setSelectedActivity] = useState<Activity | null>(null);
  const [addActivityOpen, setAddActivityOpen] = useState(false);
  const [editActivityOpen, setEditActivityOpen] = useState(false);
  const location = useLocation();
  const customerId = location.state?.customerId;
  const navigate = useNavigate();

  useEffect(() => {
    if (token) {
      setIsAuthenticated(true);
      fetchData();
    }
  }, [token]);
  
  const fetchData = async () => {
    try {
      const activitiesData = await activityService.getAllByLeadId(leadId);
      setActivities(activitiesData.content || []);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };
  
  function handleEdit(activity: Activity): void {
    setSelectedActivity(activity);
    setEditActivityOpen(true);
  }

  async function handleDelete(id: number): Promise<void> {
    try {
      await activityService.delete(id, true );
      setActivities(activities.filter(activity => activity.id !== id));
    } catch (err: any) {
      console.log(err.response?.data?.message);
    }
  }
  
  return (
    <div className="app">
      <header className="app-header">
        <h1>CRM System - Activities (Lead ID: {leadId})</h1>
        <div>
          <span>Welcome, {user?.username}</span>
          <button onClick={logout} style={{ marginLeft: '1rem' }}>Logout</button>
          <button onClick={() => navigate(-1)} style={{ marginLeft: '1rem' }}>Back</button>
        </div>
      </header>
      <div className="content">
        <div>
          <div className="export-buttons">
            <button className="add-btn" onClick={() => setAddActivityOpen(true)}>Add Activity</button>
            <AddActivityPopup
              leadId={leadId}
              customerId={customerId}
              open={addActivityOpen}
              onClose={() => setAddActivityOpen(false)}
              onSuccess={() => {
                fetchData();
                setAddActivityOpen(false);
              }}
            />
          </div>
          <table>
            <thead>
              <tr>
                <th>Activity ID</th>
                <th>Lead ID</th>
                <th>Type</th>
                <th>Content</th>
                <th>Outcome</th>
                <th>Reminder time</th>
                <th>Reminder</th>
                <th>Reminder subscription</th>
                <th></th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {activities.map(activity => (
                <tr key={activity.id}>
                  <td>{activity.id}</td>
                  <td>{activity.leadId}</td>
                  <td>{activity.type}</td>
                  <td>{activity.content}</td>
                  <td>{activity.outcome}</td>
                  <td>{activity.nextFollowUpAt}</td>
                  <td>{activity.sentReminder ? 'Already sent' : 'Not sent'}</td>
                  <td>{activity.subscribedReminder ? 'Subscribed' : 'Not subscribed'}</td>
                  <td>
                    <button className="btn-Edit" onClick={() => handleEdit(activity)}>
                      Edit
                    </button>
                  </td>
                  <td>
                    <button className="btn-Delete" onClick={() => handleDelete(activity.id)}>
                      Delete
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <EditActivityPopup
            activityOld={selectedActivity!}
            open={editActivityOpen}
            onClose={() => setEditActivityOpen(false)}
            onSuccess={() => {
              fetchData();
              setEditActivityOpen(false);
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default ActivityListPage;

