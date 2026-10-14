const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || '/api').replace(/\/+$/, '');

async function request(path, options = {}) {
  const token = localStorage.getItem('token');
  const headers = new Headers(options.headers);

  if (options.body && !headers.has('Content-Type')) {
    headers.set('Content-Type', 'application/json');
  }
  if (token) {
    headers.set('Authorization', `Bearer ${token}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, {
    ...options,
    headers,
  });
  const data = response.status === 204 ? null : await response.json();

  if (!response.ok) {
    if (response.status === 401 && token) {
      window.dispatchEvent(new Event('auth-expired'));
    }
    throw new Error(data?.message || `Request failed: ${response.status}`);
  }

  return { data };
}

export const authService = {
  login: (credentials) =>
    request('/auth/login', {
      method: 'POST',
      body: JSON.stringify(credentials),
    }),
  register: (userData) =>
    request('/auth/register', {
      method: 'POST',
      body: JSON.stringify(userData),
    }),
  getCurrentUser: () => request('/auth/me'),
};

export const materialService = {
  toggleBookmark: (materialId) =>
    request(`/materials/${materialId}/bookmark`, { method: 'POST' }),
};

export const apiRequest = request;
