# CRM Frontend

React-based frontend for the CRM system.

## Features

- User authentication
- Customer, Lead, and Activity management
- Analytics dashboard
- Export functionality with WebSocket status updates

## Setup

1. Install dependencies:
```bash
npm install
```

2. Start development server:
```bash
npm start
```

The application will start on `http://localhost:3000`

## Configuration

Update `src/App.js` to change the API base URL if needed:

```javascript
const API_BASE_URL = 'http://localhost:8080/api';
```

## Default Credentials

- **Admin**: username: `admin`, password: `admin123`
- **Users**: username: `user1` to `user10`, password: `admin123`

## Features

- Login/Logout
- View Customers, Leads, and Activities
- Export data (CSV/XLSX/PDF) with real-time status updates via WebSocket
- Analytics dashboard

## Build for Production

```bash
npm run build
```
