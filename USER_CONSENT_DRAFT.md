## User Consent Draft

### First-run consent (granular)

- By continuing, you agree that **[Company Legal Name]** ("we", "us"), **[Registered Address]**, processes your data as described below. You can withdraw consent anytime in Settings or by contacting **[Privacy Email]**.
- We use OAuth sign-in (Google/Apple/Facebook) to create your account and operate the RushHour service.

- **Notifications (required for core functionality)**: I agree to receive push notifications about car blocking/unblocking and related operational status. No marketing notifications.
- **Camera (optional; requested when used)**: I allow camera access to scan license plates to identify my car. No background capture; camera activates only when I use the feature.
- **Email (informational)**: We use your sign-in email for account, authentication, and security communications only. No marketing email without separate consent.
- **Geolocation (future; off by default)**: We do not collect location now. In a future version, we may request location to enable features like nearby coordination. You can decline.

Actions:
- Agree and continue
- Decline

Links:
- Privacy Policy: [Privacy Policy URL]
- Terms of Service: [Terms URL]

### Just-in-time permission copy

- **Pre-prompt for notifications (iOS/Android)**: "RushHour sends operational alerts when your car is blocked or when you’re free to go. You can change this anytime in Settings."
- **OS prompt rationale (Camera)**: "RushHour uses the camera to scan license plates to identify your car. No background recording."

- iOS Info.plist usage descriptions:

```xml
<key>NSCameraUsageDescription</key>
<string>RushHour uses the camera to scan license plates you choose to scan. No background capture.</string>
```

- Android permission rationale strings (example):

```xml
<string name="camera_permission_rationale">Allow camera to scan license plates you choose to scan. No background capture.</string>
<string name="notification_permission_rationale">Allow notifications for car blocking/unblocking alerts. No marketing.</string>
```

### Consent & Privacy summary (linked)

- **Controller**: [Company Legal Name], [Registered Address], contact: [Privacy Email]. Jurisdictions: [Israel/EEA/UK/US-CA, etc.].

- **What we collect**
  - From sign-in provider: first name, last name, email, optional profile photo.
  - App data: push notification token; car details you add; blocking/unblocking relationships.
  - No analytics/crash telemetry; no geolocation at this time. No camera images are stored unless you explicitly upload media (not used currently).

- **Why we collect**
  - Authenticate you and operate the service (car ownership and blocking/unblocking features).
  - Send operational push notifications about blocking status.
  - Enable camera-based plate scanning when you choose to use it.

- **Legal bases (where applicable, e.g., EEA/UK)**
  - Contract: account creation and core service operation.
  - Legitimate interests: security, service integrity, preventing misuse.
  - Consent: push notifications, camera access, any future geolocation, any marketing (not used).

- **Sharing/Processors**
  - Sign-in providers: Google/Apple/Facebook (for authentication).
  - Push delivery: Expo/FCM/APNs.
  - Hosting and infrastructure: [Cloud Provider], [Region(s)].

- **International transfers**: Data may be processed outside your country. Where required, we use appropriate safeguards (e.g., SCCs/TIAs). Details: [Link or short note].

- **Retention**
  - Account data: retained while your account is active and as required by law; deleted upon request subject to legal/operational constraints.
  - Logs: retained for [X days/months].
  - Push tokens: retained while notifications are enabled or until revoked/expired.

- **Security**: TLS in transit; encryption at rest (where applicable); access controls; least-privilege. Additional measures: [TBD].

- **Your choices and rights**
  - You can revoke notifications and camera permission in device settings and manage in-app preferences.
  - Request access, correction, deletion, or export by contacting **[Privacy Email]**. If in EEA/UK, you may complain to your supervisory authority.

- **Children**: The service is not directed to children under **[Age]**. We do not knowingly collect their data.

- **Changes**: We’ll notify you of material changes and request consent again where required.

