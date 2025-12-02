import { create } from "zustand";
import React, { useState, useEffect } from 'react';
import { useAuth } from '../context/AuthContext';
import { useNavigate } from 'react-router-dom';
import '../styles/Table.css';
import '../styles/Segment.css';
import '../styles/Tags.css';
import '../styles/GlobalButton.css';
import AddCustomerPopup from '../components/customer/AddCustomerPopup';
import { useGlobalStore } from "../store/globalStore";
import { customerService } from '../services/customerService';
import { segmentService } from '../services/segmentService';
import { tagService } from '../services/tagService';
import EditCustomerPopup from "../components/customer/EditCustomerPopup";
import StatsPopup from "../components/stats/StatsPopup";
import PdfPopup from "../components/pdf/PdfPopup";
import Pagination from "../components/pagination/Pagination";

const CustomerListPage: React.FC = () => {
  const [isAuthenticated, setIsAuthenticated] = useState(true);
  const { user, logout } = useAuth();
  const [token, setToken] = useState(localStorage.getItem('token'));
  const [customers, setCustomers] = useState([]);
  const [selectedSegment, setSelectedSegment] = useState("");
  const [selectedTags, setSelectedTags] = useState([]);
  const [open, setOpen] = useState(false);
  const navigate = useNavigate();

  const segments = useGlobalStore((s) => s.segments);
  const setSegments = useGlobalStore((s) => s.setSegments);

  const tags = useGlobalStore((s) => s.tags);
  const setTags = useGlobalStore((s) => s.setTags);

  const [pdfPreviewOpen, setPdfPreviewOpen] = useState(false);
  const [statsOpen, setStatsOpen] = useState(false);
  const [editOpen, setEditOpen] = useState(false);
  const [selectedCustomerId, setSelectedCustomerId] = useState<number | null>(null);

  const [currentPage, setCurrentPage] = useState(1);
  const [pageSize, setPageSize] = useState(10);
  const [totalItems, setTotalItems] = useState(10);

  useEffect(() => {
    if (token) {
      setIsAuthenticated(true);
      fetchData();
    }
  }, [token]);

  useEffect(() => {
    handleSearch();
  }, [currentPage, pageSize]); 

  const fetchData = async () => {
    try {     
      const customersData = await customerService.getAll(currentPage - 1, pageSize);
      const totalItemsData = await customerService.getTotalItems(user?.role === 'ADMIN' ? undefined : user?.id, selectedSegment !== "0" ? Number(selectedSegment) : undefined, selectedTags.length > 0 ? selectedTags : undefined);
      const segmentsData = await segmentService.getAll();
      const tagsData = await tagService.getAll();
      
      setCustomers(customersData.content || []);
      setTotalItems(totalItemsData);
      setSegments(segmentsData);
      setTags(tagsData);

    } catch (error) {
      console.error('Error fetching data:', error);
    }
  };

  const handleChangeSegment = (e) => {
    setSelectedSegment(e.target.value);
  };

  async function handleSearch(): Promise<void> {
    try {
      const customersData = await customerService.getAll(currentPage - 1, pageSize, 'id', 'asc', undefined, selectedSegment !== "0" ? Number(selectedSegment) : undefined, selectedTags.length > 0 ? selectedTags : undefined);
      const totalItemsData = await customerService.getTotalItems(user?.role === 'ADMIN' ? undefined : user?.id, selectedSegment !== "0" ? Number(selectedSegment) : undefined, selectedTags.length > 0 ? selectedTags : undefined);
      
      setCustomers(customersData.content || []);
      setTotalItems(totalItemsData);

    } catch (error) {
      console.error('Error fetching data:', error);
    }
  }

  const handleChangeTags = (e) => {
    const value = e.target.value;
    setSelectedTags(prev => 
      e.target.checked 
        ? [...prev, value]
        : prev.filter(id => id !== value)
    );
  };

  function handleStats(): void {
    setStatsOpen(true);
  }

  function handleEdit(id: number): void {
    setSelectedCustomerId(id);
    setEditOpen(true);
  }

  async function handleDelete(id: any): Promise<void> {
    try {
      await customerService.delete(id, true );
      setCustomers(customers.filter(customer => customer.id !== id));
    } catch (err: any) {
      console.log(err.response?.data?.message);
    }
  }

  function handleConvertToLead(id: any): void {
    try {
      navigate(`/leads/${id}`);
    } catch (err: any) {
      console.log(err.response?.data?.message);
    }
  }

  function handleExport(): void {
    setPdfPreviewOpen(true);
  }

  return (
    <div className="app">
      <header className="app-header">
        <h1>CRM System - Customers</h1>
        <div>
          <span>Welcome, {user?.username}</span>
          <button onClick={logout} style={{ marginLeft: '1rem' }}>Logout</button>
        </div>
      </header>
      
      <div className="content">
        <div>
          <div className="segment-area">
            <span>Segments:</span>
            <label className="segment-button">
              <input type="radio" name="segment" value="0" defaultChecked onChange={handleChangeSegment} />
              None
            </label>
            {segments.map(segment => (
              <label key={segment.id} className="segment-button">
                <input type="radio" name="segment" value={segment.id} onChange={handleChangeSegment}/>
                {segment.name}
              </label>
            ))}
          </div>

          <div className="tags-area">
            <span>Tags:</span>
            {tags.map(tag => (
              <label key={tag.id} className="tag-button">
              <input type="checkbox" value={tag.id} onChange={handleChangeTags} />
              <span>{tag.name}</span>
            </label>
            ))}
          </div>
          <div>Selected Tags JSON: {JSON.stringify(selectedTags)}</div>

          <div className="export-buttons">
            <button className="search-btn" onClick={handleSearch}>Search</button>
            <button className="stats-btn" onClick={handleStats}>Stats</button>
            <button className="pdf-btn" onClick={handleExport}>PDF</button>
            <button className="add-btn" onClick={() => setOpen(true)}>Add Customer</button>
            <AddCustomerPopup
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
                <th>ID</th>
                <th>Name</th>
                <th>Email</th>
                <th>Segment</th>
                <th>Activity Score</th>
                <th></th>
                <th></th>
                <th></th>
              </tr>
            </thead>
            <tbody>
              {customers.map(customer => (
                <tr key={customer.id}>
                  <td>{customer.id}</td>
                  <td>{customer.name}</td>
                  <td>{customer.email}</td>
                  <td>{customer.segmentName}</td>
                  <td>{customer.activityScore}</td>
                  <td>
                    <button className="btn-Edit" onClick={() => handleEdit(customer.id)}>
                      Edit
                    </button>
                  </td>
                  <td>
                    <button className="btn-Delete" onClick={() => handleDelete(customer.id)}>
                      Delete
                    </button>
                  </td>
                  <td>
                    <button className="btn-Goto" onClick={() => handleConvertToLead(customer.id)}>
                      Lead
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
          <EditCustomerPopup
            customerId={selectedCustomerId!}
            open={editOpen}
            onClose={() => setEditOpen(false)}
            onSuccess={() => {
              fetchData();
              setEditOpen(false);
            }}
          />
          <StatsPopup
            open={statsOpen}
            onClose={() => setStatsOpen(false)}
            onSuccess={() => {
              fetchData();
              setStatsOpen(false);
            }}
          />
          <PdfPopup
            open={pdfPreviewOpen}
            onClose={() => setPdfPreviewOpen(false)}
          />
        </div>
        <div>
          <Pagination
            currentPage={currentPage}
            totalItems={totalItems}
            pageSize={pageSize}
            onPageChange={(page) => setCurrentPage(page)}
            onPageSizeChange={(size) => {
              setPageSize(size);
              setCurrentPage(1);
            }}
          />
        </div>
      </div>
    </div>
  );
};

export default CustomerListPage;

