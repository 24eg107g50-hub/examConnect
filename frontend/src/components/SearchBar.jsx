import React from 'react';

const SearchBar = ({ value, onChange, placeholder = 'Search by keywords...', onSearch }) => {
  const handleSubmit = (e) => {
    e.preventDefault();
    if (onSearch) onSearch();
  };

  return (
    <form onSubmit={handleSubmit} className="d-flex align-items-center w-100">
      <div className="input-group">
        <span className="input-group-text bg-white border-end-0 text-muted">
          <i className="bi bi-search"></i>
        </span>
        <input
          type="text"
          className="form-control border-start-0 ps-0 shadow-none"
          placeholder={placeholder}
          value={value}
          onChange={(e) => onChange(e.target.value)}
        />
        {value && (
          <button
            type="button"
            className="btn btn-outline-secondary border-start-0 border-end-0 bg-white"
            onClick={() => onChange('')}
          >
            <i className="bi bi-x-lg small"></i>
          </button>
        )}
        {onSearch && (
          <button className="btn btn-navy" type="submit">
            Search
          </button>
        )}
      </div>
    </form>
  );
};

export default SearchBar;
