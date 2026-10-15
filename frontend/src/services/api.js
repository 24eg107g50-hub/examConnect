const defaultApiBaseUrl = window.location.hostname.endsWith('.netlify.app')
  ? 'https://examconnect-da6c.onrender.com/api'
  : '/api';
const API_BASE_URL = (import.meta.env.VITE_API_BASE_URL || defaultApiBaseUrl).replace(/\/+$/, '');

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
  const contentType = response.headers.get('Content-Type') || '';
  const isJson = contentType.includes('application/json') || contentType.includes('+json');
  const data = response.status === 204
    ? null
    : isJson
      ? await response.json()
      : await response.text();

  if (!response.ok) {
    if (response.status === 401 && token) {
      window.dispatchEvent(new Event('auth-expired'));
    }
    throw new Error(
      (isJson && data?.message) ||
      `Request failed: ${response.status}${isJson ? '' : ' (API returned a non-JSON response; check the API URL)'}`
    );
  }
  if (!isJson && response.status !== 204) {
    throw new Error('API returned a non-JSON response; check the API URL.');
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
