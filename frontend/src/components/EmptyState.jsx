import React from 'react';
import { Link } from 'react-router-dom';

const EmptyState = ({
  icon = 'bi-inbox',
  title = 'No records found',
  description = 'There are no items to display at this moment.',
  actionText,
  actionLink,
  onAction,
}) => {
  return (
    <div className="card-custom p-5 text-center my-3">
      <div className="mb-3 text-muted" style={{ fontSize: '3rem' }}>
        <i className={`bi ${icon}`}></i>
      </div>
      <h5 className="fw-bold mb-2">{title}</h5>
      <p className="text-muted small mx-auto mb-4" style={{ maxWidth: '420px' }}>
        {description}
      </p>
      {actionText && (
        <div>
          {actionLink ? (
            <Link to={actionLink} className="btn btn-navy btn-sm">
              {actionText}
            </Link>
          ) : (
            <button className="btn btn-navy btn-sm" onClick={onAction}>
              {actionText}
            </button>
          )}
        </div>
      )}
    </div>
  );
};

export default EmptyState;
