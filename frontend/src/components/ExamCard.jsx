import React from 'react';
import { Link } from 'react-router-dom';

const ExamCard = ({ exam }) => {
  return (
    <div className="card-custom h-100 d-flex flex-column p-4">
      <div className="d-flex justify-content-between align-items-start mb-2">
        <span className="badge bg-light text-primary border fw-semibold">
          {exam.category || 'National Exam'}
        </span>
        <span className="badge bg-secondary-subtle text-secondary small">
          Code: {exam.code}
        </span>
      </div>

      <h5 className="fw-bold text-dark mb-2">{exam.name}</h5>
      <p className="text-muted small mb-4 flex-grow-1" style={{ minHeight: '40px' }}>
        {exam.description || 'Comprehensive exam preparation syllabus and test materials.'}
      </p>

      {/* Stats Grid */}
      <div className="row g-2 text-center py-2 mb-3 bg-light rounded-3 border">
        <div className="col-3">
          <div className="fw-bold text-dark fs-6">{exam.subjectsCount || 0}</div>
          <div className="text-muted" style={{ fontSize: '0.68rem' }}>Subjects</div>
        </div>
        <div className="col-3 border-start">
          <div className="fw-bold text-dark fs-6">{exam.materialsCount || 0}</div>
          <div className="text-muted" style={{ fontSize: '0.68rem' }}>Notes</div>
        </div>
        <div className="col-3 border-start">
          <div className="fw-bold text-dark fs-6">{exam.questionsCount || 0}</div>
          <div className="text-muted" style={{ fontSize: '0.68rem' }}>Questions</div>
        </div>
        <div className="col-3 border-start">
          <div className="fw-bold text-dark fs-6">{exam.mockTestsCount || 0}</div>
          <div className="text-muted" style={{ fontSize: '0.68rem' }}>Mocks</div>
        </div>
      </div>

      {/* Action Buttons */}
      <div className="d-flex gap-2">
        <Link to={`/materials?examId=${exam.id}`} className="btn btn-sm btn-outline-navy flex-fill">
          Notes
        </Link>
        <Link to={`/practice?examId=${exam.id}`} className="btn btn-sm btn-outline-primary flex-fill">
          Practice
        </Link>
        <Link to={`/mock-tests?examId=${exam.id}`} className="btn btn-sm btn-navy flex-fill">
          Mock Tests
        </Link>
      </div>
    </div>
  );
};

export default ExamCard;
