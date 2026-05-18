// Helper: Parse body dari Android (form-urlencoded) ATAU dari browser (JSON)
export async function parseBody(request) {
  const contentType = request.headers.get('content-type') || '';
  
  if (contentType.includes('application/x-www-form-urlencoded')) {
    const text = await request.text();
    const params = new URLSearchParams(text);
    const obj = {};
    for (const [key, value] of params.entries()) {
      obj[key] = value;
    }
    return obj;
  }
  
  // Default: JSON
  try {
    return await request.json();
  } catch {
    return {};
  }
}
