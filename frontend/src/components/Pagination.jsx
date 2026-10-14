import React from 'react';

const Pagination = ({ currentPage, totalPages, onPageChange }) => {
  if (totalPages <= 1) return null;

  return (
    <nav className="d-flex justify-content-center my-4" aria-label="Page navigation">
      <ul className="pagination pagination-sm gap-1 mb-0">
        <li className={`page-item ${currentPage === 0 ? 'disabled' : ''}`}>
          <button className="page-link rounded-2" onClick={() => onPageChange(currentPage - 1)}>
            <i className="bi bi-chevron-left"></i> Prev
          </button>
        </li>

        {[...Array(totalPages)].map((_, idx) => {
          // Show at most 7 page buttons
          if (idx === 0 || idx === totalPages - 1 || (idx >= currentPage - 2 && idx <= currentPage + 2)) {
            return (
              <li key={idx} className={`page-item ${currentPage === idx ? 'active' : ''}`}>
                <button
                  className={`page-link rounded-2 ${currentPage === idx ? 'bg-primary border-primary' : ''}`}
                  onClick={() => onPageChange(idx)}
                >
                  {idx + 1}
                </button>
              </li>
            );
          } else if (idx === currentPage - 3 || idx === currentPage + 3) {
            return (
              <li key={idx} className="page-item disabled">
                <span className="page-link border-0">...</span>
              </li>
            );
          }
          return null;
        })}

        <li className={`page-item ${currentPage >= totalPages - 1 ? 'disabled' : ''}`}>
          <button className="page-link rounded-2" onClick={() => onPageChange(currentPage + 1)}>
            Next <i className="bi bi-chevron-right"></i>
          </button>
        </li>
      </ul>
    </nav>
  );
};

export default Pagination;
