import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { materialService } from '../services/api';
import { useAuth } from '../context/AuthContext';

const MaterialCard = ({ material, onBookmarkToggle }) => {
  const { isAuthenticated } = useAuth();
  const [isBookmarked, setIsBookmarked] = useState(material.bookmarked || false);
  const [loading, setLoading] = useState(false);

  const handleBookmark = async (e) => {
    e.preventDefault();
    if (!isAuthenticated) return;
    try {
      setLoading(true);
      const res = await materialService.toggleBookmark(material.id);
      if (res.data?.success) {
        const nextState = res.data.data?.bookmarked ?? !isBookmarked;
        setIsBookmarked(nextState);
        if (onBookmarkToggle) onBookmarkToggle(material.id, nextState);
      }
    } catch (err) {
      console.error('Failed to toggle bookmark', err);
    } finally {
      setLoading(false);
    }
  };

  const getTypeBadge = (type) => {
    switch (type) {
      case 'PDF':
        return <span className="badge bg-danger-subtle text-danger"><i className="bi bi-file-earmark-pdf me-1"></i> PDF Document</span>;
      case 'TEXT_NOTES':
        return <span className="badge bg-primary-subtle text-primary"><i className="bi bi-file-text me-1"></i> Detailed Notes</span>;
      case 'SUMMARY':
        return <span className="badge bg-warning-subtle text-dark"><i className="bi bi-card-checklist me-1"></i> Formula Sheet</span>;
      default:
        return <span className="badge bg-secondary-subtle text-secondary">{type}</span>;
    }
  };

  return (
    <div className="card-custom h-100 d-flex flex-column p-4">
      <div className="d-flex justify-content-between align-items-start mb-2">
        {getTypeBadge(material.materialType)}
        {isAuthenticated && (
          <button
            className={`btn btn-sm ${isBookmarked ? 'text-warning' : 'text-muted'} p-0 border-0`}
            onClick={handleBookmark}
            disabled={loading}
            title={isBookmarked ? 'Remove bookmark' : 'Bookmark this note'}
          >
            <i className={`bi ${isBookmarked ? 'bi-bookmark-star-fill fs-5' : 'bi-bookmark fs-5'}`}></i>
          </button>
        )}
      </div>

      <h5 className="fw-bold text-dark mb-2 mt-1">
        <Link to={`/materials/${material.id}`} className="text-dark text-decoration-none hover-primary">
          {material.title}
        </Link>
      </h5>

      <p className="text-muted small mb-3 flex-grow-1 line-clamp-2">
        {material.description || 'Comprehensive exam reference and study notes.'}
      </p>

      <div className="d-flex flex-wrap gap-1 mb-3">
        <span className="badge-category small">
          <i className="bi bi-bookmark-fill me-1 text-primary"></i>{material.examName}
        </span>
        <span className="badge-category small">
          {material.subjectName}
        </span>
        {material.topicName && (
          <span className="badge-category small text-truncate" style={{ maxWidth: '140px' }}>
            {material.topicName}
          </span>
        )}
      </div>

      <div className="d-flex justify-content-between align-items-center pt-2 border-top">
        <span className="text-muted small">
          {material.completed ? (
            <span className="text-success"><i className="bi bi-check-circle-fill me-1"></i> Completed</span>
          ) : (
            <span className="text-muted"><i className="bi bi-book me-1"></i> Study Resource</span>
          )}
        </span>
        <Link to={`/materials/${material.id}`} className="btn btn-sm btn-outline-navy">
          Read Material <i className="bi bi-arrow-right ms-1"></i>
        </Link>
      </div>
    </div>
  );
};

export default MaterialCard;
