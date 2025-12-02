import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useParams, useNavigate } from 'react-router-dom';
import '../styles/Table.css';
import '../styles/GlobalButton.css';
import AddLeadPopup from '../components/lead/AddLeadPopup';
import EditLeadPopup from '../components/lead/EditLeadPopup';
import { Lead } from '../types/Lead';
import { leadService } from '../services/leadService';

const LeadListPage: React.FC = () => {
  const { customerId } = useParams<{ customerId: string }>();
  const [isAuthenticated, setIsAuthenticated] = useState(true);
  const { user, logout } = useAuth();
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [leads, setLeads] = useState<Lead[]>([]);
  const [open, setOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [selectedLead, setSelectedLead] = useState<Lead | null>(null);
  const navigate = useNavigate();

  useEffect(() => {
    if (token) {
      setIsAuthenticated(true);
      fetchData();
    }
  }, [token]);
  
  const fetchData = async () => {
    try {
      const leadsData = await leadService.getAllByCustomerId(customerId);
      setLeads(leadsData.content || []);
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  function handleEdit(lead: Lead): void {
    setSelectedLead(lead);
    setEditOpen(true);
  }

  async function handleDelete(id: number): Promise<void> {
    try {
      await leadService.delete(id, true );
      setLeads(leads.filter(lead => lead.id !== id));
    } catch (err: any) {
      console.log(err.response?.data?.message);
    }
  }

  function handleConvertToActivity(id: number, customerId: string | undefined): void {
    try {
      navigate(`/activities/${id}`, { state: { customerId } });
    } catch (err: any) {
      console.log(err.response?.data?.message);
    }
  }
  
  return (
    <div className="app">
      <header className="app-header">
        <h1>CRM System - Leads (Customer ID: {customerId})</h1>
        <div>
          <span>Welcome, {user?.username}</span>
          <button onClick={logout} style={{ marginLeft: '1rem' }}>Logout</button>
          <button onClick={() => navigate(-1)} style={{ marginLeft: '1rem' }}>Back</button>
        </div>
      </header>
      <div className="content">
        <div>
          <div className="export-buttons">
            <button className="add-btn" onClick={() => setOpen(true)}>Add Lead</button>
            <AddLeadPopup
              customerId={customerId}
              open={open}
              onClose={() => setOpen(false)}
              onSuccess={() => {
                fetchData();
                setEditOpen(false);
              }}
            />
          </div>
          <table>
            <thead>
              <tr>
                <th>Lead ID</th>
                <th>Customer ID</th>
                <th>Customer Name</th>
                <th>Contact Name</th>
                <th>Contact Email</th>
                <th>Contact Phone</th>
                <th>Stage</th>
                <th>Status</th>
                <th></th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {leads.map(lead => (
                <tr key={lead.id}>
                  <td>{lead.id}</td>
                  <td>{lead.customerId}</td>
                  <td>{lead.customerName}</td>
                  <td>{lead.contactName}</td>
                  <td>{lead.contactEmail}</td>
                  <td>{lead.contactPhone}</td>
                  <td>{lead.stage}</td>
                  <td>{lead.status}</td>
                  <td>
                    <button  className="btn-Edit" onClick={() => handleEdit(lead)}>
                      Edit
                    </button>
                  </td>
                  <td>
                    <button className="btn-Delete" onClick={() => handleDelete(lead.id)}>
                      Delete
                    </button>
                  </td>
                  <td>
                    <button className="btn-Goto" onClick={() => handleConvertToActivity(lead.id, lead.customerId)}>
                      Activity
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <EditLeadPopup
            lead={selectedLead!}
            open={editOpen}
            onClose={() => setEditOpen(false)}
            onSuccess={() => {
              fetchData();
              setEditOpen(false);
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default LeadListPage;

