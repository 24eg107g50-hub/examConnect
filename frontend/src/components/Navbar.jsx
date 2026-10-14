import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const Navbar = () => {
  const { user, isAuthenticated, isAdmin, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  return (
    <nav className="navbar navbar-expand-lg top-navbar py-2 px-3">
      <div className="container-fluid">
        {/* Brand */}
        <Link className="navbar-brand d-flex align-items-center gap-2" to="/">
          <span className="fs-3 text-primary">
            <i className="bi bi-mortarboard-fill"></i>
          </span>
          <span className="brand-logo-text">
            Exam<span className="text-primary">Connect</span>
          </span>
          <span className="brand-badge d-none d-sm-inline-block">Govt Prep</span>
        </Link>

        {/* Mobile Toggle */}
        <button
          className="navbar-toggler border-0"
          type="button"
          data-bs-toggle="collapse"
          data-bs-target="#navbarMain"
          aria-controls="navbarMain"
          aria-expanded="false"
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        {/* Navbar Links */}
        <div className="collapse navbar-collapse" id="navbarMain">
          <ul className="navbar-nav me-auto mb-2 mb-lg-0 ms-lg-3 gap-1">
            <li className="nav-item">
              <Link className="nav-link text-secondary fw-medium" to="/exams">
                <i className="bi bi-compass me-1"></i> Explore Exams
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link text-secondary fw-medium" to="/materials">
                <i className="bi bi-journal-bookmark me-1"></i> Study Notes
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link text-secondary fw-medium" to="/practice">
                <i className="bi bi-patch-question me-1"></i> Practice
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link text-secondary fw-medium" to="/mock-tests">
                <i className="bi bi-stopwatch me-1"></i> Mock Tests
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link text-secondary fw-medium" to="/doubts">
                <i className="bi bi-chat-square-dots me-1"></i> Doubt Forum
              </Link>
            </li>
            <li className="nav-item">
              <Link className="nav-link text-primary fw-semibold" to="/ai-chat">
                <i className="bi bi-stars me-1"></i> ExamConnect AI
              </Link>
            </li>
          </ul>

          {/* Right Action / Auth */}
          <div className="d-flex align-items-center gap-2">
            {isAuthenticated ? (
              <div className="d-flex align-items-center gap-3">
                {isAdmin ? (
                  <Link to="/admin" className="btn btn-sm btn-outline-danger d-flex align-items-center gap-1">
                    <i className="bi bi-shield-lock-fill"></i> Admin Portal
                  </Link>
                ) : (
                  <Link to="/dashboard" className="btn btn-sm btn-outline-navy d-flex align-items-center gap-1">
                    <i className="bi bi-speedometer2"></i> Dashboard
                  </Link>
                )}

                <div className="dropdown">
                  <button
                    className="btn btn-sm btn-light dropdown-toggle d-flex align-items-center gap-2 border"
                    type="button"
                    data-bs-toggle="dropdown"
                    aria-expanded="false"
                  >
                    <i className="bi bi-person-circle fs-6 text-primary"></i>
                    <span className="fw-medium text-truncate" style={{ maxWidth: '120px' }}>
                      {user?.fullName || 'My Account'}
                    </span>
                    <span className={`badge ${isAdmin ? 'bg-danger' : 'bg-primary'} rounded-pill`} style={{ fontSize: '0.65rem' }}>
                      {isAdmin ? 'ADMIN' : 'STUDENT'}
                    </span>
                  </button>
                  <ul className="dropdown-menu dropdown-menu-end shadow-sm border-0">
                    <li>
                      <Link className="dropdown-item py-2" to="/profile">
                        <i className="bi bi-person me-2 text-muted"></i> My Profile
                      </Link>
                    </li>
                    {!isAdmin && (
                      <>
                        <li>
                          <Link className="dropdown-item py-2" to="/performance">
                            <i className="bi bi-graph-up-arrow me-2 text-muted"></i> Performance Analytics
                          </Link>
                        </li>
                        <li>
                          <Link className="dropdown-item py-2" to="/bookmarks">
                            <i className="bi bi-bookmark-star me-2 text-muted"></i> Saved Bookmarks
                          </Link>
                        </li>
                      </>
                    )}
                    <li><hr className="dropdown-divider" /></li>
                    <li>
                      <button className="dropdown-item py-2 text-danger" onClick={handleLogout}>
                        <i className="bi bi-box-arrow-right me-2"></i> Sign Out
                      </button>
                    </li>
                  </ul>
                </div>
              </div>
            ) : (
              <div className="d-flex align-items-center gap-2">
                <Link to="/login" className="btn btn-sm btn-outline-navy">
                  Sign In
                </Link>
                <Link to="/register" className="btn btn-sm btn-navy">
                  Get Started
                </Link>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
