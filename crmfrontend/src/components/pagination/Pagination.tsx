import React from "react";
import { ChevronLeft, ChevronRight } from "lucide-react";
import '../../styles/Pagination.css';

export default function Pagination({
  currentPage,
  totalItems,
  pageSize,
  onPageChange,
  onPageSizeChange
}) {
  const totalPages = Math.ceil(totalItems / pageSize);

  const getPages = () => {
    const pages: (number | string)[] = [];

    if (totalPages <= 7) {
      for (let i = 1; i <= totalPages; i++) pages.push(i);
    } else {
      pages.push(1);
      const left = currentPage - 1;
      const right = currentPage + 1;

      if (left > 2) pages.push("...");

      for (let i = Math.max(2, left); i <= Math.min(totalPages - 1, right); i++) {
        pages.push(i);
      }

      if (right < totalPages - 1) pages.push("...");

      pages.push(totalPages);
    }

    return pages;
  };

  const pages = getPages();

  return (
    <div className="pagination-container">
      {/* page size selector */}
      <select
        className="page-size-selector"
        value={pageSize}
        onChange={(e) => onPageSizeChange(Number(e.target.value))}
      >
        {[10, 20, 50].map((size) => (
          <option key={size} value={size}>
            {size} per page
          </option>
        ))}
      </select>

      {/* pagination buttons */}
      <div className="page-buttons">
        {/* Previous */}
        <button
          className="nav-button"
          disabled={currentPage === 1}
          onClick={() => onPageChange(currentPage - 1)}
        >
          <ChevronLeft size={16} />
        </button>

        {/* page numbers */}
        {pages.map((p, index) =>
          p === "..." ? (
            <span key={`ellipsis-${index}`} className="ellipsis">…</span>
          ) : (
            <button
              key={`page-${p}`}
              onClick={() => onPageChange(Number(p))}
              className={`page-button ${p === currentPage ? 'active' : ''}`}
            >
              {p}
            </button>
          )
        )}

        {/* Next */}
        <button
          className="nav-button"
          disabled={currentPage === totalPages}
          onClick={() => onPageChange(currentPage + 1)}
        >
          <ChevronRight size={16} />
        </button>
      </div>
    </div>
  );
}
