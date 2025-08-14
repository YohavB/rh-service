## FE Error Handling Guide

### Error Schema
All backend errors return:
```json
{
  "code": "ERROR_TYPE",
  "message": "Human-friendly error message",
  "status": 422
}
```

### ErrorType values
Key values you may encounter:
- AUTHENTICATION → 401
- INVALID_JWT → 422
- CORRUPTED_DATA → 422
- INVALID_INPUT → 422
- DB_ACCESS → 422
- RESOURCE_ALREADY_EXISTS → 409
- RESOURCE_NOT_EXISTS → 404
- ENTITY_NOT_FOUND → 404
- BAD_CREDENTIAL → 403
- CAR_HAS_NO_OWNER → 403
- USER_CONSENT_REQUIRED → 403
- HTTP_CALL → 500
- UNKNOWN → 500

### Client handling patterns
- If status is 401 (AUTHENTICATION): trigger logout or refresh flow
- If code is USER_CONSENT_REQUIRED: show consent UI then retry with agreedConsent=true
- If code is INVALID_INPUT: present the message as a generic validation error
- For 404 codes: show a not-found message
- For 409: indicate the resource already exists (offer navigation)
- For 403: show forbidden; for CAR_HAS_NO_OWNER, inform user action cannot be performed
- For 500: show generic error and prompt retry

### Examples

TypeScript fetch wrapper:
```ts
async function request(input: RequestInfo, init?: RequestInit) {
  const res = await fetch(input, init);
  if (!res.ok) {
    const err: { code: string; message: string; status: number } = await res.json();
    throw err;
  }
  return res.json();
}
```

Handling consent flow:
```ts
try {
  await request('/api/v1/auth/google', { method: 'POST', body: JSON.stringify({ token }) });
} catch (e: any) {
  if (e.code === 'USER_CONSENT_REQUIRED') {
    const agreed = await showConsentModal();
    if (agreed) {
      const url = new URL('/api/v1/auth/google', location.origin);
      url.searchParams.set('agreedConsent', 'true');
      await request(url.toString(), { method: 'POST', body: JSON.stringify({ token }) });
    }
  } else {
    showToast(e.message || 'Something went wrong');
  }
}
```

