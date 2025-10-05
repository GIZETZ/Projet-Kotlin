# Musep50 Trésorerie Express

## Overview

Musep50 Trésorerie Express is a financial treasury management application designed for associations, specifically focusing on tracking member payments across three operation types: membership fees (adhésion), exceptional contributions (cotisations exceptionnelles), and cash fund collections (fonds de caisse). The application enables quick payment recording, progress tracking, list generation for sharing via WhatsApp, and basic financial reporting.

The application is built as a full-stack web application with offline-first capabilities, targeting mobile users (particularly Android) with a focus on simplicity and efficiency for treasury managers.

## User Preferences

Preferred communication style: Simple, everyday language.

## System Architecture

### Frontend Architecture

**Framework & UI Library:**
- React with TypeScript for type safety and developer experience
- Vite as the build tool and development server
- Wouter for lightweight client-side routing
- shadcn/ui component library with Radix UI primitives for accessible, customizable components
- Tailwind CSS for utility-first styling with custom design tokens

**State Management:**
- TanStack Query (React Query) for server state management and caching
- Local state management using React hooks
- localStorage for user session persistence

**Design System:**
- Material Design adaptation with custom color palette
- Light mode primary with dark mode support
- Typography: Inter font family from Google Fonts
- Mobile-first responsive design with bottom navigation for mobile devices
- Custom design tokens defined in CSS variables for consistent theming

**Key Frontend Patterns:**
- Component-based architecture with reusable UI components
- Form handling with react-hook-form and Zod validation
- Toast notifications for user feedback
- Modal dialogs for forms and confirmations
- Progressive enhancement for offline capabilities

### Backend Architecture

**Runtime & Framework:**
- Node.js with Express.js for HTTP server
- TypeScript throughout for type safety
- ES Modules for modern JavaScript patterns

**API Design:**
- RESTful API structure under `/api` routes
- JSON request/response format
- Session-based authentication with PIN codes
- Error handling middleware with standardized error responses

**Key Backend Patterns:**
- Route handlers organized in `server/routes.ts`
- Storage abstraction layer in `server/storage.ts` for database operations
- Middleware for request logging and error handling
- Development-only Vite middleware integration for HMR

### Data Storage

**Database:**
- SQLite (better-sqlite3) for local-first data storage
- Drizzle ORM for type-safe database queries and schema management
- Configured for PostgreSQL dialect in production (via drizzle.config.ts) with Neon serverless PostgreSQL support
- Database schema managed through migrations in `shared/schema.ts`

**Schema Design:**
- **users**: Member information, roles, and PIN authentication
- **operations**: Financial campaigns/operations with target amounts and dates
- **paiements**: Individual payment records linked to operations and users
- **parametres**: Application configuration (e.g., default membership amount)

**Data Relationships:**
- Operations have many payments (one-to-many)
- Users have many payments (one-to-many)
- Payments reference both operations and users (foreign keys)

### Authentication & Security

**Authentication Mechanism:**
- PIN-based authentication (4-digit codes)
- Email + PIN credential verification
- User session stored in localStorage (client-side)
- No encryption implemented for PINs (stored as plain text)

**Security Considerations:**
- Simple PIN-based access control suitable for trusted environments
- No password hashing or advanced security measures implemented
- Client-side session management without server-side session tokens

### External Dependencies

**Third-Party UI Components:**
- Radix UI primitives for accessible component foundations (@radix-ui/react-*)
- shadcn/ui component system (configured in components.json)
- Lucide React for iconography
- react-icons for additional icons (WhatsApp)
- date-fns for date formatting and manipulation (French locale)

**Development Tools:**
- Replit-specific plugins for development environment (@replit/vite-plugin-*)
- TypeScript for type checking
- PostCSS with Tailwind CSS and Autoprefixer
- ESBuild for production bundling

**Database & ORM:**
- better-sqlite3 for SQLite database
- Drizzle ORM with drizzle-kit for migrations
- @neondatabase/serverless for PostgreSQL serverless support
- drizzle-zod for schema validation

**Form & Validation:**
- react-hook-form for form state management
- @hookform/resolvers for validation integration
- Zod for schema validation (via Drizzle)

**Planned Integrations:**
- WhatsApp sharing via Web Share API or Intent system
- PDF/Image/CSV export functionality (mentioned in UI but not fully implemented)
- Mobile-specific features planned for Android native version