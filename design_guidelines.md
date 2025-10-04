# Design Guidelines: Musep50 Trésorerie Express

## Design Approach

**Selected Approach:** Design System - Material Design adaptation
**Justification:** This is a utility-focused financial productivity tool requiring clarity, consistency, and data-dense displays. Material Design provides robust patterns for forms, data visualization, and mobile-first interfaces ideal for treasury management.

## Core Design Principles

1. **Clarity First:** Financial data must be immediately readable with clear visual hierarchy
2. **Efficiency:** Minimize clicks for frequent actions (recording payments, viewing progress)
3. **Trust & Security:** Professional appearance that reinforces data security and reliability
4. **Mobile Optimized:** Touch-friendly targets, thumb-zone navigation, optimized for one-handed use

---

## Color Palette

### Light Mode (Primary)
- **Primary:** 220 70% 50% (Trust blue - headers, CTAs, active states)
- **Primary Variant:** 220 80% 40% (Darker blue for emphasis)
- **Secondary:** 142 70% 45% (Success green - collected amounts, confirmations)
- **Background:** 220 15% 98% (Soft white with subtle blue tint)
- **Surface:** 0 0% 100% (Pure white for cards, modals)
- **Text Primary:** 220 20% 15% (Near black with blue undertone)
- **Text Secondary:** 220 15% 45% (Medium gray for labels)

### Accent & Status Colors
- **Success:** 142 70% 45% (Payment complete, targets met)
- **Warning:** 35 90% 55% (Approaching deadlines, partial payments)
- **Error:** 0 70% 50% (Overdue, validation errors)
- **Info:** 200 80% 50% (Neutral information, tips)

### Dark Mode
- **Background:** 220 20% 12%
- **Surface:** 220 15% 18%
- **Primary:** 220 70% 60%
- **Text Primary:** 220 15% 95%

---

## Typography

**Font Families:**
- **Primary:** Inter (via Google Fonts) - Clean, highly readable, excellent for data
- **Numeric:** Tabular figures variant for aligned financial displays

**Type Scale:**
- **H1 Dashboard:** 2rem (32px), font-weight 700 - Operation titles
- **H2 Sections:** 1.5rem (24px), font-weight 600 - Card headers, modal titles  
- **Body Large:** 1rem (16px), font-weight 400 - Primary content, form inputs
- **Body Small:** 0.875rem (14px), font-weight 400 - Secondary info, labels
- **Caption:** 0.75rem (12px), font-weight 500 - Metadata, timestamps
- **Numbers Large:** 1.75rem (28px), font-weight 700, tabular-nums - Main amounts
- **Numbers Small:** 1rem (16px), font-weight 600, tabular-nums - Table data

---

## Layout System

**Spacing Scale:** Tailwind units of 2, 4, 6, 8, 12, 16 (p-2, m-4, gap-6, py-8, etc.)

**Container Widths:**
- Mobile: Full width with px-4 padding
- Tablet: max-w-3xl
- Desktop: max-w-7xl for dashboard grid

**Grid Patterns:**
- Dashboard operations: Single column mobile, 2-column md, 3-column lg
- Payment forms: Single column with max-w-md centering
- Data tables: Horizontal scroll on mobile, full table on md+

---

## Component Library

### Navigation
- **Top Bar:** Sticky header with app logo, current user indicator, menu toggle
- **Bottom Navigation (Mobile):** Fixed bar with Dashboard, New Payment, Reports, Settings (4 primary actions)
- **Sidebar (Desktop):** Collapsible left sidebar with operation filters and quick actions

### Cards & Containers
- **Operation Cards:** Rounded corners (rounded-lg), shadow-md, p-6
  - Header: Operation name + type badge
  - Progress bar: Height h-3, rounded-full, animated width transition
  - Stats grid: 3 columns showing Ciblé/Collecté/Restant
  - Action buttons: Edit, View Details, Export
  
- **Payment Cards:** Compact list items, p-4, border-b, tap to expand
  - Primary: Payer name + amount (bold)
  - Secondary: Date, method, payment badge
  - Expandable: Full details with comment and receipt reference

### Forms
- **Input Fields:** 
  - Height: h-12 (touch-friendly)
  - Border: 2px solid with focus ring-2 ring-primary
  - Label: Floating or top-aligned, text-sm font-medium
  - Error state: Red border + text-sm error message below
  
- **Quick Payment Form:**
  - Full-screen modal on mobile
  - Centered card max-w-md on desktop
  - Large touch targets for operation selector (min h-14)
  - Numeric keypad optimization for amount input
  - Prominent "Enregistrer" button (h-14, w-full, bg-primary)

### Data Display
- **Progress Indicators:**
  - Horizontal bars: h-3, background gray-200, filled primary/success gradient
  - Percentage text: Positioned top-right, font-semibold
  - Animation: smooth transition-all duration-300

- **Stats Display:**
  - Card-based layout with icon, label, value
  - Large numbers: text-2xl font-bold tabular-nums
  - Trend indicators: Small arrows with color coding

- **Payment Tables:**
  - Sticky header on scroll
  - Alternating row colors (subtle)
  - Right-aligned numeric columns
  - Sort indicators in headers
  - Row hover: bg-gray-50 cursor-pointer

### Buttons & Actions
- **Primary CTA:** bg-primary text-white h-12 px-6 rounded-lg font-semibold shadow-sm hover:shadow-md
- **Secondary:** border-2 border-primary text-primary bg-transparent
- **Danger:** bg-red-500 text-white (for delete, reset actions)
- **Icon Buttons:** w-10 h-10 rounded-full, centered icon, subtle hover bg-gray-100
- **FAB (Floating Action):** Fixed bottom-right (mobile), w-14 h-14, rounded-full, primary color, shadow-lg
  - Primary action: "Nouveau paiement" with + icon

### Modals & Overlays
- **PIN Entry:** Centered modal, max-w-sm, numeric pad layout with large buttons (grid-cols-3)
- **Export Options:** Bottom sheet on mobile, centered modal on desktop
  - Radio buttons for format selection (PDF/CSV/Image/Text)
  - Preview area showing sample output
  - Share buttons with WhatsApp icon prominent

### Lists & Filters
- **Retardataires Screen:** 
  - Filterable list with search bar at top
  - Checkbox selection for bulk messaging
  - Contact cards with name, amount due, days overdue
  - Floating action: "Envoyer rappels" showing count of selected

### Badges & Tags
- **Operation Type:** Small pill badges (px-3 py-1 rounded-full text-xs font-semibold)
  - Adhésion: blue-100 text-blue-800
  - Fonds de caisse: green-100 text-green-800
  - Cotisation: purple-100 text-purple-800
  
- **Payment Method:** Icon + label badges
- **Status:** Dot indicator + text (En cours/Clôturé)

---

## Responsive Behavior

**Mobile-First Breakpoints:**
- Base (320px+): Single column, stacked components, bottom nav
- md (768px+): 2-column grids, side-by-side stats, expanded cards
- lg (1024px+): 3-column dashboard, sidebar navigation, data tables

**Touch Optimization:**
- Minimum tap target: 44x44px (w-11 h-11)
- Increased padding in forms: p-4 instead of p-2
- Bottom-aligned primary actions for thumb reach
- Swipe gestures for card actions (delete, archive)

---

## Animations & Interactions

**Minimal & Purposeful:**
- Progress bar fill: transition-all duration-500 ease-out
- Card hover: transform scale-[1.02] duration-200
- Modal enter: fade + slide from bottom (mobile), fade + scale (desktop)
- Form validation: shake animation on error (animate-shake)
- Success feedback: checkmark icon with scale-in animation

**No Animations:**
- Avoid decorative transitions
- No scroll-triggered effects
- No loading spinners for local operations (instant feedback)

---

## WhatsApp Integration UI

**Share Button Styling:**
- WhatsApp green accent: bg-[#25D366] with white WhatsApp icon
- Text: "Partager via WhatsApp" with icon
- Alternative: Standard share icon opening system sheet

**Preview Modal:**
- Show formatted message text in chat-bubble style
- Copy button for manual paste
- Direct share button opening WhatsApp Web/App

---

## Security & Privacy Visual Cues

- **PIN Screen:** Large numeric keypad, dot indicators for entered digits, backspace button
- **Locked State:** Gray overlay with lock icon on sensitive data
- **Biometric Option:** Fingerprint icon if available, smooth transition from PIN
- **Data Export:** Shield icon with "Chiffré" badge on encrypted exports

---

## Empty States & Feedback

- **No Operations:** Illustration + "Créer votre première opération" CTA
- **No Payments:** "Aucun paiement enregistré" with quick add button
- **Success Messages:** Toast notifications (top-right desktop, top mobile) with checkmark, auto-dismiss 3s
- **Error Messages:** Inline below fields + toast for system errors

---

## Images

**No hero images required** - This is a utility dashboard app. Visual hierarchy comes from data visualization, progress bars, and clear typography. 

**Icons:** Use Heroicons (outline style) via CDN for consistency - finance, chart-bar, users, document-report, cog, bell icons throughout.