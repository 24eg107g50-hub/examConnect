import React from 'react';
import { NavLink } from 'react-router-dom';

const Sidebar = () => {
  return (
    <aside className="dashboard-sidebar d-none d-md-block">
      <div className="text-uppercase fw-bold text-muted mb-3 ps-3" style={{ fontSize: '0.72rem', letterSpacing: '0.8px' }}>
        Learning Hub
      </div>
      <nav className="nav flex-column mb-4">
        <NavLink to="/dashboard" end className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-speedometer2 fs-5"></i>
          <span>Dashboard</span>
        </NavLink>
        <NavLink to="/exams" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-compass fs-5"></i>
          <span>Explore Exams</span>
        </NavLink>
        <NavLink to="/materials" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-journal-bookmark fs-5"></i>
          <span>Study Materials</span>
        </NavLink>
        <NavLink to="/bookmarks" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-bookmark-star fs-5"></i>
          <span>My Bookmarks</span>
        </NavLink>
      </nav>

      <div className="text-uppercase fw-bold text-muted mb-3 ps-3" style={{ fontSize: '0.72rem', letterSpacing: '0.8px' }}>
        Practice & Evaluation
      </div>
      <nav className="nav flex-column mb-4">
        <NavLink to="/practice" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-patch-question fs-5"></i>
          <span>Practice Questions</span>
        </NavLink>
        <NavLink to="/mock-tests" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-stopwatch fs-5"></i>
          <span>Mock Tests</span>
        </NavLink>
        <NavLink to="/performance" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-graph-up-arrow fs-5"></i>
          <span>Analytics & Trends</span>
        </NavLink>
      </nav>

      <div className="text-uppercase fw-bold text-muted mb-3 ps-3" style={{ fontSize: '0.72rem', letterSpacing: '0.8px' }}>
        Assistance & Community
      </div>
      <nav className="nav flex-column">
        <NavLink to="/doubts" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-chat-square-dots fs-5"></i>
          <span>Doubt Forum</span>
        </NavLink>
        <NavLink to="/ai-chat" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-stars fs-5 text-primary"></i>
          <span className="text-primary fw-semibold">ExamConnect AI</span>
        </NavLink>
        <NavLink to="/profile" className={({ isActive }) => `sidebar-nav-item ${isActive ? 'active' : ''}`}>
          <i className="bi bi-person fs-5"></i>
          <span>My Profile</span>
        </NavLink>
      </nav>
    </aside>
  );
};

export default Sidebar;
