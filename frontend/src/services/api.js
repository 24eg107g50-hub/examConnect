
@'
export async function getQuestions(page = 0, size = 10) {
  const response = await fetch(
    `/api/questions?page=${page}&size=${size}`
  );

  if (!response.ok) {
    throw new Error(`Request failed: ${response.status}`);
  }

  const result = await response.json();
  const data = result.data ?? result;

  return {
    questions: data.content ?? data.items ?? data.questions ?? [],
    totalElements: data.totalElements ?? 0,
    totalPages: data.totalPages ?? 1,
    page: data.number ?? data.page ?? page,
  };
}
'@ | Set-Content -Path .\src\api.js -Encoding utf8
