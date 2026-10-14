import React from 'react';
import { Link } from 'react-router-dom';

const Footer = () => {
  return (
    <footer className="bg-white border-top py-5 mt-auto">
      <div className="container">
        <div className="row g-4">
          <div className="col-lg-4 col-md-6">
            <div className="d-flex align-items-center gap-2 mb-3">
              <span className="fs-3 text-primary">
                <i className="bi bi-mortarboard-fill"></i>
              </span>
              <span className="brand-logo-text">
                Exam<span className="text-primary">Connect</span>
              </span>
            </div>
            <p className="text-muted small pe-lg-4">
              Your centralized learning platform for government competitive exams. Preparing for UPSC, SSC, Banking, Railways, TSPSC, and APPSC with curated notes, timed mock tests, and AI-powered doubt solving.
            </p>
            <div className="d-flex gap-3 text-muted">
              <span className="badge bg-light text-dark border">
                <i className="bi bi-shield-check text-success me-1"></i> RAG Grounded AI
              </span>
              <span className="badge bg-light text-dark border">
                <i className="bi bi-lightning-charge text-warning me-1"></i> Timed Evaluation
              </span>
            </div>
          </div>

          <div className="col-lg-2 col-md-6 col-6">
            <h6 className="fw-bold text-dark mb-3">Exams</h6>
            <ul className="list-unstyled text-muted small d-flex flex-column gap-2 mb-0">
              <li><Link to="/exams?category=National" className="text-decoration-none text-muted">UPSC Civil Services</Link></li>
              <li><Link to="/exams?category=National" className="text-decoration-none text-muted">SSC CGL & CHSL</Link></li>
              <li><Link to="/exams?category=Banking" className="text-decoration-none text-muted">IBPS & SBI PO</Link></li>
              <li><Link to="/exams?category=Railways" className="text-decoration-none text-muted">RRB NTPC</Link></li>
              <li><Link to="/exams?category=State" className="text-decoration-none text-muted">TSPSC & APPSC</Link></li>
            </ul>
          </div>

          <div className="col-lg-2 col-md-6 col-6">
            <h6 className="fw-bold text-dark mb-3">Preparation</h6>
            <ul className="list-unstyled text-muted small d-flex flex-column gap-2 mb-0">
              <li><Link to="/materials" className="text-decoration-none text-muted">Study Materials</Link></li>
              <li><Link to="/practice" className="text-decoration-none text-muted">Practice Bank</Link></li>
              <li><Link to="/mock-tests" className="text-decoration-none text-muted">Mock Examinations</Link></li>
              <li><Link to="/doubts" className="text-decoration-none text-muted">Discussion Forum</Link></li>
              <li><Link to="/ai-chat" className="text-decoration-none text-muted">AI Assistant</Link></li>
            </ul>
          </div>

          <div className="col-lg-4 col-md-6">
            <h6 className="fw-bold text-dark mb-3">ExamConnect Platform</h6>
            <p className="text-muted small">
              Built as a modern, production-grade learning management platform with Spring Boot 3, Java 21, MySQL 8, and React.
            </p>
            <div className="p-3 rounded-3 bg-light border small text-muted">
              <div className="d-flex align-items-center gap-2 mb-1">
                <i className="bi bi-clock-history text-primary"></i>
                <span className="fw-medium text-dark">Platform Status: Active & Operational</span>
              </div>
              <div>Connected to MySQL 8.0 & Flyway Migrations V1-V2</div>
            </div>
          </div>
        </div>

        <div className="border-top pt-4 mt-4 d-flex flex-column flex-sm-row justify-content-between align-items-center text-muted small">
          <div>
            &copy; {new Date().getFullYear()} ExamConnect. All rights reserved.
          </div>
          <div className="d-flex gap-3 mt-2 mt-sm-0">
            <span className="text-muted">Education For All</span>
            <span>•</span>
            <span className="text-muted">Privacy First</span>
            <span>•</span>
            <span className="text-muted">Secure Evaluation</span>
          </div>
        </div>
      </div>
    </footer>
  );
};

export default Footer;
