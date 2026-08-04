# Business Requirements Document (BRD)
## Financial Collaboration Platform

**Document Version:** 2.0  
**Date:** 2026-07-27  
**Status:** Draft

---

## 1. Executive Summary

**Product Name:** Financial Collaboration Platform

**Business Problem:**
When groups of people (friends on trips, roommates, family, colleagues) share expenses, they struggle with two critical problems:
1. **Complexity in debt calculation** - Manual tracking of who owes whom is error-prone and time-consuming
2. **Lack of transparency in group finances** - No clear record of who paid what and how much each person owes

**Proposed Solution:**
A collaborative financial management platform that automatically tracks shared expenses, optimizes debt settlement, and provides transparency across personal and group finances.

**Expected Business Value:**
- Reduce time spent on post-trip/post-month debt calculation from 30+ minutes to < 5 minutes
- Increase transparency, reducing payment conflicts and misunderstandings
- Enable groups to make smarter financial decisions through budgeting and insights

---

## 2. Business Background

### Market Context
- **Existing Solutions:** Splitwise (exists but focuses only on expense splitting, no budget/analytics)
- **Market Gap:** No integrated platform combining expense tracking, group management, budget monitoring, and intelligent settlement
- **Target Market:** Informal groups needing financial collaboration (students, young professionals, families)

### Why Now?
- Digital payment adoption increasing → more informal financial transactions
- Groups increasingly global/remote → need transparent financial tracking
- Younger generation expects apps for everyday financial tasks

---

## 3. Problem Statement

### Current State Pain Points

**For Personal Finance Users:**
- No visibility into spending patterns
- Difficulty setting and monitoring budgets
- Can't categorize spending by type (food, transport, entertainment)
- No alerts when overspending

**For Group Finance:**
- After a group trip: "Who paid what?" → manual recalculation, confusion, delays
- Calculating debts: A owes B 300k, B owes C 300k → people don't realize this can be simplified to A→C
- Settlement requires multiple payments (inefficient)
- Disputes due to lost records or memory gaps

**Specific Scenarios:**
1. **Travel Group (5 people, 3-day trip)**
   - Someone pays for hotel (6M)
   - Someone pays for taxi (900k)
   - Someone pays for meals (multiple transactions)
   - After trip: 2+ hours calculating who owes whom, multiple payment requests

2. **Roommates (3 people, monthly)**
   - Rent: A pays
   - Utilities: B pays
   - Internet: C pays
   - End of month: 3 different calculations, 3 back-and-forth messages

3. **Family (5 people)**
   - Mom tracks grocery spending
   - Dad tracks utilities
   - Kids contribute inconsistently
   - No clear picture of who should reimburse whom

### Impact of Problem
- **Time wasted:** 30+ minutes per settlement period
- **Conflicts:** 15-20% of groups report payment disputes
- **Inefficiency:** Multiple redundant payments instead of optimized ones

---

## 4. Business Objectives

### Primary Objectives

**BO-01: Reduce Settlement Friction**
- **Goal:** Decrease time to calculate and settle group debts
- **Target:** From 30+ minutes to < 5 minutes per settlement
- **Measurement:** Average settlement time per group
- **Timeline:** By end of Phase 1

**BO-02: Increase Financial Transparency**
- **Goal:** All group members have real-time visibility into shared finances
- **Target:** 100% of transactions visible to authorized group members
- **Measurement:** Transparency audit (can each member verify their balance?)
- **Timeline:** By end of Phase 1

**BO-03: Enable Smart Expense Optimization**
- **Goal:** Settlement calculation is optimized to minimize payments
- **Target:** 40% reduction in number of required payment transactions
- **Example:** Instead of A→B→C→A (3 payments), optimize to A→C (1 payment)
- **Measurement:** Comparison of payment optimization ratio
- **Timeline:** By end of Phase 1

**BO-04: Facilitate Budget-Aware Spending (Phase 2)**
- **Goal:** Users can set and monitor spending budgets
- **Target:** 70% of users set at least one budget
- **Measurement:** Budget adherence rate, alert effectiveness
- **Timeline:** By end of Phase 2

### Secondary Objectives

**BO-05: Build Reusable Financial Platform**
- Enable expansion to other use cases (subscriptions, recurring bills, savings groups)

**BO-06: Create User Trust Through Accuracy**
- Zero calculation errors in settlement computation
- Transparent, auditable expense records

---

## 5. Stakeholders

### Internal Stakeholders

**Product Owner / PM (You)**
- Role: Define requirements, prioritize features, make trade-offs
- Responsibility: Backlog management, scope decisions
- Interested in: Timeline, quality, portfolio impact

**Developer (You)**
- Role: Build the system, implement requirements
- Responsibility: Architecture, code quality, testing
- Interested in: Technical feasibility, learning value

**Tester (Future)**
- Role: Quality assurance, test planning
- Responsibility: Test case design, bug reporting
- Interested in: Testability, acceptance criteria clarity

### External Stakeholders

**End Users - Personal Finance Tracker**
- Example: Nguyễn A (Student, 22)
- Goals: Understand spending, stay within budget
- Challenges: Budget overruns, unclear expense categories
- Expectations: Simple tracking, budget alerts

**End Users - Group Organizer**
- Example: Trần B (Trip organizer, 28)
- Goals: Organize expenses, settle fairly with minimal friction
- Challenges: Complex debt calculations, disputes
- Expectations: Automatic settlement, clear breakdown

**End Users - Group Members**
- Example: Lê C (Trip participant)
- Goals: Know how much they owe/should receive
- Challenges: Distrust in manual calculations
- Expectations: Transparent, verifiable balances

**Group Stakeholders (Family, Roommates, Clubs)**
- Role: Participate in expense tracking and settlement
- Expectations: Fair calculation, easy payment, transparency

### Stakeholder Concerns & Priorities

| Stakeholder | Primary Concern | Priority |
|-------------|-----------------|----------|
| End User (Payer) | Fair settlement calculation | Must |
| End User (Debtor) | Clear debt visibility | Must |
| Group Owner | Transparency & reduced conflict | Must |
| Developer | System maintainability | Should |
| Future Investor | User retention | Could |

---

## 6. Product Scope

### What's IN Scope

**Personal Finance Module:**
- Individual expense tracking
- Expense categorization
- Personal budget setting (Phase 2)
- Personal analytics dashboard (Phase 2)

**Group Finance Module:**
- Group creation and member management
- Shared expense tracking
- Multiple split methods (equal, percentage, amount-based, share-based)
- Debt calculation
- Settlement optimization
- Settlement workflow (request → confirm → complete)

**System Features:**
- User authentication and authorization
- Real-time notifications (Phase 2)
- Basic expense search and filtering
- Audit trail (who did what, when)

### What's OUT of Scope (Phase 1)

- Bank account integration (can mock in Phase 2)
- OCR receipt recognition (Phase 3)
- AI expense categorization (Phase 3)
- Multi-currency support (Phase 3)
- Mobile app (web only)
- Payment processing (mock payments only)
- Tax reporting
- Enterprise features (advanced permissions, audit logs for compliance)

### Boundary Clarifications

| Item | In/Out | Reasoning |
|------|--------|-----------|
| **Receipt OCR** | Phase 3 | Not MVP, nice-to-have |
| **Bank sync** | Phase 3 | Requires secure integration |
| **International users** | Phase 2 | Focus on Vietnam first |
| **Recurring expenses** | Phase 2 | Valuable but not critical MVP |
| **Advanced analytics** | Phase 2 | MVP needs basic tracking first |
| **Mobile app** | Future | Responsive web sufficient |

---

## 7. User Personas & User Journeys

### Persona 1: Personal Finance Tracker
**Name:** Nguyễn A  
**Age:** 22  
**Occupation:** University student  
**Goal:** Understand where monthly allowance goes  
**Pain Point:** No budget, overspends on food/entertainment  
**Tech Savviness:** High

**User Journey:**
```
1. Signup with email
2. Add personal expenses daily (breakfast, lunch, coffee, transport)
3. View spending by category
4. Set Food budget at 1.5M/month
5. Receive alert at 80% budget
6. Reduce spending accordingly
7. Review month-end breakdown
```

**Success Metric:** User consistently tracks expenses and stays within budget for 3+ months

---

### Persona 2: Trip Organizer
**Name:** Trần B  
**Age:** 28  
**Occupation:** Marketing Manager  
**Goal:** Organize Dalat trip (6 people) with minimal post-trip hassle  
**Pain Point:** Last trip took 2 hours to settle all debts, multiple payment requests  
**Tech Savviness:** Medium-High

**User Journey:**
```
1. Signup and create group "Dalat 2027"
2. Invite 5 friends (via email)
3. Each person joins the group
4. Create expense: "Hotel 6M" → split equally (1M per person)
5. Create expense: "Taxi 900k" → split among 4 people only (225k each)
6. Create expense: "Meals Day 1 - 1.2M" → split equally
7. After trip: System calculates debts
8. System optimizes: Instead of 15 payment requests, suggests 3-4
9. Request payments from debtors
10. Debtors confirm payments
11. Settlement complete
```

**Success Metric:** Settlement time < 5 minutes, zero disputes

---

### Persona 3: Group Member (Roommate)
**Name:** Lê C  
**Age:** 25  
**Occupation:** Software Engineer  
**Goal:** Know exact amount owed to/by roommates monthly  
**Pain Point:** Unclear who paid what, confusion about bills  
**Tech Savviness:** High

**User Journey:**
```
1. Join "Room 302" group
2. Set recurring expenses:
   - Rent: Every 1st of month (1.5M shared equally)
   - Utilities: When paid
3. See dashboard: Current balance with each roommate
4. Month end: Know exactly who owes whom
5. Confirm payments from others
6. Receive confirmation when they pay
```

**Success Metric:** Zero confusion, easy monthly settlement

---

## 8. Business Process & Workflows

### Core Business Process: Group Expense Settlement

**Happy Path:**
```
User A creates Group "Trip 2027" (6 members)
    ↓
User A adds expenses:
  - Hotel: 6M (split equal) → each owes 1M
  - Taxi: 900k (split 4 people) → each owes 225k
  - Meals: 1.2M (split equal) → each owes 200k
    ↓
System calculates balances:
  - A is payer, owes nothing
  - B-F each owe: 1M + 225k + 200k = 1.425M to A
    ↓
System optimizes settlement:
  - Instead of 5 separate payments to A
  - Recognize: B owes A 1.425M, C owes A 1.425M, etc.
  - Result: 5 total payments to A
    ↓
System generates payment requests:
  - Send to B, C, D, E, F
    ↓
Each recipient confirms payment received
    ↓
Settlement marked complete
```

**Business Rules for this process:**
- Cannot delete group if debt exists
- Cannot settle if payer hasn't submitted all expenses
- Settlement request must include: who pays, who receives, amount, evidence (screenshot, receipt)
- Confirmed settlement is permanent (no reversal without evidence)

---

## 9. Business Rules

### Core Business Rules

**BR-001: Expense Integrity**
- An expense must have at least 1 payer
- An expense must have at least 1 person in the split
- Expense amount must be > 0
- Expense date cannot be in future

**BR-002: Split Calculation**
- Equal split: amount / number of people = each person's share
- Percentage split: percentages must sum to 100%
- Amount split: amounts must sum to total expense
- Share split: amount / total shares = value per share
- No negative amounts or splits

**BR-003: Debt Calculation**
- Balance = Sum of amounts person owes - Sum of amounts owed to person
- Positive balance = owes money
- Negative balance = owed money
- Balance cannot exist outside a group context

**BR-004: Settlement Workflow**
- Settlement can only be initiated when debts exist
- Settlement request must be confirmed by debtor before marking complete
- Confirmed settlement cannot be reversed (immutable)
- Settlement marks specific debts as paid

**BR-005: Group Constraints**
- Group cannot be deleted if active debts exist
- Group owner can remove members (inheritance of debts TBD)
- Only group members can view group expenses
- Public groups vs private groups

**BR-006: Budget Rules**
- Budget must be > 0
- Budget period: daily, weekly, monthly, yearly
- Budget tracking: sum of expenses in period
- Alert triggered at 80%, 100%, 120% of budget
- Budget resets based on period

**BR-007: Authorization**
- Users can only see expenses they're part of
- Group owner can view all group data
- Members can only modify their own personal expenses
- Settlement authority: both payer and debtor must confirm

---

## 10. Domain Definitions & Glossary

### Key Entities & Definitions

**User:** An individual account holder who can create expenses and join groups

**Expense:** A financial transaction (either personal or group)
- Personal Expense: Individual spending (no split)
- Group Expense: Shared spending (has split logic)

**Group:** A collection of users for shared expense tracking
- Examples: Trip, Roommates, Family, Club
- Has members, expenses, and settlement history

**Split:** Division of an expense among participants
- Types: Equal, Percentage, Amount-based, Share-based
- Result: Each member's portion of the expense

**Balance:** The financial standing between two people within a group
- Calculated as: money one person owes minus money owed to them
- Context: Always within a specific group

**Debt:** A specific obligation to pay
- Created by: Expense splits
- Example: "A owes B 500k for hotel expense"

**Settlement:** Process of clearing all debts within a group
- Steps: Calculate → Optimize → Request → Confirm → Complete

**Optimization (Settlement Optimization):** Reducing number of payment transactions
- Algorithm: Graph-based debt minimization
- Example: A→B→C becomes A→C
- Benefit: Fewer transactions, faster settlement

**Category:** Classification of an expense
- Types: Food, Transport, Entertainment, Utilities, Rent, etc.

**Tag:** User-defined label for grouping related expenses
- Examples: "Trip", "Groceries", "Project X"

**Budget:** A spending limit for a period
- Scope: Personal per-category, or Group-wide
- Period: Daily, Weekly, Monthly, Yearly

**Transaction:** Any financial record (internal term, not same as Expense)
- Includes: Expenses, Payments, Reversals

**Payment:** Confirmation of money transferred to settle a debt
- Required for: Settlement confirmation
- Not required for: Expense splitting (just calculation)

### Key Distinctions

**Expense vs Transaction:**
- Expense: Business term, what user creates and sees
- Transaction: System term, internal ledger entry
- Expense can generate multiple transactions (if split multiple ways)

**Settlement vs Payment:**
- Settlement: Process of resolving all debts between parties
- Payment: Individual act of money transfer
- One settlement can involve multiple payments

**Balance vs Debt:**
- Balance: Net amount owed between two people in a group
- Debt: Individual obligation from a specific expense

**Group Expense vs Personal Expense:**
- Group: Shared cost, split among members
- Personal: Individual cost, no split

---

## 11. High-Level Features (MoSCoW Prioritization)

### MUST Have (MVP - Phase 1)

| Priority | Feature | Business Value | Timeline |
|----------|---------|-----------------|----------|
| **MUST** | User Registration & Auth | Foundation | Week 1 |
| **MUST** | Personal Expense CRUD | Core functionality | Week 2 |
| **MUST** | Group Creation & Mgmt | Enable group use cases | Week 2 |
| **MUST** | Group Expense Creation | Core value | Week 3 |
| **MUST** | Split Calculation (4 types) | Fair expense division | Week 3 |
| **MUST** | Debt Calculation | Foundation for settlement | Week 3 |
| **MUST** | Settlement Optimization | Key differentiator | Week 4-5 |
| **MUST** | Settlement Workflow | Enable actual settlement | Week 5 |
| **MUST** | Balance Visibility | Enable trust & transparency | Week 3 |

### SHOULD Have (Phase 2)

| Priority | Feature | Business Value | Timeline |
|----------|---------|-----------------|----------|
| **SHOULD** | Budget Setting & Monitoring | Behavioral change | Week 6-7 |
| **SHOULD** | Budget Alerts | Proactive spending management | Week 7 |
| **SHOULD** | Notifications (multi-channel) | User engagement | Week 8 |
| **SHOULD** | Analytics Dashboard | Insights, engagement | Week 8-9 |
| **SHOULD** | Expense Categorization | User organization | Week 2 (add to Phase 1) |
| **SHOULD** | Recurring Expenses | Reduce manual entry | Week 9 |
| **SHOULD** | Expense Search & Filter | Usability | Week 9 |

### COULD Have (Phase 3)

| Priority | Feature | Business Value | Timeline |
|----------|---------|-----------------|----------|
| **COULD** | Receipt OCR | Convenience, error reduction | Not in MVP |
| **COULD** | AI Categorization | Automation | Not in MVP |
| **COULD** | Spending Recommendations | Insights, retention | Not in MVP |
| **COULD** | Multi-currency | International users | Not in MVP |
| **COULD** | Bank Integration | Automatic expense import | Not in MVP |
| **COULD** | Mobile App | Accessibility | Not in MVP |

### WON'T Have (Out of Scope)

- Tax reporting
- Enterprise features
- Real payment processing (mock only)
- Advanced audit compliance

---

## 12. Assumptions

### Technical Assumptions
- Users have internet connectivity
- Users have modern web browsers (Chrome, Firefox, Safari)
- Initial user base small enough for single database

### Business Assumptions
- Target users are tech-savvy (students, young professionals)
- Users willing to use new app for group trips/shared living
- Groups are typically 2-10 people (not 100+)
- Informal transactions (not business accounting)

### User Assumptions
- Users understand basic financial concepts (debt, balance)
- Users have email addresses
- Users will engage with budgets/analytics if shown value
- Trust issues can be solved through transparency

---

## 13. Constraints

### Timeline Constraints
- Solo development: 1 person coding full-time
- Realistic timeline: 5-6 months (not 6-8 weeks)
  - Phase 1 (MVP): 8-10 weeks
  - Phase 2: 6-8 weeks
  - Phase 3 (if time): 4-6 weeks

### Technical Constraints
- No native mobile development (web only initially)
- No complex AI/ML algorithms (Phase 3)
- Must use free/low-cost infrastructure
- Initial user base: < 1000

### Business Constraints
- Zero budget (personal project)
- No hired team (solo developer)
- No customer support team
- Limited to portfolio/learning value

---

## 14. Risks & Mitigation

### Risk Matrix

| Risk | Severity | Probability | Mitigation |
|------|----------|-------------|-----------|
| **Scope Creep** | High | High | Strict Phase gates, no mid-phase changes |
| **Settlement Algorithm Complexity** | Medium | Medium | Start with greedy algorithm, optimize later |
| **Microservices Complexity** | Medium | Medium | Use modular monolith initially, refactor to services |
| **Timeline Overrun** | High | High | Build MVP with essential features only, buffer time |
| **Performance Bottleneck** | Medium | Medium | Load testing Phase 1, optimize critical paths |
| **Database Design Rework** | Medium | Low | Thorough ERD design before coding |
| **Testing Coverage Insufficient** | High | Low | TDD from start, >70% coverage target |
| **User Adoption** | Medium | Medium | Build with friends/family feedback, real usage |

---

## 15. Success Criteria & Metrics

### Functional Success Metrics
- ✅ All MUST features implemented and bug-free
- ✅ Settlement optimization reduces payment transactions by ≥40%
- ✅ Zero calculation errors in 1000+ test cases
- ✅ All user journeys (persona 1-3) completable

### Quality Success Metrics
- ✅ Test coverage ≥70%
- ✅ Code quality score (SonarQube) ≥80%
- ✅ API response time <200ms (p95)
- ✅ Handle 100+ concurrent users

### Business Success Metrics
- ✅ Settlement time <5 minutes (from 30+ minutes)
- ✅ Settlement accuracy 100%
- ✅ Can demonstrate with real group (5+ people)

### Portfolio Success Metrics
- ✅ Complete SDLC documentation (BRD → SRS → Design → Code → Deploy)
- ✅ Can explain architectural decisions
- ✅ Can discuss settlement optimization algorithm
- ✅ Shows enterprise patterns (microservices, event-driven, etc.)

---

## 16. Roadmap & Release Plan

### Phase 1: MVP (8-10 weeks)
**Goal:** Functional group expense tracking with settlement optimization

**Deliverables:**
- User authentication
- Personal & group expense tracking
- Debt calculation & settlement optimization
- Basic notifications

**Success Criteria:** All MUST features working, zero critical bugs

---

### Phase 2: Extended (6-8 weeks)
**Goal:** Smart financial management features

**Deliverables:**
- Budget management with alerts
- Multi-channel notifications
- Analytics dashboard
- Recurring expenses
- Expense categorization

**Success Criteria:** Positive user feedback, engagement metrics improve

---

### Phase 3: Advanced (4-6 weeks, if time permits)
**Goal:** Advanced features for retention

**Deliverables:**
- Receipt OCR
- AI features
- Financial recommendations
- Multi-currency support

**Success Criteria:** TBD based on Phase 1-2 learnings

---

## 17. Next Steps

1. **[Current]** Business Requirements approved
2. **[Week 1]** Software Requirements Specification (SRS) - detailed requirements
3. **[Week 1-2]** Use Case Diagrams & Specifications
4. **[Week 2-3]** Domain Model & Entity Relationship Diagram (ERD)
5. **[Week 3]** Microservice Architecture Design
6. **[Week 3-4]** API Specification (OpenAPI/Swagger)
7. **[Week 4-13]** Development, Testing, Deployment

---

## 18. Approval & Sign-off

| Role | Name | Signature | Date |
|------|------|-----------|------|
| Product Owner | Thành | - | 2026-07-27 |
| Technical Lead | Thành | - | 2026-07-27 |
| Stakeholder Input | Friends/Family | - | TBD |

---

## Document History

| Version | Date | Author | Changes |
|---------|------|--------|---------|
| 1.0 | 2026-07-27 | Claude | Initial draft (hybrid) |
| 2.0 | 2026-07-27 | Claude | Refined per feedback - focus on Business |

---

## Appendix: Related Documents

- **SRS.md** - Software Requirements Specification (Functional & Non-Functional)
- **USE_CASES.md** - Detailed Use Case Specifications
- **DOMAIN_MODEL.md** - Domain entities and relationships
- **ERD.md** - Entity Relationship Diagram
- **ARCHITECTURE.md** - System architecture & microservices design
- **API_SPEC.md** - OpenAPI specification

