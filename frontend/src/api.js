import { apiRequest } from './services/api.js';

export async function getQuestions(page = 0, size = 10) {
  const response = await apiRequest(
    `/questions?page=${page}&size=${size}`
  );
  const result = response.data;
  const data = result.data ?? result;

  return {
    questions: data.content ?? data.items ?? data.questions ?? [],
    totalElements: data.totalElements ?? 0,
    totalPages: data.totalPages ?? 1,
    page: data.number ?? data.page ?? page,
  };
}
