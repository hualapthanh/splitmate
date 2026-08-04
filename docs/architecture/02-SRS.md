# Software Requirements Specification (SRS)
## Financial Collaboration Platform

**Document Version:** 1.0  
**Date:** 2026-07-27  
**Status:** Draft  
**Referenced BRD Version:** 2.0

---

## 1. Introduction

### Purpose
This SRS defines what the Financial Collaboration Platform **must do** and **how it must perform**.

### Scope
This document covers:
- Functional Requirements (features and capabilities)
- Non-Functional Requirements (performance, security, scalability)
- System constraints
- Acceptance Criteria

**Out of Scope:** Architecture design, data model (see SDD for architecture)

### Related Documents
- BRD.md - Business context and objectives
- USE_CASES.md - Detailed use case flows
- ERD.md - Data model design
- API_SPEC.md - API contracts

---

## 2. Functional Requirements

### 2.1 User Management & Authentication

#### FR-01: User Registration
**Description:** New users can create an account with email and password

**Acceptance Criteria:**
- User enters email, password, confirm password, name
- System validates email format (RFC 5322)
- System validates password strength (min 8 chars, 1 uppercase, 1 number, 1 special char)
- System checks email not already registered
- User receives confirmation email
- User can confirm email to activate account
- System sends welcome email after confirmation
- Unconfirmed accounts auto-delete after 7 days

**Error Cases:**
- Invalid email format → Display error, suggest format
- Password too weak → Display requirements
- Email already exists → Suggest password reset
- Email confirmation timeout → Allow resend

**Performance:** Registration completes within 3 seconds

---

#### FR-02: User Login
**Description:** Users can authenticate using email and password, receive JWT token

**Acceptance Criteria:**
- User enters email and password
- System validates credentials against stored hash (bcrypt)
- System generates JWT token (1 hour expiry)
- System returns token and user profile
- Failed attempts tracked (lock after 5 attempts for 15 minutes)
- "Remember me" option stores refresh token (30 days)

**Token Contents:**
- user_id
- email
- roles
- iat (issued at)
- exp (expiry)

**Error Cases:**
- Invalid email → Generic error ("Invalid credentials")
- Invalid password → Generic error
- Account locked → Display unlock time
- Email not confirmed → Prompt confirmation

**Performance:** Login completes within 2 seconds

---

#### FR-03: User Profile Management
**Description:** Users can view and update their profile information

**Acceptance Criteria:**
- User can view profile: email, name, avatar, join date, groups count
- User can update: name, avatar (upload image)
- User can change email (requires confirmation on new email)
- User can change password (requires current password)
- Avatar upload: max 5MB, formats: jpg/png/webp
- System resizes avatar to 200x200px
- Changes reflected immediately in app

**Authorization:**
- Users can only edit their own profile
- Cannot change email to existing user email

**Performance:** Profile load <500ms, update <1s

---

#### FR-04: Password Reset
**Description:** Users can reset forgotten password via email link

**Acceptance Criteria:**
- User enters email on reset page
- System sends reset link to email (valid for 1 hour)
- User clicks link, receives form to enter new password
- System validates new password strength
- System updates password and invalidates all existing tokens
- User receives confirmation email
- Link is single-use (can't reuse same link)

**Error Cases:**
- Email not in system → Still show "Check your email" (security)
- Link expired → Prompt user to request new reset
- Password same as old → Display error

**Performance:** Reset request processed within 2 seconds

---

#### FR-05: Logout
**Description:** Users can logout and invalidate their session

**Acceptance Criteria:**
- User clicks logout
- System invalidates JWT token (adds to blacklist)
- System clears refresh token
- User redirected to login page
- Browser clears stored token
- All open sessions of user can be viewed/managed

**Performance:** Logout completes within 1 second

---

### 2.2 Personal Expense Management

#### FR-06: Create Personal Expense
**Description:** Users can record personal expense with category and tags

**Acceptance Criteria:**
- User enters: amount, description, date, category, tags, payment method
- System validates: amount > 0, date not in future, category from enum
- User can attach receipt image (optional)
- System creates expense and stores it
- Expense initially has status: "DRAFT" → "CONFIRMED"
- User can immediately see expense in list

**Payment Methods:**
- Cash
- Credit Card
- Debit Card
- Mobile Wallet
- Bank Transfer

**Categories:**
- Food & Dining
- Transport
- Entertainment
- Utilities
- Rent & Housing
- Healthcare
- Shopping
- Other

**Validation Rules:**
- Amount: 0 < amount ≤ 1,000,000,000
- Date: must be ≤ today
- Description: 1-500 characters

**Performance:** Create expense <1 second

---

#### FR-07: Read Personal Expenses
**Description:** Users can view their personal expense history

**Acceptance Criteria:**
- User sees list of all personal expenses
- Default sort: by date (newest first)
- Shows: date, description, amount, category, payment method
- User can click expense to see details
- Details page shows: all fields, receipt image if exists, edit/delete options

**Performance:** Load expense list <500ms, details <300ms

---

#### FR-08: Filter & Search Personal Expenses
**Description:** Users can filter and search their personal expenses

**Acceptance Criteria:**
- Filter by: date range, category, payment method, tag
- Search by: description, amount range
- Filters are combinable
- Results update in real-time
- Show count of matching expenses
- "Clear filters" button available

**Advanced Filtering:**
- Amount range: min-max
- Date: preset (This month, Last month, Last 3 months, Custom)
- Multiple categories: AND/OR logic

**Performance:** Filter results <1 second (up to 10K expenses)

---

#### FR-09: Update Personal Expense
**Description:** Users can modify personal expense details

**Acceptance Criteria:**
- User can edit: amount, description, date, category, tags, payment method, receipt
- Cannot edit expense if already split in group (must use group expense workflow)
- System validates same rules as creation
- Changes are logged (who changed what when) - for audit trail
- User receives confirmation of change
- Change reflected immediately

**Audit Trail:**
- Shows: "Updated amount from 50k to 100k by User A on 2026-07-27 14:30"

**Performance:** Update completes <1 second

---

#### FR-10: Delete Personal Expense
**Description:** Users can delete personal expenses

**Acceptance Criteria:**
- User can delete expense (soft delete - keep audit trail)
- Cannot delete if used in group expense split
- Shows confirmation: "This action cannot be undone"
- Deleted expense removed from dashboard
- Can view in "Deleted" section for 30 days
- After 30 days: hard delete

**Authorization:** Only expense creator can delete

**Performance:** Delete completes <500ms

---

### 2.3 Group Management

#### FR-11: Create Group
**Description:** Users can create a group for shared expense tracking

**Acceptance Criteria:**
- User enters: group name, description, group type, currency, initial members (optional)
- System validates: name 1-100 chars, unique within user's groups
- Group types: Trip, Roommates, Family, Project, Other
- System generates unique group ID
- Creator automatically becomes group owner
- Group status: ACTIVE

**Acceptance Criteria Details:**
- Group name: not empty, < 100 chars
- Description: optional, < 500 chars
- Currency: default VND, can select others
- Privacy: Private (invitation only) default

**Performance:** Create group <500ms

---

#### FR-12: View Groups
**Description:** Users can see all groups they belong to

**Acceptance Criteria:**
- Show: group name, member count, current balance in group, last activity
- Sort: by recent activity
- Each group card clickable to view details
- Shows member avatars (hover for details)
- Color-coded: positive balance (green), negative (red), neutral (gray)

**Group Details View:**
- Group name, description, type, members list
- Total expenses, total settled
- Current balance with each member
- Group creation date, owner info

**Performance:** Load groups list <500ms, details <300ms

---

#### FR-13: Invite Group Members
**Description:** Group owner can invite users to join group

**Acceptance Criteria:**
- Owner enters: email addresses of invitees (one at a time or bulk)
- System sends invitation email to each invitee
- Email contains: group name, description, join link
- Invitation valid for 7 days
- Invitee can accept/decline via email link or app
- If accepted: member added to group immediately
- System notifies owner of acceptance/decline

**Bulk Invite:**
- Support up to 50 emails at once
- Show summary: "Inviting 5 members"

**Performance:** Send invitations <1 second total

---

#### FR-14: Remove Group Members
**Description:** Group owner can remove members from group

**Acceptance Criteria:**
- Owner can remove any member (except self)
- Shows warning: "Member will see group is removed from their list"
- Removed member cannot see group expenses after removal
- Cannot remove if member has unsettled debts:
  - Option 1: Settle debts first
  - Option 2: Owner absorbs debt
- Removed member receives notification

**Performance:** Remove member <500ms

---

#### FR-15: Leave Group
**Description:** Members can leave a group they belong to

**Acceptance Criteria:**
- Member clicks "Leave Group"
- Shows warning: "You will no longer see this group"
- If member has unsettled debts: cannot leave, must settle first
- After leaving: member removed from group
- Owner notified of member departure
- Member can rejoin if re-invited

**Performance:** Leave group <500ms

---

#### FR-16: Delete Group
**Description:** Group owner can delete a group

**Acceptance Criteria:**
- Owner can delete only if NO active debts exist
- Shows warning: "Deleting removes all expense records"
- After deletion: group archived (history kept for 90 days)
- Members notified group is deleted
- Cannot recover deleted group

**Performance:** Delete group <500ms

---

#### FR-17: Update Group Details
**Description:** Group owner can update group information

**Acceptance Criteria:**
- Owner can edit: name, description, type, currency
- Cannot edit: creation date, members list (use invite/remove)
- Changes reflected immediately to all members
- Members notified of changes

**Performance:** Update <500ms

---

### 2.4 Group Expense Management

#### FR-18: Create Group Expense
**Description:** Any group member can create a shared expense

**Acceptance Criteria:**
- User selects group, enters: amount, description, date, payer, category, attachments
- System validates: amount > 0, payer is group member, date ≤ today
- Expense created with status: DRAFT (ready to split)
- Payer cannot be changed after split assignment (or requires re-split)
- Can attach receipt image (optional)

**Acceptance Criteria Details:**
- Amount: must be > 0
- Payer: one group member (must select)
- Date: cannot be future
- Category: selected from list
- Attachment: optional, same rules as personal expense

**Performance:** Create expense <1 second

---

#### FR-19: Assign Split to Group Expense
**Description:** Define how group expense is divided among members

**Acceptance Criteria:**
- User chooses split method from 4 options:
  1. **Equal Split:** Divide equally among selected members
  2. **Percentage Split:** Specify percentage for each member
  3. **Amount Split:** Specify exact amount for each member
  4. **Share Split:** Assign shares (e.g., A=1 share, B=2 shares, C=1 share)
- System validates split equals total expense amount (±0.01 tolerance)
- Shows preview: "Person A: 500k, Person B: 250k, Person C: 250k"
- Member can exclude themselves from split
- Payer automatically receives $0 share (they paid already)

**Validation Rules:**
- At least 1 member in split (besides payer)
- Percentages sum to 100% (±0.01%)
- Amounts sum to total expense (±0.01)
- Shares > 0

**Performance:** Calculate and save split <500ms

---

#### FR-20: Update Group Expense & Split
**Description:** Member who created expense can modify it and re-split

**Acceptance Criteria:**
- Expense creator can edit: amount, description, date, payer, split
- Changing amount triggers recalculation of splits
- Cannot edit if already settled (marked as SETTLED)
- Must update if original calculation had rounding errors
- Changes reflected immediately in balances

**Performance:** Update expense <1 second

---

#### FR-21: View Group Expenses
**Description:** Group members can see all expenses in the group

**Acceptance Criteria:**
- Shows: date, description, payer, amount, category, split breakdown
- List sorted by date (newest first)
- Click expense to see details: full split, who owes how much, receipt if exists
- Color-coded payer (who paid)
- Shows status: DRAFT, CONFIRMED, SETTLED
- Members see only expenses they're part of

**Performance:** Load expense list <500ms

---

#### FR-22: Delete Group Expense
**Description:** Expense creator can delete group expense

**Acceptance Criteria:**
- Only creator can delete
- Cannot delete if already settled (status = SETTLED)
- Shows warning: "Deleting will recalculate balances"
- After deletion: balances recalculated
- Members notified of deletion
- Soft delete initially (recoverable for 7 days)

**Performance:** Delete <500ms

---

### 2.5 Debt Calculation & Settlement

#### FR-23: Calculate Balances
**Description:** System automatically calculates who owes whom in a group

**Acceptance Criteria:**
- After each expense is confirmed, system recalculates balances
- Balance calculated per pair of members in group
- Positive balance = owes money, Negative balance = owed money
- For each member: show balance with each other member
- Update in real-time (if using WebSocket)
- Example:
  - A paid 1M for expense, B owes 500k
  - A paid 500k for expense, C owes 500k
  - Result: A→B: 500k, A→C: 500k

**Calculation Rules:**
- Sum all expenses where person is debtor
- Subtract all expenses where person is creditor
- Balance = Owed - Owes

**Performance:** Recalculate balances <500ms (even for 1000 expenses)

---

#### FR-24: View Balances
**Description:** Members can see their financial balance with each person in group

**Acceptance Criteria:**
- Dashboard shows: "With [Member Name]: you owe X / they owe you Y"
- List all members and balances
- Color-coded: green (owed to you), red (you owe), gray (settled)
- Click member to see breakdown (which expenses)
- Shows net balance only (not individual transactions)

**Acceptance Criteria Details:**
- List members sorted by balance amount
- Show avatar, name, balance
- Click for expense breakdown

**Performance:** Load balances <300ms

---

#### FR-25: Optimize Settlement (Graph Minimization)
**Description:** System suggests minimal payment plan to settle all group debts

**Key Feature - This is the differentiator!**

**Acceptance Criteria:**
- Given group debts, system calculates optimal settlement
- Algorithm minimizes number of transactions required
- Example:
  ```
  Initial debts:
  A owes B: 300k
  B owes C: 300k
  C owes D: 100k
  
  Optimized:
  A pays C: 300k
  C pays D: 100k
  (Reduced from 3 to 2 payments)
  ```
- Algorithm handles complex scenarios (cycles, multiple paths)
- Shows both "Current state" and "Optimized settlement"
- User can override suggestions if needed

**Algorithm Approach:**
- Phase 1: Build directed graph of debts
- Phase 2: Identify cycles and net them out
- Phase 3: Suggest payment order (debtor → creditor)
- Timeline: Must complete for 100+ transactions in <1 second

**Performance:** Optimization <1 second for up to 100 members, 10K expenses

---

#### FR-26: Request Payments
**Description:** Creditor can formally request payment from debtor

**Acceptance Criteria:**
- Creditor clicks "Request Payment" on optimized settlement
- System generates payment request for each debtor
- Payment request includes: amount, debtor name, creditor name, expenses included
- Sends notification to debtor
- Debtor can see pending requests in app
- Payment request valid for 30 days
- Creditor can cancel payment request (remit debt)
- Debtor can dispute payment request (mark as disagreed, reason optional)

**Acceptance Criteria Details:**
- Request includes: amount, due date (30 days), list of expenses
- Notification sent: email + in-app
- Shows payment instruction (e.g., "Transfer to A's bank account")
- Request marked as PENDING

**Performance:** Create payment request <500ms

---

#### FR-27: Confirm Payment
**Description:** Debtor confirms they have paid the requested amount

**Acceptance Criteria:**
- Debtor receives payment request notification
- Debtor clicks "Confirm Payment" (implies: "I have paid")
- Debtor can optionally attach proof (screenshot, receipt)
- System marks: payment CONFIRMED
- Creditor notified of payment confirmation
- Both parties see: "Settled on [date]"
- Payment is immutable (cannot be reversed without both parties agreeing)

**Acceptance Criteria Details:**
- Confirmation includes: date, time, optional proof
- Notification to creditor: "A confirmed payment of 500k on July 27"
- Show in transaction history

**Performance:** Confirm payment <500ms

---

#### FR-28: Dispute Payment
**Description:** Debtor can dispute a payment request if they disagree

**Acceptance Criteria:**
- Debtor receives payment request
- Debtor can click "I Disagree" with optional reason
- Reason limit: 500 characters
- Status marked as: DISPUTED
- Creditor notified with reason
- Displays message: "This payment is disputed, awaiting creditor response"
- Creditor can: Acknowledge, Adjust amount, or Escalate
- Disputed payments don't count toward settlement completion

**Acceptance Criteria Details:**
- Dispute shows reason to creditor
- Allows creditor to adjust request or close it
- Group owner can arbitrate (future feature)

**Performance:** Dispute payment <500ms

---

#### FR-29: View Settlement Status
**Description:** Members can see progress toward complete settlement

**Acceptance Criteria:**
- Dashboard shows: "X of Y payments confirmed"
- Progress bar visualizing completion
- List: Pending requests, Confirmed payments, Disputed payments
- Filter by status
- Show timestamps for each action
- Mark settlement "Complete" when all payments confirmed

**Performance:** Load settlement status <300ms

---

#### FR-30: Finalize Settlement
**Description:** Group owner marks settlement as complete

**Acceptance Criteria:**
- Available only after all payments confirmed
- Clicking "Complete Settlement" removes all members' debts
- System records: settlement closed on [date]
- Cannot reopen closed settlement (history kept)
- Members notified settlement is complete
- Balances reset to 0 for next cycle

**Performance:** Complete settlement <500ms

---

### 2.6 Budget Management

#### FR-31: Create Personal Budget
**Description:** Users can set spending limits per category for personal expenses

**Acceptance Criteria:**
- User selects category: Food, Transport, Entertainment, etc.
- User enters: limit amount, period (daily, weekly, monthly, yearly)
- System validates: amount > 0
- Budget created with status: ACTIVE
- User sees budget in dashboard immediately

**Acceptance Criteria Details:**
- Limit: must be > 0
- Period: auto-resets (daily resets at midnight, weekly on Monday, monthly on 1st, yearly on Jan 1)
- Multiple budgets per period allowed (e.g., Food 1.5M/month AND Food 500k/week)

**Performance:** Create budget <500ms

---

#### FR-32: Monitor Budget Usage
**Description:** System tracks spending against budget and shows progress

**Acceptance Criteria:**
- Dashboard shows budget progress: "Food: 800k / 1.5M (53%)"
- Progress bar for each budget
- Breakdown of which expenses count toward budget
- Shows: spent, remaining, days left in period
- Updates in real-time as new expenses added

**Color Coding:**
- Green: 0-79% of budget
- Yellow: 80-99% of budget
- Red: 100%+ (over budget)

**Performance:** Update budget progress <500ms

---

#### FR-33: Budget Threshold Alerts
**Description:** System alerts users when approaching budget limits

**Acceptance Criteria:**
- Alert at 80% of budget: "You've spent 80% of your Food budget"
- Alert at 100% of budget: "You've reached your Food budget limit"
- Alert at 120% of budget: "You've exceeded your Food budget by 20%"
- Alerts delivered via: in-app notification, email (if opted-in), push (mobile future)
- Can disable alerts per budget
- Show suggestion: "Consider reducing Food spending"

**Performance:** Send alert <1 second

---

#### FR-34: Update Budget
**Description:** Users can modify budget limits

**Acceptance Criteria:**
- User can edit: amount, period, category
- User can temporarily disable budget
- Changes take effect immediately
- Show confirmation: "Budget updated"
- History of budget changes kept (audit trail)

**Performance:** Update budget <500ms

---

#### FR-35: Delete Budget
**Description:** Users can remove a budget

**Acceptance Criteria:**
- User can delete active budget
- Shows warning: "Spending limit removed for this category"
- Expenses not deleted, just no longer tracked against budget
- Can recreate budget anytime

**Performance:** Delete budget <300ms

---

#### FR-36: Group Budget
**Description:** Group owner can set group-wide budget (Phase 2+)

**Acceptance Criteria:**
- Owner sets: total group budget for period
- System tracks: total group spending against budget
- Shows: % of group budget used
- Members can see progress

**Performance:** <1 second

---

### 2.7 Notifications

#### FR-37: In-App Notifications
**Description:** Users receive notifications within the app for important events

**Events Triggering Notifications:**
- Expense added to group you're in
- Payment request from someone
- Payment confirmed by someone
- You reach budget threshold
- Member invited you to group
- Group updated/settings changed
- Settlement completed

**Acceptance Criteria:**
- Notification appears in notification center (bell icon)
- Shows: event, timestamp, actionable (click to view relevant page)
- Mark as read/unread
- Can dismiss individual notifications
- Can dismiss all notifications
- View notification history (past 30 days)
- Show unread count in bell icon

**Performance:** Deliver notification <2 seconds

---

#### FR-38: Email Notifications
**Description:** Users can receive email notifications for key events (Phase 2)

**Acceptance Criteria:**
- Email sent for: payment request, payment confirmed, group invitation
- Email templates professional and clear
- Unsubscribe link in every email
- Includes action link (e.g., "Confirm Payment")
- User can configure email preferences (which events to notify)

**Performance:** Send email <5 seconds

---

#### FR-39: WebSocket Real-Time Updates
**Description:** Dashboard updates in real-time when group changes (Phase 2)

**Acceptance Criteria:**
- When member A adds expense, all group members see update within 1 second
- When payment confirmed, balance updates immediately
- When settlement optimized, all members see new plan
- No need to refresh browser

**Performance:** Deliver update <1 second to all connected users

---

### 2.8 Analytics & Insights

#### FR-40: Personal Spending Dashboard (Phase 2)
**Description:** Users see breakdown of personal spending by category

**Acceptance Criteria:**
- Shows: pie chart of spending by category (last month, month selection)
- Shows: spending trend over time (line chart)
- Shows: top spending categories ranked
- Filter by date range
- Comparison: "This month vs last month: +15%"
- Export as PDF/CSV

**Performance:** Load dashboard <1 second

---

#### FR-41: Group Analytics Dashboard (Phase 2)
**Description:** Group members see group expense analytics

**Acceptance Criteria:**
- Shows: total group spending, per-member contribution
- Shows: most expensive categories in group
- Member comparison: "A paid 35% of expenses"
- Trend: group spending over time
- Settlement status: % of debts settled

**Performance:** Load analytics <1 second

---

#### FR-42: Spending Recommendations (Phase 2)
**Description:** System suggests ways to reduce spending (Phase 2+)

**Acceptance Criteria:**
- Alert: "You spent 18% more on Food this month vs last month"
- Suggestion: "You exceeded Food budget 3 consecutive months. Consider setting limit to 1M"
- Insight: "If you reduce dining out by 20%, you save 1.2M/month"

**Performance:** Generate recommendations <2 seconds

---

### 2.9 Expense Categorization & Tagging

#### FR-43: Categorize Expenses
**Description:** Expenses automatically or manually categorized (implemented in Phase 1)

**Acceptance Criteria:**
- Pre-defined categories available at expense creation
- User selects category during expense creation
- Can recategorize expense anytime
- Categories shown in filters and reports

**Categories:**
- Food & Dining
- Transport & Delivery
- Entertainment
- Utilities & Bills
- Rent & Housing
- Healthcare & Fitness
- Shopping & Personal
- Other

**Performance:** <500ms

---

#### FR-44: Tag Expenses
**Description:** Users can create custom tags for organizing expenses

**Acceptance Criteria:**
- User creates tags (e.g., "Trip", "Project X", "Groceries")
- Tags reusable across expenses
- User can select multiple tags per expense
- Filter expenses by tag
- Show tag cloud
- Rename/delete tags (cascade to expenses)

**Performance:** <300ms

---

## 3. Non-Functional Requirements

### 3.1 Performance Requirements

#### Response Time
- API endpoints: <200ms (p95)
- Page load: <1 second
- Dashboard load: <1 second
- Data query (1000 expenses): <500ms
- Settlement optimization (100 members, 10K expenses): <1 second

#### Throughput
- Support 100+ concurrent users per group
- Support 1000+ total concurrent users
- Process 1000 expenses/minute without degradation

#### Resource Usage
- Backend memory: < 2GB idle
- Database size: < 50GB (supports 1M expenses + users)

### 3.2 Reliability & Availability

#### Uptime
- Target: 99.9% availability
- Planned maintenance: < 2 hours/month

#### Data Integrity
- Zero data loss for confirmed transactions
- Automatic daily backups
- Recovery capability: restore to within 15 minutes of failure

#### Fault Tolerance
- System continues functioning if non-critical services down
- Graceful degradation (e.g., notifications fail, but expenses still tracked)

### 3.3 Security Requirements

#### Authentication & Authorization
- JWT tokens with 1-hour expiry
- Refresh tokens with 30-day expiry
- Password hashing: bcrypt with salt
- Rate limiting: 100 requests/minute per user
- Account lockout: 5 failed attempts → 15 minute lock

#### Data Protection
- HTTPS/TLS for all communication
- Encrypt sensitive data at rest (passwords, PII)
- SQL injection prevention (parameterized queries)
- XSS prevention (input sanitization)
- CSRF protection (token-based)

#### Access Control
- Users can only see their own expenses
- Group members can see group expenses
- Group owner has elevated permissions
- No cross-group data visibility

#### Audit Trail
- Log: who changed what, when, why
- Keep logs for 1 year
- Immutable transaction history

### 3.4 Scalability

#### Horizontal Scaling
- Stateless services (can run multiple instances)
- Database replication for read scaling
- Cache layer (Redis) for frequent queries
- Message queue (Kafka) for async operations

#### Data Scaling
- Database partitioning by group (future)
- Archive old data (> 2 years)
- Index optimization for search

### 3.5 Maintainability

#### Code Quality
- SonarQube score: ≥80%
- Test coverage: ≥70%
- Code reviewed before merge
- Documentation: ≥80% of code

#### Documentation
- API documentation (OpenAPI/Swagger)
- README with setup instructions
- Architecture documentation
- Database schema documentation

### 3.6 Usability

#### User Interface
- Mobile-responsive design
- Intuitive navigation
- Clear error messages
- Loading indicators
- Confirmation dialogs for destructive actions

#### Accessibility
- WCAG 2.1 AA compliance (basic)
- Keyboard navigation support
- Screen reader support for critical elements

### 3.7 Compatibility

#### Browsers
- Chrome 90+
- Firefox 88+
- Safari 14+
- Edge 90+

#### Devices
- Desktop (1920x1080 minimum)
- Tablet (iPad Air, Samsung Tab)
- Mobile future (responsive tested, app Phase 2+)

### 3.8 Deployment & Operations

#### Deployment
- Docker containerization
- Docker Compose for local development
- Kubernetes ready (future)
- CI/CD pipeline (GitHub Actions)

#### Monitoring
- Prometheus metrics collection
- Grafana dashboards
- Error tracking (Sentry)
- Distributed tracing (OpenTelemetry)

#### Logging
- Centralized logging
- Log levels: DEBUG, INFO, WARN, ERROR
- Retention: 30 days

---

## 4. System Constraints

### Technical Constraints
- Language: Java 21
- Framework: Spring Boot 3.3
- Database: PostgreSQL 14+
- Cache: Redis 7+
- Message Broker: Kafka 3.5+
- Architecture: Microservices (10 services)

### Infrastructure Constraints
- No paid services (free tier only)
- Single database initially
- No CDN
- No complex clustering initially

### Team & Timeline Constraints
- Solo developer
- 5-6 months full-time
- Must complete MVP in 10 weeks
- Limited testing infrastructure

---

## 5. Acceptance Criteria Summary

### Phase 1 (MVP) Must Complete
- ✅ All MUST functional requirements (FR-01 to FR-30)
- ✅ All MUST non-functional requirements (performance, security, availability)
- ✅ 70%+ test coverage
- ✅ Complete API documentation
- ✅ Runs locally with Docker Compose
- ✅ Zero critical bugs
- ✅ Can demonstrate with real group (5+ people)

### Phase 2 Should Complete
- ✅ All SHOULD requirements
- ✅ Multi-channel notifications
- ✅ Analytics dashboard
- ✅ Budget monitoring

### Phase 3 Could Complete
- ✅ Nice-to-have features (OCR, AI, recommendations)
- ✅ Mobile app

---

## 6. Assumptions & Dependencies

### Assumptions (from BRD)
- Users have email
- Users understand financial concepts
- Groups are 2-10 people typically
- Initial user base < 1000

### Dependencies
- Email service for notifications
- PostgreSQL database
- Kafka message broker
- Redis cache
- Container runtime (Docker)

### External Dependencies
- Email provider (SMTP or service)
- DNS for domain
- Storage for attachments (MinIO or S3)

---

## 7. Glossary

| Term | Definition |
|------|-----------|
| Expense | Financial transaction record (personal or group) |
| Split | Division of group expense among members |
| Balance | Net amount one person owes another in a group |
| Debt | Specific obligation to pay for an expense |
| Settlement | Process of clearing all debts in group |
| Budget | Spending limit for category/group in period |
| Transaction | System-level record of any financial event |
| Payer | Person who paid the initial expense |
| Debtor | Person who owes money for shared expense |
| Creditor | Person owed money for shared expense |

---

## 8. Appendix: Use Case Mapping

| Use Case | Primary FR | Supporting FR |
|----------|-----------|----------------|
| Register & Login | FR-01, FR-02, FR-05 | - |
| Track Personal Expenses | FR-06 to FR-10 | FR-43, FR-44 |
| Create Group & Invite | FR-11, FR-13 | - |
| Add Group Expense | FR-18, FR-19 | FR-43 |
| Calculate & View Balances | FR-23, FR-24 | - |
| Optimize & Request Settlement | FR-25, FR-26 | - |
| Confirm Payments | FR-27, FR-28, FR-29 | - |
| Complete Settlement | FR-30 | - |
| Manage Budget | FR-31 to FR-35 | FR-33 |

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-07-27 | Claude | Initial SRS (separated from BRD) |

